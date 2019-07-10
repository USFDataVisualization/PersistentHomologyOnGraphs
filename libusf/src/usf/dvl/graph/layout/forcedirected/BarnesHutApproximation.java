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

public class BarnesHutApproximation {

	public static float theta = 0.7f;
	public static float distance_threshold_percent = 0.2f;
	public static int   leaf_node_threshold = 1;
	
	KDTree tree;
	public float gminX, gmaxX;
	public float gminY, gmaxY;
	public float range;
	public BarnesHutApproximation( ArrayList<FDLVertex> g ){
			
		gminX = Float.MAX_VALUE; gmaxX = -Float.MAX_VALUE;
		gminY = Float.MAX_VALUE; gmaxY = -Float.MAX_VALUE;
		
		for( FDLVertex v : g ) {
			gminX = Math.min( gminX,  v.getPositionX() );
			gmaxX = Math.max( gmaxX,  v.getPositionX() );
			gminY = Math.min( gminY,  v.getPositionY() );
			gmaxY = Math.max( gmaxY,  v.getPositionY() );
		}	
		range = Math.max(gmaxX-gminX, gmaxY-gminY);
		
		tree = new KDTree( g, gminX, gmaxX, gminY, gmaxY, 8 );
		//System.out.println();
	}
	
	public ArrayList<FDLVertex> getNodes( FDLVertex v ){
		ArrayList<FDLVertex> ret = new ArrayList<FDLVertex>();
		tree.buildList( ret, v );
		return ret;		
	}
	
	public ArrayList<KDTree> getBoxes(FDLVertex vertex) {
		ArrayList<KDTree> ret = new ArrayList<KDTree>();
		tree.buildBoxes(ret, vertex);
		return ret;
	}
	
	
	public class KDTree extends FDLVertex  {
		
		ArrayList<FDLVertex> verts;
		KDTree left = null, right = null;
		public float minX, maxX;
		public float minY, maxY;
		public boolean highlight = false;

		public KDTree(ArrayList<FDLVertex> g, float _minX, float _maxX, float _minY, float _maxY, int max_depth ) {
			super(-1, 0, 0);

			minX = _minX; maxX = _maxX;
			minY = _minY; maxY = _maxY;
			verts = g;
			
			build(max_depth);
		}
		
		private void build(int max_depth ) {
			//for( int d = max_depth; d < 8; d++) System.out.print(" ");
			//System.out.println(minX + "-" + maxX + " x " + minY + "-" + maxY + " => " + verts.size());
			//System.out.flush();
			
			if( verts.size() > leaf_node_threshold && max_depth > 0 ) {

				ArrayList<FDLVertex> l = new ArrayList<FDLVertex>();
				ArrayList<FDLVertex> r = new ArrayList<FDLVertex>();

				float splitX = (minX+maxX)/2;
				float splitY = (minY+maxY)/2;

				int direction = ((maxX-minX) > (maxY-minY)) ? 0 : 1;

				for( FDLVertex v : verts ) {
					if( (direction==0 && v.getPositionX() < splitX) ||
							( direction==1 && v.getPositionY() < splitY ) ) {
						l.add(v);
					}
					else {
						r.add(v);
					}
				}

				if( direction == 0 ) {
					if( l.size() > 0 ) left  = new KDTree( l, minX, splitX, minY, maxY, max_depth-1 );
					if( r.size() > 0 ) right = new KDTree( r, splitX, maxX, minY, maxY, max_depth-1 );
				}
				if( direction == 1 ) {
					if( l.size() > 0 ) left  = new KDTree( l, minX, maxX, minY, splitY, max_depth-1 );
					if( r.size() > 0 ) right = new KDTree( r, minX, maxX, splitY, maxY, max_depth-1 );
				}
			}

			superNode = verts.size();

			double sumPX = 0;
			double sumPY = 0;
			for( FDLVertex v : verts ) {
				sumPX += v.getPositionX();
				sumPY += v.getPositionY();
			}

			if( superNode > 0 ) setPosition( (float)(sumPX/superNode), (float)(sumPY/superNode) );

		
		}

		public void buildList(ArrayList<FDLVertex> ret, FDLVertex v) {
			
			float d = getDistance(v)[0];
			float r = getDistanceRatio(d);

			if( left == null && right==null) {
				if( (r > theta) && d < distance_threshold_percent*range ) {
					ret.addAll(verts);
				}
				else {
					ret.add(this);
				}
			}
			else if( r > theta ) {
				if( left  != null ) left.buildList(ret, v);
				if( right != null ) right.buildList(ret, v);
			}
			else {
				ret.add(this);
			}
			
		}
		
		public void buildBoxes(ArrayList<KDTree> ret, FDLVertex v) {
			
			highlight = false;
			float d = getDistance(v)[0];
			float r = getDistanceRatio(d);

			
			if( left == null && right==null ) {
				ret.add(this);
				highlight = (r > theta) && d < distance_threshold_percent*range;
			}
			else if( r > theta ) {
				if( left  != null ) left.buildBoxes(ret, v);
				if( right != null ) right.buildBoxes(ret, v);
			}
			else {
				ret.add(this);
			}
		}		
		
		
		private float [] getDistance( FDLVertex v ) {
			float dX0 = minX - v.getPositionX();
			float dX1 = maxX - v.getPositionX();
			float dY0 = minY - v.getPositionY();
			float dY1 = maxY - v.getPositionY();
			float d00 = dX0*dX0 + dY0*dY0;
			float d01 = dX0*dX0 + dY1*dY1;
			float d10 = dX1*dX1 + dY0*dY0;
			float d11 = dX1*dX1 + dY1*dY1;
			float maxD = (float) Math.sqrt(Math.max( Math.max(d00,d01), Math.max(d10,d11) ));
			float minD = (float) Math.sqrt(Math.min( Math.min(d00,d01), Math.min(d10,d11) ));
			if( minX <= v.getPositionX() && v.getPositionX() <= maxX ) minD = Math.min( minD, Math.min( Math.abs(dY0), Math.abs(dY1) ) );
			if( minY <= v.getPositionY() && v.getPositionY() <= maxY ) minD = Math.min( minD, Math.min( Math.abs(dX0), Math.abs(dX1) ) );
			if( minX <= v.getPositionX() && v.getPositionX() <= maxX && minY <= v.getPositionY() && v.getPositionY() <= maxY ) minD = 0;
			
			return new float [] {minD,maxD};
		}
		
		private float getDistanceRatio( float d ) {
			float rX = (maxX-minX)/d;
			float rY = (maxY-minY)/d;
			return Math.max( rX, rY );
		}
		
	}

}
