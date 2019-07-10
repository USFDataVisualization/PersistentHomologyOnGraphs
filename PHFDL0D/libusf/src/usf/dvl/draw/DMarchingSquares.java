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
import processing.core.PConstants;

public class DMarchingSquares extends DBasic {

	double[][] pfield;
	double threshold;
	
	public DMarchingSquares(PApplet p) {
		super(p);
	}

	public void setData( double[][] _pfield, double _threshold ){
		pfield    = _pfield;
		threshold = _threshold;
	}
	
	private void interpVertex( float x0, float y0, float f0, float x1, float y1, float f1 ){
		float t = (float) ((threshold-f0) / (f1-f0));
		if( t>0 && t<1 ){
			papplet.vertex( x0*(1-t)+x1*t, y0*(1-t)+y1*t );
		}
	}
	
	@Override
	public void draw() {
		if( pfield == null ) return;
		
		papplet.pushMatrix();
		papplet.translate( this.u0, this.v0 );
		papplet.scale( (float)this.w / (float)pfield.length, (float)this.h / (float)pfield[0].length );
		
		
		boolean strokeEnable = papplet.g.stroke;
		papplet.g.stroke = false;

		if( papplet.g.fill ){

			for(int i0 = 0; i0 < pfield.length-1; i0++){
				int i1 = i0+1;
				for(int j0 = 0; j0 < pfield[i0].length-1; j0++){
					int j1 = j0+1;
					
					if( pfield[i0][j0] >= threshold || pfield[i1][j0] >= threshold || pfield[i0][j1] >= threshold || pfield[i1][j1] >= threshold ){
					
						papplet.beginShape(PConstants.POLYGON);
							if( pfield[i0][j0] >= threshold ) papplet.vertex( i0,j0 );
							interpVertex( i0,j0,(float)pfield[i0][j0],  i1,j0,(float)pfield[i1][j0] );
							
							if( pfield[i1][j0] >= threshold ) papplet.vertex( i1,j0 );
							interpVertex( i1,j0,(float)pfield[i1][j0],  i1,j1,(float)pfield[i1][j1] );
							
							if( pfield[i1][j1] >= threshold ) papplet.vertex( i1,j1 );
							interpVertex( i1,j1,(float)pfield[i1][j1],  i0,j1,(float)pfield[i0][j1] );
	
							if( pfield[i0][j1] >= threshold ) papplet.vertex( i0,j1 );
							interpVertex( i0,j1,(float)pfield[i0][j1],  i0,j0,(float)pfield[i0][j0] );
	
						papplet.endShape();
					}
					
				}
			}
		}
		
		papplet.g.stroke = strokeEnable;
		
		if( papplet.g.stroke ){
			for(int i0 = 0; i0 < pfield.length-1; i0++){
				int i1 = i0+1;
				for(int j0 = 0; j0 < pfield[i0].length-1; j0++){
					int j1 = j0+1;
					
					if( pfield[i0][j0] >= threshold || pfield[i1][j0] >= threshold || pfield[i0][j1] >= threshold || pfield[i1][j1] >= threshold ){
						
							papplet.beginShape(PConstants.LINES);
							interpVertex( i0,j0,(float)pfield[i0][j0],  i1,j0,(float)pfield[i1][j0] );
							interpVertex( i1,j0,(float)pfield[i1][j0],  i1,j1,(float)pfield[i1][j1] );
							interpVertex( i1,j1,(float)pfield[i1][j1],  i0,j1,(float)pfield[i0][j1] );
							interpVertex( i0,j1,(float)pfield[i0][j1],  i0,j0,(float)pfield[i0][j0] );
							papplet.endShape();
	
					}
				}
			}
		}
		papplet.popMatrix();
			
	
	}

}
