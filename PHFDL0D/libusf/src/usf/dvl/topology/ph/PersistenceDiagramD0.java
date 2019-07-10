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

import java.util.Comparator;

import usf.dvl.common.DisjointSet1D;
import usf.dvl.topology.eGraph;
import usf.dvl.topology.eGraph.Distance;

public class PersistenceDiagramD0 extends PersistenceDiagram<PersistenceFeature0D> {

	public PersistenceDiagramD0( eGraph egraph ){

		egraph.sort();
		egraph.optimize();

		for( int i = 0; i < egraph.points.size(); i++ ){
			features.add( new PersistenceFeature0D( ) );
		}

		DisjointSet1D djs = new DisjointSet1D(egraph.points.size());
		for( int i = 0; i < egraph.egraph.size(); i++){
			Distance d = egraph.egraph.get(i);
			int si = djs.find( d.i );
			int sj = djs.find( d.j );

			if( si == sj ) continue;

			int sk = djs.union( si, sj );

			if( si == sk ){
				features.get(sj).death = d.d;
				features.get(sj).setCauseOfDeath( d.i, d.j );
				features.get(sj).setEGraph( egraph );
			}
			else{
				features.get(si).death = d.d;
				features.get(si).setCauseOfDeath( d.i, d.j );
				features.get(si).setEGraph( egraph );
			}
		}
	}

	public void calculateMSTSets() {
		/*
		for( int i = 0; i < features.size(); i++ ) {
			features.get(i).getMSTSizeA();
		}*/
		
		Thread [] thrds = new Thread[4];
		for( int cur = 0; cur < thrds.length; cur++ ) {
			final int thrdID = cur;
			thrds[cur] = new Thread() {
				@Override public void run() {
					//System.out.println("started:" + thrdID );
					for( int i = thrdID; i < features.size(); i+= thrds.length ) {
						features.get(i).getMSTSizeA();
					}
					//System.out.println("finished:" + thrdID );
				}
			};
			thrds[cur].start();
		}

		try {
			for( Thread t : thrds ) {
				t.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	// new sorting method for ratios -- added	
	public void sortByPersistenceAndSplitRatio( ){
		
		features.sort( new Comparator<PersistenceFeature0D>(){
			@Override public int compare(PersistenceFeature0D o1, PersistenceFeature0D o2) {
				if( o1.getPersistence() < o2.getPersistence() ) return 1;
				else if( o1.getPersistence() > o2.getPersistence() ) return -1;
				
				int [] a = o1.getSetSize();
				int [] b = o2.getSetSize();

				int differenceA =  Math.abs(a[0] - a[1]); 					
				int differenceB = Math.abs(b[0] - b[1]);

				if( differenceA < differenceB ) return 1;
				else if( differenceA > differenceB ) return -1;
				return 0;
			}
		});

	}

}
