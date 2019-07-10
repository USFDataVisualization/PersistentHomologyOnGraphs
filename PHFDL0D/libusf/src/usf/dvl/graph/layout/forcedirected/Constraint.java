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

package usf.dvl.graph.layout.forcedirected;


public interface Constraint {

	void constrainPoint( FDLVertex vert );

	
	static public class RectangularConstraint implements Constraint {
		
		public float x,y,w,h;
		public RectangularConstraint( float _x, float _y, float _w, float _h ){
			x = _x;
			y = _y;
			w = _w;
			h = _h;
		}
		
		@Override public void constrainPoint(FDLVertex vert) {
			float vx = vert.getPositionX();
			float vy = vert.getPositionY();
			vx = Math.max(vx, x); vx = Math.min(vx, x+w);
			vy = Math.max(vy, y); vy = Math.min(vy, y+h);
			vert.setPosition(vx, vy);
		}
	}

	
	static public class CircularConstraint implements Constraint {
		public float cx,cy,r;
		
		public CircularConstraint( float _cx, float _cy, float _r ){
			cx = _cx;
			cy = _cy;
			r = _r;
		}
		
		@Override public void constrainPoint(FDLVertex vert) {
			float vx = vert.getPositionX();
			float vy = vert.getPositionY();
			float dx = vx-cx;
			float dy = vy-cy;
			float len = (float)Math.sqrt(dx*dx+dy*dy);
			if( len > r ){
				vx = dx*r/len+cx;
				vy = dy*r/len+cy;
				vert.setPosition(vx, vy);
			}
		}
	}

	
	
}
