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

package usf.dvl.graph.layout;

public class GraphLayoutVertex {
	protected float x,y;
	protected int id;

	public GraphLayoutVertex( int _id, float _x, float _y ){
		id = _id;
		x = _x;
		y = _y;
	}

	public int getID(){ return id; }

	public void setPosition( float _x, float _y ){
		x = _x;
		y = _y;
	}

	public float getPositionX(){ return x; }
	public float getPositionY(){ return y; }

}
