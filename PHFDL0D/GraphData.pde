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

public class GraphData extends DBasic {

  ControlP5 cp5;

  Graph graph = new Graph();
  PersistenceDiagramD0 pd;
  HashMap<String,MVertex> nodeNames = new HashMap<String,MVertex>();
  HashMap<Integer,String> catLabels = new HashMap<Integer,String>();
   
  QualitativeColormap defaultColor = new QualitativeColormap( 0xffa6cee3,0xff1f78b4,0xffb2df8a,0xff33a02c,
                                                                0xfffb9a99,0xffe31a1c,0xff8C564B,0xffff7f00,
                                                                0xffcab2d6,0xff6a3d9a,0xffffff99,0xffb15928);

  QualitativeColormap senateColor  = new QualitativeColormap.SenateColor();
  QualitativeColormap scienceColor = new QualitativeColormap(0xff99ccff, 0xff1f78b4, 0xff97bc4e, 0xff007b0c,
                                                             0xffff9b98, 0xffcc0000, 0xfff5a33e, 0xffff7619,
                                                             0xffbfabd2, 0xff562f7e, 0xffffeb7a, 0xff8e5119,
                                                             0xff670a0a);
  
  public int curColorID = 0;
  public QualitativeColormap curColor = defaultColor;
  
  public int minDegree, maxDegree;
  
  public boolean paused = false;
  public boolean unweighted = false; // for calculating jaccard index
  public int unweightedEgoHops = 1; // how big of a neighborhood to calculate jaccard index with
  

  // these are initialized in GraphData
  public float fdlRestingLength = 5;
  public float fdlPointScale = 3;
  public float fdlTimestep = .55f;
  public float fdlEdgeScale = 0.1f;
  public float fdlSpringConstant = 1.0f;
  public float fdlCoulombConstant = 50.0;

  public float fpfSpringLength    = 1;
  public float fpfSpringForce     = 25;
  public float fpfCoulombConstant = 10000; 
  
  public float curCenX = 0;
  public float curCenY = 0;
  public float scale = 1;

  public boolean colorByDegree = false; 
  // warning: sizeByDegree does not work for 2008 senate datasets
  public boolean sizeByDegree = false; 

  public boolean autoscaling = true;
  
  public float clusterSpringLength = 200;
  
  public JSONObject barcodeStatus = null;

  
  public void resetPositions(){
    println("reset positions");
    curCenX = curView.fdd.getWidth()/2.0f;
    curCenY = curView.fdd.getHeight()/2.0f;
    scale = 1;
    curView.fdd.resetPositions();  
  }

