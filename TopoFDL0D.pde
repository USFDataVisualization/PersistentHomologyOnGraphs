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

import java.util.*;
import controlP5.*;

// select file to load on startup
String loadFile = null;
String dataDir = null;

MainView curView;
GraphData data;

boolean saveImage = false;
boolean saveVideo = false;
boolean drawLegend = true;
boolean saveImageNamed = false;

void settings(){
  size(1200, 800, P3D);
  smooth(8);
}

void setup() {
  ortho(); 
  textFont(createFont("Arial",24,true));
  frameRate(500);
  
  if( loadFile != null ){
    fileSelected( dataFile(loadFile) );
  }
  else
    selectInput("Select a file to process:", "fileSelected");
}

void fileSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
    exit(); return;
  }
  noLoop();
    loadFile = selection.getAbsolutePath();
    dataDir = selection.getParent() + "/";
    println(dataDir);
    curView = new MainView(this);
    data = new GraphData( this, loadJSONObject( loadFile ) );
    curView.setData( data );
  loop();
}

void draw() {
  background( 255 );
  ortho(); 
 
  if( curView != null ){
    if( curView.fdd != null ){
      curView.fdd.setPrimitiveDetail(5);
      if( saveImage || saveVideo ){
        curView.fdd.setPrimitiveDetail(20);
      }
    }
  
    curView.setPosition(0,0,width,height);
    curView.draw();
  }

  if( !saveImage && !saveVideo ){
    stroke(0);
    strokeWeight(1);
    fill(0);
    textSize(12);
    textAlign(LEFT);
  }

  if( saveImage || saveVideo ){
    saveFrame("frame######.png");
  }
  saveImage = false;
  if( saveImageNamed ){
    String saveFile = loadFile.substring( 0, loadFile.length()-5) + ".png";
    saveFrame(saveFile);
    saveImageNamed = false;
  }
  
  if( frameRate > 40 && frameCount%120 == 0 ){
    println("FPS: " + frameRate );
  }
  else if( frameRate < 40 && frameCount%80 == 0 ){
    println("FPS: " + frameRate );
  }
  else if( frameRate < 20 && frameCount%40 == 0 ){
    println("FPS: " + frameRate );
  }
  else if( frameRate < 5 && frameCount%10 == 0 ){
    println("FPS: " + frameRate );
  }
  

}
 
void keyPressed() {
  switch( key ){
    case 'v': saveVideo = !saveVideo; break;
    //case 's': saveImage = true; break;
    case 's': { saveImageNamed = true; /*saveLayoutForTopoQuality();*/ data.saveJSON(); println("write to json file successfully"); break; }
    case 'p': data.paused = !data.paused; break;
    //case '+': curView.fdd.rotation += 0.05f; break;
    //case '-': curView.fdd.rotation -= 0.05f; break;
    case 'c': if( data != null ) data.toggleVisible(); break;
    case 'l': drawLegend = !drawLegend; break;
    //case 't': saveLayoutForTopoQuality(); break;
    default: curView.keyPressed();
  }
}

void mousePressed(){
  if( curView != null ) curView.mousePressed();
}

void mouseDragged(){
  if( curView != null ) curView.mouseDragged();
}

void mouseReleased(){
  if( curView != null ) curView.mouseReleased();
}

void mouseWheel(MouseEvent event) {
  if( curView != null ) curView.mouseWheel( event.getCount() ); 
}


void saveLayoutForTopoQuality(){
  
    BasicFDL fdd = curView.fdd;
    
    // iterate through nodes
      JSONArray positions = new JSONArray();
      for( int i = 0; i < fdd.fdl.countVertex(); i++ ){
        JSONObject pnt = new JSONObject();
        pnt.put( "idx", i );
        pnt.put( "x", fdd.fdl.getVertex(i).getPositionX() );
        pnt.put( "y", fdd.fdl.getVertex(i).getPositionY() );
        positions.append(pnt);
      }
      
      JSONArray bars = new JSONArray();
      BarcodeDrawing bd = curView.bc0;
      for( int i = 0; i < bd.bars.size(); i++){
        JSONObject bar = new JSONObject();
        bar.put("idx",i);
        bar.put("type","norm");
        if( bd.bars.get(i).force.curMode == ForcePersistenceFeature0DMode.ATTRACT ) bar.put("type","attr");
        if( bd.bars.get(i).force.curMode == ForcePersistenceFeature0DMode.REPEL   ) bar.put("type","repl");
        bars.append(bar);
      }
    
      PMatrix2D tform = fdd.tform;
      JSONArray jtform = new JSONArray();
      jtform.append( tform.m00 );
      jtform.append( tform.m01 );
      jtform.append( tform.m02 );
      jtform.append( tform.m10 );
      jtform.append( tform.m11 );
      jtform.append( tform.m12 );
      
      JSONObject json = new JSONObject();
      json.put("positions",positions);
      json.put("bars",bars);
      json.put("tform",jtform);
      
      String saveFile = loadFile.substring( 0, loadFile.length()-5) + ".topo";
      //println("Saving: " + dataDir + "topo" + frameCount + ".json" );
      //saveJSONObject( json, dataDir + "topo" + frameCount + ".json" );
      println("Saving: " + saveFile );
      saveJSONObject( json, saveFile );
      
}
