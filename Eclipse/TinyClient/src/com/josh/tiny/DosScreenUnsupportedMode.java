package com.josh.tiny;
import java.awt.image.*;
import java.awt.*;

public class DosScreenUnsupportedMode extends DosScreen
{
	
	
	
	BufferedImage bufferedImage;
	
	public String name = "Unsupported Mode"; 							// The name of this screen mode
	
	
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
		return 6;
	}
	
	int lastVgaMode    = -1;		// Forces an update to be true on first pass
	int lastVesaStatus = -1;		// Forces an update to be true on first pass
	int lastVesaMode   = -1;		// Forces an update to be true on first pass
	
	public boolean update(byte[] data , int len )
	{
		
		int vgaMode    = (data[0] & 0xff) | (( data[1] & 0xff) << 8 ) ;
		int vesaStatus = (data[2] & 0xff) | (( data[3] & 0xff) << 8 ) ;
		int vesaMode   = (data[4] & 0xff) | (( data[5] & 0xff) << 8 ) ;
		
		if (   vgaMode != lastVgaMode || vesaStatus!=lastVesaStatus || vesaMode != lastVesaMode ) {
		
			synchronized (bufferedImage) {
				
				Graphics g = bufferedImage.getGraphics();
				
				FontMetrics fontMetrics = g.getFontMetrics();
				
				String topLine = "The TINY host does not support this mode.";  // This should be the longest line in the window
				
				int maxWidth = fontMetrics.stringWidth( topLine );

				int height = fontMetrics.getHeight();
				
				int indent = fontMetrics.charWidth('M') * 3;  // Indent each line three "m" sized spaces
				
				
				g.clearRect( 0 , 0 , maxWidth , height * 7  );
				
				
				g.drawString( "The TINY host does not support this mode." , indent , height*2 );
				
				g.drawString( "VGA Mode   : [0x"+Integer.toHexString(vgaMode)    +"]", indent , height*4 );
				g.drawString( "VESA Status: [0x"+Integer.toHexString(vesaStatus) +"]", indent , height*5 );
				g.drawString( "VESA Mode  : [0x"+Integer.toHexString(vesaMode)   +"]", indent , height*6 );
								
			}
			
			lastVgaMode    = vgaMode;
			lastVesaStatus = vesaStatus;
			lastVgaMode    = vesaMode;
		
			return true;
		}
		
		return false;
		
	}
	
	public DosScreenUnsupportedMode() {
		
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