  public GraphData( PApplet theParent, JSONObject data ){
    super(theParent);
    setPosition(width-200,height-460,200,460);
    
    // read in parameters for forces&springs
    JSONArray params = null;
    if( data.hasKey("parameters") ){ params = data.getJSONArray("parameters"); }
    if( data.hasKey("params") ){ 
      params = new JSONArray( ); 
      params.append( data.getJSONObject("params") );
    }
    if( params != null ){
      for( int i = 0; i < params.size(); i++ ){
        JSONObject param = params.getJSONObject(i);
        if( param.hasKey("unweighted") ) unweighted = param.getBoolean("unweighted");
        if( param.hasKey("unweightedEgoHops") ) unweightedEgoHops = param.getInt("unweightedEgoHops");
        if( param.hasKey("fdlRestingLength") ) fdlRestingLength = param.getFloat("fdlRestingLength");
        if( param.hasKey("fdlPointScale") ) fdlPointScale = param.getFloat("fdlPointScale");
        if( param.hasKey("fdlTimestep") ) fdlTimestep = param.getFloat("fdlTimestep");
        if( param.hasKey("fdlEdgeScale") ) fdlEdgeScale = param.getFloat("fdlEdgeScale");
        if( param.hasKey("fdlSpringConstant") ) fdlSpringConstant = param.getFloat("fdlSpringConstant");
        if( param.hasKey("fdlCoulombConstant") ) fdlCoulombConstant = param.getFloat("fdlCoulombConstant");
        if( param.hasKey("fpfSpringLength") ) fpfSpringLength = param.getFloat("fpfSpringLength");
        if( param.hasKey("fpfSpringForce") ) fpfSpringForce = param.getFloat("fpfSpringForce");
        if( param.hasKey("fpfCoulombConstant") ) fpfCoulombConstant = param.getFloat("fpfCoulombConstant");
        if( param.hasKey("colormap") ) switchColorMap( param.getInt("colormap") );
        if( param.hasKey("fdlCurCenX") ) curCenX = param.getFloat("fdlCurCenX");
        if( param.hasKey("fdlCurCenY") ) curCenY = param.getFloat("fdlCurCenY");
        if( param.hasKey("fdlScale") )  scale = param.getFloat("fdlScale");
        if( param.hasKey("paused") )  paused = param.getBoolean("paused");
        if( param.hasKey("clusterSpringLength") )  clusterSpringLength = param.getFloat("clusterSpringLength");
      }
    }    
    
    if( data.hasKey("barcode") ){
      barcodeStatus = data.getJSONObject("barcode");
    }
    JSONArray n = data.getJSONArray("nodes");
    for( int i = 0; i < n.size(); i++ ){
      graph.addVertex( new MVertex( n.getJSONObject(i) ) );
      
    }
    println("Nodes: " + graph.getNodeCount() );
    
    JSONArray e = data.getJSONArray("links");
    for( int i = 0; i < e.size(); i++ ){
      // add edge function based on if graph is unweighted or not
      graph.addEdge( new MEdge( e.getJSONObject(i) ) );
   
    }
    println("Edges: " + graph.getEdgeCount() );
    
    
    println(unweightedEgoHops);
    // if graph is unweighted, calculate jaccard index for weights
    if( unweighted ){
      HashMap<Graph.Vertex,EgoGraph> egos = new HashMap<Graph.Vertex,EgoGraph>();
      int t1 = millis();
      for(int i = 0; i < graph.getNodeCount(); i++ ){
        egos.put( graph.getVertex(i), new EgoGraph( graph.getVertex(i), unweightedEgoHops ) );
      }
      int t2 = millis();
      for( int i = 0; i < graph.getEdgeCount(); i++ ){
        Graph.Edge edge = graph.getEdge(i);
        edge.w = egos.get( edge.v0 ).jaccardIndex( egos.get( edge.v1 ) );
      }
      int t3 = millis();
      for(int i = 0; i < graph.getNodeCount(); i++ ){
        Graph.Vertex curV = graph.getVertex(i);
        for( int j = 0; j < curV.getAdjacentCount(); j++ ){
          Graph.Vertex curN = curV.getAdjacentAt(j);
          curV.setAdjacentWeight(j, (float)egos.get( curV ).jaccardIndex( egos.get( curN ) ) );
        }
      }
      int t4 = millis();
      println( (t2-t1) + " " + (t3-t2) + " " + (t4-t3) );
    }
    

    JSONArray grps = data.getJSONArray("groups");
    if( grps != null ){
      for( int i = 0; i < grps.size(); i++ ){
        JSONObject cGrp = grps.getJSONObject(i);
        String lbl = cGrp.getString("label");
        catLabels.put( cGrp.getInt("id"), ((lbl==null)?"":lbl) );
      }
    }
    


    int startM = millis();
      eGraph egraph = new eGraph();
      for(int i = 0; i < graph.getNodeCount(); i++ ){
        egraph.add( (MetricSpaceNode)graph.nodes.get(i) );
      }
      pd = new PersistenceDiagramD0( egraph );
      if( graph.getNodeCount() > 5000 ) { pd.sortByOppositePersistence(); }
      else{
        pd.calculateMSTSets();
        pd.sortByPersistenceAndSplitRatio();
      }
    int endM = millis();
    println( "PH Calculation Took: " + (endM-startM) + " ms" );
    
    minDegree = Integer.MAX_VALUE;
    maxDegree = -Integer.MAX_VALUE;
    for( Graph.Vertex v : graph.getVertices() ){
      int temp = v.getAdjacentCount();
      minDegree = min(minDegree, temp);
      maxDegree = max(maxDegree, temp);
    }
    
    
    setupControls();
  }
  
  
  public void draw(){}
  
