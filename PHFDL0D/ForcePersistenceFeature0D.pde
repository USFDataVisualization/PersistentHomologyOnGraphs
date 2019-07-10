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

enum ForcePersistenceFeature0DMode { 
  DISABLED, ATTRACT, REPEL
};

public class ForcePersistenceFeature0D {

  ForcePersistenceFeature0DMode curMode = ForcePersistenceFeature0DMode.DISABLED;
  ForceDirectedLayout fdl;
  PersistenceFeature0DAttraction pattr;
  PersistenceFeature0DRepulsion  prep;
  PersistenceFeature0D pd;
  

  public ForcePersistenceFeature0D(ForceDirectedLayout _fdl, FDLVertex _n0, FDLVertex _n1, PersistenceFeature0D _pd ) {
    fdl = _fdl;
    pd = _pd;
    pattr = new PersistenceFeature0DAttraction(fdl, _n0, _n1);
    prep = new PersistenceFeature0DRepulsion(_n0, _n1);
  }
  
  HashSet<Integer> getSetA(){
    return pd.getHashSetA();
  }
  HashSet<Integer> getSetB(){
    return pd.getHashSetB();
  }


  void disable() {       curMode = ForcePersistenceFeature0DMode.DISABLED; pattr.disable(); prep.disable(); }
  void enableAttract() { curMode = ForcePersistenceFeature0DMode.ATTRACT;  pattr.enable();  prep.disable(); }
  void enableRepel() {   curMode = ForcePersistenceFeature0DMode.REPEL;    pattr.disable(); prep.enable();  }

  public void update() { pattr.update(); prep.update(); }


  public class PersistenceFeature0DAttraction extends PairwiseForce.SpringAttractiveForces {

    boolean enabled = false;
    PersistenceFeature0DAttraction(ForceDirectedLayout _fdl, FDLVertex _n0, FDLVertex _n1) {
      super(_fdl, false);
      add( new SpringAttractiveForce(_n0, _n1 ) );
    }

    public void enable( ) {
      if ( enabled ) return; 
      fdl.addForces( this );
      enabled = true;
      
    }
    
    public void update( ){
      if( enabled ){
        springConstant = data.fpfSpringForce;
        springLength   = data.fpfSpringLength;
      }
    }

    public void disable() {
      if ( !enabled ) return;
      fdl.removeForces( this );
      enabled = false;
    }
  }


  @SuppressWarnings("serial")
    public class PersistenceFeature0DRepulsion extends ArrayList<BasicForce> {

    FDLVertex n0, n1;
    BarnesHutApproximation bhA, bhB;
    boolean enabled = false;
    HashSet<Integer> A, B;
    
    public PersistenceFeature0DRepulsion(FDLVertex _n0, FDLVertex _n1 ) {
      n0 = _n0;
      n1 = _n1;
    }

    public void enable( ) {
      if ( enabled ) return;
      if ( size() < fdl.getVertices().size() ) {
        for ( FDLVertex v : fdl.getVertices() ) {
          add( new PF0DForce( v ) );
        }
      }
      fdl.addForces( this );
      enabled = true;
    }

    public void disable() {
      if ( !enabled ) return;
      fdl.removeForces( this );
      enabled = false;
    }

    public void update( ) {
      if ( enabled ) {
        ArrayList<FDLVertex> vgA = new ArrayList<FDLVertex>( );
        ArrayList<FDLVertex> vgB = new ArrayList<FDLVertex>( );

        A = pd.getHashSetA();
        B = pd.getHashSetB();

        for ( FDLVertex v : fdl.getVertices() ) { 
          if ( A.contains(v.getID()) ) vgB.add(v);
          if ( B.contains(v.getID()) ) vgA.add(v);
        }

        bhA = new BarnesHutApproximation(vgA);
        bhB = new BarnesHutApproximation(vgB);
      }
    }

    class PF0DForce extends PerNodeForce {
      PF0DForce( FDLVertex _t ) {
        super(_t);
      }
      public void updateForce() {
        force[0] = 0.0f;
        force[1] = 0.0f;

        if ( bhA == null || bhB == null ) return;

        int targetID = target.getID();  

        ArrayList<FDLVertex> g = null;
        if ( A.contains(targetID) ) g = bhA.getNodes(target);
        if ( B.contains(targetID) ) g = bhB.getNodes(target);

        if ( g == null ) { println( "null" ); return; }

        for (FDLVertex node : g) { 
          float dx = target.getPositionX() - node.getPositionX();
          float dy = target.getPositionY() - node.getPositionY();
          float rSquared = dx * dx + dy * dy + 0.0001f; //to avoid zero deviation
          force[0] += data.fpfCoulombConstant * dx / rSquared * node.superNode;
          force[1] += data.fpfCoulombConstant * dy / rSquared * node.superNode;
        }
      }
    }
  }
}
