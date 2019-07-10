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

class BarcodeBar extends DBasic {

  boolean selected = false;
  boolean hovered = false;
  boolean inf = false;
  boolean visible = true;
  int v1, v2, setSize1, setSize2;
  
  ForcePersistenceFeature0D force = null;
  PersistenceFeature0D mypd = null;
  ForceDirectedDrawing fdd = null;  
  
  BarcodeBar( PApplet p, ForceDirectedDrawing _fdd, ForcePersistenceFeature0D _f, PersistenceFeature0D _pd ){ 
    super(p);
    
    fdd   = _fdd;
    force = _f;      
    mypd  = _pd;
    
    int [] cause = mypd.getCauseOfDeath();
    v1 = cause[0];
    v2 = cause[1];
    setSize1 = (data.graph.getNodeCount() > 5000) ? 0 : mypd.getMSTSizeA();
    setSize2 = (data.graph.getNodeCount() > 5000) ? 1 : mypd.getMSTSizeB();
    
  }

  void draw(){
    if( !visible ) return;
    
    hovered = pointInside(mouseX,mouseY);
    if( hovered ) fdd.hover = force;
    
    papplet.stroke(100);
    strokeWeight(1.5);
    if( hovered ) strokeWeight(3);
  
    float w1 = (float)setSize1 / (float)(setSize1 + setSize2) * w ;
    
    int color1 = fdd.getColorScheme().getFill(v1);
    papplet.fill(color1);
    if( force.curMode == ForcePersistenceFeature0DMode.REPEL   ) papplet.fill( papplet.lerpColor( color1, color(0), 0.65 ) );
    if( force.curMode == ForcePersistenceFeature0DMode.ATTRACT ) papplet.fill( papplet.lerpColor( color1, color(255), 0.65 ) );

    papplet.rect( u0, v0, w1, h );
         
    int color2 = fdd.getColorScheme().getFill(v2);
    papplet.fill(color2);
    if( force.curMode == ForcePersistenceFeature0DMode.REPEL   ) papplet.fill( papplet.lerpColor( color2, color(0), 0.65 ) );
    if( force.curMode == ForcePersistenceFeature0DMode.ATTRACT ) papplet.fill( papplet.lerpColor( color2, color(255), 0.65 ) );

     papplet.rect(u0+w1, v0, w-w1, h);

  }
  
  public void enableContraction(){
    if( force.curMode == ForcePersistenceFeature0DMode.ATTRACT ) return;
    if( force.curMode == ForcePersistenceFeature0DMode.REPEL   ) return;
    force.enableAttract();
   }
   
   public void disableContraction(){
    if( force.curMode != ForcePersistenceFeature0DMode.ATTRACT ) return;
    force.disable();
  }
  
  public void enableRepulsion(){
    if( force.curMode == ForcePersistenceFeature0DMode.REPEL   ) return;
    force.enableRepel();
  }
  
  public void disableRepulsion(){
    if( force.curMode != ForcePersistenceFeature0DMode.REPEL   ) return;
    force.disable(); 
  }

  public boolean mousePressed( ){
    if( !pointInside( mouseX, mouseY ) ) return false;

    selected = !selected;
    if( force != null ){
      
      if( force.curMode != ForcePersistenceFeature0DMode.REPEL ){       
        enableRepulsion();
      }
      else {  
        disableRepulsion();
      }
      
    }
    return true;
  }
}
