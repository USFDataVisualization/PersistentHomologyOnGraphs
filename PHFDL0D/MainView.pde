////////////////////////////////////////////////////////////////////////////
//
//  TopoFDL0D --- Persistent Homology Guided Force-Directed Graph Layouts
//  Copyright (C) 2019 Ashley Suh & Paul Rosen
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

class MainView extends DBasic {

  BarcodeDrawing bc0 =null;
  ForceDirectedDrawing fdd =null; 
  GraphData g;
  
  MainView( PApplet p ) {
    super(p);
  }

  void setData( GraphData _grphs ) {
    g = _grphs;
    fdd = new ForceDirectedDrawing( papplet, g.graph, bc0, 100, 100 );
    bc0 = new BarcodeDrawing(papplet);
  }

  void draw() {
    
    
    if( fdd != null ){
      fdd.setPosition(230, 10, w-250, h-20);
      fdd.setData( g.pd );
      fdd.draw();
    }
    
    if( bc0 != null ){
      bc0.setPosition( 5, 5, 210, h-10 );
      bc0.setData( fdd, g.pd );
      bc0.draw();
    }
    
    if( drawLegend ){
      if( g != null && g.catLabels.size() > 0 ) {
        pushMatrix();
        translate(0, 0, 100);
        int keyW = data.curColor.getDrawWidth(papplet, g.catLabels);
        data.curColor.draw( papplet, width-keyW-20, 20, g.catLabels );
        popMatrix();
      }
    }

}


 
  boolean keyPressed() {
    if ( fdd != null && fdd.keyPressed() ) return true;
    if ( bc0 != null && bc0.keyPressed() ) return true;
    return false;
  }

  boolean mousePressed() {
    if ( fdd != null && fdd.mousePressed() ) return true;
    if ( bc0 != null && bc0.mousePressed() ) return true;
    return false;
  }

  boolean mouseDragged() { return false; }

  boolean mouseReleased() {
    if ( fdd != null && fdd.mouseReleased() ) return true;
    if ( bc0 != null && bc0.mouseReleased() ) return true;
    return false;
  }
  
  boolean mouseWheel( float amnt ) {
    if ( fdd != null && fdd.mouseWheel(amnt) ) return true;
    if ( bc0 != null && bc0.mouseWheel(amnt) ) return true;
    return false; 
  }
}
