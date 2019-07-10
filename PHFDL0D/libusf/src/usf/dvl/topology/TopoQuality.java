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

import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PMatrix2D;
import processing.data.JSONArray;
import processing.data.JSONObject;
import usf.dvl.common.SystemX;
import usf.dvl.topology.ph.PersistenceDiagramD0;

public class TopoQuality extends PApplet {

	int w=625, h=400;

	/*
	PersistenceDiagramD0 record_pd = null;
	PMatrix2D record_tform = null;
	PMatrix2D record_itform = null;
	*/
	Dataset d0, d1;
	String f0, f1;
	boolean interactive = true;

	public void settings() {
		size(w, h);
	}

	public void setup() {
		d0 = new Dataset(this,f0);
		d1 = new Dataset(this,f1);
	}
	
	public TopoQuality( String _f0, String _f1, boolean _interactive ){
		f0 = _f0;
		f1 = _f1;
		interactive = _interactive;
		PApplet.runSketch(new String[]{this.getClass().getName()}, this);
	}


	public void draw() {
		background(255);

		int [] types = new int[d1.bars.size()];
		Arrays.fill( types, 0 );
		for( int i = 0; i < d1.bars.size(); i++){
			if( d1.getBarType(i).equals( "attr") ) types[i] = -1;
			if( d1.getBarType(i).equals( "repl") ) types[i] =  1;
		}
		int [] typesSorted = Arrays.copyOf( types, types.length );
		Arrays.sort( typesSorted );

		float totImrv = 0;
		totImrv += getFeatures( d0.pd, types, d1.pd, typesSorted,  -1 );
		totImrv += getFeatures( d0.pd, types, d1.pd, typesSorted,   0 );
		totImrv += getFeatures( d0.pd, types, d1.pd, typesSorted,   1 );
		float avgImprv = totImrv / types.length;
		
		println("Average Improvement: " + avgImprv + "%");
		
		d0.drawPD( 0, types );
		d1.drawPD( 200, typesSorted );

		/*
		float avgA = avgPD( d1.pd, typesSorted, -1 );
		float avgU = avgPD( d1.pd, typesSorted,  0 );
		float avgR = avgPD( d1.pd, typesSorted,  1 );
		float RavgA = avgPD( d0.pd, types, -1 );
		float RavgU = avgPD( d0.pd, types,  0 );
		float RavgR = avgPD( d0.pd, types,  1 );
		println( RavgA + "-" + avgA + "->" + (RavgA - avgA)*100/RavgA + "%, " + avgR + "-" + RavgR + "->" + (avgR - RavgR)*100/RavgR + "%, " + RavgU + "-" + avgU + "->" + Math.abs(RavgU - avgU)*100/RavgU + "%" );
		println( (RavgA - avgA) + (avgR - RavgR) - Math.abs(RavgU - avgU) );
		*/
		if( !interactive ) exit();
	}

	private float getFeatures(PersistenceDiagramD0 pd0, int[] types0, PersistenceDiagramD0 pd1, int[] types1, int type ) {
		float totImprv = 0;
		int i0 = 0, i1 = 0;
		int cnt = 0;
		while( i0 < types0.length && i1 < types1.length ) {
			if( types0[i0] != type ) { i0++; continue; }
			if( types1[i1] != type ) { i1++; continue; }
			float imprv = 0;
			if( type == -1 ) imprv = (pd0.getDeath(i0) - pd1.getDeath(i1)) / pd0.getDeath(i0) * 100;
			if( type ==  1 ) imprv = (pd1.getDeath(i1) - pd0.getDeath(i0)) / pd0.getDeath(i0) * 100;
			if( type == 0 ) imprv = -Math.abs(pd1.getDeath(i1) - pd0.getDeath(i0)) / pd0.getDeath(i0) * 100;
			//System.out.println( "   " + i0 + " " + i1 + " " + pd0.getDeath(i0) + " " + pd1.getDeath(i1) + " ==> " + imprv + "%" );
			totImprv += imprv;
			i0++; i1++;
			cnt++;
		}
		println( type + " " + totImprv/cnt );
		return totImprv;
	}

	float avgPD( PersistenceDiagramD0 pd, int [] types, int type ){
		float t = sumPD( types, type );
		if( t == 0 ) return 0;
		return totalPD( pd, types, type ) / t;
	}


	float totalPD( PersistenceDiagramD0 pd, int [] types, int type ){
		float sum = 0;
		for( int i = 0; i < pd.size()-1; i++){
			if(type == types[i]){
				sum += pd.getDeath(i);
			}
		}    
		return sum;
	}

