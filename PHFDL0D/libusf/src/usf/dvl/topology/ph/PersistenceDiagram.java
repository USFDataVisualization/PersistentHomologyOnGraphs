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

package usf.dvl.topology.ph;

import java.util.Comparator;
import java.util.Vector;


public class PersistenceDiagram<FeatureType extends PersistenceFeature> {

	protected Vector<FeatureType> features = new Vector<FeatureType>();

	public PersistenceDiagram( ){ }

	public int size(){
		return features.size();
	}
	public float getBirth(int i){ return (float)features.get(i).getBirth(); }
	public float getDeath(int i){ return (float)features.get(i).getDeath(); }
	public float getPersistence(int i){ return getDeath(i)-getBirth(i); }
	public float getMaximum(){
		float ret = 0;
		for( int i = 0; i < size(); i++ ){
			if( Float.isFinite(getDeath(i)) )
				ret = Math.max(ret,getDeath(i));
		}
		return ret;
	}


	public void add( FeatureType p ){
		features.add(p);
	}
	public PersistenceFeature get( int i ){
		return features.get(i);
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer( );
		for(int i = 0; i < features.size(); i++){
			sb.append( features.get(i) );
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public String toPointString(){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < features.size(); i++){
			FeatureType p = features.get(i);
			sb.append( p.birth + " " );
			sb.append( (p.death==Double.POSITIVE_INFINITY?"99999999":p.death) );
			sb.append( "\n" );
		}
		return sb.toString();
	}

	public PersistenceDiagram<FeatureType> getByDimension( int dim ){
		PersistenceDiagram<FeatureType> pd = new PersistenceDiagram<FeatureType>( );
		for( FeatureType p : features ){
			if( p.dim == dim )
				pd.add( p );
		}		
		return pd;
	}

	public void sortByBirth( ){
		features.sort( new Comparator<PersistenceFeature>(){
			@Override public int compare(PersistenceFeature o1, PersistenceFeature o2) {
				if( o1.birth < o2.birth ) return -1;
				if( o1.birth > o2.birth ) return  1;
				if( o1.death < o2.death ) return -1;
				if( o1.death > o2.death ) return  1;
				return 0;
			}
		});
	}

	public void sortByDeath( ){
		features.sort( new Comparator<PersistenceFeature>(){
			@Override public int compare(PersistenceFeature o1, PersistenceFeature o2) {
				if( o1.death < o2.death ) return -1;
				if( o1.death > o2.death ) return  1;
				if( o1.birth < o2.birth ) return -1;
				if( o1.birth > o2.birth ) return  1;
				return 0;
			}
		});
	}

	public void sortByPersistence( ){
		features.sort( new Comparator<PersistenceFeature>(){
			@Override public int compare(PersistenceFeature o1, PersistenceFeature o2) {
				if( o1.getPersistence() < o2.getPersistence() ) return -1;
				if( o1.getPersistence() > o2.getPersistence() ) return  1;
				return 0;
			}
		});
	}

	public void sortByOppositePersistence( ) {
		features.sort( new Comparator<PersistenceFeature>(){
			@Override public int compare(PersistenceFeature o1, PersistenceFeature o2) {
				if( o1.getPersistence() < o2.getPersistence() ) return 1;
				if( o1.getPersistence() > o2.getPersistence() ) return -1;
				return 0;
			}
		});
	}


}
