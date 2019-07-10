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

/**
 * One dimensional Disjoint Set functionality. This is a naive implementation. Expected runtime 
 * for any operation is O( a(n) ). This is effectively O(1). 
 */
public class DisjointSet1D {

	private int[] array;

	/**
	 *  Construct a disjoint sets object.
	 *
	 *  @param numElements the initial number of elements--also the initial
	 *  number of disjoint sets, since every element is initially in its own set.
	 **/
	public DisjointSet1D(int numElements) {
		array = new int [numElements];
		for (int i = 0; i < array.length; i++) {
			array[i] = i;
		}
	}

	/**
	 *  union() unites two disjoint sets into a single set.  A 
	 *  union-by-lower_id heuristic is used to choose the new root.
	 *
	 *  @param set1 a member of the first set.
	 *  @param set2 a member of the other set.
	 **/
	public int union( int set1, int set2 ){
		int r0 = find( set1 );
		int r1 = find( set2 );
		
		if ( r0 < r1 ){
			array[r1] = r0;
			return r0;
		}
		else {
			array[r0] = r1;
			return r1;
		}
	}


	/**
	 *  find() finds the name of the set containing a given element.
	 *  Performs path compression along the way.
	 *
	 *  @param x the element sought.
	 *  @return the set containing x.
	 **/
	public int find(int x) {
		if ( array[x] == x ) {
			return x;                         // x is the root of the tree; return it
		} else {
			array[x] = find(array[x]);	// Find out who the root is; compress path by making the root x's parent.
			return array[x];			// Return the root                                       
		}
	}


	public int getWidth() {
		return array.length;
	}
	

	public int size() {
		return array.length;
	}



	/**
	 *  main() is test code.  All the find()s on the same output line should be
	 *  identical.
	 **/
	public static void main(String[] args) {
		int NumElements = 128;
		int NumInSameSet = 16;

		DisjointSet1D s = new DisjointSet1D(NumElements);

		for (int k = 1; k < NumInSameSet; k *= 2) {
			for (int j = 0; j + k < NumElements; j += 2 * k) {
				s.union( j , j+k );
			}
		}

		for (int i = 0; i < NumElements; i++) {
			System.out.print(s.find(i) + "*");
			if (i % NumInSameSet == NumInSameSet - 1) {
				System.out.println();
			}
		}
		System.out.println();

	}

}

