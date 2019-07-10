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
import java.util.HashMap;

public abstract class PairwiseForce extends BasicForce {


	public  static final float SPRING_CONSTANT_DEFAULT	= 0.1f;
	public  static final float SPRING_LENGTH_DEFAULT		= 10.0f;

	FDLVertex node0, node1;

	protected PairwiseForce( FDLVertex _node0, FDLVertex _node1 ){ 
		node0 = _node0;
		node1 = _node1;
	}

	public void applyForce() {
		node0.addForce( force[0], force[1] );
		node1.addForce(-force[0],-force[1] );
	}


	@SuppressWarnings("serial")
	public static class SpringAttractiveForces extends ArrayList<BasicForce> {
		HashMap<Pair,BasicForce> map = new HashMap<Pair,BasicForce>();
		public float springConstant = SPRING_CONSTANT_DEFAULT;
		public float springLength   = SPRING_LENGTH_DEFAULT;

		protected ForceDirectedLayout fdl;

		protected SpringAttractiveForces( ForceDirectedLayout _fdl, boolean buildSprings ){
			this( _fdl, SPRING_CONSTANT_DEFAULT, buildSprings );
		}
		
		public SpringAttractiveForces( ForceDirectedLayout _fdl ){
			this( _fdl, SPRING_CONSTANT_DEFAULT, true );
		}

		public SpringAttractiveForces( ForceDirectedLayout _fdl, float _springConstant ){
			this( _fdl, _springConstant, true);
		}
		
		public SpringAttractiveForces( ForceDirectedLayout _fdl, float _springConstant, boolean buildSprings ){
			fdl = _fdl;
			springConstant = _springConstant;
			if( buildSprings ) {
				for( FDLVertex v : fdl.getVertices() ) {
					for( int i = 0; i < v.getAdjacentCount(); i++ ) {
						FDLVertex v1 = v.getAdjacentAt(i);
						if( v.getID() < v1.getID() ) {
							SpringAttractiveForce f = new SpringAttractiveForce(v,v1);
							add( f );
							map.put( new Pair(v,v1), f );
						}
					}
				}
			}
		}
		
		public SpringAttractiveForce addSpring( FDLVertex _node0, FDLVertex _node1 ){
			//add( new SpringAttractiveForce(_node0, _node1 ) );
			SpringAttractiveForce ret = new SpringAttractiveForce(_node0, _node1 );
			add(ret);
			map.put( new Pair(_node0, _node1), ret );
			return ret;
		}


		public class SpringAttractiveForce extends PairwiseForce {

			public float springLengthOverride = -1;
			public SpringAttractiveForce( FDLVertex _node0, FDLVertex _node1 ){
				super( _node0, _node1 );
			}
			
			@Override
			public void updateForce() {
				float dx = node1.getPositionX() - node0.getPositionX();
				float dy = node1.getPositionY() - node0.getPositionY();

				float l = (float) (Math.sqrt(dx * dx + dy * dy) + 0.0001f); //to avoid zero deviation
				float springLengthX = springLength * dx / l;
				float springLengthY = springLength * dy / l;
				if( springLengthOverride > 0 ) {
					springLengthX = springLengthOverride * dx / l;
					springLengthY = springLengthOverride * dy / l;
				}
				force[0] = springConstant * (dx - springLengthX);
				force[1] = springConstant * (dy - springLengthY);
			}

		}
		
		public SpringAttractiveForce getSpring( FDLVertex _node0, FDLVertex _node1 ) {
			Pair p = new Pair(_node0,_node1);
			if( !map.containsKey(p) ) return null;
			return (SpringAttractiveForce)map.get( p );
		}
	}
	

	private static class Pair {
		FDLVertex n0, n1;
		Pair( FDLVertex _node0, FDLVertex _node1 ){
			if( _node0.getID() < _node1.getID() ) {
				n0 = _node0;
				n1 = _node1;
			}
			else {
				n1 = _node0;
				n0 = _node1;
			}
		}
		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((n0 == null) ? 0 : n0.hashCode());
			result = prime * result + ((n1 == null) ? 0 : n1.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if( !( obj instanceof Pair)  ) return false;
			Pair p = (Pair)obj;
			return (p.n0==this.n0) && (p.n1==this.n1);
		}
		
	}

}
