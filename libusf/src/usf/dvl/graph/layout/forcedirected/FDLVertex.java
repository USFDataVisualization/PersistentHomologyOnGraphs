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

import java.util.ArrayList;

import usf.dvl.graph.layout.GraphLayoutVertex;

public class FDLVertex extends GraphLayoutVertex {

	float ax=0,ay=0;
	float vx=0,vy=0;
	float mass = 1;
	float diam = 1;
	float fx=0,fy=0;

	public int superNode = 1;
	
	ArrayList<FDLVertex> adjacent = new ArrayList<FDLVertex>();
	ArrayList<Float> springLen = new ArrayList<Float>();

	public FDLVertex( int _id, float _x, float _y ){
		super(_id,_x,_y);
	}
	

	public void addAdjacent( FDLVertex g, float naturalSpringLength ){
		adjacent.add(g);
		springLen.add(naturalSpringLength);
	}

	public int getAdjacentCount( ){ return adjacent.size(); }
	public FDLVertex getAdjacentAt( int i ){ return adjacent.get(i); }
	public float getAdjacentSpringLengthAt(int i ){ return springLen.get(i); }
 

	public void  setMass( float m ){ mass = m; }
	public float getMass( ){ return mass; }


	public void  setDiameter( float d ){ diam = d; }
	public float getDiameter( ){ return diam; }


	public void setVelocity( float _vx, float _vy ){
		vx = _vx;
		vy = _vy;
	}

	
	public float getVelocityX(){ return vx; }
	public float getVelocityY(){ return vy; }
	
	
	public void setAcceleration( float _ax, float _ay ){
		ax = _ax;
		ay = _ay;
	}

	public float getAccelerationX(){ return ax; }
	public float getAccelerationY(){ return ay; }


	public synchronized void addForce( float _fx, float _fy ){
		fx += _fx;
		fy += _fy;
	}

	
	public float getForceX() { return fx; }
	public void clearForce() { fx = fy = 0; }
	public float getForceY() { return fy; }
	
	
}
