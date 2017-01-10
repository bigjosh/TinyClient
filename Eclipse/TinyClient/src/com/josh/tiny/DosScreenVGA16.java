package com.josh.tiny;

import java.awt.image.*;

public class DosScreenVGA16 extends DosScreen
{
	

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

	public int signatureSize; 			// The size of a buffer for this screen type
	
	final int sizeX;					// Number of pixels in x direction
	final int sizeY;
	final int bitplanes;				// These are the VGA bitplanes that represent color
	final int slices;					// The screen is divided into small slices for transmition
	
	final int sliceSize;				// The number of bytes in each slice
	
	boolean pixelBuffer[][][];		// What is in each bitplane
	int rgbBuffer[][];				// RGB values
	
	int viewRequestCode;
		
	public DosScreenVGA16( int  width, int  height, int bitplanes, int slices , int viewRequestCode  ) {

		sizeX 		   = width;
		sizeY 		   = height;
		this.bitplanes = bitplanes;
		this.slices    = slices;
		
		this.sliceSize = ( sizeX * sizeY ) / 8 / slices;
		
		this.viewRequestCode = viewRequestCode;
		
		pixelBuffer = new boolean[width][height][bitplanes];
		rgbBuffer = new int[width][height];
			
		//What should be displayed for this window based on the last update
		bufferedImage = new BufferedImage( sizeX  , sizeY , BufferedImage.TYPE_INT_RGB );
				
		name = "VGA "+width+"x"+height+"x" +bitplanes +"/"+ slices ;
		shortName = "VGA "+width+"x"+height+"x"+bitplanes;

		
		signatureSize = ( (height * width) /8 / slices ) + 2;		// 8 pixels per byte plus a final 2 bytes indicating the bitplane and slice indexes
		
	}
	
	// DisplayBuffer is organized like a VGA text mode screen with 1 byte for ASCII and one byte for color
	
	/*
	 
	 @return true if there was a change to the displayed image
	 
	 */
	
	private void setPixelRGB( int x , int y ) {
		
		int rgb = 0;
		
		for( int bitplane = 0 ; bitplane < bitplanes ; bitplane++ ) {

			rgb >>= 1;
			
			if (pixelBuffer[x][y][bitplane]) {
				
				rgb |= 0x08;
			}
			
		}
		
		rgbBuffer[x][y] = vga16Colors[rgb];
		
	}
	
	
	private void updateBufferedImage() {		// Update the bufferedImage to match the RGBbuffer
		
		synchronized (bufferedImage) {
							
			for( int x = 0 ; x < sizeX ; x++ ) {
				
				for( int y = 0 ; y < sizeY ; y++ ) {
									
					bufferedImage.setRGB( x , y , rgbBuffer[x][y] );
					
				}
				
			}
					
		}
		
	}
	
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
		
	//	System.out.println("Response bp:"+bitplane+" sl:"+slice);
		
		if ( bitplane >= this.bitplanes || slice >= this.slices) {
			
			System.out.println("Recieved a corrupted frame: slice="+slice +" bitplane="+bitplane+" ["+name+"]"  );
			return false;
			
		}
		
		lastBitPlane = bitplane;// Save these values so the next request will have them
		lastSlice    = slice;
				
//		System.out.println("Vga16Packet bitplane="+bitplane+" slice="+slice + " check="+ check );
		

		int x;		// Place in local image
		int y;  	// Place in local image
		
		boolean changed = false;
		
		int bytesPerLine = sizeX / 8;
		int linesPerSlice = ( sliceSize / bytesPerLine  );
		
		y =  slice * linesPerSlice;
		
		x = (( sliceSize * slice ) - ( y * bytesPerLine )) * 8;
	
		for( int i = 0 ; i < sliceSize ; i++ ) {	// loop through bytes in received packet
			
			int b = data[i];
			
			//System.out.println("data = "+b );
			
			for( int j = 0 ; j < 8 ; j++ ) {				// loop through bits in received packet
				
				boolean pixel = (b &  0x80) == 0x80;
				
				if ( pixelBuffer[x][y][bitplane] != pixel ) {
					
					pixelBuffer[x][y][bitplane] = pixel;
					
					setPixelRGB( x , y );
					
					changed = true;
					
				}
				
				b <<= 1;
				
				x++;
				
				if ( x >= sizeX ) {
					
					x=0;
					y++;
					
					
				}
				
				
			}
			

			
		}
		
		if (changed) {
					
			updateBufferedImage();
			return( true );
			
		}
					
		return false;
		
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
