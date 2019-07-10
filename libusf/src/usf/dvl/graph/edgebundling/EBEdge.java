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

package usf.dvl.graph.edgebundling;

import java.util.ArrayList;

 public class EBEdge {

     private ArrayList<EBNode> subNodes;
     private ArrayList<EBNode> tempSubNodes;
     private EBNode endNode;
     private EBNode startNode;

     public EBEdge(EBNode startNode, EBNode endNode){
         if(startNode.getX() <= endNode.getX()){
           this.startNode = startNode;
             this.endNode = endNode;
         } else{
             this.endNode = startNode;
             this.startNode = endNode;
         }
     }

     public EBNode getEndNode() {
         return endNode;
     }

     public void setEndNode(EBNode endNode) {
         this.endNode = endNode;
     }

     public EBNode getStartNode() {
         return startNode;
     }

     public void setStartNode(EBNode startNode) {
         this.startNode = startNode;
     }

     public ArrayList<EBNode> getSubNodes(){
         return subNodes;
     }

     public ArrayList<EBNode> getTempSubNodes(){
         return tempSubNodes;
     }

     public void setTempSubNodes(ArrayList<EBNode> subNodes){
         tempSubNodes = subNodes;
     }

     public boolean isIncident(EBNode node){
         return (node.equals(startNode) || node.equals(endNode));
     }

     public double dx(){
         return endNode.getX()-startNode.getX();
     }

     public double dy(){
         return endNode.getY()-startNode.getY();
     }

     public double compatibility(EBEdge other){
         return angleComp(other)*posComp(other)*scaleComp(other)*visComp(other);
     }

     public double magnitude(){
         return Math.sqrt(dx()*dx()+dy()*dy());
     }

     public double visibility(EBEdge other){
         EBNode ds = new EBNode(other.getStartNode().getX()+dy(), other.getStartNode().getY()-dx());
         EBNode de = new EBNode(other.getEndNode().getX()+dy(), other.getEndNode().getY()-dx());
         EBNode is = lineIntersection(new EBEdge(other.getStartNode(), ds));
         EBNode ie = lineIntersection(new EBEdge(other.getEndNode(), de));
         EBNode im = new EBEdge(is, ie).midPoint();
         if(is == null || im == null || ie == null)
             return 0;
         return Math.max(0, 1-(2*midPoint().distance(im))/is.distance(ie));
     }

     public int[] subNodesXInt(){
         int[] subX = new int[subNodes.size()];
         for(int i=0; i < subNodes.size(); i++){
             subX[i] = subNodes.get(i).getXInt();
         }
         return subX;
     }

     public int[] subNodesYInt(){
         int[] subY = new int[subNodes.size()];
         for(int i=0; i < subNodes.size(); i++){
             subY[i] = subNodes.get(i).getYInt();
         }
         return subY;
     }

     public EBNode midPoint(){
         double x = (startNode.getX()+endNode.getX())/2;
         double y = (startNode.getY()+endNode.getY())/2;
         return new EBNode(x,y);
     }

     public void deleteSubNodes(){
         subNodes = null;
     }

     public void finalizeSubNodes(){
         subNodes = tempSubNodes;
         tempSubNodes = null;
       }

     public void increaseSubNodes(){
         if (subNodes == null){
             subNodes = new ArrayList<EBNode>();
             subNodes.add(startNode);
             subNodes.add(midPoint());
             subNodes.add(endNode);
         } else{
             ArrayList<EBNode> tempList = new ArrayList<EBNode>();
             for(int i=0; i < subNodes.size()-1; i++){
                 tempList.add(subNodes.get(i));
                 tempList.add(new EBNode((subNodes.get(i).getX()+subNodes.get(i+1).getX())/2,
                         (subNodes.get(i).getY()+subNodes.get(i+1).getY())/2));
             }
             tempList.add(endNode);
             subNodes = tempList;
         }
     }

     private double angleComp(EBEdge other){
         double angle = Math.cos(Math.acos(Math.abs((dx()*other.dx()+dy()*other.dy())
                 / (magnitude()*other.magnitude()))));
         return angle;
     }

     private double posComp(EBEdge other){
         EBEdge bridge = new EBEdge(midPoint(), other.midPoint());
         double lavg = (magnitude()+other.magnitude())/2;
         double pos = lavg/(lavg+bridge.magnitude());
         return pos;
     }

     private double scaleComp(EBEdge other){
         double lavg = (magnitude()+other.magnitude())/2;
         double scale = 2/(lavg/Math.min(magnitude(), other.magnitude())
                 + Math.max(magnitude(), other.magnitude())/lavg);
         return scale;
     }

     private double visComp(EBEdge other){
         return Math.min(visibility(other), other.visibility(this));
     }

     private EBNode lineIntersection(EBEdge other){
         double x1 = startNode.getX();
         double y1 = startNode.getY();
         double x2 = endNode.getX();
         double y2 = endNode.getY();
         double x3 = other.getStartNode().getX();
         double y3 = other.getStartNode().getY();
         double x4 = other.getEndNode().getX();
         double y4 = other.getEndNode().getY();

         if((x1-x2)*(y3-y4)-(y1-y2)*(x3-x4) == 0)
             return null;
         double px = ((x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4))/((x1-x2)*(y3-y4)-(y1-y2)*(x3-x4));
         double py = ((x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4))/((x1-x2)*(y3-y4)-(y1-y2)*(x3-x4));
         return new EBNode(px, py);
     }
     
     
     public EBNode getClosestNode( EBNode s ){
         EBNode t = null;
         double nDist = Double.POSITIVE_INFINITY;
         for(EBNode u : getSubNodes()){
             if(s.distance(u) < nDist){
                 nDist = s.distance(u);
                 t = u;
             }
         }
         return t;
     }
     
}
