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

package usf.dvl.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;


public class Graph {

	protected HashMap<Vertex,Integer> nodeIndex = new HashMap<Vertex,Integer>();

	public ArrayList<Vertex> nodes = new ArrayList<Vertex>();
	public ArrayList<Edge>   edges = new ArrayList<Edge>( );

	public Graph( ){ }

	public int getNodeCount(){ return nodes.size(); }
	public int getEdgeCount(){ return edges.size(); }


	public int getVertexIndex( Vertex v ){
		return nodeIndex.get(v);
	}

	public Vertex addVertex( Vertex v ){
		nodeIndex.put(v,nodes.size());
		nodes.add(v);
		return v;
	}
	
	public Vertex addVertex( ){
		Vertex ret = new Vertex( );
		nodeIndex.put(ret,nodes.size());
		nodes.add(ret);
		return ret;		
	}
	
	public void merge( Graph G ){
		nodes.addAll(G.nodes);
		edges.addAll(G.edges);
		// rebuild index
		for(int i = 0; i < nodes.size(); i++ ){
			nodeIndex.put(nodes.get(i),i);
		}
	}

	public Vertex getVertex(int i) {
		return nodes.get(i);
	}
	public Collection<Vertex> getVertices() {
		return nodes;
	}

	public Edge addEdge( int nid0, int nid1, double w ){
		return addEdge( new Edge( nodes.get(nid0), nodes.get(nid1), w ) );
	}

	public Edge addEdge( Edge e ){
		edges.add(e);
		e.v0.addAdjacent( e.v1, e.w );
		e.v1.addAdjacent( e.v0, e.w );
		return e;
	}
	
	public Edge getEdge( int i ){
		return edges.get(i);
	}

	public Collection<Edge> getEdges() {
		return edges;
	}

	public String toDot(){

		StringBuffer dot_node = new StringBuffer( );
		StringBuffer dot_edge = new StringBuffer( );
		for(int i = 0; i < this.getNodeCount(); i++){
			Vertex curr = this.nodes.get(i);

			dot_node.append( "\t" + curr.toDot(this) + "\n");

			for( int n = 0; n < curr.getAdjacentCount(); n++ ){
				Vertex nei = curr.getAdjacentAt(n);
				if( getVertexIndex(curr) < getVertexIndex(nei) ){
					dot_edge.append( "\t" + getVertexIndex(curr) + " -- " + getVertexIndex(nei) + "\n");
				}
			}					
		}
		return "graph{\n" + dot_node + dot_edge + "}"; 
	}
	

	

	public double [] dijkstra( Graph.Vertex source ){
		double  [] distance = new double[getNodeCount()];   
		boolean [] visited  = new boolean[getNodeCount()];

		Arrays.fill( visited, false );
		Arrays.fill( distance, Double.MAX_VALUE );
		distance[ getVertexIndex(source) ] = 0;

		PriorityQueue<Graph.Vertex> vlist = new PriorityQueue<Graph.Vertex>( 10, new DijkstraDistanceComparator(distance) );
		vlist.add( source );

		while( !vlist.isEmpty() ){
			Graph.Vertex curV = vlist.poll();
			int         curI = getVertexIndex(curV);
			
			if( visited[curI] ) continue;
			visited[curI] = true;

			for(int i = 0; i < curV.getAdjacentCount(); i++){
			//for( Graph.Vertex neiV : curV.getAdjacentVertices() ) {
				Graph.Vertex neiV = curV.getAdjacentAt(i);
				int    neiI = getVertexIndex(neiV);
				double d    = curV.getAdjacentWeight(i);
				//double d    = curV.getAdjacentWeight(neiV);
				double  alt = distance[curI] + d;
				
				if( alt < distance[neiI] ){
					distance[neiI] = alt;
				}
				
				if( !visited[neiI] ){
					vlist.add( neiV );
				}
			}
		}

		return distance;
	}
	
	private class DijkstraDistanceComparator implements Comparator<Graph.Vertex> {
		double [] ds;   
		public DijkstraDistanceComparator( double [] _ds ){
			ds = _ds;
		}
		@Override
		public int compare(Graph.Vertex o1, Graph.Vertex o2) {
			if( ds[getVertexIndex(o1)] < ds[getVertexIndex(o2)] ) return -1;
			if( ds[getVertexIndex(o1)] > ds[getVertexIndex(o2)] ) return  1;
			if( getVertexIndex(o1) < getVertexIndex(o2) ) return -1;
			if( getVertexIndex(o1) > getVertexIndex(o2) ) return  1;
			return 0;
		}
	}
	

	
	static public class Vertex {
		private Vector<Vertex> nodeList   = new Vector<Vertex>();
		private Vector<Float>  nodeWeight = new Vector<Float>();
		protected Vertex( ){ }

		public void   addAdjacent( Vertex v, float w ){ nodeList.add(v); nodeWeight.add(w); }
		public void   addAdjacent( Vertex v, double w ){ nodeList.add(v); nodeWeight.add((float)w); }
		public int    getAdjacentCount( ){ return nodeList.size(); }
		public Vertex getAdjacentAt(int i){ return nodeList.get(i); }
		public float  getAdjacentWeight(int i){ return nodeWeight.get(i); }
		public float  setAdjacentWeight(int i, float w){ return nodeWeight.set(i,w); }
		public Collection<Vertex> getAdjacent(){ return nodeList; }
		public String toDot(Graph G){ return G.getVertexIndex(this) + " [label=\"" + G.getVertexIndex(this) + " (" + G.getVertexIndex(this) + ")\"];"; }
		public String getUID( ){ return ""; }
	}

	static public class Edge{ 
		public Vertex v0,v1;
		public double w=0;
		protected Edge( ){ }
		protected Edge( Vertex _v0, Vertex _v1, double _w ){
			v0 = _v0;
			v1 = _v1;
			w  = _w;
		}
	}





}