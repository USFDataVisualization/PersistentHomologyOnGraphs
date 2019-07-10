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

import java.io.IOException;
import java.io.PrintWriter;

import processing.core.PApplet;
import usf.dvl.graph.Graph;
import usf.dvl.graph.layout.forcedirected.ForceDirectedLayout;

public class DForceDirectedLayout extends DGraphDrawing {
	
	public ForceDirectedLayout fdl;
	public Graph g;

	public float pointSize = 2;
	public float edgeScale = 1;

	public DForceDirectedLayout( PApplet p, Graph _g, int w, int h ){
		super( p );

		g = _g;
		fdl = new ForceDirectedLayout( g, w, h );

		setData( new FDLPoints(), new FDLLines() );
		setColorScheme( new FDLColor() );
	}
	
	@Override 
	public void setPosition( int u0, int v0, int w, int h ){
		super.setPosition(u0, v0, w, h);
	    fdl.setSize(w,h);
	}

	public void saveData( String filename ) throws IOException {
		PrintWriter pw = new PrintWriter( filename );
		for( int i = 0; i < fdl.countVertex(); i++ ){
			pw.println( fdl.getVertex(i).getPositionX() + " " + fdl.getVertex(i).getPositionY() );
		}
		pw.close();
	}


	public void loadData( String filename ){
		String lines[] = papplet.loadStrings(filename);
		if( lines == null ) return;
		for( int i = 0; i < lines.length && i < fdl.countVertex(); i++ ){
			String [] f = PApplet.split(lines[i], ' ');
			fdl.setVertexPosition( i, Float.parseFloat(f[0]), Float.parseFloat(f[1]) );
		}
	}

	class FDLPoints implements DPositionSet2D {
		public int count(){ return fdl.countVertex(); }
		public float getX(int idx){ return fdl.getVertex(idx).getPositionX(); }
		public float getY(int idx){ return fdl.getVertex(idx).getPositionY(); }
		public float getSize(int idx){ return pointSize; }
	}

	class FDLLines implements DLineSet {
		public int [] getLine( int idx ){ 
			Graph.Edge e = g.getEdge(idx);
			return new int[]{ g.getVertexIndex(e.v0), g.getVertexIndex(e.v1) };
		}
		public int count(){ return g.getEdgeCount(); }
		public float getWeight( int idx ){ return PApplet.constrain( (float)g.getEdge(idx).w*edgeScale, 2, 10 ); }
	}

	class FDLColor implements ColorScheme {
		public int getFill( int idx ){ return papplet.color(100,100,200); }
		public int getStroke( int idx ){ return papplet.color(0); }
		public int getShadow( ){ return papplet.color(200); }
	}


	@Override
	public void draw(){
		
		fdl.update();

		if( getSelected() >= 0 ){
			fdl.setVertexPosition( getSelected(), papplet.mouseX-u0, papplet.mouseY-v0 );
		}
		
		super.draw();
	}
	


}


