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

public class EBNode {
	private FDLVertex v = null;
     private double x, y;

     public EBNode(double x, double y){
         this.x = x;
         this.y = y;
     }

     public EBNode(FDLVertex vertex) {
    	 v = vertex;
	}

	public double getX(){
		if( v != null ) return v.getPositionX();
         return x;
     }

     public int getXInt(){
         return (int)Math.round(getX());
     }

     public double getY(){
 		if( v != null ) return v.getPositionY();
         return y;
     }

     public int getYInt(){
         return (int)Math.round(getY());
     }
     
     public float fx = 0, fy = 0;
     public void clearForce(){
    	 fx = 0;
    	 fy = 0;
     }
     
     

     public void setCoords(double x, double y){
         this.x = x;
         this.y = y;
     }

     public double distance(EBNode other){
         EBEdge edge = new EBEdge(this, other);
         return edge.magnitude();
     }
     
     
     
}
