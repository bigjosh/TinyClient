package com.josh.tiny;


// This class actually makes a view reqest packet based on the nessisary params
// each requestable screen mode will create on of these with all the details needed to request that mode
// These must match waht is expected on the host

 class ScreenRequestSender {
	
	int viewRequestCode;
	
	int mode;
	
	int a1;
	int a2;
	

		
	public void fillRequestPacket(byte[] buffer , int bitPlane , int slice )
	{
		buffer[0] = (byte) viewRequestCode;
		buffer[9] = (byte) mode;

		buffer[10]= (byte) a1;
		buffer[11]= (byte) a2;
		
		
		buffer[12]= (byte) bitPlane;
		buffer[13]= (byte) slice;
		
//		System.out.println( "Request bit:"+bitPlane+" Slice:"+slice +" a1:"+a1+" a2:"+a2);
				
	}
		
	ScreenRequestSender( int viewRequestCode, int mode , int a1 , int a2 ) {
			
		this.viewRequestCode = viewRequestCode;
		this.mode = mode;
		this.a1   = a1;
		this.a2   = a2;
		
	}
			
	
	public static int BUFFER_SIZE = 14;
	
}
