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

package usf.dvl.draw;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import processing.core.PApplet;
import processing.core.PConstants;

public class QualitativeColormap {

	  int [] colors;
	  
	  public QualitativeColormap( int ... cols ){
	    colors = cols;
	  }
	  
	  public int getColor( int i ){
	    return colors[i%colors.length];
	  }
	  
	  public int size(){ return colors.length; }
	  
	  
	  
	  public static class Rainbow_Cycle_6 extends QualitativeColormap {
		  public Rainbow_Cycle_6(){
			    super(0xffff0000, 0xffffff00, 0xff00ff00, 
			    		0xff00ffff, 0xff0000ff, 0xffff00ff );
			  }
			}	  
	  
	  
	  public static class Set1_9 extends QualitativeColormap {
		  public Set1_9(){
			    super(0xffe41a1c, 0xff377eb8, 0xff4daf4a, 
			    		0xff984ea3, 0xffff7f00, 0xffffff33, 
			    		0xffa65628, 0xfff781bf, 0xff999999 );
			  }
			}
	  
	  
	public static class Paired_12 extends QualitativeColormap {
	  public Paired_12(){
	    super(0xffa6cee3,0xff1f78b4,0xffb2df8a,0xff33a02c,
	    		0xfffb9a99,0xffe31a1c,0xfffdbf6f,0xffff7f00,
	    		0xffcab2d6,0xff6a3d9a,0xffffff99,0xffb15928);
	  }
	}
	
	
	public static class Paired_13 extends QualitativeColormap{
		public Paired_13() {
			super(0xff6b8e23,0xff50c878,0xffff0000,0xff87ceeb,0xff0000ff,
					0xff670a0a,0xffe6e6fa,0xfff0e130,0xffffef00,0xffcb4154,
					0xffc54b8c,0xffffdab9,0xffffff00);
		}
	}
	
	public static class SenateColor extends QualitativeColormap{
		public SenateColor() {
			super(0xff0e4bef,0xffcc0000,0xff6b3a9d);
		}
	}

	public static class Set3_12 extends QualitativeColormap {
	  public Set3_12(){
	    super(0xff8dd3c7,0xffffffb3,0xffbebada,0xfffb8072,
	    		0xff80b1d3,0xfffdb462,0xffb3de69,0xfffccde5,
	    		0xffd9d9d9,0xffbc80bd,0xffccebc5,0xffffed6f);
	  }
	}

	
	public int getDrawWidth( PApplet papplet, String [] labels ) {

		int maxWidth = 0;
	    for( int i = 0; i < labels.length; i++ ){
	        String k = labels[i];
	        papplet.textSize(16);
	        maxWidth = (int)Math.max( maxWidth, papplet.textWidth(k) );
	    }
	    
	    return maxWidth+40;
	}
	
	public int getDrawWidth( PApplet papplet, final HashMap<Integer,String> labels ) {

		int maxWidth = 0;
		for( String k : labels.values() ){
			papplet.textSize(16);
			maxWidth = (int)Math.max( maxWidth, papplet.textWidth(k) );
		}
	    return maxWidth+40;
	}

	
	
	public void draw( PApplet papplet, int u0, int v0, String [] labels ) {

		int maxWidth = 0;
	    for( int i = 0; i < labels.length; i++ ){
	        String k = labels[i];
	        papplet.textSize(16);
	        maxWidth = (int)Math.max( maxWidth, papplet.textWidth(k) );
	    }
	    
		papplet.strokeWeight(3);
		papplet.stroke(0);
		papplet.fill( 255, 255, 255, 100 );
	     papplet.pushMatrix();
	       papplet.translate( 0,0, -5 );
	       papplet.rect( u0,  v0, maxWidth+40, labels.length*20+10 );
	       papplet.popMatrix();
		
		
        papplet.sphereDetail(5);
   papplet.noStroke();
   papplet.lights();
   papplet.lightSpecular(75, 75, 75);
   papplet.directionalLight(150, 150, 150, 1, 0, -1);

    for( int i = 0; i < labels.length; i++ ){
     float x = u0+15;
     float y = v0+i*20+15;
     float s = 8;
     papplet.pushMatrix();
       papplet.translate( x,y, 0 );
       papplet.fill( getColor(i) );
       papplet.sphere( s );
       papplet.translate( 0, 0, -2 );
       papplet.fill(0, 0, 0);
       papplet.sphere( s+1 );
     papplet.popMatrix();
   }
   
    for( int i = 0; i < labels.length; i++ ){
      String k = labels[i];

      papplet.fill(0);
      papplet.textSize(16);
      papplet.textAlign( PConstants.LEFT, PConstants.CENTER );
      papplet.text( k, u0+30, v0+i*20+15 );
    }
	}
	
	
	public void draw( PApplet papplet, int u0, int v0, final HashMap<Integer,String> labels ) {

		int maxWidth = 0;
		for( String k : labels.values() ){
			papplet.textSize(16);
			maxWidth = (int)Math.max( maxWidth, papplet.textWidth(k) );
		}

		papplet.strokeWeight(3);
		papplet.stroke(0);
		papplet.fill( 255, 255, 255, 100 );
		papplet.pushMatrix();
		papplet.translate( 0,0, -5 );
		papplet.rect( u0,  v0, maxWidth+40, labels.size()*20+10 );
		papplet.popMatrix();


		papplet.sphereDetail(5);
		papplet.noStroke();
		papplet.lights();
		papplet.lightSpecular(75, 75, 75);
		papplet.directionalLight(150, 150, 150, 1, 0, -1);
		
		Vector<Integer> labelID = new Vector<Integer>( labels.keySet() );
		labelID.sort( new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				return labels.get(o1).compareTo( labels.get(o2) );
			}
		});
		
		
		int i = 0;
		for( int curID : labelID ){
			float x = u0+15;
			float y = v0+i*20+15;
			float s = 8;
			papplet.pushMatrix();
			papplet.translate( x,y, 0 );
			papplet.fill( getColor(curID) );
			papplet.sphere( s );
			papplet.translate( 0, 0, -2 );
			papplet.fill(0, 0, 0);
			papplet.sphere( s+1 );
			papplet.popMatrix();
			i++;
		}

		i = 0;
		for( int curID : labelID ){
			String k = labels.get(curID);

			papplet.fill(0);
			papplet.textSize(16);
			papplet.textAlign( PConstants.LEFT, PConstants.CENTER );
			papplet.text( k, u0+30, v0+i*20+15 );
			i++;
		}
	}
}
