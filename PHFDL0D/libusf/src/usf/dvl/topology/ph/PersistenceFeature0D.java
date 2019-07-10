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

package usf.dvl.topology.ph;

import java.util.HashSet;
import java.util.Stack;

import usf.dvl.graph.Graph;
import usf.dvl.topology.eGraph;
import usf.dvl.topology.eGraph.eGraphVertex;

public class PersistenceFeature0D extends PersistenceFeature {
	
	protected int causeI=-1, causeJ=-1;
	HashSet<Integer> A = null;
	HashSet<Integer> B = null;
	eGraph egraph = null;
	
	protected PersistenceFeature0D( double _death ) {
		super(0, 0, _death);
	}

	protected PersistenceFeature0D( ) {
		super(0, 0, Double.POSITIVE_INFINITY );
	}
	
	public void setCauseOfDeath( int i, int j ){
		causeI = i;
		causeJ = j;
	}
	
	public int [] getCauseOfDeath(){
		return new int[]{causeI,causeJ};
	}

	
	public HashSet<Integer> getHashSetA( ){
		if( A == null ) calculateSets();
		return A;
	}
	
	public HashSet<Integer> getHashSetB( ){
		if( B == null ) calculateSets();
		return B;
	}
	
	public int getMSTSizeA( ) {
		if( A == null ) calculateSets();
		if( A == null ) return 0;
		return A.size();
	}
	
	public int getMSTSizeB( ) {
		if( B == null ) calculateSets();
		if( B == null ) return 0;
		return B.size();
	}

	private void calculateSets() {
		if( egraph == null ) return;

		A = new HashSet<Integer>();
		B = new HashSet<Integer>();
		
		Graph g = egraph.getGraph();
		
		Graph.Vertex vI = g.getVertex(causeI);
		Graph.Vertex vJ = g.getVertex(causeJ);
		
		Stack<Graph.Vertex> proc = new Stack<Graph.Vertex>();

		proc.push(vI);
		while( !proc.isEmpty() ) {
			eGraphVertex v = (eGraphVertex)proc.pop();
			if( v == vJ || A.contains(v.getID()) ) continue;
			A.add( v.getID() );
			proc.addAll( v.getAdjacent() );
		}
		
		proc.push(vJ);
		while( !proc.isEmpty() ) {
			eGraphVertex v = (eGraphVertex)proc.pop();
			if( v == vI || B.contains(v.getID()) ) continue;
			B.add( v.getID() );
			proc.addAll( v.getAdjacent() );
		}
		

		/*
		DisjointSet1D djs = new DisjointSet1D(egraph.points.size());
	    for (int i = 0; i < egraph.egraph.size(); i++) {
		      eGraph.Distance d = egraph.egraph.get(i);
		      if ( (d.i == causeI && d.j == causeJ) || (d.i == causeI && d.j == causeJ) ) continue;
		      djs.union(d.i, d.j);
	    }
	    
	    for( int i = 0; i < egraph.points.size(); i++ ) {
	    		if( djs.find(i) == djs.find(causeI) ) A.add(i);
	    		else if( djs.find(i) == djs.find(causeJ) ) B.add(i);
	    		//else { // for multiple connected components, stuff ends up in neither set
	    		//	System.err.println("EdgeMST: something didn't connect!" );
	    		//}
	    }
	    */
	    
	    // make sure the larger set is always B
	    if( A.size() > B.size() ) {
	    		HashSet<Integer> tmp = A;
	    		A = B;
	    		B = tmp;
	    }
	    
	}

	public void setEGraph(eGraph egraph) {
		this.egraph = egraph;
	}
	
	public int [] getSetSize() {
		return new int[] {getMSTSizeA( ),getMSTSizeB( )};
	}



}