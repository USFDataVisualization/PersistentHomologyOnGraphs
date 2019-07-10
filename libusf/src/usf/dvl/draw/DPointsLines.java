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

public class DPointsLines extends DPoints {

	DLineSet lines = null;
	ColorScheme cSchemeL = null;


	public DPointsLines(PApplet p){
		super(p);
	}

	public void setData( DPositionSet2D _pos ){
		super.setData(_pos);
		lines = null;
	}

	public void setData( DPositionSet2D _pos, DLineSet _lines ){
		super.setData(_pos);
		lines = _lines;
	}

	public void setLineColorScheme( ColorScheme _cSchemeL ){
		cSchemeL = _cSchemeL;
	}
	
	boolean showLines = true, showPoints = true;
	
	public void disableLines() { showLines = false; }
	public void enableLines() { showLines = true; }
	public void disablePoints() { showPoints = false; }
	public void enablePoints() { showPoints = true; }

	protected void drawLines() {
		if( pos == null || lines == null ) return;

		for(int i = 0; i < lines.count(); i++){
			int [] e = lines.getLine( i );

			float x0 = PApplet.constrain(u0+(float)pos.getX( e[0] ), u0, u0+w);
			float y0 = PApplet.constrain(v0+(float)pos.getY( e[0] ), v0, v0+h);
			float x1 = PApplet.constrain(u0+(float)pos.getX( e[1] ), u0, u0+w);
			float y1 = PApplet.constrain(v0+(float)pos.getY( e[1] ), v0, v0+h);

			papplet.strokeWeight( lines.getWeight(i) );
			papplet.pushMatrix();
			if( cSchemeL != null ){
				papplet.stroke( cSchemeL.getStroke(i) );
			}
			else{
				papplet.stroke(100);
			}
			papplet.line( x0,y0,x1,y1 );

			papplet.translate( 3, 3, -10 );
			if( cSchemeL != null ){
				papplet.stroke( cSchemeL.getShadow() );
			}
			else{
				papplet.stroke(200);
			}
			papplet.popMatrix();

		}
		 
		papplet.strokeWeight(1);		
	}

	public void draw() {
		if( showPoints ) drawPoints();
		if( showLines ) drawLines();
	}

}
