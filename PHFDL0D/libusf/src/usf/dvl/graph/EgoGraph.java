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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import usf.dvl.common.Pair;
import usf.dvl.common.SetX;

public class EgoGraph extends HashSet< Graph.Vertex > {

	private static final long serialVersionUID = -7832664838247796672L;

	public EgoGraph( Graph.Vertex v, int hops ){
		Queue<EgoPair> proc = new LinkedList<EgoPair>();
		proc.add( new EgoPair(v,0) );
		
		while( !proc.isEmpty() ) {
			EgoPair curr = proc.poll();
			Graph.Vertex curV = curr.getFirst();
			int   		 curH = curr.getSecond();
			
			this.add(curV);
			if( curH < hops ) {
				for( Graph.Vertex n : curV.getAdjacent() ) {
					proc.add( new EgoPair(n,curH+1) );
				}
			}
		}
	}
	
	private class EgoPair extends Pair<Graph.Vertex,Integer> {
		public EgoPair(Graph.Vertex first, Integer second) {
			super(first, second);
		}
	};

	public float jaccardIndex( EgoGraph s1 ) {
		return SetX.jaccardIndex(this, s1);
	}
	
	public static float jaccardIndex( EgoGraph s0, EgoGraph s1 ) {
		return SetX.jaccardIndex(s0, s1);
	}
		
}
