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

import processing.core.PApplet;

public class DPoints extends DBasic {

	protected DPositionSet2D pos = null;
	protected ColorScheme colScheme = null;
	int selected = -1;
	int spheredetail = 5;
	int borderSize = 1;
	
	boolean modeSpheres = true;
	boolean shadowEnable = true;
	
	public DPoints(PApplet p){
		super(p);
	}

	public void setData( DPositionSet2D _pos ){
		pos = _pos;
	}

	public void setColorScheme( ColorScheme c ){
		colScheme = c;
	}
	
	public ColorScheme getColorScheme( ){  
		return colScheme;
	}
	
	public void setPrimitiveDetail( int detail ) {
		spheredetail = detail;
	}
	
	public void setBorderSize( int size ) {
		borderSize = size;
	}
	
	public void setPrimitiveModeSpheres( ) {
		modeSpheres = true;
	}
	
	public void setPrimitiveModeCircles( ) {
		modeSpheres = false;
	}
	
	public void disableShadows() { shadowEnable = false; }
	public void enableShadows() { shadowEnable = true; }
	
	
	private void drawPointsAsSpheres() {
		if( pos == null ) return;
		
		if( spheredetail > 0 )
			papplet.sphereDetail(spheredetail);

		papplet.noStroke();
		papplet.lights();
		//papplet.lightSpecular(75, 75, 75);
		papplet.lightSpecular(25, 25, 25);
		papplet.directionalLight(150, 150, 150, 1, 0, -1);
		for(int i = 0; i < pos.count(); i++){
			float x = PApplet.constrain(u0+(float)pos.getX( i ), u0, u0+w);
			float y = PApplet.constrain(v0+(float)pos.getY( i ), v0, v0+h);
			float s = (float)pos.getSize( i );
			papplet.pushMatrix();
			papplet.translate( x,y, 0 );
			if( colScheme != null ){
				papplet.fill( colScheme.getFill(i) );
			}
			else{
				papplet.fill( 100, 100, 200 );
			}
			papplet.sphere( s );
			papplet.popMatrix();
		}

		papplet.noLights();
		for(int i = 0; i < pos.count(); i++){
			float x = PApplet.constrain(u0+(float)pos.getX( i ), u0, u0+w);
			float y = PApplet.constrain(v0+(float)pos.getY( i ), v0, v0+h);
			float s = (float)pos.getSize( i );
			papplet.pushMatrix();
			papplet.translate( x,y, -s-borderSize );
			if( colScheme != null ){
				papplet.fill( colScheme.getStroke(i) );
			}
			else{
				papplet.fill(0, 0, 0);
			}
			papplet.sphere( s+borderSize );
			papplet.popMatrix();
		}		
		
		if( shadowEnable ) {
			if( colScheme != null ){
				papplet.fill(colScheme.getShadow());
			}
			else{
				papplet.fill(200);
			}
			for(int i = 0; i < pos.count(); i++){
				float x = PApplet.constrain(u0+(float)pos.getX( i ), u0, u0+w);
				float y = PApplet.constrain(v0+(float)pos.getY( i ), v0, v0+h);
				float s = (float)pos.getSize( i );
				papplet.pushMatrix();
				papplet.translate( x+3,y+3, (-s-borderSize)*2 );
				//papplet.ellipse( 0,0, 2*(s+1), 2*(s+1) );
				papplet.sphere( s+borderSize );
				papplet.popMatrix();
			}
		}
		papplet.noFill();		
				
	}
	
	private void drawPointsAsCircles( ) {
		if( pos == null ) return;

		
		//if( spheredetail > 0 )
		//	papplet.sphereDetail(spheredetail);

		papplet.noStroke();
		//papplet.strokeWeight(borderSize);
		//papplet.stroke( 0 );
		papplet.fill( 100, 100, 200 );
		for(int i = 0; i < pos.count(); i++){
			float x = PApplet.constrain(u0+(float)pos.getX( i ), u0, u0+w);
			float y = PApplet.constrain(v0+(float)pos.getY( i ), v0, v0+h);
			float s = (float)pos.getSize( i );
			if( colScheme != null ){
				//papplet.stroke( colScheme.getStroke(i) );
				papplet.fill( colScheme.getFill(i) );
			}
			papplet.ellipse( x,y, 2*s, 2*s );
		}
		
		papplet.noStroke();
		papplet.fill(0);
		papplet.pushMatrix();
		papplet.translate( 0,0, -5 );
		for(int i = 0; i < pos.count(); i++){
			float x = PApplet.constrain(u0+(float)pos.getX( i ), u0, u0+w);
			float y = PApplet.constrain(v0+(float)pos.getY( i ), v0, v0+h);
			float s = (float)pos.getSize( i );
			if( colScheme != null ){
				papplet.fill( colScheme.getStroke(i) );
			}
			papplet.ellipse( x,y, 2*(s+borderSize), 2*(s+borderSize) );
		}
				
		
		if( shadowEnable ) {
			papplet.noStroke();
			if( colScheme != null ){
				papplet.fill(colScheme.getShadow());
			}
			else{
				papplet.fill(200);
			}
			//papplet.pushMatrix();
			papplet.translate( 0,0, -5 );
			for(int i = 0; i < pos.count(); i++){
				float x = PApplet.constrain(u0+(float)pos.getX( i ), u0, u0+w);
				float y = PApplet.constrain(v0+(float)pos.getY( i ), v0, v0+h);
				float s = (float)pos.getSize( i );
				papplet.ellipse( x+3,y+3, 2*(s), 2*(s) );
			}
		}
		papplet.popMatrix();

	}
	
	protected void drawPoints() {
		if( modeSpheres ) 
			this.drawPointsAsSpheres();
		else {
			this.drawPointsAsCircles();
		}
		
	}
	
	public void draw() {
		drawPoints();
	}

	private float roi = -1;
	public void enableSelection(float radiusOfInfluence){
		roi = radiusOfInfluence;
	}
	public void disableSelection(){
		roi = -1;
	}

	public int getSelected(){ return selected; }

	public boolean mousePressed(){
		if( roi < 0 ) return false;
		if( pos == null ) return false;
		selected = -1;
		float closestDistSq = roi*roi;
		for(int i = 0; i < pos.count(); i++){
			float x = PApplet.constrain(u0+(float)pos.getX( i ), u0, u0+w);
			float y = PApplet.constrain(v0+(float)pos.getY( i ), v0, v0+h);

			float dx = x-papplet.mouseX;
			float dy = y-papplet.mouseY;
			float d = dx*dx + dy*dy;
			if( d < closestDistSq ){
				closestDistSq = d;
				selected = i;
			}
		}
		return selected >= 0;
	}

	public boolean mouseReleased(){ 
		if( selected >= 0 ){
			selected = -1;
			return true;
		}
		return false;
	}

}
