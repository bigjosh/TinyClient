package com.josh.tiny;

import java.awt.Color;
import java.awt.image.*;


public class DosScreenTextSliced extends DosScreen
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

	final int screenHeightChars;
	final int screenWidthChars;
	
	final byte onScreenBuffer[];		// What is actually on screen
	
	int onScreenCursorX;
	int onScreenCursorY;
	
	VgaSoftFont vgaSoftFont;
		
	// Keep these local for performance;
	final int charHeight;
	final int charWidth;
	
	final int bufferHeight;
	final int bufferWidth;
	
	int bufferArray[];
	
	int requestMode;
		
	int slices;
	
	int dataPerSlice;
	
	int pages;
			
	// The request mode is the mode byte that gets sent to the host to request this mode
		
	public DosScreenTextSliced( int  width, int  height , int requestMode , int pages , int slicesize , int slices , VgaSoftFont vgaSoftFont )  {
		
		this.vgaSoftFont =  vgaSoftFont;
		
		charHeight = vgaSoftFont.height;
		charWidth = vgaSoftFont.width;		
				
		screenHeightChars = height;
		screenWidthChars = width;
		 
		bufferWidth = charWidth * screenWidthChars;
		bufferHeight = charHeight * screenHeightChars;
		
		onScreenBuffer = new byte[screenHeightChars * screenWidthChars * 2 ];
				
		//What should be displayed for this window based on the last update
		bufferedImage = new BufferedImage( bufferWidth  , bufferHeight , BufferedImage.TYPE_INT_RGB );
		
		// RGB values before drawing to the BufferedImage
		bufferArray = new int[ bufferWidth * bufferHeight ];
		
		name = "Text-Sliced "+width+"x"+height;

		this.dataPerSlice = slicesize;		
		
		this.signatureSize = dataPerSlice + 6 ;		// 2 bytes per char followed by 4 bytes of cursor info + 1 byte slice + 1 byte reserved
		
		this.requestMode = requestMode;
			
		this.slices = slices;
		
		this.pages = pages;
				
	}
	
	// DisplayBuffer is organized like a VGA text mode screen with 1 byte for ASCII and one byte for color
	
	/*
	 
	 @return true if there was a change to the displayed image
	 
	 */
	
	
	boolean cursorBlink = true;

	
	void drawChar( int x , int y , byte displayChar , byte displayColor  ) {		// Draws the char from onto the bufferArray
		
		boolean[][] m = vgaSoftFont.pixels[ displayChar & 0xff ];		// Get the matrix
		
		int onColor = vgaSoftFont.colors[ displayColor & 0x0f ];
		int offColor = vgaSoftFont.colors[ (displayColor & 0xf0) >> 4 ];
					
		int dest = ( ((y * charHeight) ) * (screenWidthChars*charWidth )) + (x * charWidth);								// What is the starting byte in the dest buffer?
		
		for( int l = 0 ; l < charHeight ; l++ ) {		// do each line of the char
			
			boolean[] scanline = m[ l ];					// Get the appropriate scanline from the matrix
			
			for( int p = 0 ; p < charWidth ; p++ ) {
				
				bufferArray[ dest++ ] =  scanline[p] ?  onColor : offColor ;		// each pixel on the line
				
			}
			
			dest += ( ( screenWidthChars -1 ) * charWidth );		// skip down to the next line
			
		}

	}
	
	static int CURSOR_ON_COLOR = Color.WHITE.getRGB();
	
	
	void drawCursor( int x , int y , int t , int b ) {			// Draws the cursor onto the bufferArray
		
		//System.out.println( "cursor x=" + x + " y="+y);
				
		int dest =  ( ((y * charHeight) + t ) * (screenWidthChars * charWidth ) ) + (x * charWidth) ;								// What is the starting byte in the dest buffer?
		
		if ( b >= charHeight-1 ) {
			b=charHeight-1;
		}
		
		for( int l = t ; l <= b ; l++ ) {		// do each line of the char
						
			for( int p = 0 ; p < charWidth ; p++ ) {
				
				bufferArray[ dest++ ] =  CURSOR_ON_COLOR ;		// each pixel on the line
				
			}
			
			dest += ( ( screenWidthChars -1 ) * charWidth );		// skip down to the next line
			
		}		

	}
	
	
	public boolean update( byte[] data , int len ) {
		
		int slice    = (int) data[0] & 0xff;	
		
		//System.out.println("Slice " + slice + " Lastslice=" + lastSlice );
		
		lastSlice = slice;
		

		int dataIndex = 6; 					// pointer into recieved packet data, skip the 6 byte header
		
		int bufferIndex = dataPerSlice * slice;	// pointer into local screen buffer
		
		int charIndex = bufferIndex/2;
		
		int y = charIndex / screenWidthChars;
		
		int x = charIndex - ( y * screenWidthChars );
		
		while(  dataIndex < len ) {
												
			byte displayChar = data[ dataIndex ];			// Get the byte at the current screen position
			
			byte displayColor = data[ dataIndex + 1 ];		// Get the byte at the current screen position
			
			byte onScreenChar = onScreenBuffer[ bufferIndex ];
			
			byte onScreenColor = onScreenBuffer[ bufferIndex + 1 ];
			
			if ( displayChar != onScreenChar || displayColor != onScreenColor ) {	// Only redraw it if if has changed from what is on the screen already or it is under the old cursor

				drawChar( x , y , displayChar , displayColor );
				
				onScreenBuffer[ bufferIndex ] = displayChar;
				
				onScreenBuffer[ bufferIndex+1] = displayColor;
												
			} 
				
			x++;
			
			if (x >= screenWidthChars ) {
				
				x = 0;
				
				y ++;
				
				if (y >= screenHeightChars ) {
					
					y =0;
					
				}
				
			}		
			
			bufferIndex += 2;
			dataIndex += 2;
				
		}
		
		// First draw char to replace old cursor pos
		
				
		if (cursorBlink) {			// Show cursor only on alternating screen paints	
			
				int cursorIndex = ((int) (data[2] & 0xff)) | ( ( (int) (data[3] &0xff) ) << 8 );   

				int cursorY  = cursorIndex / screenWidthChars;
								
				int cursorX  = cursorIndex - (cursorY * screenWidthChars);
				int cursorT  = (int) data[4] & 0x01f;
				int cursorB  = (int) data[5] & 0x01f;
														
				if (cursorX <  screenWidthChars && cursorY < screenHeightChars ) {		// Is cursor on screen?
					
					drawCursor( cursorX , cursorY, cursorT , cursorB );
																					
				}
			
			onScreenCursorX = cursorX;
			onScreenCursorY = cursorY;
			
			cursorBlink = false;
			
		} else {
		
			// redraw the char under the displayed cursor
			
			if (onScreenCursorX <  screenWidthChars && onScreenCursorY < screenHeightChars ) {		// Is stale cursor on screen?
				
				int onScreenCursorIndex = (( onScreenCursorY * screenWidthChars ) + onScreenCursorX ) * 2; 

				byte displayChar = onScreenBuffer[ onScreenCursorIndex ];			// Get the byte at the current screen position					
				byte displayColor = onScreenBuffer[ onScreenCursorIndex + 1 ];		// Get the byte at the current screen position
			
				drawChar( onScreenCursorX , onScreenCursorY , displayChar , displayColor );
				
			}
			
			cursorBlink = true;			

		}

			
		synchronized (bufferedImage) {
			
			bufferedImage.setRGB( 0 , 0 , bufferWidth , bufferHeight , bufferArray , 0 , bufferWidth );
			
		}
		
		
		return( true );		// Always redraw because the cursor will have blinked
		
	}
	
	public int getSignatureDataLength()
	{
		return( signatureSize );		// 2 bytes per char (ascii code + color) + 1 byte for cursor x + 1 byte for cursor y
	}
	
	
	
	void addPages( ScreenRequestSenderList.ScreenRequestSenderNodeBranch node , int mode , int a1 ) {
		
		
		
		for( int p = 0 ; p < pages ; p++ ) {
	
			node.addLeaf("Page " + p , new ScreenRequestSender( 'W' , mode , a1 , p ) );
			
		}
				
	}

	public int getLastBitPlane() {
		return 0;
	}
	
	int lastSlice = 255;
	
	public int getLastSlice() {
		return lastSlice;
	}
	
	
	void addScreenRequestSenders(ScreenRequestSenderList.ScreenRequestSenderNodeBranch node)
	{
		
		ScreenRequestSenderList.ScreenRequestSenderNodeBranch subNode = node.addBranch( name );

		ScreenRequestSenderList.ScreenRequestSenderNodeBranch colorNode = subNode.addBranch( "Color (B800)" );
		
		addPages( colorNode , requestMode , 0x00 );

		ScreenRequestSenderList.ScreenRequestSenderNodeBranch monoNode = subNode.addBranch( "Mono (B000)" );
		
		addPages( monoNode , requestMode , 0x01 );
		
	}
	
}