  public void setupControls() {
    
    
    cp5 = new ControlP5(papplet).setColorForeground(color(75,75,100)).setColorBackground(color(100)).setColorActive(color(50,50,50));
    cp5.addTextlabel("Standard Force Label").setText("Standard Force Parameters").setPosition(u0+10,v0+10).setColorValue(color(0,0,0));
    cp5.addSlider(this,"fdlTimestep").setValue(fdlTimestep).setRange(0.01, 2.0).setPosition(u0+10,v0+25).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Timestep");
    cp5.addSlider(this,"fdlRestingLength").setValue(fdlRestingLength).setRange(0.01, 10).setPosition(u0+10,v0+40).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Spring Length");
    cp5.addSlider(this,"fdlSpringConstant").setValue(fdlSpringConstant).setRange(0.01, 10).setPosition(u0+10,v0+55).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Spring Constant");
    cp5.addSlider(this,"fdlCoulombConstant").setValue(fdlCoulombConstant).setRange(0.01, 1000).setPosition(u0+10,v0+70).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Coulomb Constant");

    cp5.addTextlabel("0D Force Label").setText("0D PH Force Parameters").setPosition(u0+10,v0+90).setColorValue(color(0,0,0));
    cp5.addSlider(this,"fpfSpringLength").setValue(fpfSpringLength).setRange(0.01, 10).setPosition(u0+10,v0+105).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Spring Length");
    cp5.addSlider(this,"fpfSpringForce").setValue(fpfSpringForce).setRange(0.01, 20).setPosition(u0+10,v0+120).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Spring Constant");
    cp5.addSlider(this,"fpfCoulombConstant").setValue(fpfCoulombConstant).setRange(0.01, 10000).setPosition(u0+10,v0+135).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Coulomb Constant");

    cp5.addTextlabel("Drawing Label").setText("Drawing Parameters").setPosition(u0+10,v0+155).setColorValue(color(0,0,0));
    cp5.addSlider(this,"fdlPointScale").setValue(fdlPointScale).setRange(1, 20).setPosition(u0+10,v0+170).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Point Scale");
    cp5.addSlider(this,"fdlEdgeScale").setValue(fdlEdgeScale).setRange(0.01, 10).setPosition(u0+10,v0+185).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Edge Scale");
    cp5.addTextlabel("Colormap").setText("Colormap").setPosition(u0+10,v0+200).setColorValue(color(0,0,0));
    cp5.addRadioButton("switchColorMap").setSize(20,20).setPosition(u0+30,v0+215).setColorForeground(color(120)).setColorActive(color(0)).setColorLabel(color(0)).setItemsPerRow(2).setSpacingColumn(50)
         .addItem("Default",0).addItem("Science",1).addItem("Senate",2).addItem("By Degree",3).activate(curColorID);

    cp5.addTextlabel("Clustering").setText("Clustering").setPosition(u0+10,v0+265).setColorValue(color(0,0,0));
    cp5.addSlider(this,"clusterSpringLength").setValue(clusterSpringLength).setRange(10, 1000).setPosition(u0+10,v0+280).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Spring Length");

    cp5.addTextlabel("Jaccard").setText("Jaccard (chng req save/reload data)").setPosition(u0+10,v0+300).setColorValue(color(0,0,0));
    CheckBox cb = cp5.addCheckBox(this,"callbackJaccardEnable").setSize(20,20).setPosition(u0+30,v0+315).setColorForeground(color(120)).setColorActive(color(0)).setColorLabel(color(0)).addItem("Enable",0);
    if( unweighted ) cb.activate( 0 );
    cp5.addRadioButton("callbackSwitchEgo").setSize(20,20).setPosition(u0+30,v0+340).setColorForeground(color(120)).setColorActive(color(0)).setColorLabel(color(0)).setItemsPerRow(2).setSpacingColumn(55)
         .addItem("Ego Hops: 1",1).addItem("Ego Hops: 2",2).addItem("Ego Hops: 3",3).activate(unweightedEgoHops-1);


    cp5.addButton(this,"resetPositions").setPosition(u0+w/2-50,v0+390).setSize(100,20).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Reset Positions");///\.setValue(0)
    cp5.addButton(this,"saveJSON").setPosition(u0+w/2-50,v0+420).setSize(100,20).getCaptionLabel().setColor(color(0,0,0) ).toUpperCase(false).setText("Save Data");///.setValue(0)

    cp5.addTextlabel("visibility").setText("Controls can be shown/hidden by pressing 'c'").setPosition(u0+5,v0+450).setColorValue(color(0,0,0));
    
  }
  
  public int switchEgo = 0;
  
  void show(){ cp5.setVisible(true); }
  void hide(){ cp5.setVisible(false); }
  boolean isVisible(){ return cp5.isVisible(); }
  void toggleVisible(){ cp5.setVisible(!cp5.isVisible()); }
  void setVisible(boolean visible){ cp5.setVisible(visible); }


  public void switchColorMap( int a ){
    println("colmap: " + a );
    curColorID = a;
      colorByDegree = false;
      switch(a){
        case 0: curColor = defaultColor; break;
        case 1: curColor = scienceColor; break;
        case 2: curColor = senateColor;  break;
        case 3: colorByDegree = true; break;
      }
    }
    
    
    /*
    public void switchEgo( int a ){
      println("asdf");
      unweightedEgoHops = a;
    }
    
  void jaccardEnable(float[] a) {
    unweighted = (a[0]>0);
    println(a);
  }
      */
 
