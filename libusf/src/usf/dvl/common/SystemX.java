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

package usf.dvl.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;


public class SystemX {

	public static String [] readFileContents( BufferedReader reader ) throws IOException {
		Vector<String> ret = new Vector<String>();
		 String line;
		 while( ( line = reader.readLine() ) != null ) {
	            ret.add(line);
	        }
		 reader.close();
		 return ret.toArray( new String[ret.size()] );
	}	


	public static String [] readFileContents( String filename ) throws IOException {
		return readFileContents( new BufferedReader(new FileReader( filename )) );
	}

	public static String [] readFileContents( URL filename ) throws IOException {
		return readFileContents( new BufferedReader( new InputStreamReader( filename.openStream() ) ) );
	}

	public static String [] readFileContents(File file) throws IOException {
		return readFileContents( new BufferedReader(new FileReader( file )) );
	}	
	
	public static String [] readFileContents(InputStream input) throws IOException {
		return SystemX.readFileContents( new BufferedReader(new InputStreamReader(input) ) );
	}
	
	
	public static boolean fileExists( String filename ) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader( filename ));
			 reader.close();
	    } catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return true;
	}
	

}