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

class BarcodeHyperbolicZoom extends DBasic {
  
  boolean hyperEnabled = true;
  float hyperMag  = 7.5;

  float hyperMag0 = 80.0;
  float hyperMag1 = 40.0;
  
  float hyperV = -1;
  float hyperStart = -1;
  float hyperStop  = -1;
  
  float yStart = -1;
  float yEnd   = -1;
  
  float minSizeInterp = 1;
  float maxSizeInterp = 30;
  
  boolean selected = false;
  
  float tanh(float x){
    return ( exp(2*x) - 1 ) / ( exp(2*x) + 1 ) ;  
  }
  
  float hyperbolicTransform( float y ){
    if( !hyperEnabled ) return y / h;
    float param;
    if( y < hyperV ) 
      param = constrain( hyperMag0 * (y - hyperV) / h, -5, 5 );
    else
      param = constrain( hyperMag1 * (y - hyperV) / h, -5, 5 );
    return tanh( param );
  }
  
  float hyperbolicMap( float y ){
    if( !hyperEnabled ) return y;
    float hv = hyperbolicTransform(y);
     if( hv < 0 ) return map( hv, hyperStart-0.00001f, 0, v0, constrain(hyperV, yStart, yEnd ) ); 
     return map( hv, 0, hyperStop+0.00001f, constrain(hyperV, yStart, yEnd ), v0+h ); 
  }


  BarcodeHyperbolicZoom( PApplet p ){
    super(p);
  }
  
  
  void draw() {

    yStart = v0;
    yEnd   = v0+h;

    if( hyperV < 0 ) hyperV = yEnd;
    if( selected ){
      hyperV = map( mouseY, v0+45, v0+h-45, v0, v0+h );
    }
    hyperV = constrain( hyperV, yStart, yEnd );
    
    hyperMag0 = map( hyperV, yStart, (yStart+yEnd)/2, hyperMag*3, hyperMag );
    hyperMag1 = map( hyperV, yEnd,   (yStart+yEnd)/2, hyperMag*3, hyperMag );
    hyperMag0 = constrain( hyperMag0, hyperMag, hyperMag*3 );
    hyperMag1 = constrain( hyperMag1, hyperMag, hyperMag*3 );
    
    hyperStart = hyperbolicTransform(yStart);
    hyperStop  = hyperbolicTransform(yEnd);
    
    noFill();
    stroke(0);
    strokeWeight(3);
    noStroke();
    
    float vStep = (float)h/250;
    float maxv0 = hyperbolicMap( (float)hyperV-vStep/2 );
    float maxv1 = hyperbolicMap( (float)hyperV+vStep/2 );
    float scale = maxv1 - maxv0;
    
    float colorScale = 255;
    if( selected || pointInside( mouseX, mouseY ) ) colorScale = 200;
    
    fill(colorScale);
    for( float curV = v0; curV < v0+h; curV+=vStep ){
      float v0 = hyperbolicMap( (float)curV );
      float v1 = hyperbolicMap( (float)curV+vStep );
      if( hyperEnabled )
        fill( colorScale*(1-(v1-v0)/scale) );
      rect( u0,curV, w, vStep);
    }
  }
  
  public void scroll( float amnt ){
    hyperV += amnt*2;
  }
  
  public boolean mousePressed( ){
    if( !pointInside( mouseX, mouseY ) ) return false;
    selected = true;
    return true;
  }

  public boolean mouseReleased( ){
   selected = false; 
   return false;
  }
}
