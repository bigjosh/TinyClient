package com.josh.tiny;

import java.awt.image.*;

public class DosScreenVESA103Nibble extends DosScreen
{
	
	private String name;
	
	private String shortName;
	
	private BufferedImage bufferedImage;
	
	public BufferedImage getBufferedImage()
	{
		return bufferedImage;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getShortName() {
		return shortName;
	}	
	
	static int vga16Colors[] = {		// RGB values for the first 16 VBGA colors
		
		0x000000,
		0x000080,
		0x008000,
		0x008080,
		0x800000,
		0x800080,
		0x808000,
		0xC0C0C0,
		0x808080,
		0x0000FF,
		0x00FF00,
		0x00FFFF,
		0xFF0000,
		0xFF00FF,
		0xFFFF00,
		0xFFFFFF

	};

	
	static int vga4Colors[] = {		// RGB values for four very different colors
		
		0x000000,
		0x0000FF,
		0x00FF00,
		0xFF0000,
		0xFFFFFF
		
	};
	
	
	public int signatureSize; 			// The size of a buffer for this screen type
	
	final int sizeX=800;				// Number of pixels in x direction
	final int sizeY=600;
	final int slices=16;				// The screen is divided into small slices for transmission
	
	final int banks=8;
	
	final int sliceSize=2048;				// The number of bytes in each slice
			
	int viewRequestCode;
		
	public DosScreenVESA103Nibble( int viewRequestCode  ) {

		
		this.viewRequestCode = viewRequestCode;
					
		//What should be displayed for this window based on the last update
		bufferedImage = new BufferedImage( sizeX  , sizeY , BufferedImage.TYPE_INT_RGB );
				
		name = "VGA VESA 800x600x256 4-bit Compacted " + banks +"/"+ slices ;
		
		shortName = "VGA 800x600x256 4-bits" ;
		
		signatureSize = sliceSize + 2;		// final 2 bytes indicating the bank and slice indexes
		
	}
		 
	
	// High initial values for these will cause them to always wrap to zero on the first request and
	// then start scanning at the top of the screen.
	
	int lastSlice 	 = 255; 					//Last slice recieved
	int lastBank	 = 255;						//bank of last packet recieved
	
	public int getLastBitPlane() {
		return 0;
	}
	
	public int getLastSlice() {
		return lastSlice;
	}
		
		
	public boolean update( byte[] data  , int len ) {
		
		int bank     = data[ sliceSize + 0 ] & 0xff ;
		int slice    = data[ sliceSize + 1 ] & 0xff ;
		
//		System.out.println("Response bp:"+bank+" sl:"+slice);
		
		if ( slice >= this.slices) {
			
			System.out.println("Recieved a corrupted frame: slice="+slice +" bank="+bank+" ["+name+"]"  );
			return false;
			
		}
		
		lastBank     = bank;  // Save these values so the next request will have them
		lastSlice    = slice;
				
//		System.out.println("Vga16Packet bitplane="+bitplane+" slice="+slice + " check="+ check );
		
		// Find the offset from the upper left corner of the screen for the first recieved pixel 
		
		int offset = ((bank * (slices*sliceSize) ) + (slice * sliceSize) ) * 2;  
		
		int x;		// Place in local image
		int y;  	// Place in local image
				
		y = (offset /  sizeX);
		
		x = offset - ( y * sizeX );
		
		if (x>=sizeX || y >= sizeY ) {
			
			System.out.println("Recieved an corrupted frame: x="+x +" y="+y+" ["+name+"]"  );
			return false;
			
		}
		
				
		synchronized (bufferedImage) {
					
			for( int i = 0 ; i < sliceSize ; i++ ) {	// loop through bytes in received packet
				
				int packedByte = data[i];
				
				//System.out.println( "PackedByte=" + Integer.toString( packedByte , 16 )+ "high nibble=" +(packedByte>>4)); 
				
				bufferedImage.setRGB( x , y , vga16Colors[ packedByte & 0x0F ] );
				
				x++;
				
				// No need to check here because a slice will never end on a nibble boundary 
				
				bufferedImage.setRGB( x , y , vga16Colors[ ( packedByte & 0xF0 ) >> 4 ] );
				
				x++;
				
				if ( x>= sizeX ) {
					
					x = 0;
					
					y++;
					
					if (y>=sizeY) {
						
						break;
						
					}
					
				}
				
			}
			
		}
					
		
		return( true );								
			
	}
	
	public int getSignatureDataLength()
	{
		return( signatureSize );		// 2 bytes per char (ascii code + color) + 1 byte for cursor x + 1 byte for cursor y
	}


	void addScanSystems( ScreenRequestSenderList.ScreenRequestSenderNodeBranch node , int mode ) {
			
		node.addLeaf("Progressive" , new ScreenRequestSender( 'W' , mode , 0x00 , 0 ) );
		node.addLeaf("Interlaced"  , new ScreenRequestSender( 'W' , mode , 0x01 , 0 ) );
		
	}
	
	void addScreenRequestSenders(ScreenRequestSenderList.ScreenRequestSenderNodeBranch node)
	{
		ScreenRequestSenderList.ScreenRequestSenderNodeBranch subNode = node.addBranch( name );
		
		addScanSystems( subNode , viewRequestCode );
		
	}
	
	
}
