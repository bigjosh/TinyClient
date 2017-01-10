package com.josh.tiny;
import java.awt.image.*;

// This screen should never be recieved - it just serves to make automatic view mode requests

public class DosScreenAutomaticCompatible extends DosScreen
	{
				
		BufferedImage bufferedImage;
		
		public String name = "Automatic (Legacy)"; 							// The name of this screen mode
		
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
		
		public DosScreenAutomaticCompatible() {
			
		}
		
		
		void addScreenRequestSenders(ScreenRequestSenderList.ScreenRequestSenderNodeBranch node)
		{
			node.addLeaf("Automatic (Legacy)" , new ScreenRequestSender( 'V' , 0 , 0 , 0 ) );
			
		}
		
	}


