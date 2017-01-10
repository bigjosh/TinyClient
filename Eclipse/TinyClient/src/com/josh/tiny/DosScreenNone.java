package com.josh.tiny;
import java.awt.image.*;
import java.awt.*;

public class DosScreenNone extends DosScreen
{

	BufferedImage bufferedImage;
		
	public String name = "Not set"; 							// The name of this screen mode

	
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
		return 0;
	}
	
	
	public boolean update(byte[] data , int len )
	{
		return false;
	}
	
	final String message = "(No remote screen available)";
	
	public DosScreenNone() {
		
		bufferedImage = new BufferedImage( 300 , 200 ,  BufferedImage.TYPE_INT_RGB );
			
		Graphics g = bufferedImage.getGraphics();
	
		FontMetrics fm = g.getFontMetrics();
	
		int textHeight = fm.getHeight();
			
		int textWidth = fm.stringWidth( message );
		
		
		g.drawString( message , (300/2) - (textWidth/2) , (200/2) - (textHeight/2) );
		
	}

	// High initial values for these will cause them to always wrap to zero on the first request and
	// then start scanning at the top of the screen.
	
	
	public int getLastBitPlane() {
		return 255;
	}
	
	public int getLastSlice() {
		return 255;
	}
	
	
	void addScreenRequestSenders(ScreenRequestSenderList.ScreenRequestSenderNodeBranch node)
	{
		// Do nothing since people should not be able to manually pick this DosScreen
	}
	
}
