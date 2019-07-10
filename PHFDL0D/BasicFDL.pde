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
 
class BasicFDL extends DGraphDrawing {

  ForceDirectedLayout fdl;
  Graph g;

  PerNodeForce.LinearAttractiveForces forceSLA;
  PerNodeForce.PairwiseRepulsiveForces forceRF;
  PairwiseForce.SpringAttractiveForces forceSAF;
  
  FDLPoints points;
  FDLLines  lines;
  
  
  float eb_timestep = 0.5f;
  float eb_SpringLength = 1.0f;
  float eb_SpringForce = 1.8f;
  //float eb_AttractiveForce = 3.5f; // 3.5 for miserables
  float eb_AttractiveForce = 2.0f; // 2.0 for train
  
  BasicFDL( PApplet p, Graph _g, int w, int h ){
    super( p );
    g = _g;

    fdl = new ForceDirectedLayout( g, w, h );
    
    forceRF = new PerNodeForce.PairwiseRepulsiveForces(fdl);
    forceSAF = new PairwiseForce.SpringAttractiveForces(fdl);
    forceSLA = new PerNodeForce.LinearAttractiveForces( fdl, w/2, h/2 );

    fdl.addForces( forceRF );
    fdl.addForces( forceSAF );
    fdl.addForces( forceSLA );
    
    points = new FDLPoints();
    lines  = new FDLLines();
    setData( points, lines );
    setColorScheme( points );
    setLineColorScheme( lines );
    enableSelection( 20 );
    

    setPrimitiveDetail( 5 );
    setBorderSize( 1 );

    setPrimitiveModeSpheres();
    //if( g.nodes.size() > 200 ){
    //  setPrimitiveDetail( 1 );
    //  setPrimitiveModeCircles( );
    //}
    
    if( g.nodes.size() > 20 ){
      setPrimitiveDetail( 1 );
      setPrimitiveModeCircles( );
    }
    
    if( g.edges.size() > 500 ){
      disableEdgeBundling();
    }
    else {
      enableEdgeBundling();
    }
    
    
  }

  @Override
  public void setPosition(int u0, int v0, int w, int h){
    if( this.w != w || this.h != h ){
      data.curCenX = w/2;
      data.curCenY = h/2;
    }
    super.setPosition(u0,v0,w,h);
  }
  

  class FDLPoints extends ColorScheme.Default implements DPositionSet2D {
    public int count(){ return fdl.countVertex(); }
    public float getX(int idx){ 
      return mapX( fdl.getVertex(idx).getPositionX(), fdl.getVertex(idx).getPositionY() ); 
    }
    public float getY(int idx){ 
      return mapY( fdl.getVertex(idx).getPositionX(), fdl.getVertex(idx).getPositionY() ); 
    }
    public float getSize(int idx){
      if( data.sizeByDegree ){
        Graph.Vertex vert = g.getVertex(idx);
        int degree = vert.getAdjacentCount();
        float val = map(degree, data.minDegree, data.maxDegree, data.fdlPointScale, data.fdlPointScale*2);
        return val;
      }
      return constrain( data.fdlPointScale, 1, 20 ) ; 
    }
      
        
    int getFill( int idx ){
       
      if( data.colorByDegree ){
        Graph.Vertex vert = g.getVertex(idx);
        int degree = vert.getAdjacentCount();
        
        // yellow -> red
        float red = map(degree, data.minDegree, data.maxDegree, 255, 240);
        float green = map(degree, data.minDegree, data.maxDegree, 255, 59);
        float blue = map(degree, data.minDegree, data.maxDegree, 178, 32);

        return color(red, green, blue);
      }
      
      GraphData.MVertex v = (GraphData.MVertex)g.nodes.get(idx);
      return data.curColor.getColor( v.grp );
      
    }

    int getStroke( int idx ){
      return color(0);
    }
    
    int getShadow( ){ return color(200); }
    
  }

  class FDLLines extends ColorScheme.Default implements DLineSet {
    int [] getLine( int idx ){
      Graph.Edge e = g.edges.get(idx);
      return new int[]{g.getVertexIndex(e.v0),g.getVertexIndex(e.v1)};
    }

    int count(){ return g.edges.size(); }

    float getWeight( int idx ){
      return constrain( (float)((GraphData.MEdge)g.edges.get(idx)).drawW*data.fdlEdgeScale, 2, 3 );
    }
    
    int getFill( int idx ){ return color(0, 0, 0, 50); }
    int getStroke( int idx ){ return color(100,100,100, 50); }
    //int getStroke( int idx ){ return color(150,150,150,20); }
    int getShadow( ){ return color(200,200,200,25); }
  }

  boolean loadPositions = true;
  boolean resetPositions = false;
  public void resetPositions(){ resetPositions = true; }
  