	float sumPD( int [] types, int type ){
		float sum = 0;
		for( int i = 0; i < types.length; i++){
			sum += (type == types[i]) ? 1 : 0;
		}    
		return sum;
	}



	
	class Dataset {
		JSONArray pos, bars, tform;
		eGraph egraph = new eGraph();
		PersistenceDiagramD0 pd;
		
		Dataset( PApplet p, String file ){
			JSONObject json = p.loadJSONObject(file);
			pos = json.getJSONArray("positions");
			bars = json.getJSONArray("bars");
			tform = json.getJSONArray("tform");
			
			for(int i = 0; i < pos.size(); i++ ){
				float x = pos.getJSONObject(i).getFloat("x");
				float y = pos.getJSONObject(i).getFloat("y");
				egraph.add( new EuclideanSpaceNode2D( x,y ) );
				//System.out.println(egraph.egraph.size() );
				if( egraph.egraph.size() > 100000 ) egraph.optimize();
			}
			pd = new PersistenceDiagramD0( egraph );
			pd.sortByPersistence();
		}
		
		String getBarType( int idx ) { return bars.getJSONObject(idx).getString("type"); }

		void drawPD( int xstart, int [] types ){
			fill(100);
			stroke(0);
			float [] curPD = new float[pd.size()-1];
			// skip the last because it is infinite
			for( int i = 0; i < pd.size()-1; i++){
				fill(100);
				if( types[i] == -1 ) fill(100,0,0);
				if( types[i] ==  1 ) fill(0,0,100); 

				float x0 = pd.getBirth(i);
				float x1 = pd.getDeath(i);
				//if( Float.isInfinite( pd.getDeath(i) ) ) x1 = 400;
				float h = (float)height / (pd.size()-1);
				float y = h*i;
				rect( xstart+x0,y, (x1-x0)/1.5f, h );
				curPD[i] = x1-x0;
			}    
		}		
	}

	class EuclideanSpaceNode2D implements MetricSpaceNode {
		float x,y;
		EuclideanSpaceNode2D( float _x, float _y ){
			x = _x;
			y = _y;
		}
		public float distance( MetricSpaceNode d ){
			if( d instanceof EuclideanSpaceNode2D )
				return PApplet.dist( x,y, ((EuclideanSpaceNode2D)d).x,((EuclideanSpaceNode2D)d).y );
			return Float.POSITIVE_INFINITY;
		}
	}

	public static void main( String [] args ) {
		String f0 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/bcsstk/bcsstk_standard.topo";
		String f1 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/bcsstk/bcsstk_partial_contract.topo";
		String f2 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/bcsstk/bcsstk_more_contract.topo";
		String f3 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/bcsstk/bcsstk_most_contract.topo";
		//new TopoQuality( f0, f1, false );
		//new TopoQuality( f0, f2, false );
		//new TopoQuality( f0, f3, false );

		String f4 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/6ary/6ary_standard.topo";
		String f5 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/6ary/6ary_partially_contract.topo";
		String f6 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/6ary/6ary_contract.topo";
		//new TopoQuality( f4, f5, false );
		//new TopoQuality( f4, f6, false );

		String f7 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/barbell/barbell_standard.topo";
		String f8 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/barbell/barbell_full_contract.topo";
		//new TopoQuality( f7, f8, false );

		String f9 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/lobster/lobster_standard.topo";
		String f10 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/synthetic/lobster/lobster_most_contract.topo";
		//new TopoQuality( f9, f10, false );

		String f11 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/real_world/les_miserables/mis_standard.topo";
		String f12 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/real_world/les_miserables/mis_combo.topo";
		//new TopoQuality( f11, f12, false );

		String f13 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/real_world/senate/2007_antivote/us.senate.2007.antivote.default.topo";
		String f14 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/real_world/senate/2007_antivote/us.senate.2007.antivote.topo";
		String f15 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/real_world/senate/2007_covote/us.senate.2007.covote.topo";
		String f16 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/real_world/senate/2008_antivote/us.senate.2008.antivote.topo";
		String f17 = "/Users/prosen/Code/topological-preserving-projection/0DimFeatures/TopoFDL0D/data/layouts/real_world/senate/2008_covote/us.senate.2008.covote.topo";
		//new TopoQuality( f13, f14, false );
		new TopoQuality( f13, f15, false );
		//new TopoQuality( f13, f16, false );
		//new TopoQuality( f13, f17, false );
		
		
	}
}
