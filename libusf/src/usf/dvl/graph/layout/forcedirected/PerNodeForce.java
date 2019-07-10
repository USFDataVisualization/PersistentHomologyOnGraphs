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

public abstract class PerNodeForce extends BasicForce {

	public static final float COULOMB_CONSTANT_DEFAULT						= 100.0f;
	public static final float SINGULAR_LINEAR_ATTRACTIVE_CONSTANT_DEFAULT		= 0.0f;


	protected FDLVertex target;

	protected PerNodeForce( FDLVertex _target ){ 
		target = _target;
	}

	public void applyForce() {
		target.addForce( force[0], force[1] );
	}
	
	
	
	
	
	@SuppressWarnings("serial")
	public static class PairwiseRepulsiveForces extends ArrayList<BasicForce> {
		
		public float coulombConstant = COULOMB_CONSTANT_DEFAULT;
		ForceDirectedLayout fdl;
		
		public PairwiseRepulsiveForces( ForceDirectedLayout _fdl ){ 
			fdl = _fdl;
			for( FDLVertex v : fdl.getVertices() ) {
				add( new RepulsiveForce( v ) );
			}
		}
		
		public PairwiseRepulsiveForces( ForceDirectedLayout _fdl, float _coulombConstant ){
			coulombConstant = _coulombConstant;
			fdl = _fdl;
			for( FDLVertex v : fdl.getVertices() ) {
				add( new RepulsiveForce( v ) );
			}
		}
		

		class RepulsiveForce extends PerNodeForce {
			RepulsiveForce( FDLVertex _t ){
				super(_t);
			}
			public void updateForce() {
				ArrayList<FDLVertex> proxy = fdl.bhApprox.getNodes(target);
				force[0] = 0.0f;
				force[1] = 0.0f;
				for(int j = 0; j < proxy.size(); j++){ //Coulomb's law
					FDLVertex node = proxy.get(j);
					if(node != target){
						float dx = target.getPositionX() - node.getPositionX();
						float dy = target.getPositionY() - node.getPositionY();
						float rSquared = dx * dx + dy * dy + 0.0001f; //to avoid zero deviation
						force[0] += coulombConstant * dx / rSquared * node.superNode;
						force[1] += coulombConstant * dy / rSquared * node.superNode;
					}
				}
			}
		}		
	}
	

	
	@SuppressWarnings("serial")
	public static class LinearAttractiveForces extends ArrayList<BasicForce> {
		
		public float centroidX = 0;
		public float centroidY = 0;
		public float pullScaleFactor = SINGULAR_LINEAR_ATTRACTIVE_CONSTANT_DEFAULT;
		ForceDirectedLayout fdl;
		
		public LinearAttractiveForces( ForceDirectedLayout _fdl, float _centroidX, float _centroidY ){ 
			centroidX = _centroidX;
			centroidY = _centroidY;
			fdl = _fdl;
			for( FDLVertex v : fdl.getVertices() ) {
				add( new LinearAttractiveForce( v ) );
			}
		}
		public LinearAttractiveForces( ForceDirectedLayout _fdl, float _centroidX, float _centroidY, float _pullScaleFactor ){ 
			centroidX = _centroidX;
			centroidY = _centroidY;
			pullScaleFactor = _pullScaleFactor;
			fdl = _fdl;
			for( FDLVertex v : fdl.getVertices() ) {
				add( new LinearAttractiveForce( v ) );
			}
		}		
		
		class LinearAttractiveForce extends PerNodeForce {
			LinearAttractiveForce( FDLVertex _t ){
				super(_t);
			}
			public void updateForce() {
			
				force[0] = 0.0f;
				force[1] = 0.0f;
	
				float dummyDx = target.getPositionX() - centroidX;
				float dummyDy = target.getPositionY() - centroidY;
				force[0] -= pullScaleFactor * dummyDx;
				force[1] -= pullScaleFactor * dummyDy;
			}
		}		
	}
	
		
}
