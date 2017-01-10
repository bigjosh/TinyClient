package com.josh.tiny;

// Implements a lookup table for translating local keys into DOS key codes


import java.io.*;
import java.util.*;



public class KeyLookupTable
{
	
	// Allows a custom lookup table of codes, mostly to handle forgein keyboards
	
	public static class CustomKey {
		
		public String name;
		
		int modifiers;
		int key;
		
		public int doscode;
		
		CustomKey( String name , int modifiers , int key , int doscode ) {
			
			this.name = name;
			
			this.modifiers = modifiers;
			this.key = key;
			
			this.doscode = doscode;
			
			
		//	System.out.println( "Customkey "+name + " "+modifiers+" " +key+ " "+ doscode );
		}
	}
	
	
	public static CustomKey[] customKeys = null;
	
	public static int customTableLookup( int modifiers , int key ) {
		
		if (customKeys == null ) {
			
			return -1;
			
		}
		
		for( CustomKey k : customKeys ) {
			
			if ( k.key == key && k.modifiers == modifiers ) {
				
				return( k.doscode );
				
			}
			
		}
		
		return( -1 ) ;
		
	}
	
	
	static public class KeyboardTableParsingException extends Exception {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -5278211961413882506L;
		
		String string;
		
		KeyboardTableParsingException( String error , String filename , int line ) {
			
			string = "KeyboardTableParsingException: "+error+" in file "+filename+" line #"+line;
			
		}
		
		public String toString() {
			
			return string;
		}
		
	}
	
	public static synchronized int readTranslationTable( String filename ) throws FileNotFoundException, IOException, KeyLookupTable.KeyboardTableParsingException {
		
		BufferedReader i = new BufferedReader( new FileReader( filename ));
		
		StreamTokenizer st = new StreamTokenizer(  i );
		
		java.util.Vector<CustomKey> keys = new Vector<CustomKey>();
		
		
		if (customKeys != null ) {			// Preserve any that were already loaded
			
			for( CustomKey c : customKeys ) {
				
				keys.add( c );
				
			}
		}
		
		st.slashSlashComments( true );
		st.lowerCaseMode( false );
		st.eolIsSignificant( true );
		
		st.parseNumbers();
		
		int count = 0;
		
		int ttype;
		
		while ( (ttype = st.nextToken()) != StreamTokenizer.TT_EOF ) {
			
			if (ttype == StreamTokenizer.TT_EOL) {			// Blank line
				
				continue;
			}
			
			if ( ttype != '\"' ) {
				
				throw new KeyboardTableParsingException( "key name expected", filename , st.lineno() );
				
			}
			
			String name = st.sval;
			
			
			ttype = st.nextToken();
			
			if ( ttype != StreamTokenizer.TT_NUMBER ) {
				
				throw new KeyboardTableParsingException( "modifiers expected", filename , st.lineno() );
				
			}
			
			
			int modifiers = (int) st.nval;
			
			ttype = st.nextToken();
			
			if ( ttype != StreamTokenizer.TT_NUMBER ) {
				
				throw new KeyboardTableParsingException( "key expected", filename , st.lineno() );
				
			}
			
			
			int key = (int) st.nval;
			
			
			ttype = st.nextToken();
			
			if ( ttype != StreamTokenizer.TT_NUMBER ) {
				
				throw new KeyboardTableParsingException( "doscode expected", filename , st.lineno() );
				
			}
			
			
			int doscode = (int) st.nval;
			
			
			ttype = st.nextToken();
			
			if ( ttype != StreamTokenizer.TT_EOL ) {
				
				throw new KeyboardTableParsingException( "End of line expected", filename , st.lineno() );
				
			}
			
			
			CustomKey c = new CustomKey( name , modifiers , key , doscode );
			
			keys.add( c );
			
			count++;
			
			
		} while ( ttype != StreamTokenizer.TT_EOF );
		
				
		customKeys = keys.toArray( new CustomKey[ keys.size()  ]);
						
		i.close();
		
		return count;
		
	}
	
	
}
