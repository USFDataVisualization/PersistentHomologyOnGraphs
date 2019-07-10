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

package usf.dvl.graph.layout.forcedirected;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import usf.dvl.graph.Graph;
import usf.dvl.graph.Graph.Edge;
import usf.dvl.graph.layout.GraphLayout;
import usf.dvl.graph.layout.forcedirected.BarnesHutApproximation.KDTree;

public class ForceDirectedLayout extends GraphLayout<FDLVertex> {

	public static int TPOOL_SIZE = 4;
	public static int TPOOL_THREADS = 8;
	
	//public static final float TOTAL_KINETIC_ENERGY_DEFAULT = Float.MAX_VALUE;
	public static final float DAMPING_COEFFICIENT_DEFAULT   = 0.8f;
	public static final float TIME_STEP_DEFAULT             = 0.4f;
	
	private ExecutorService tpool = Executors.newFixedThreadPool(TPOOL_SIZE);
	
	public float totalKineticEnergy	= Float.MAX_VALUE;//TOTAL_KINETIC_ENERGY_DEFAULT;
	public float dampingCoefficient	= DAMPING_COEFFICIENT_DEFAULT;
	public float timeStep			= TIME_STEP_DEFAULT;


	BarnesHutApproximation bhApprox;
	

	//ArrayList<Force>         forces = new ArrayList<Force>();
	public ArrayList<BasicForce>    bforces = new ArrayList<BasicForce>();
	ArrayList<Constraint>    constraints = new ArrayList<Constraint>();
	//ArrayList<PerNodeForce>  anForces = new ArrayList<PerNodeForce>();
	//ArrayList<PairwiseForce> pwForces = new ArrayList<PairwiseForce>();

	protected ForceDirectedLayout( int width, int height ) {
		super( width, height );
	}
	
	public ForceDirectedLayout( Graph _g, int width, int height ){ 	
		super( _g, width, height );

		for( Edge e : _g.getEdges() ){
			int n0 = _g.getVertexIndex(e.v0);
			int n1 = _g.getVertexIndex(e.v1);
			getVertex( n0 ).addAdjacent( getVertex( n1 ), 10 );
			getVertex( n1 ).addAdjacent( getVertex( n0 ), 10 );			
		}
	}
	
		
	public void anneal( float amount ){
		Random random = new Random();
		for( FDLVertex curr : layoutVerts ){
			float nx = curr.getPositionX() + (random.nextFloat() * 2.0f - 1.0f) * amount;
			float ny = curr.getPositionY() + (random.nextFloat() * 2.0f - 1.0f) * amount;
			curr.setPosition(nx, ny);
		}
	}
	
	public void setMass( int idx, float newMass ){
		layoutVerts.get(idx).mass = newMass;
	}
	
	public void setMass( float newMass ){
		for( FDLVertex curr : layoutVerts ){
			curr.mass = newMass;
		}
	}
	
	public void setRestingLengths( float newLen ){
		for( FDLVertex curr : layoutVerts ){
			for(int i = 0; i < curr.springLen.size(); i++){
				curr.springLen.set(i,newLen);
			}
		}
	}


	@Override
	protected FDLVertex createVertex(usf.dvl.graph.Graph.Vertex v, float x, float y) {
		FDLVertex ret = new FDLVertex( layoutVerts.size(), x,y);
		ret.setMass(100);
		ret.setDiameter(5);
		return ret;
	}


	public void addForces( Collection<BasicForce> forces ) {	bforces.addAll(forces); }
	public void addForce( BasicForce force ) { bforces.add(force); }
	public void removeForces( Collection<BasicForce> forces ) {	bforces.removeAll(forces); }
	public void removeForce( BasicForce force ) { bforces.remove(force); }
	
	
	public void addConstraint( Constraint c ){ constraints.add(c); }
	public void removeConstraint( Constraint c ){ constraints.remove(c); }

	public float getTotalKineticEnergy( ){ return this.totalKineticEnergy; }
	
	
	public void update( ){ update(this.timeStep); }

	public ArrayList<KDTree> getBHBoxes( FDLVertex v ){
		return bhApprox.getBoxes( v );
	}
	
