package com.josh.tiny;
/**
 * VgaFontGenerator.java
 *
 * Reads in a VGA.com file and generates java code for it.
 */


import java.io.*;


public class VgaFontGenerator
{
	
	public static void main( String args[] ) throws Exception {
		
		byte[] inLine = new byte[1];		// One line of pixels

		StringBuffer outLine = new StringBuffer(8);
		
		RandomAccessFile f = new RandomAccessFile( args[0] , "r" );
				
		f.seek( 0x163 );		// Jump to the start of the font data
		
		for( int c = 0 ; c < 256 ; c++ ) {
			
			System.out.println( "    {");
					
			for( int y=0; y < 16 ; y++ ) {
				
				f.read( inLine );
	
				outLine.setLength(0);
				
				for( int x = 0; x < 8 ; x++ ) {
					
					int b = inLine[0];		// The byte
					
					int m = 128 >> x ;		// The mask
					
					if ( (b & m) != 0 ) {
						
						outLine.append('X');
						
					} else {
						
						outLine.append(' ');
						
					}
					
				}
				
				System.out.println( "        \""+outLine+"\"," );
				
			}
			
			System.out.println("    },");
			
		}
		
		f.close();
				
	}
	
}

