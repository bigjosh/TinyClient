package com.josh.tiny;

import java.awt.image.*;


public class DosScreenText extends DosScreen
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

	final int screenHeightchars;
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
	
	int pages;
			
	// The request mode is the mode byte that gets sent to the host to request this mode
		
	public DosScreenText( int  width, int  height , int requestMode , int pages , VgaSoftFont vgaSoftFont )  {
		
		this.vgaSoftFont =  vgaSoftFont;
		
		charHeight = vgaSoftFont.height;
		charWidth = vgaSoftFont.width;		
				
		screenHeightchars = height;
		screenWidthChars = width;
		 
		bufferWidth = charWidth * screenWidthChars;
		bufferHeight = charHeight * screenHeightchars;
		
		onScreenBuffer = new byte[screenHeightchars * screenWidthChars * 2 ];
				
		//What should be displayed for this window based on the last update
		bufferedImage = new BufferedImage( bufferWidth  , bufferHeight , BufferedImage.TYPE_INT_RGB );
		
		// RGB values before drawing to the BufferedImage
		bufferArray = new int[ bufferWidth * bufferHeight ];
		
		name = "Text "+width+"x"+height;
		
		signatureSize = ( height * width * 2 ) + 4;		// 2 bytes per char followed by 4 bytes of cursor info
		
		this.requestMode = requestMode;
		this.pages = pages;
				
	}
	
	// DisplayBuffer is organized like a VGA text mode screen with 1 byte for ASCII and one byte for color
	
	/*
	 
	 @return true if there was a change to the displayed image
	 
	 */
	
	boolean cursorBlink = true;
	
	public boolean update( byte[] data , int len ) {
		
		
		int cursorX = (int) data[ screenHeightchars*screenWidthChars*2  +0 ] & 0xff;
		int cursorY = (int) data[ screenHeightchars*screenWidthChars*2  +1 ] & 0xff;
		int cursorT = (int) data[ screenHeightchars*screenWidthChars*2  +2 ] & 0x01f;
		int cursorB = (int) data[ screenHeightchars*screenWidthChars*2  +3 ] & 0x01f;
		
		boolean cursorDisabled = (((int) data[ screenHeightchars*screenWidthChars*2  +2 ] )& 0x20 ) !=0 ; 
				
		// boolean cursorMoved = (cursorX != onScreenCursorX) || (cursorY != onScreenCursorY );
		
		for( int y = 0 ; y < screenHeightchars ; y++ ) {
			
			for( int x = 0 ; x < screenWidthChars ; x++ ) {
				
				int index = ( (y * screenWidthChars ) + x) * 2;	// Where are we in the inbuffer?
				
				byte displayChar = data[ index ];			// Get the byte at the current screen position
				
				byte displayColor = data[ index + 1 ];		// Get the byte at the current screen position
				
				byte onScreenChar = onScreenBuffer[ index ];
				
				byte onScreenColor = onScreenBuffer[ index + 1 ];
				
				if ( displayChar != onScreenChar || displayColor != onScreenColor || (x == onScreenCursorX && y == onScreenCursorY ) ) {	// Only redraw it if if has changed from what is on the screen already or it is under the old cursor
					
					boolean[][] m = vgaSoftFont.pixels[ displayChar & 0xff ];		// Get the matrix
					
					int onColor = vgaSoftFont.colors[ displayColor & 0x0f ];
					int offColor = vgaSoftFont.colors[ (displayColor & 0xf0) >> 4 ];
					
					int dest = ( ((y * charHeight) ) * (screenWidthChars*charWidth )) + (x * charWidth);								// What is the starting byte in the dest buffer?
					
					for( int l = 0 ; l < charHeight ; l++ ) {
						
						boolean[] scanline = m[ l ];					// Get the scanline from the matrix
						
						for( int p = 0 ; p < charWidth ; p++ ) {
							
							bufferArray[ dest++ ] =  scanline[p] ?  onColor : offColor ;
							
						}
						
						dest += ( ( screenWidthChars -1 ) * charWidth );
						
					}
					
					onScreenBuffer[ index ] = displayChar;
					
					onScreenBuffer[ index + 1] = displayColor;
					
				}
				
			}
		}
		
//		System.out.println( "Cursorx:" + cursorX + " ScreenWidthCars:" + screenWidthChars + " CursorY:" + cursorY + " ScreenHeightChars:" + screenHeightchars  + " CursorT:" + cursorT + " CursorB:" + cursorB +" CursorDisabled:"+cursorDisabled);
//		System.out.println( "Raw CursorTop Register:"+ data[ screenHeightchars*screenWidthChars*2  +2 ] + " Raw CursorBot Register:"+ data[ screenHeightchars*screenWidthChars*2  +3 ]);
		
		
		if (!cursorDisabled ) {
		
			if (cursorBlink) {			// Show cursor only on alternating screen paints	
				
					if (cursorX <  screenWidthChars && cursorY < screenHeightchars ) {		// Is cursor on screen?
						
												
						for( int l = cursorT ; l <= cursorB ; l++ ) {		// scan though the lines of the cursor
							
							if ( l < charHeight) {							// Make sure cursor is no on a line past the bottom of the char cell																		
							
								int dest = ( ((cursorY * charHeight) ) * ( screenWidthChars*charWidth )) + ( cursorX * charWidth) + ( l * charWidth * screenWidthChars);						// What is the starting byte in the dest buffer?
								
								for( int p = 0 ; p < charWidth ; p++ ) {
									
									bufferArray[ dest ] = vgaSoftFont.colors[15];			// Set to white
									
									dest++;
									
								}
							}
							
						}
					}
				
				onScreenCursorX = cursorX;
				onScreenCursorY = cursorY;
				
				cursorBlink = false;
				
			} else {
				
				cursorBlink = true;
				
			}
			
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
	
	public int getLastSlice() {
		return 0;
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
