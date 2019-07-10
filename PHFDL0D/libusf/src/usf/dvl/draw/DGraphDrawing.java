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
import usf.dvl.graph.edgebundling.EdgeBundling;
import usf.dvl.graph.layout.forcedirected.FDLVertex;

public class DGraphDrawing extends DPointsLines {

	private EdgeBundling	 eb			  = null;
	private float   		 eb_time_step = EdgeBundling.TIME_STEP_DEFAULT;
	private boolean 		 eb_enabled	  = true;

	public DGraphDrawing(PApplet p){
		super(p);
	}

	@Override
	public void setData( DPositionSet2D _pos ){
		if( pos == _pos ) return;
		super.setData(_pos);
		eb = null;
	}

	@Override
	public void setData( DPositionSet2D _pos, DLineSet _lines ){
		if( pos == _pos && lines == _lines ) return;
		super.setData(_pos,_lines);
		eb = new EdgeBundling( pos, lines, getWidth(), getHeight() );
	}

	public void disableEdgeBundling( ) { eb_enabled = false; }
	public void enableEdgeBundling( ) { 
		eb_enabled = true;
		if( eb == null ) eb = new EdgeBundling( pos, lines, getWidth(), getHeight() );
	}
	public boolean isEnabledEdgeBundling( ) { return eb_enabled; }

	public void setEdgeBundlingTimestep( float timestep ) {
		eb_time_step = timestep;
	}
	public void setEdgeBundlingSpringLength( float v ) {
		if( eb != null ) eb.setSpringLength(v);
	}
	public void setEdgeBundlingSpringForce( float v ) {
		if( eb != null ) eb.setSpringForce(v);
	}
	public void setEdgeBundlingAttractiveForce( float v ) {
		if( eb != null ) eb.setAttractiveForce(v);
	}
	
	
	protected void drawEdgeBundles() {
		if( pos == null ) return;
		if( lines == null ) return;
		if( eb == null ) return;

		eb.update( eb_time_step );

		for(int i = 0; i < lines.count(); i++){
			papplet.strokeWeight(lines.getWeight(i));

			int [] e = eb.getEdge(i);
			for( int j = 0; j < e.length-1; j++){

				FDLVertex vert0 = eb.getVertex(e[j]);
				FDLVertex vert1 = eb.getVertex(e[j+1]);

				float x0 = PApplet.constrain(u0+vert0.getPositionX(), u0, u0+w);
				float y0 = PApplet.constrain(v0+vert0.getPositionY(), v0, v0+h);
				float x1 = PApplet.constrain(u0+vert1.getPositionX(), u0, u0+w);
				float y1 = PApplet.constrain(v0+vert1.getPositionY(), v0, v0+h);

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
				papplet.line( x0,y0,x1,y1 );
				papplet.popMatrix();
			}
		}

		papplet.strokeWeight(1);
	}

	public void draw() {
		if( showPoints ) drawPoints();
		papplet.pushMatrix();
		papplet.translate(0, 0, -10 );
		if( eb_enabled )
			drawEdgeBundles();
		else
			if( showLines ) drawLines();
		papplet.popMatrix();
	}



}
