package com.josh.tiny;

import java.awt.image.*;

public class DosScreenVESA103 extends DosScreen
{
	
	private String name;
	
	private String shortName;
	
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
		return shortName;
	}	
	
	static int vgaColors[] = {		// RGB values for the first 16 VBGA colors
		 0x000000  , // DAC Register #000 
		 0x0000ab  , // DAC Register #001 
		 0x00ab00  , // DAC Register #002 
		 0x00abab  , // DAC Register #003 
		 0xab0000  , // DAC Register #004 
		 0xab00ab  , // DAC Register #005 
		 0xabab00  , // DAC Register #006 
		 0xababab  , // DAC Register #007 
		 0x000055  , // DAC Register #008 
		 0x0000ff  , // DAC Register #009 
		 0x00ab55  , // DAC Register #010 
		 0x00abff  , // DAC Register #011 
		 0xab0055  , // DAC Register #012 
		 0xab00ff  , // DAC Register #013 
		 0xabab55  , // DAC Register #014 
		 0xababff  , // DAC Register #015 
		 0x005500  , // DAC Register #016 
		 0x0055ab  , // DAC Register #017 
		 0x00ff00  , // DAC Register #018 
		 0x00ffab  , // DAC Register #019 
		 0xab5500  , // DAC Register #020 
		 0xab55ab  , // DAC Register #021 
		 0xabff00  , // DAC Register #022 
		 0xabffab  , // DAC Register #023 
		 0x005555  , // DAC Register #024 
		 0x0055ff  , // DAC Register #025 
		 0x00ff55  , // DAC Register #026 
		 0x00ffff  , // DAC Register #027 
		 0xab5555  , // DAC Register #028 
		 0xab55ff  , // DAC Register #029 
		 0xabff55  , // DAC Register #030 
		 0xabffff  , // DAC Register #031 
		 0x550000  , // DAC Register #032 
		 0x5500ab  , // DAC Register #033 
		 0x55ab00  , // DAC Register #034 
		 0x55abab  , // DAC Register #035 
		 0xff0000  , // DAC Register #036 
		 0xff00ab  , // DAC Register #037 
		 0xffab00  , // DAC Register #038 
		 0xffabab  , // DAC Register #039 
		 0x550055  , // DAC Register #040 
		 0x5500ff  , // DAC Register #041 
		 0x55ab55  , // DAC Register #042 
		 0x55abff  , // DAC Register #043 
		 0xff0055  , // DAC Register #044 
		 0xff00ff  , // DAC Register #045 
		 0xffab55  , // DAC Register #046 
		 0xffabff  , // DAC Register #047 
		 0x555500  , // DAC Register #048 
		 0x5555ab  , // DAC Register #049 
		 0x55ff00  , // DAC Register #050 
		 0x55ffab  , // DAC Register #051 
		 0xff5500  , // DAC Register #052 
		 0xff55ab  , // DAC Register #053 
		 0xffff00  , // DAC Register #054 
		 0xffffab  , // DAC Register #055 
		 0x555555  , // DAC Register #056 
		 0x5555ff  , // DAC Register #057 
		 0x55ff55  , // DAC Register #058 
		 0x55ffff  , // DAC Register #059 
		 0xff5555  , // DAC Register #060 
		 0xff55ff  , // DAC Register #061 
		 0xffff55  , // DAC Register #062 
		 0xffffff  , // DAC Register #063 
		 0x9a2929  , // DAC Register #064 
		 0xa32929  , // DAC Register #065 
		 0xab2929  , // DAC Register #066 
		 0xb32929  , // DAC Register #067 
		 0xbb2929  , // DAC Register #068 
		 0xc32929  , // DAC Register #069 
		 0x29297a  , // DAC Register #070 
		 0x292982  , // DAC Register #071 
		 0x29298a  , // DAC Register #072 
		 0x292992  , // DAC Register #073 
		 0x29299a  , // DAC Register #074 
		 0x2929a3  , // DAC Register #075 
		 0x2929ab  , // DAC Register #076 
		 0x2929b3  , // DAC Register #077 
		 0x2929bb  , // DAC Register #078 
		 0x2929c3  , // DAC Register #079 
		 0xb7b7ff  , // DAC Register #080 
		 0xc7b7ff  , // DAC Register #081 
		 0xdbb7ff  , // DAC Register #082 
		 0xecb7ff  , // DAC Register #083 
		 0x000051  , // DAC Register #084 
		 0xffffff  , // DAC Register #085 
		 0xff9a04  , // DAC Register #086 
		 0x626262  , // DAC Register #087 
		 0x00ffff  , // DAC Register #088 
		 0x390039  , // DAC Register #089 
		 0xc30000  , // DAC Register #090 
		 0xffecb7  , // DAC Register #091 
		 0xffffb7  , // DAC Register #092 
		 0xecffb7  , // DAC Register #093 
		 0xdbffb7  , // DAC Register #094 
		 0xc7ffb7  , // DAC Register #095 
		 0xb7ffb7  , // DAC Register #096 
		 0xb7ffc7  , // DAC Register #097 
		 0xb7ffdb  , // DAC Register #098 
		 0xb7ffec  , // DAC Register #099 
		 0xb7ffff  , // DAC Register #100 
		 0xb7ecff  , // DAC Register #101 
		 0xb7dbff  , // DAC Register #102 
		 0xb7c7ff  , // DAC Register #103 
		 0x000072  , // DAC Register #104 
		 0x1c0072  , // DAC Register #105 
		 0x390072  , // DAC Register #106 
		 0x550072  , // DAC Register #107 
		 0x720072  , // DAC Register #108 
		 0x720055  , // DAC Register #109 
		 0x720039  , // DAC Register #110 
		 0x72001c  , // DAC Register #111 
		 0x720000  , // DAC Register #112 
		 0x721c00  , // DAC Register #113 
		 0x723900  , // DAC Register #114 
		 0x725500  , // DAC Register #115 
		 0x727200  , // DAC Register #116 
		 0x557200  , // DAC Register #117 
		 0x397200  , // DAC Register #118 
		 0x1c7200  , // DAC Register #119 
		 0x007200  , // DAC Register #120 
		 0x00721c  , // DAC Register #121 
		 0x007239  , // DAC Register #122 
		 0x007255  , // DAC Register #123 
		 0x007272  , // DAC Register #124 
		 0x005572  , // DAC Register #125 
		 0x003972  , // DAC Register #126 
		 0x001c72  , // DAC Register #127 
		 0x00ff00  , // DAC Register #128 
		 0x453972  , // DAC Register #129 
		 0x553972  , // DAC Register #130 
		 0x623972  , // DAC Register #131 
		 0x723972  , // DAC Register #132 
		 0x723962  , // DAC Register #133 
		 0x723955  , // DAC Register #134 
		 0x723945  , // DAC Register #135 
		 0x723939  , // DAC Register #136 
		 0x724539  , // DAC Register #137 
		 0x725539  , // DAC Register #138 
		 0x726239  , // DAC Register #139 
		 0x727239  , // DAC Register #140 
		 0x627239  , // DAC Register #141 
		 0x002900  , // DAC Register #142 
		 0x002d00  , // DAC Register #143 
		 0x003100  , // DAC Register #144 
		 0x003500  , // DAC Register #145 
		 0x003900  , // DAC Register #146 
		 0x003d00  , // DAC Register #147 
		 0x004100  , // DAC Register #148 
		 0x004500  , // DAC Register #149 
		 0x004900  , // DAC Register #150 
		 0x004d00  , // DAC Register #151 
		 0x005100  , // DAC Register #152 
		 0x005500  , // DAC Register #153 
		 0x005900  , // DAC Register #154 
		 0x005d00  , // DAC Register #155 
		 0x006200  , // DAC Register #156 
		 0x006600  , // DAC Register #157 
		 0x290029  , // DAC Register #158 
		 0x2d002d  , // DAC Register #159 
		 0x310031  , // DAC Register #160 
		 0x350035  , // DAC Register #161 
		 0x390039  , // DAC Register #162 
		 0x3d003d  , // DAC Register #163 
		 0x410041  , // DAC Register #164 
		 0x450045  , // DAC Register #165 
		 0x490049  , // DAC Register #166 
		 0x4d004d  , // DAC Register #167 
		 0x510051  , // DAC Register #168 
		 0x550055  , // DAC Register #169 
		 0x590059  , // DAC Register #170 
		 0x5d005d  , // DAC Register #171 
		 0x620062  , // DAC Register #172 
		 0x660066  , // DAC Register #173 
		 0x180018  , // DAC Register #174 
		 0x1c041c  , // DAC Register #175 
		 0x210821  , // DAC Register #176 
		 0x250c25  , // DAC Register #177 
		 0x291029  , // DAC Register #178 
		 0x2d142d  , // DAC Register #179 
		 0x311831  , // DAC Register #180 
		 0x351c35  , // DAC Register #181 
		 0x392139  , // DAC Register #182 
		 0x3d253d  , // DAC Register #183 
		 0x412941  , // DAC Register #184 
		 0x452d45  , // DAC Register #185 
		 0x493149  , // DAC Register #186 
		 0x4d354d  , // DAC Register #187 
		 0x513951  , // DAC Register #188 
		 0x553d55  , // DAC Register #189 
		 0x214100  , // DAC Register #190 
		 0x104100  , // DAC Register #191 
		 0x004100  , // DAC Register #192 
		 0x004110  , // DAC Register #193 
		 0x004121  , // DAC Register #194 
		 0x004131  , // DAC Register #195 
		 0x004141  , // DAC Register #196 
		 0x003141  , // DAC Register #197 
		 0x002141  , // DAC Register #198 
		 0x001041  , // DAC Register #199 
		 0x212141  , // DAC Register #200 
		 0x292141  , // DAC Register #201 
		 0x312141  , // DAC Register #202 
		 0x392141  , // DAC Register #203 
		 0x412141  , // DAC Register #204 
		 0x412139  , // DAC Register #205 
		 0x7a0000  , // DAC Register #206 
		 0x820000  , // DAC Register #207 
		 0x8a0000  , // DAC Register #208 
		 0x920000  , // DAC Register #209 
		 0x9a0000  , // DAC Register #210 
		 0xa30000  , // DAC Register #211 
		 0xab0000  , // DAC Register #212 
		 0xb30000  , // DAC Register #213 
		 0xbb0000  , // DAC Register #214 
		 0xc30000  , // DAC Register #215 
		 0xcb0000  , // DAC Register #216 
		 0xd30000  , // DAC Register #217 
		 0xdb0000  , // DAC Register #218 
		 0xe40000  , // DAC Register #219 
		 0xec0000  , // DAC Register #220 
		 0xf40000  , // DAC Register #221 
		 0x005555  , // DAC Register #222 
		 0x045959  , // DAC Register #223 
		 0x085d5d  , // DAC Register #224 
		 0x0c6262  , // DAC Register #225 
		 0x106666  , // DAC Register #226 
		 0x146a6a  , // DAC Register #227 
		 0x186e6e  , // DAC Register #228 
		 0x1c7272  , // DAC Register #229 
		 0x217676  , // DAC Register #230 
		 0x257a7a  , // DAC Register #231 
		 0x297e7e  , // DAC Register #232 
		 0x2d8282  , // DAC Register #233 
		 0x318686  , // DAC Register #234 
		 0x358a8a  , // DAC Register #235 
		 0x398e8e  , // DAC Register #236 
		 0x3d9292  , // DAC Register #237 
		 0x181818  , // DAC Register #238 
		 0x212121  , // DAC Register #239 
		 0x292929  , // DAC Register #240 
		 0x313131  , // DAC Register #241 
		 0x393939  , // DAC Register #242 
		 0x414141  , // DAC Register #243 
		 0x494949  , // DAC Register #244 
		 0x515151  , // DAC Register #245 
		 0x595959  , // DAC Register #246 
		 0x626262  , // DAC Register #247 
		 0x6a6a6a  , // DAC Register #248 
		 0x727272  , // DAC Register #249 
		 0x7a7a7a  , // DAC Register #250 
		 0x828282  , // DAC Register #251 
		 0x8a8a8a  , // DAC Register #252 
		 0x929292  , // DAC Register #253 
		 0x000000  , // DAC Register #254 
		 0xffffff  , // DAC Register #255 

	};


	
	
	public int signatureSize; 			// The size of a buffer for this screen type
	
	final int sizeX=800;				// Number of pixels in x direction
	final int sizeY=600;
	final int slices=16;				// The screen is divided into small slices for transmission
	
	final int banks=8;
	
	final int sliceSize=4096;				// The number of bytes in each slice
			
	int viewRequestCode;
		
	public DosScreenVESA103( int viewRequestCode  ) {

		
		this.viewRequestCode = viewRequestCode;
					
		//What should be displayed for this window based on the last update
		bufferedImage = new BufferedImage( sizeX  , sizeY , BufferedImage.TYPE_INT_RGB );
				
		name = "VGA VESA 800x600x256 " + banks +"/"+ slices ;
		
		shortName = "VGA 800x600x256" ;
		
		signatureSize = sliceSize + 2;		// final 2 bytes indicating the bank and slice indexes
		
	}
		 
	
	// High initial values for these will cause them to always wrap to zero on the first request and
	// then start scanning at the top of the screen.
	
	int lastSlice 	 = 255; 					//Last slice recieved
	int lastBank	 = 255;						//bank of last packet recieved
	
	public int getLastBitPlane() {
		return 0;
	}
	
	public int getLastSlice() {
		return lastSlice;
	}
		
		
	public boolean update( byte[] data  , int len ) {
		
		int bank     = data[ sliceSize + 0 ] & 0xff ;
		int slice    = data[ sliceSize + 1 ] & 0xff ;
		
//		System.out.println("Response bp:"+bank+" sl:"+slice);
		
		if ( slice >= this.slices) {
			
			System.out.println("Recieved a corrupted frame: slice="+slice +" bank="+bank+" ["+name+"]"  );
			return false;
			
		}
		
		lastBank     = bank;  // Save these values so the next request will have them
		lastSlice    = slice;
				
//		System.out.println("Vga16Packet bitplane="+bitplane+" slice="+slice + " check="+ check );
		
		// Find the offset from the upper left corner of the screen for the first recieved pixel 
		
		int offset = ((bank * (slices*sliceSize) ) + (slice * sliceSize) );  
		
		int x;		// Place in local image
		int y;  	// Place in local image
				
		y = (offset /  sizeX);
		
		x = offset - ( y * sizeX );
		
		if (x>=sizeX || y >= sizeY ) {
			
			System.out.println("Recieved an corrupted frame: x="+x +" y="+y+" ["+name+"]"  );
			return false;
			
		}
		
				
		synchronized (bufferedImage) {
					
			for( int i = 0 ; i < sliceSize ; i++ ) {	// loop through bytes in received packet
								
				bufferedImage.setRGB( x , y , vgaColors[ data[i] &0xff] );
				
				x++;
								
				if ( x>= sizeX ) {
					
					x = 0;
					
					y++;
					
					if (y>=sizeY) {
						
						break;
						
					}
					
				}
			
			}
			
		}
					
		
		return( true );								
			
	}
	
	public int getSignatureDataLength()
	{
		return( signatureSize );		// 2 bytes per char (ascii code + color) + 1 byte for cursor x + 1 byte for cursor y
	}


	void addScanSystems( ScreenRequestSenderList.ScreenRequestSenderNodeBranch node , int mode ) {
			
		node.addLeaf("Progressive" , new ScreenRequestSender( 'W' , mode , 0x00 , 0 ) );
		node.addLeaf("Interlaced"  , new ScreenRequestSender( 'W' , mode , 0x01 , 0 ) );
		
	}
	
	void addScreenRequestSenders(ScreenRequestSenderList.ScreenRequestSenderNodeBranch node)
	{
		ScreenRequestSenderList.ScreenRequestSenderNodeBranch subNode = node.addBranch( name );
		
		addScanSystems( subNode , viewRequestCode );
		
	}
	
	
}