  public void saveJSON( ){
    
    println(switchEgo);
    
    for( int i = 0; i < curView.fdd.fdl.countVertex(); i++ ){
      GraphData.MVertex v = (GraphData.MVertex)graph.nodes.get(i);
      v.x = curView.fdd.fdl.getVertex(i).getPositionX();
      v.y = curView.fdd.fdl.getVertex(i).getPositionY(); 
    }
  
    // iterate through nodes
    JSONArray nodes = new JSONArray();
    for( int i = 0; i < graph.nodes.size(); i++ ){
      nodes.append( ((GraphData.MVertex)graph.nodes.get(i)).jsonSerialize() );
    }
    
    // iterate through edges
    JSONArray edges = new JSONArray();
    for( int i = 0; i < graph.edges.size(); i++ ){
      edges.append( ((GraphData.MEdge)graph.edges.get(i)).jsonSerialize() );
    }
    
    // write groups
    JSONArray groups = new JSONArray();
    for( int id : catLabels.keySet() ){
      JSONObject group = new JSONObject();
      group.setInt( "id", id );
      group.setString( "label", catLabels.get(id) );
      groups.append(group);
    }
    
    // write parameters
    JSONObject params = new JSONObject();
    params.setBoolean("unweighted", unweighted);
    params.setInt("unweightedEgoHops", unweightedEgoHops);
    params.setFloat("fdlRestingLength", fdlRestingLength);
    params.setFloat("fdlPointScale", fdlPointScale);
    params.setFloat("fdlTimestep", fdlTimestep);
    params.setFloat("fdlEdgeScale", fdlEdgeScale);
    params.setFloat("fdlSpringConstant", fdlSpringConstant);
    params.setFloat("fdlCoulombConstant", fdlCoulombConstant);
    params.setFloat("fpfSpringLength", fpfSpringLength);
    params.setFloat("fpfSpringForce", fpfSpringForce);
    params.setFloat("fpfCoulombConstant", fpfCoulombConstant);
    if( curColor == defaultColor ) params.setFloat("colormap", 0);
    if( curColor == scienceColor ) params.setFloat("colormap", 1);
    if( curColor == senateColor  ) params.setFloat("colormap", 2);
    if( colorByDegree  ) params.setFloat("colormap", 3);
    params.setFloat("fdlCurCenX", curCenX );
    params.setFloat("fdlCurCenY", curCenY );
    params.setFloat("fdlScale", scale );
    params.setBoolean("paused", paused );
    params.setFloat("clusterSpringLength", clusterSpringLength);
    
    JSONObject json = new JSONObject();
    json.setJSONArray( "nodes", nodes );
    json.setJSONArray( "links", edges );
    json.setJSONArray( "groups", groups );
    json.setJSONObject( "params", params );
    json.setJSONObject( "barcode", curView.bc0.toJSON() );
    
    
    PrintWriter output = createWriter( papplet.dataFile( loadFile ) );
    output.print( json );
    output.close();
    
    println("Wrote to JSON file successfully to \"" + papplet.dataFile( loadFile ) + "\"");
  }  
  
  class MVertex extends Graph.Vertex implements MetricSpaceNode {
      String name;
      int grp;
      float x=Float.NaN, y=Float.NaN;
      
      MVertex( JSONObject obj ){
        name = obj.getString("id");
        grp  = obj.getInt("group");
        if( obj.hasKey("x") ) x = obj.getFloat("x");
        if( obj.hasKey("y") ) y = obj.getFloat("y");
        nodeNames.put( name, this );
      }

      float distance( MetricSpaceNode o ){
        for(int i = 0; i < getAdjacentCount(); i++ ){
          if( getAdjacentAt(i) == o ){ return (float)getAdjacentWeight(i); }
        }
        return Float.POSITIVE_INFINITY;
      }
      
      JSONObject jsonSerialize( ){
        JSONObject ret = new JSONObject();
        ret.put("id",name);
        ret.put("group",grp);
        ret.put("x",x);
        ret.put("y",y);
        return ret;
      }
    }

    class MEdge extends Graph.Edge {
      String src, tgt;
      float drawW;
      
      MEdge( JSONObject obj ){
        // checks if graph is weighted or unweighted
        if( obj.hasKey("value") ){
          drawW = obj.getFloat("value");
          w = 1.0f/(float)obj.getFloat("value");
          //w = (float)obj.getFloat("value");
        }
        else {
          drawW = 1; 
          w = 1; // placeholder for weight 
          unweighted = true;
        }
        //drawW = obj.getFloat("value");
        //w = (float)obj.getFloat("value");
        src = obj.getString("source");
        tgt = obj.getString("target");
        if( w == 0 ) w = 0.00001f;
        v0 = nodeNames.get( src );
        v1 = nodeNames.get( tgt );
      }  
      
      JSONObject jsonSerialize( ){
        JSONObject ret = new JSONObject();
        ret.put("source",src);
        ret.put("target",tgt);
        if( Double.isInfinite(drawW) )
          ret.put("value",1.0f/0.00001f);
        else
          ret.put("value",drawW);
        return ret;
      }      
    }
    
}


  public void switchColorMap( int a ){ data.switchColorMap(a); }
    
    
public void callbackSwitchEgo( int a ){
  data.unweightedEgoHops = a;
  println(a);
}
    
    
void callbackJaccardEnable(float[] a) {
    data.unweighted = (a[0]>0);
    println(a);
}
  
  
