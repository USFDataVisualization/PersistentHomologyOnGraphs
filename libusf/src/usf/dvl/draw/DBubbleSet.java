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

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import processing.core.PApplet;
import usf.dvl.bubbleset.BubbleSet;

public class DBubbleSet extends DBasic {

	DSetPosition2D points;
	BubbleSet bs2 = new BubbleSet();
    HashMap<Integer,Integer>    setColor = new HashMap<Integer,Integer>();
    HashMap<Integer,Integer>    setStroke = new HashMap<Integer,Integer>();
    HashMap<Integer,Float>      setStrokeWeight = new HashMap<Integer,Float>();
    HashMap<Integer,double[][]> setPotential = new HashMap<Integer,double[][]>();
    DMarchingSquares ms;
    float temporalBlendFactor = 0.8f;
    
    public static int borderbuffer = 100;

	public DBubbleSet(PApplet p) {
		super(p);
		ms = new DMarchingSquares(p);
	    bs2.setSkip(1);
	    bs2.setPixelGroup(5);	
	}
	
	@Override
	public void setPosition( int u0, int v0, int w, int h ){
		super.setPosition(u0, v0, w, h);
		ms.setPosition(u0-borderbuffer, v0-borderbuffer, w+borderbuffer*2, h+borderbuffer*2);
	}
	
	public void setData( DSetPosition2D _points ){
		points = _points;
	}
	
	public void setSetFill( int setID, int color ){
		setColor.put( setID, color );
	}
	public void setSetStroke( int setID, int color ){
		setStroke.put( setID, color );
	}
	public void setSetStrokeWeight( int setID, float w ){
		setStrokeWeight.put( setID, w );
	}

	
	@Override
	public void draw() {
		if( points == null ) return;
		
		
		if( points.count() > 100 ) {
			
	        papplet.noStroke();
	        papplet.pushMatrix();
	        papplet.translate(0,0,-5);
	        for( int i = 0; i < points.count(); i++ ){
	        		int s = points.getSetID(i);
				if( !setColor.containsKey(s) ) continue;
				
        			float x = u0 + points.getX(i);
        			float y = v0 + points.getY(i);
        			
        			int fcol = papplet.lerpColor(setColor.get(s), papplet.color(255), (float)(255-papplet.alpha(setColor.get(s)))/255.0f );
				papplet.fill( papplet.red(fcol), papplet.green(fcol), papplet.blue(fcol), 255 );
				papplet.ellipse( x,y, 30, 30 );
	        }
	        papplet.flush();
	        papplet.translate(0,0,-1.5f);
	        
			
	        for( int i = 0; i < points.count(); i++ ){
	        		int s = points.getSetID(i);
				if( !setColor.containsKey(s) ) continue;
				if( !setStroke.containsKey(s) ) continue;
				
	        		float x = u0 + points.getX(i);
		        float y = v0 + points.getY(i);
		        
    				int fcol = papplet.lerpColor(setColor.get(s), papplet.color(255), (float)(255-papplet.alpha(setColor.get(s)))/255.0f );
    				papplet.fill( papplet.red(fcol), papplet.green(fcol), papplet.blue(fcol), 255 );
				papplet.stroke( setStroke.get(s) );
				if( setStrokeWeight.containsKey(s) ) 
					papplet.strokeWeight( Math.max(1,setStrokeWeight.get(s)) );
				else 
					papplet.strokeWeight( 1 );

	           papplet.ellipse( x,y, 33, 33 );
	        }
	        papplet.popMatrix();        

			return;
		}
		else {
			Vector<Rectangle> rects = new Vector<Rectangle>();
			HashSet<Integer> sets = new HashSet<Integer>();
			for(int i = 0; i < points.count(); i++){
				sets.add(points.getSetID(i));
				rects.add( new Rectangle((int)points.getX(i)-3,(int)points.getY(i)-3,6,6) );
			}
			
			for( int s : sets ){
				Vector<Rectangle> member = new Vector<Rectangle>();
				Vector<Rectangle> nonMember = new Vector<Rectangle>();
				for(int i = 0; i < points.count(); i++){
					if( points.getSetID(i) == s )
						member.add( rects.get(i) );
					else
						nonMember.add( rects.get(i) );
				}			
				
				
				bs2 = new BubbleSet();		
			    bs2.setSkip(1);
			    bs2.setPixelGroup(5);
			    
				Rectangle activeRegion = new Rectangle(-borderbuffer, -borderbuffer, w+borderbuffer*2, h+borderbuffer*2);
					
				double [][] newPotential = bs2.createPotentialArea(member.toArray(new Rectangle[member.size()]), 
												nonMember.toArray(new Rectangle[nonMember.size()]), activeRegion);
				
				if( !setPotential.containsKey(s) ){
					setPotential.put(s, newPotential);
				}
				else{
					double[][] oldPotential = setPotential.get(s);
					
					if( oldPotential.length != newPotential.length || oldPotential[0].length != newPotential[0].length ){
						setPotential.put(s, newPotential);
					}
					else{
						for(int i = 0; i < oldPotential.length; i++){
							for(int j = 0; j < oldPotential[i].length; j++){
								oldPotential[i][j] = oldPotential[i][j]*temporalBlendFactor + newPotential[i][j]/bs2.getThreshold()*(1-temporalBlendFactor); 
							}
						}
					}
				}
				
				ms.setData( setPotential.get(s), 1 );
				
				
	
				papplet.fill(0,0,0,100);
				papplet.stroke(0);
				papplet.strokeWeight(1);
				
				if(setColor.containsKey(s) ){
					papplet.fill( setColor.get(s) );
				}
				if(setStroke.containsKey(s) ){
					papplet.stroke( setStroke.get(s) );
				}
				if(setStrokeWeight.containsKey(s) ){
					papplet.strokeWeight( setStrokeWeight.get(s) );
				}
				ms.draw();
	
				
			}
		}
	}
	

	public interface DSetPosition2D extends DPositionSet2D {
		int getSetID(int i);
	}


}
