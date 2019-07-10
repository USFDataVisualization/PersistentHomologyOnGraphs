////////////////////////////////////////////////////////////////////////////
//
//  libusf --- Library of common functions
//  Copyright (C) 2019 Paul Rosen
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <https://www.gnu.org/licenses/>.
//
////////////////////////////////////////////////////////////////////////////

package usf.dvl.graph.edgebundling;

import java.util.ArrayList;
import java.util.Vector;

import usf.dvl.draw.DLineSet;
import usf.dvl.draw.DPositionSet2D;
import usf.dvl.graph.layout.forcedirected.BasicForce;
import usf.dvl.graph.layout.forcedirected.Constraint;
import usf.dvl.graph.layout.forcedirected.FDLVertex;
import usf.dvl.graph.layout.forcedirected.ForceDirectedLayout;
import usf.dvl.graph.layout.forcedirected.PerNodeForce;
import usf.dvl.graph.layout.forcedirected.PairwiseForce.SpringAttractiveForces;

public class EdgeBundling extends ForceDirectedLayout {

	public static int subdivisions = 10;
	
	protected DPositionSet2D pnts;
	protected DLineSet lns;
	
	protected Vector<FDLVertex> anchors = new Vector<FDLVertex>();
	protected Vector<int[]> edges = new Vector<int[]>();
	
	SpringAttractiveForces springForce;
	PairwiseAttractiveForce attractiveForce;
	
	public EdgeBundling( DPositionSet2D _pnts, DLineSet _lns, int width, int height) {
		super(width, height);
		
		pnts = _pnts;
		lns  = _lns;
		
		springForce = new SpringAttractiveForces(this, 1.5f);
		springForce.springLength = 1;
		attractiveForce = new PairwiseAttractiveForce(this,10.0f);
		
		//this.addForces( springForce ); 
		//this.addForces( attractiveForce );
		this.addConstraint( new FixVerticesConstraint() );
		
	}
	
	public void setSpringForce( float amnt ) {
		springForce.springConstant = amnt;	
	}
	
	public void setAttractiveForce( float amnt ) {
		attractiveForce.coulombConstant = amnt;
	}
	
	public void setSpringLength( float amnt ) {
		springForce.springLength = amnt;
	}	
	
	public void update( float deltaT ){
		
		/*
		System.out.println(springForce.springConstant);
		System.out.println(attractiveForce.coulombConstant);
		System.out.println(springForce.springLength);
		System.out.println(deltaT);
		System.out.println();
		*/
		
		for(int i = anchors.size(); i < pnts.count(); i++){
			FDLVertex v = createVertex(null, pnts.getX(i), pnts.getY(i));
			v.setMass(10000);
			v.setDiameter(0);
			anchors.add( v );
			layoutVerts.add(v);
		}
		
		while( edges.size() < lns.count() ){
			int [] vID = lns.getLine(edges.size());

			FDLVertex v = anchors.get(vID[0]);
			FDLVertex w = anchors.get(vID[1]);

			FDLVertex [] edgeV  = new FDLVertex[subdivisions];
			edgeV[0] = v; 		
			edgeV[edgeV.length-1] = w;
				
			for(int k = 1; k < edgeV.length-1; k++ ){
				float t = (float)k/(edgeV.length-1);
				float x = v.getPositionX()*(1-t) + w.getPositionX()*t;
				float y = v.getPositionY()*(1-t) + w.getPositionY()*t;
				
				edgeV[k] = createVertex(null,x,y);
				edgeV[k].setMass(10);
				edgeV[k].setDiameter(1);
				layoutVerts.add(edgeV[k]);
				((EBVertex)edgeV[k]).setParents(v,w);
				((EBVertex)edgeV[k]).fixDirection = k;
				addForce( attractiveForce.addRepulsiveForce(edgeV[k]) );
			}
			
			for(int k = 0; k < edgeV.length-1; k++){
				addForce( springForce.addSpring( edgeV[k], edgeV[k+1] ) );
				//addSpring( edgeV[k].getID(), edgeV[k+1].getID(), 0.1f );
			}

			int [] edgeID = new int[subdivisions];
			for( int k = 0; k < edgeID.length; k++ ){
				edgeID[k] = edgeV[k].getID();
			}
			edges.add(edgeID);

		}
		
		super.update(deltaT);
	}
	
	/*
	protected Vertex addVertex( float x, float y, float mass, float diameter ){
		Vertex ret = new EBVertex( layoutVerts.size(), x,y );
		ret.setMass(mass);
		ret.setDiameter(diameter);
		layoutVerts.add( ret );
		return ret;
	}*/
	
	@Override
	protected FDLVertex createVertex(usf.dvl.graph.Graph.Vertex v, float x, float y) {
		FDLVertex ret = new EBVertex( layoutVerts.size(), x,y);
		return ret;
	}	
	
	

	@SuppressWarnings("serial")
	public class PairwiseAttractiveForce extends ArrayList<BasicForce> {
		
		public float coulombConstant = PerNodeForce.COULOMB_CONSTANT_DEFAULT;
		ForceDirectedLayout fdl;
		
