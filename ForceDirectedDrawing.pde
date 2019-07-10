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
 
class ForceDirectedDrawing extends BasicFDL {

  PersistenceDiagram pd;
  BarcodeDrawing bd;
  DBubbleSet bs = null;

  ForcePersistenceFeature0D hover = null; 

  ForceDirectedDrawing( PApplet p, Graph _g, BarcodeDrawing _bd, int w, int h ){
    super(p,_g,w,h);
    bd = _bd;

    points = new FDLBubblePoints();
    setData( points, lines );
    setColorScheme( points );

    bs = new DBubbleSet( papplet );
    bs.setData( (FDLBubblePoints)points );
  
    bs.setSetFill(0,color(200,0,0,75));
    bs.setSetStroke(0,color(200,0,0));
    bs.setSetStrokeWeight(0,0.5);
      
    bs.setSetFill(1,color(0,0,200,75));
    bs.setSetStroke(1,color(0,0,200));
    bs.setSetStrokeWeight(1,0.5);


  }
  

  @Override
  public void setPosition(int u0, int v0, int w, int h){
    super.setPosition(u0,v0,w,h);
    
    try{
      if( bs != null ) bs.setPosition(u0,v0,w,h);
    } catch( Exception e ){
      e.printStackTrace();
    }
  }
  


  public void setData( PersistenceDiagram pd ){
    if( this.pd == pd ) return;
    this.pd = pd;
  }

  class FDLBubblePoints extends FDLPoints implements DBubbleSet.DSetPosition2D {
    
    public int getSetID(int idx){
      if( hover == null ) return 2;
      if( hover.getSetA().contains(idx) ){ return 0; }
      if( hover.getSetB().contains(idx) ){ return 1; }
      return 2; 
    } 
    
    int getStroke( int idx ){
      
      if( bs == null && hover != null ){
        if( hover.getSetA().contains(idx) ) return color(200,0,0,75); 
        if( hover.getSetB().contains(idx) ) return color(0,0,200,75);
      }
      
      return color(0);
    }
    
  }



  public void draw(){
    
    
    setBorderSize( 1 );
    try{
      if( hover != null ){ 
      if( bs == null ){
        noStroke();
        pushMatrix();
        translate(0,0,-5);
        for( int i = 0; i < fdl.countVertex(); i++ ){
           float x = u0 + mapX( fdl.getVertex(i).getPositionX(), fdl.getVertex(i).getPositionY() );
           float y = v0 + mapY( fdl.getVertex(i).getPositionX(), fdl.getVertex(i).getPositionY() );
           if( hover.getSetA().contains(i) ) fill(220,150,150); 
           if( hover.getSetB().contains(i) ) fill(150,150,220);
           ellipse( x,y, 30, 30 );
        }
        translate(0,0,-0.5f);
        for( int i = 0; i < fdl.countVertex(); i++ ){
           float x = u0 + mapX( fdl.getVertex(i).getPositionX(), fdl.getVertex(i).getPositionY() );
           float y = v0 + mapY( fdl.getVertex(i).getPositionX(), fdl.getVertex(i).getPositionY() );
           if( hover.getSetA().contains(i) ) fill(255,0,0);
           if( hover.getSetB().contains(i) ) fill(0,0,255);
           ellipse( x,y, 33, 33 );
        }
        popMatrix();        
      }
      if( bs != null ){
        translate(0,0,-100);
        bs.draw();
        translate(0,0,100);
      }
        flush();
      }
    } catch(Exception e){
     e.printStackTrace(); 
    }
        
    super.draw();
    
  }
  
  
}
