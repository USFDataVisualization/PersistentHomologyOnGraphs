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

package usf.dvl.topology.ph;

public class PersistenceFeature {
	
	protected int dim;
	protected double birth, death;
	
	public PersistenceFeature( int _dim, double _birth, double _death ){
		dim   = _dim;
		birth = _birth;
		death = _death;			
	}
	
	public String toString(){
		return dim + " " + birth + " " + death;
	}
	
	public double getBirth(){
		return birth;
	}
	
	public int getDimension() { return dim; }
	
	public double getDeath(){
		return death;
	}
	
	public double getPersistence(){
		return death-birth;
	}

}
