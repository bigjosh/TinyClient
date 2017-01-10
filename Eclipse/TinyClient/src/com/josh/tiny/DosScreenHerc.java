package com.josh.tiny;

import java.awt.image.*;


public class DosScreenHerc extends DosScreen
{
	
	
	private String name;
	
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
		return name;
	}	
	
	
	public int signatureSize; 			// The size of a buffer for this screen type
	
	final int sizeX;					// Number of pixels in x direction
	final int sizeY;
	final int bitplanes;				// These are the VGA bitplanes that represent color
	final int slices;					// The screen is divided into small slices for transmition
	
	final int sliceSize;				// The number of bytes in each slice
	
	final int bytesPerLine; 			// Number of bytes in a horazontal row of pixels
	final int linesPerSlice; 			// Number of horazontal lines of pixels in each slice
		
	int viewRequestCode;
	
	public DosScreenHerc( int  width, int  height, int bitplanes, int slices , int viewRequestCode  ) {
		
		sizeX 		   = width;
		sizeY 		   = height;
		this.bitplanes = bitplanes;
		this.slices    = slices;
		
		this.sliceSize = (sizeX /8 )*(sizeY/bitplanes/slices) ;
		
		this.bytesPerLine = sizeX / 8;
		
		this.linesPerSlice = ( sliceSize / bytesPerLine  );
		
		
		this.viewRequestCode = viewRequestCode;
		
		//What should be displayed for this window based on the last update
		bufferedImage = new BufferedImage( sizeX  , sizeY , BufferedImage.TYPE_BYTE_BINARY );
		
		name = "Herc "+width+"x"+height+"x" +bitplanes +"/"+ slices ;
		
		signatureSize = sliceSize + 2;		// 8 pixels per byte plus a final 2 bytes indicating the bitplane and slice indexes
		
	}
	

	static final int RGB_BLACK = 0xffffff;
	static final int RGB_WHITE = 0x000000;
		
	// High initial values for these will cause them to always wrap to zero on the first request and
	// then start scanning at the top of the screen.
	
	int lastBitPlane = 255; 					//Last bitplane recieved
	int lastSlice 	 = 255; 					//Last slice recieved
	
	public int getLastBitPlane() {
		return lastBitPlane;
	}
	
	public int getLastSlice() {
		return lastSlice;
	}
	
	
	
	public boolean update( byte[] data  , int len ) {
		
		int bitplane = data[ sliceSize + 0 ] & 0xff ;
		int slice    = data[ sliceSize + 1 ] & 0xff ;
		
//		System.out.println("Response bp:"+bitplane+" sl:"+slice);
		
		if ( bitplane >= this.bitplanes || slice >= this.slices) {
			
			System.out.println("Recieved a corrupted frame: slice="+slice +" bitplane="+bitplane+" ["+name+"]"  );
			return false;
			
		}
		
		lastBitPlane = bitplane;// Save these values so the next request will have them
		lastSlice    = slice;
		
		
		int x;		// Place in local image
		int y;  	// Place in local image
							
		y =  (slice * (linesPerSlice * bitplanes ) ) + bitplane;

		int index = 0 ; // location inside recieved buffer
				
		for( int line = 0 ; line < linesPerSlice ; line++ ){
			
			x=0;
			
			for( int byteInLine =0; byteInLine < bytesPerLine; byteInLine++ ) {
			
				int mask = 0x80;
				
				for( int bit = 0 ; bit < 8 ; bit++ ) {
					
					if ( (data[index] & mask )== 0  ) { 		// Pixel clear
						
						bufferedImage.setRGB( x ,y , RGB_WHITE );
						
					} else { 								// Pixel set

						bufferedImage.setRGB( x ,y , RGB_BLACK );
						
					}
					
					x++;
					mask >>= 1; 		// Roate bit mask down
					
				}
				
				index++;
				
			}
			
			y += 4; //Herc grphics memory is interleaved 4 lines at a time
			
		}
		
		
		return( true );
				
	}
	
	public int getSignatureDataLength()
	{
		return( signatureSize );		// 2 bytes per char (ascii code + color) + 1 byte for cursor x + 1 byte for cursor y
	}
	
	
	void addScanSystems( ScreenRequestSenderList.ScreenRequestSenderNodeBranch node ,  int mode , int page ) {
		
		node.addLeaf("Progressive" , new ScreenRequestSender( 'W' , mode , 0x00 , page) );
		node.addLeaf("Interlaced"  , new ScreenRequestSender( 'W' , mode , 0x01 , page ) );
		
	}
	
	void addScreenRequestSenders(ScreenRequestSenderList.ScreenRequestSenderNodeBranch node)
	{

		ScreenRequestSenderList.ScreenRequestSenderNodeBranch subNode = node.addBranch( name );
		
		for(int page=0; page <2 ; page++) {

			ScreenRequestSenderList.ScreenRequestSenderNodeBranch subsubNode = subNode.addBranch( "Page #"+page );
			
			addScanSystems( subsubNode  , viewRequestCode , page );
		
		}
		
	}
	
	
}