	private class ForceThread implements Runnable {
		int off,skip;
		ForceThread(int _off, int _skip){
			off = _off;
			skip = _skip;
		}
		@Override
		public void run() {
			for( int i = off; i < bforces.size(); i+=skip ) {
				bforces.get(i).updateForce();
				//bforces.get(i).applyForce();
			}
		}
	}
	
	private class PositionThread implements Runnable {
		int off,skip;
		float partialKineticEnergy = 0;
		float deltaT;
		PositionThread(int _off, int _skip, float _deltaT ){
			off = _off;
			skip = _skip;
			deltaT = _deltaT;
		}
		@Override
		public void run() {
			for( int i = off; i < layoutVerts.size(); i+=skip ) {
					FDLVertex target = layoutVerts.get(i);
					
					float accelerationX = target.getForceX() / target.getMass();
					float accelerationY = target.getForceY() / target.getMass();
					
					target.setAcceleration(accelerationX, accelerationY);

					float velocityX = (target.getVelocityX() + deltaT * accelerationX) * dampingCoefficient;
					float velocityY = (target.getVelocityY() + deltaT * accelerationY) * dampingCoefficient;

					target.setVelocity(velocityX, velocityY);			
					
					partialKineticEnergy += (target.getMass() * Math.pow((target.getVelocityX() + target.getVelocityY()), 2.0f));

					float x = (float) (target.getPositionX() + deltaT * target.getVelocityX() + target.getAccelerationX() * Math.pow(deltaT, 2.0f) / 2.0f);
					float y = (float) (target.getPositionY() + deltaT * target.getVelocityY() + target.getAccelerationY() * Math.pow(deltaT, 2.0f) / 2.0f);

					target.setPosition( x, y );

					for( Constraint c : constraints ){
						c.constrainPoint( target );
					}
			}
		}
	}	

	public void update( float deltaT ){ 
		totalKineticEnergy = 0.0f;
		
		bhApprox = new BarnesHutApproximation(layoutVerts);
				
		Future<?> [] wait = new Future<?>[TPOOL_THREADS];
		for(int i = 0; i < wait.length; i++){
			wait[i] = tpool.submit( new ForceThread(i,TPOOL_THREADS) );
		}
		
		for(int i = 0; i < layoutVerts.size(); i++){
			layoutVerts.get(i).clearForce();
		}
		
		for(int i = 0; i < wait.length; i++){
			while( !wait[i].isDone() ){ 
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
		}
		
		
		for(int i = 0; i < bforces.size(); i++){
			bforces.get(i).applyForce();
		}
		
		for(int i = 0; i < wait.length; i++){
			wait[i] = tpool.submit( new PositionThread(i,TPOOL_THREADS,deltaT) );
		}
		
		for(int i = 0; i < wait.length; i++){
			while( !wait[i].isDone() ){ 
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
		}
		
		
		
		/*
		for(int i = 0; i < layoutVerts.size(); i++){
			FDLVertex target = layoutVerts.get(i);
			
			float accelerationX = target.getForceX() / target.getMass();
			float accelerationY = target.getForceY() / target.getMass();
			
			target.setAcceleration(accelerationX, accelerationY);

			float velocityX = (target.getVelocityX() + deltaT * accelerationX) * dampingCoefficient;
			float velocityY = (target.getVelocityY() + deltaT * accelerationY) * dampingCoefficient;

			target.setVelocity(velocityX, velocityY);			
			
			totalKineticEnergy += (target.getMass() * Math.pow((target.getVelocityX() + target.getVelocityY()), 2.0f));

			float x = (float) (target.getPositionX() + deltaT * target.getVelocityX() + target.getAccelerationX() * Math.pow(deltaT, 2.0f) / 2.0f);
			float y = (float) (target.getPositionY() + deltaT * target.getVelocityY() + target.getAccelerationY() * Math.pow(deltaT, 2.0f) / 2.0f);

			target.setPosition( x, y );

			for( Constraint c : constraints ){
				c.constrainPoint( target );
			}
		}*/

	}





	
}