		public PairwiseAttractiveForce( ForceDirectedLayout _fdl ){ 
			fdl = _fdl;
			for( FDLVertex v : fdl.getVertices() ) {
				add( new RepulsiveForce( v ) );
			}
		}
		
		public PairwiseAttractiveForce( ForceDirectedLayout _fdl, float _coulombConstant ){
			coulombConstant = _coulombConstant;
			fdl = _fdl;
			for( FDLVertex v : fdl.getVertices() ) {
				add( new RepulsiveForce( v ) );
			}
		}
		
		public RepulsiveForce addRepulsiveForce( FDLVertex _t ) {
			add( new RepulsiveForce(_t) );
			return (RepulsiveForce)get(size()-1);
		}

		class RepulsiveForce extends PerNodeForce {
			RepulsiveForce( FDLVertex _t ){
				super(_t);
			}
			public void updateForce() {
				force[0] = 0.0f;
				force[1] = 0.0f;
				EBVertex target = (EBVertex)this.target;
				
				if( target.parentDistance() < 75 ) return;
				
				float lengthScale0 = Float.min(1.5f, (target.parentDistance()-75)/150);
				
				for(int j = 0; j < layoutVerts.size(); j++){ //Coulomb's law
					EBVertex node = (EBVertex)layoutVerts.get(j);
					if( !node.hasParents() ) continue;
					if( node.sameParents(target) ) continue;
					if( node.parentDistance() < 75 ) continue;
					
					float lengthScale1 = Float.min(1.5f, (node.parentDistance()-75)/150);
					
					
					
					float similarity = node.parentDirectionSimilarity(target);
					float dx = target.getPositionX() - node.getPositionX();
					float dy = target.getPositionY() - node.getPositionY();
					float rSquared = dx * dx + dy * dy + 0.0001f; //to avoid zero deviation
					//float r = (float) Math.pow(rSquared,0.8);
					if( rSquared > 5 ){
						force[0] -= lengthScale0*lengthScale1*similarity * coulombConstant * dx / rSquared;
						force[1] -= lengthScale0*lengthScale1*similarity * coulombConstant * dy / rSquared;
					}
				}
			}
		}		
	}	

	/*
	public class PairwiseAttractiveForce extends Force {
		
		public float coulombConstant = COULOMB_CONSTANT_DEFAULT;
		
		public PairwiseAttractiveForce( ){ }
		public PairwiseAttractiveForce( float _coulombConstant ){
			coulombConstant = _coulombConstant;
		}

		
		public float [] getForce( ForceDirectedLayout fdl, Vertex _target ){
			
			EBVertex target = (EBVertex)_target;
			
			if( target.parentDistance() < 75 ) return new float[]{0,0};
			
			float lengthScale0 = Float.min(1.5f, (target.parentDistance()-75)/150);
			
			
			float forceX = 0.0f;
			float forceY = 0.0f;
			for(int j = 0; j < layoutVerts.size(); j++){ //Coulomb's law
				EBVertex node = (EBVertex)layoutVerts.get(j);
				if( !node.hasParents() ) continue;
				if( node.sameParents(target) ) continue;
				if( node.parentDistance() < 75 ) continue;
				
				float lengthScale1 = Float.min(1.5f, (node.parentDistance()-75)/150);
				
				
				
				float similarity = node.parentDirectionSimilarity(target);
				float dx = target.getPositionX() - node.getPositionX();
				float dy = target.getPositionY() - node.getPositionY();
				float rSquared = dx * dx + dy * dy + 0.0001f; //to avoid zero deviation
				//float r = (float) Math.pow(rSquared,0.8);
				if( rSquared > 5 ){
					forceX -= lengthScale0*lengthScale1*similarity * this.coulombConstant * dx / rSquared;
					forceY -= lengthScale0*lengthScale1*similarity * this.coulombConstant * dy / rSquared;
				}
			}
			
			return new float[]{forceX,forceY};
		}
		
	} 
	*/
	
	
	public class FixVerticesConstraint implements Constraint {
		
		@Override public void constrainPoint(FDLVertex vert) {
			if( vert.getID() < pnts.count() ){
				float vx = pnts.getX(vert.getID());
				float vy = pnts.getY(vert.getID());
				vert.setPosition( vx, vy );
			}

			//if( vert.getID() < baseFDL.countVertex() ){
			//	Vertex other = baseFDL.getVertex(vert.getID());
			//	vert.setPosition( other.getPositionX(), other.getPositionY() );
			//}
		}
		
	}
	
	public class FixDirectionConstraint implements Constraint {
		
		@Override public void constrainPoint(FDLVertex _vert) {
			EBVertex vert = (EBVertex)_vert;
			
			if( vert.fixDirection == 1 || vert.fixDirection == (subdivisions-2) ){

				float t = (float)vert.fixDirection/(subdivisions-1);
				float x = vert.p0.getPositionX()*(1-t) + vert.p1.getPositionX()*t;
				float y = vert.p0.getPositionY()*(1-t) + vert.p1.getPositionY()*t;
				
				vert.setPosition(x, y);
				
			}			
		}
		
	}

	
	
	public int getEdgeCount(){ return edges.size(); }
	public int[] getEdge(int i) {
		return edges.get(i);
	}

}
