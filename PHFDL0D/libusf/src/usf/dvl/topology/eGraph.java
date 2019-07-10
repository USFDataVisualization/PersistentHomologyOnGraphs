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

package usf.dvl.topology;

import java.util.Comparator;
import java.util.Vector;

import usf.dvl.common.DisjointSet1D;
import usf.dvl.graph.Graph;

public class eGraph {

	public Vector<MetricSpaceNode> points = new Vector<MetricSpaceNode>( );
	public Vector<Distance>		   egraph = new Vector<Distance>();
	
	private boolean sorted    = true;
	private boolean optimized = true;
	
	Graph g = null;
	
	public eGraph( ){ }
	
	public void add( MetricSpaceNode newpnt ){
		int idx = points.size();
		points.add( newpnt );
		for( int jdx = 0; jdx < idx; jdx++ ){
			float d = newpnt.distance(points.get(jdx));
			 // skip unconnected elements
			if( Float.isFinite(d) ) egraph.add( new Distance( idx, jdx, d ) );
		}
		sorted = false;
		optimized = false;
	}
	
	public eGraph clone( ){
		eGraph ret = new eGraph();
		for( MetricSpaceNode p : points ){
			ret.points.add(p);
		}
		for( Distance e : egraph ){
			ret.egraph.add(e);
		}
		return ret;
	}
	
	public void filterMaxDistance( float maxd ){
		Vector<Distance> newgrp = new Vector<Distance>( );
		for( Distance d : egraph ){
			if( d.d <= maxd )
				newgrp.add(d);
		}
		egraph = newgrp;
	}
	
	
	public float getMaxDistance( ){
		return egraph.lastElement().d; 
	}
	
	public void sort( ){
		if( sorted ) return;
		egraph.sort( new Comparator<Distance>( ){
			public int compare( Distance o1, Distance o2 ){
				if( o1.d < o2.d ) return -1;
				if( o1.d > o2.d ) return  1;
				if( Math.min(o1.i, o1.j) < Math.min( o2.i, o2.j) ) return -1; // these don't really matter
				if( Math.min(o1.i, o1.j) > Math.min( o2.i, o2.j) ) return  1; // except that they make order
				if( Math.max(o1.i, o1.j) < Math.max( o2.i, o2.j) ) return -1; // deterministic for equal
				if( Math.max(o1.i, o1.j) > Math.max( o2.i, o2.j) ) return  1; // distance values
				return 0;
			}
		});
		sorted = true;
	}
	
	public void optimize( ){
		if( optimized ){ return; }

		Vector<Distance> opt = new Vector<Distance>( );
		DisjointSet1D djs = new DisjointSet1D( points.size() );
		
		sort();

		for(int i = 0; i < egraph.size(); i++){
			Distance curr = egraph.get(i);
			
			int seti = djs.find( curr.i );
			int setj = djs.find( curr.j );
			
			if( seti != setj ){
				djs.union( seti,  setj );
				opt.add( curr );
			}
		}
		egraph = opt;
		optimized = true;

		g = new Graph();
		for( int i = 0; i < points.size(); i++ ) {
			g.addVertex( new eGraphVertex(i) );
		}
		for( Distance e : egraph ){
			g.addEdge( e.i, e.j, e.d );
		}
		
	}
	
	public Graph getGraph() {
		return g;
	}
	
	
	public class eGraphVertex extends Graph.Vertex {
		int id;
		protected eGraphVertex( int _id ) { id = _id; }
		public int getID() { return id; }
	}
	
	
	public class Distance {
		public int i, j;
		public float d;
		
		Distance( int _i, int _j, float _d ){
			i = _i;
			j = _j;
			d = _d;
		}
	}
	

}
