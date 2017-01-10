package com.josh.tiny;

// A DosScreen represents on of the many types of screens that can be displayed
// on a DOS computer

import java.awt.image.*;


public abstract class DosScreen {
	
	abstract public BufferedImage getBufferedImage();	// The actual image you can display on the client screen- make sure to syncrhonize while drawing it
	
	abstract public String getName();				// The human readable name of this screen mode
	
	abstract public String getShortName();			// Human readable name for status window
	
	//abstract public String getFullName();
	
	abstract public boolean update( byte[] data , int len );	// Update the visible screen with a new data packet
	
	abstract public int getSignatureDataLength(); 	// the expected length of the data[] in update()
	
	//The screen type should add itself as branch to the supplied node, then list all of its modes as subbranches or leafes under that
	
	abstract void addScreenRequestSenders( ScreenRequestSenderList.ScreenRequestSenderNodeBranch node );
	
	abstract int getLastBitPlane();
	abstract int getLastSlice();

	
}