package com.josh.tiny;

import java.awt.image.*;

// This screen should never be received - it just serves to make automatic view mode requests

public class DosScreenAutomatic extends DosScreen
{
	



	BufferedImage bufferedImage;
	
	String name = "Automatic"; 							// The name of this screen mode
	
	public BufferedImage getBufferedImage()
	{
		return null;
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
		return -1;
	}
	
	
	
	public boolean update(byte[] data , int len )
	{
		
		
		
		return( false ) ;
		
	}
	

	
	public int getLastBitPlane() {
		return 0;
	}
	
	public int getLastSlice() {
		return 0;
	}
	
	public DosScreenAutomatic() {

	}
	
	
	void addScreenRequestSenders(ScreenRequestSenderList.ScreenRequestSenderNodeBranch node)
	{
		ScreenRequestSenderList.ScreenRequestSenderNodeBranch subnode = node.addBranch( "Automatic" );
		
		subnode.addLeaf("Progressive" , new ScreenRequestSender( 'W' , 0 , 0x00 , 0 ) );
		subnode.addLeaf("Interlaced"  , new ScreenRequestSender( 'W' , 0 , 0x01 , 0 ) );
		
	}
	
}


