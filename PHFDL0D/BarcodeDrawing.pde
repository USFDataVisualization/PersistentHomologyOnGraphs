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

class BarcodeDrawing extends DBasic {
  
  PersistenceDiagram pd;
  ForceDirectedDrawing fdd;
  
  float x_scale, y_scale;
  float x_start, y_start;

  float maxDeath = 0;
  boolean hideInf = false;
  boolean mouseOver = false; 
  
  ArrayList<BarcodeBar> bars;
  BarcodeScrollbar sb;
  BarcodeHyperbolicZoom hzoom;

  ArrayList<ForcePersistenceFeature0D> forces = new ArrayList<ForcePersistenceFeature0D>();


  public BarcodeDrawing( PApplet p ){
    super(p);
    
    sb = new BarcodeScrollbar(p);
    hzoom = new BarcodeHyperbolicZoom(p);
  }
  
  JSONObject toJSON(){
    JSONObject ret = new JSONObject();
    ret.put( "scrollBX", sb.bx );
    JSONArray status = new JSONArray();
    for( int i = 0; i < bars.size(); i++ ){
      if( bars.get(i).force.curMode == ForcePersistenceFeature0DMode.REPEL ) 
        status.append( "REPEL" );
      else if( bars.get(i).force.curMode == ForcePersistenceFeature0DMode.ATTRACT ) 
        status.append( "ATTRACT" );
      else 
        status.append( "normal" );
    }
    ret.put( "status", status );
    return ret;
  }

  public void setData( ForceDirectedDrawing _fdd, PersistenceDiagram _pd ){
    if( pd == _pd ) return;
    
    fdd = _fdd;
    pd  = _pd;

    bars = new ArrayList<BarcodeBar>();
     
    sb.bars = bars;
    
    // remove old pairwise forces
    for( ForcePersistenceFeature0D f : forces ){
      f.disable();
    }
    forces.clear();

    for(int i = 0; i < pd.size(); i++){ 
      int [] cause = ((PersistenceFeature0D)pd.get( i )).getCauseOfDeath();
      
      maxDeath = max( maxDeath, 1.0/pd.getDeath(i));
      
      
      if( cause[0] < 0 || cause[1] < 0 ){
        //bars[i] = new DBox( papplet );
      }
      else {
   
        FDLVertex v1 = fdd.fdl.getVertex(cause[0]); 
        FDLVertex v2 = fdd.fdl.getVertex(cause[1]);
   
        ForcePersistenceFeature0D f = new ForcePersistenceFeature0D( fdd.fdl, v1, v2, ((PersistenceFeature0D)pd.get( i )) );
        
        forces.add( f );
        bars.add(new BarcodeBar( papplet, fdd, f, ((PersistenceFeature0D)pd.get( i )) ));
        
      }
    } 

    
    if( Float.isInfinite( maxDeath ) || maxDeath > 1e10  || maxDeath == 0 ) maxDeath = 2;
    maxDeath *= 1.1;

    positionElements();
    
    if( data.barcodeStatus != null ){
       sb.bx = data.barcodeStatus.getFloat("scrollBX");
       JSONArray barStatus = data.barcodeStatus.getJSONArray("status");
       for( int i = 0; i < barStatus.size() && i < bars.size(); i++ ){
         if( barStatus.getString(i).equals("ATTRACT") ) bars.get(i).enableContraction();
         if( barStatus.getString(i).equals("REPEL") ) bars.get(i).enableRepulsion();
       }
    }
  }
  

  public void setPosition( int _sx, int _sy, int _w, int _h ){
    super.setPosition(_sx, _sy, _w, _h);
    sb.setPosition( _sx,_sy,_w-17,_h);
    hzoom.setPosition( _sx+w-15,_sy+25,10,_h-30);
    positionElements();
  }

  public void hideInfinite( boolean val ){
    hideInf = val;
    positionElements();
  }
  


  private void positionElements( ){
    if( bars == null ) return;

    for( int i = 0; i < bars.size(); i++ ){    
      float d = ( bars.get(i).mypd == null ) ? 0.0001 : (float)(1.0/bars.get(i).mypd.getDeath());
      bars.get(i).visible = !( hideInf && d > maxDeath );
      bars.get(i).inf     = ( d > maxDeath );
    }
    
    
    x_start = u0+5;
    y_start = v0+25;
    
    x_scale = (float)(0.95f*((w)/maxDeath));
    y_scale = (float)(h-30)/(float)(bars.size());
    //y_scale = 20;
    
    float yEnd = v0+h;


    hzoom.hyperEnabled = bars.size() >= 80;
    hzoom.hyperMag = map( bars.size(), 80, 5000, 2.5, 100 );

    float curV = y_start;
    
    for( int i = 0; i < bars.size(); i++ ){    

      float b = 0;
      float d = 0.0001;
  
      if( bars.get(i).mypd != null ){
        b = (float)bars.get(i).mypd.getBirth();
        d = (float)(1.0/bars.get(i).mypd.getDeath());
      }
     
      bars.get(i).visible = !( hideInf && d > maxDeath );
      bars.get(i).inf     = ( d > maxDeath );
            
      float bv0 = hzoom.hyperbolicMap( curV );
      float bv1 = hzoom.hyperbolicMap( constrain( curV + y_scale, y_start, yEnd) );
      
      if( bars.get(i).visible ){
        int currU = (int)(x_start + b*x_scale);
        int currW = (!bars.get(i).inf) ? (int)((d-b)*x_scale) : (w-17);
        int currV = (int)bv0;
        int currH = (int)constrain((bv1-bv0)-2,1,100);
        bars.get(i).setPosition( currU, currV, currW, currH );
        curV += y_scale;
      }
    }

}

  public void draw( ) {
    if( bars == null ) return;

    for( ForcePersistenceFeature0D f : forces ){
      f.update();
    }

    fdd.hover = null;
    
    // drawing for scrollbar
    sb.draw();

    // drawing for bars
    for( BarcodeBar b : bars ){
      b.draw();
    }
    
    hzoom.draw();

    // drawing for outline box
    papplet.noFill();
    papplet.strokeWeight(1.0f);
    papplet.stroke(75);
    papplet.rect( u0, v0, w, h );

  }

  public boolean mousePressed( ){
    if( sb.mousePressed() ){ return true; }
    
    if( !pointInside(mouseX,mouseY) ) return false;
    
    if( hzoom.mousePressed() ) return true;
    for( BarcodeBar b : bars ){
      if( b.mousePressed( ) ){ return true; }
    }
    return true;
  }

  public boolean mouseReleased( ){
    sb.mouseReleased();
    hzoom.mouseReleased();
    return pointInside(mouseX,mouseY);
  }

  boolean mouseWheel( float amnt ) {
    if( pointInside(mouseX, mouseY) ) 
      hzoom.scroll(amnt);
    return false;
  }
}
