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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import usf.dvl.common.SystemX;
import usf.dvl.graph.Graph;

public abstract class GraphLayout<VTYPE extends GraphLayoutVertex> {

	protected ArrayList<VTYPE> layoutVerts = new ArrayList<VTYPE>( );
	protected HashMap<Graph.Vertex,VTYPE> graph2layout = new HashMap<Graph.Vertex,VTYPE>( );

	protected int w, h;

	protected GraphLayout( int width, int height ) {
		w = width;
		h = height;
	}

	public GraphLayout( Graph _g, int width, int height ){ 	
		w = width;
		h = height;

		Random random = new Random();
		for( int i = 0; i < _g.getNodeCount(); i++){
			VTYPE v = createVertex( _g.getVertex(i), random.nextInt(width), random.nextInt(height) );
			layoutVerts.add( v );
			graph2layout.put( _g.getVertex(i), v );
		}
	}


	public int getWidth() { return w; }
	public int getHeight() { return h; }

	public void setSize( int width, int height ){
		if( w != width || h != height ){
			for( VTYPE curr : layoutVerts ){
				float nx = curr.getPositionX() * (float)width / (float)w;
				float ny = curr.getPositionY() * (float)height / (float)h;
				curr.setPosition(nx, ny);
			}
			w = width;
			h = height;
		}
	}


	public int countVertex(){ return layoutVerts.size(); }
	protected abstract VTYPE createVertex( Graph.Vertex v, float x, float y );
	public VTYPE getVertex( int i ){ return layoutVerts.get(i); }
	public ArrayList<VTYPE> getVertices(){ return layoutVerts; }


	public void savePointPositionData( String filename ) throws FileNotFoundException{
		PrintWriter pw = new PrintWriter( filename );
		for( int i = 0; i < layoutVerts.size(); i++ ){
			pw.println( "{\"x\": " + layoutVerts.get(i).getPositionX() + ", \"y\": " + layoutVerts.get(i).getPositionY() +"},");
		}
		pw.close();
	}


	public void loadPointPositionData( String filename ) throws IOException {
		String lines[] = SystemX.readFileContents(filename);
		if( lines == null ) return;
		for( int i = 0; i < lines.length && i < layoutVerts.size(); i++ ){
			String [] parts = lines[i].split("\\s+");
			setVertexPosition( i, Float.parseFloat(parts[0]), Float.parseFloat(parts[1]) );
		}
	}

	public void setVertexPosition( int i, float x, float y ){
		layoutVerts.get(i).x = x;
		layoutVerts.get(i).y = y;
	}

	public abstract void update( );



}
