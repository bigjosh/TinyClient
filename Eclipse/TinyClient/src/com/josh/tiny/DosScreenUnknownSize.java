package com.josh.tiny;
import java.awt.image.*;
import java.awt.*;


// This screen is used to display an incoming packet that the client does not recognize

public class DosScreenUnknownSize extends DosScreen
{
	

	BufferedImage bufferedImage;
	
	public String name = "Unknown Size"; 							// The name of this screen mode
	
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
		
			
		synchronized (bufferedImage) {
		
			Graphics g = bufferedImage.getGraphics();
			
			int height = g.getFontMetrics().getHeight();
			
			int indent = g.getFontMetrics().charWidth('M') * 3;  // Indent each line three "m" sized spaces
			
			g.drawString( "The client does not recognize this screen size:" + len , indent , height * 2 );
			
			
		}
		
		return( true ) ;
			
	}
	
	
	public DosScreenUnknownSize() {
		
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
		// Do nothing since people should not be able to manually pick this DosScreen
	}
	
}
