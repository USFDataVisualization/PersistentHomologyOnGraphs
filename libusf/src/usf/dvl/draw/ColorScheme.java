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

public interface ColorScheme {
	int getFill( int idx );
	int getStroke( int idx );
	int getShadow( );
	
	public class Default implements ColorScheme {
		public int getFill( int idx ){	 return 0xFFC80000; }
		public int getStroke( int idx ){ return 0xFF000000; }
		public int getShadow( ){ 		 return 0xFFC8C8C8; }
	}

}
 