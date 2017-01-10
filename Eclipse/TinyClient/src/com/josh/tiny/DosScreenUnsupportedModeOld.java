package com.josh.tiny;
import java.awt.image.*;
import java.awt.*;

public class DosScreenUnsupportedModeOld extends DosScreen
{
		
	// This support an old style "mode unsupported" packet that was only one byte long and only give the VGA mode
	// The new packet is 6 bytes long and includes VESE info.
	
	
	BufferedImage bufferedImage;
	
	public String name = "Unsupported Mode (legacy)"; 							// The name of this screen mode
	
	
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
	
	
	public int getSignatureDataLength()
	{
		return 1;
	}
	
	int lastVgaMode    = -1;		// Forces an update to be true on first pass
	
	public boolean update(byte[] data , int len )
	{
		
		int vgaMode    = (data[0] & 0xff) ;
		
		if (   vgaMode != lastVgaMode ) {
		
			synchronized (bufferedImage) {
				
				Graphics g = bufferedImage.getGraphics();
												
				g.clearRect( 0 , 0 , (int) g.getClipBounds().getWidth() , (int) g.getClipBounds().getHeight() );
				
				int height = g.getFontMetrics().getHeight();
				
				int indent = g.getFontMetrics().charWidth('M') * 3;  // Indent each line three "m" sized spaces
				
				
				g.drawString( "The host does not support this mode." , indent , height*2 );
				
				g.drawString( "VGA Mode   : "+vgaMode    , indent , height*3 );
				g.drawString( "VESA Status: (legacy)" , indent , height*4 );
				g.drawString( "VESA Mode  : (legacy)"   , indent , height*5 );
				
			}
			
			lastVgaMode    = vgaMode;
			
			return true;
		}
		
		return false;
		
	}
	
	public DosScreenUnsupportedModeOld() {
		
		bufferedImage = new BufferedImage( 640 , 200 ,  BufferedImage.TYPE_INT_RGB );
		
	}

	public int getLastBitPlane() {
		return 0;
	}
	
	public int getLastSlice() {
		return 0;
	}
	
	
	void addScreenRequestSenders(ScreenRequestSenderList.ScreenRequestSenderNodeBranch node)
	{
		// This screen is not munaully selectable
		
	}
	
	
}
