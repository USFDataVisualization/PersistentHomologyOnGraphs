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

package usf.dvl.graph.edgebundling;

import usf.dvl.graph.layout.forcedirected.FDLVertex;

public class EBVertex  extends FDLVertex {
	FDLVertex p0, p1;
	int fixDirection = 0;
	
	public EBVertex(int _id, float _x, float _y) {
		super(_id, _x, _y);
	}

	public void setParents(FDLVertex v, FDLVertex w) {
		p0 = v; p1 = w;			
	}
	
	public boolean sameParents(EBVertex v ){
		return p0==v.p0 && p1==v.p1;
	}
	
	public boolean hasParents(){
		return !( p0 == null || p1 == null );	
	}
	
	public float parentDistance(){
		if( p0 == null || p1 == null ) return 0;
		float dx = p0.getPositionX()-p1.getPositionX();
		float dy = p0.getPositionY()-p1.getPositionY();
		return (float)Math.sqrt( dx*dx + dy*dy );
	}
	
	public float parentDirectionSimilarity(EBVertex v){
		
		if( p0 == null || p1 == null ) return 0;
		
		float dx0 = (p0.getPositionX()-p1.getPositionX())/parentDistance();
		float dy0 = (p0.getPositionY()-p1.getPositionY())/parentDistance();

		float dx1 = (v.p0.getPositionX()-v.p1.getPositionX())/v.parentDistance();
		float dy1 = (v.p0.getPositionY()-v.p1.getPositionY())/v.parentDistance();
		
		return dx0*dx1 + dy0*dy1;

	}
}
