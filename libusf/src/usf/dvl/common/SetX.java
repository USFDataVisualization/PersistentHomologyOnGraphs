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

package usf.dvl.common;

import java.util.HashSet;
import java.util.Set;

public class SetX {


	public static float jaccardIndex( Set<?> s0, Set<?> s1 ) {
		HashSet<Object> intersect = new HashSet<Object>( s0 );
		HashSet<Object> union     = new HashSet<Object>( s0 );
		
		intersect.retainAll( s1 );
		union.addAll( s1 );
		
		return (float)(intersect.size()) / (float)(union.size());
	}
	
	
}
