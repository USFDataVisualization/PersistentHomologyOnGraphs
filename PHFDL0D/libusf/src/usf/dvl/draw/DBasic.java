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

public abstract class DBasic {
	protected PApplet papplet;
	protected int u0,v0,w,h;

	protected DBasic( PApplet p ){
		this.papplet = p;
	}

	public void setPosition( int _u0, int _v0, int _w, int _h ){
		u0 = _u0;
		v0 = _v0;
		w = _w;
		h = _h;
	}
	public int getU0( ){ return u0; }
	public int getV0( ){ return v0; }
	public int getWidth( ){ return w; }
	public int getHeight( ){ return h; }

	public abstract void draw( );

	public boolean keyPressed( ){ return false; }
	public boolean mousePressed( ){ return false; }
	public boolean mouseReleased( ){ return false; }
	public boolean mouseWheel( float amnt ) { return false; }

	void drawRotatedLabel( String label, float rot, float x, float y ){
		papplet.pushMatrix();
		papplet.translate( x, y );
		papplet.rotate(rot);
		papplet.text( label, 0, 0 );
		papplet.popMatrix();

	}
	
	public boolean pointInside( float x, float y ) {
	    if( x < u0 || x > (u0+w) ) return false;
	    if( y < v0 || y > (v0+h) ) return false;
	    return true;
	}

}