  public void update(){

    fdl.setSize((int)w,(int)h);

    forceRF.coulombConstant = data.fdlCoulombConstant;

    forceSAF.springConstant = data.fdlSpringConstant;
    forceSAF.springLength   = data.fdlRestingLength;

    forceSLA.centroidX = w/2;
    forceSLA.centroidY = h/2;
    forceSLA.pullScaleFactor = 0.5;

    fdl.timeStep = data.fdlTimestep;

    if( !data.paused ){
      try {
        fdl.update();
      } catch( Exception e ){
        e.printStackTrace();
      }
    }
        
    if( getSelected() >= 0 ){
      fdl.setVertexPosition( getSelected(), unmapX(mouseX-u0,mouseY-v0), unmapY(mouseX-u0,mouseY-v0) );
    }

    if( resetPositions ){
      for( int i = 0; i < fdl.countVertex(); i++ ){
        fdl.setVertexPosition( i, random(50,w-50),random(50,h-50) );
        fdl.getVertex(i).setPosition(random(50,w-50),random(50,h-50));
        fdl.getVertex(i).setVelocity(0,0);
        fdl.getVertex(i).setAcceleration(0,0);
        fdl.getVertex(i).clearForce();
      }
      resetPositions = false;
    }
    
    if( loadPositions ){
      for( int i = 0; i < data.graph.getNodeCount(); i++ ){
        GraphData.MVertex v = (GraphData.MVertex)data.graph.getVertex(i);
        if( !Float.isNaN(v.x) && !Float.isNaN(v.y) )
          fdl.getVertex(i).setPosition(v.x,v.y);
      }
      loadPositions = false;
    }

    if( data.autoscaling ){
      float xmin = Float.MAX_VALUE, xmax = -Float.MAX_VALUE;
      float ymin = Float.MAX_VALUE, ymax = -Float.MAX_VALUE;
      double totalX = 0, totalY = 0;
      for(int i = 0; i < fdl.countVertex(); i++ ){
        totalX += fdl.getVertex(i).getPositionX();
        totalY += fdl.getVertex(i).getPositionY();
        xmin = min( xmin, fdl.getVertex(i).getPositionX() ); 
        xmax = max( xmax, fdl.getVertex(i).getPositionX() ); 
        ymin = min( ymin, fdl.getVertex(i).getPositionY() );
        ymax = max( ymax, fdl.getVertex(i).getPositionY() );
      }
      data.curCenX = lerp( data.curCenX, (float)(totalX/fdl.countVertex()), 0.01f );
      data.curCenY = lerp( data.curCenY, (float)(totalY/fdl.countVertex()), 0.01f );
      float scaleX = max( (xmax-data.curCenX), (data.curCenX-xmin) ) / w*data.scale*2;
      float scaleY = max( (ymax-data.curCenY), (data.curCenY-ymin) ) / h*data.scale*2;
      float useScale = constrain( max(scaleX,scaleY), 0.875f, 0.975f );
      data.scale *= map( useScale, 0.875f, 0.975f, 1.005f, 0.995f );
    }
    else{
      data.curCenX = w/2;
      data.curCenY = h/2;
      data.scale = 1;
    }
    tform.reset();
    tform.translate( w/2, h/2 );
    tform.rotate( rotation );
    tform.scale( data.scale );
    tform.translate( - data.curCenX,  - data.curCenY );
    itform = tform.get();
    itform.invert();
  }

  float rotation = 0;
  PMatrix2D tform = new PMatrix2D();
  PMatrix2D itform = new PMatrix2D();

  float mapX( float x, float y ) { return tform.multX(x,y); }
  float mapY( float x, float y ) { return tform.multY(x,y); }
  float unmapX( float x, float y ) { return itform.multX(x,y); }
  float unmapY( float x, float y ) { return itform.multY(x,y); }


  boolean keyPressed() {
    switch( key ){
      case '+': rotation += 0.05f; return true;
      case '-': rotation -= 0.05f; return true;
    }
   return false;
 }  
 
 
  public void draw(){
    
    setEdgeBundlingTimestep( eb_timestep );
    setEdgeBundlingSpringLength( eb_SpringLength );
    setEdgeBundlingSpringForce( eb_SpringForce );
    setEdgeBundlingAttractiveForce( eb_AttractiveForce );
    
    int startM=0, endM=0;
    startM = millis();
    update();
    endM = millis();
    if( frameRate > 20  && frameCount%80 == 0 ||
        frameRate <= 20 && frameCount%40 == 0 ||
        frameRate <= 5  && frameCount%10 == 0 ) println( "FDL took: " + (endM-startM) + " ms" );
    

      
    if( getSelected() >= 0 ){
      GraphData.MVertex v = (GraphData.MVertex)g.nodes.get(getSelected());
      fill(0);
      textSize(18);
      textAlign( RIGHT,BOTTOM);
      text( v.name, u0+w-210, v0+h-10 );
    }
    
    if( data.paused ){
      fill(0);
      textSize(10);
      textAlign( LEFT,TOP );
      text( "paused", u0+5, v0+5 );
    }
    
    super.draw();
    
    
    
      
    /*
    //debugging visualization for Barnes Hut
    if( getSelected() >= 0 ){
      pushMatrix();
      translate(u0+w/2,v0+h/2,10);
      scale(scale,scale,1);
      translate(-curCenX, -curCenY, 0 );
      noFill();
      stroke(255,0,0);
      strokeWeight(3/scale);
      for( BarnesHutApproximation.KDTree b : fdl.getBHBoxes(fdl.getVertex(getSelected())) ){
         rect( b.minX, b.minY, b.maxX-b.minX, b.maxY-b.minY );
      }
      popMatrix();
    }
    */
  }
  
}
