package com.josh.tiny;
import java.util.*;

public class DosScreenManager
{
	
	ScreenRequestSenderList screenRequestSenderList;

	public ScreenRequestSenderList.ScreenRequestSenderNodeBranch getScreenRequestSenderRootNode() {
		
		return screenRequestSenderList.getRootNode();
		
	}
	
	ArrayList<DosScreen> dosScreenList = new ArrayList<DosScreen>();
	
	DosScreen unknownScreen = new DosScreenUnknownSize(); // This is a special case where the received screen does not match any known size
	
	public DosScreen findDosScreen( int dataSize ) {
		
		for( DosScreen screen : dosScreenList ) {
			
			if ( screen.getSignatureDataLength() == dataSize ) {
				
				return( screen );
			}
			
		}
		
		return( unknownScreen );
	}
	
	
	private void addScreen( DosScreen dosScreen ) {
		
		//First add to the list for incoming lookups
		
		if ( findDosScreen( dosScreen.getSignatureDataLength() ) != null ) {
			
			assert false : "Tried to add a screen with an existing signature length NEW:"+dosScreen.getName()+" EXISTING:"+findDosScreen( dosScreen.getSignatureDataLength() ).getName();
		}
		
		dosScreenList.add( dosScreen );
		
		//Then ask the dos screen to add itself to the tree structure used for the UI menu....
		
		ScreenRequestSenderList.ScreenRequestSenderNodeBranch rootNode = screenRequestSenderList.getRootNode();
		dosScreen.addScreenRequestSenders( rootNode );
		
	}

	public DosScreen getDosScreenFromIndex( int index ) {
		
		return( dosScreenList.get( index ) );
		
	}
		
	
	public int getMaxiumDataSize() {
		
		int maxsize = 0 ;
		
		for( DosScreen screen : dosScreenList ) {
			
			if (screen.getSignatureDataLength() > maxsize ) {
				
				maxsize = screen.getSignatureDataLength();
				
			}
		}
		
		return maxsize;
	}
	
	
	ScreenRequestSenderList getScreenRequestSenderList() {
		
		return screenRequestSenderList;
		
	}

	
	DosScreen noneScreen = new DosScreenNone();
	
	public DosScreen getNoneScreen() {
		
		return noneScreen;
	}
	
	ScreenRequestSenderList.ScreenRequestSenderItem  getScreenRequestSenderItemFromIndex( int index ) {
		
		return screenRequestSenderList.getScreenRequestSenderItemFromIndex(index);
	}
	
	int getdefaultScreenRequestSenderIndex() {
		
		return 0;
		
	}
	
	
	
	public DosScreenManager( VgaSoftFont vgaSoftFont ) {
		
		screenRequestSenderList = new ScreenRequestSenderList();
			
		// Note that you have to add the automatic requester first so it will always be index #0
		
		addScreen( new DosScreenAutomatic() );

		addScreen( new DosScreenAutomaticCompatible() );
		
		addScreen( new DosScreenText( 40 , 25 , 0x01 , 4 , vgaSoftFont ) );
		addScreen( new DosScreenText( 80 , 25 , 0x02 , 2 , vgaSoftFont ) );
		addScreen( new DosScreenText( 80 , 43 , 0x03 , 1 , vgaSoftFont ) );
		addScreen( new DosScreenText( 80 , 50 , 0x04 , 1 , vgaSoftFont ) );
		
		
		addScreen( new DosScreenVGA16( 320, 200 , 4 ,  4 , 0x05 ) );
		addScreen( new DosScreenVGA16( 640, 200 , 4 ,  4 , 0x06 ) );
		addScreen( new DosScreenVGA16( 640, 350 , 2 ,  8 , 0x07 ) );
		addScreen( new DosScreenVGA16( 640, 350 , 4 , 10 , 0x08 ) );
		addScreen( new DosScreenVGA16( 640, 480 , 4 , 10 , 0x09 ) );		
		
		addScreen( new DosScreenHerc( 720, 348 , 4 ,  3 , 0x0A ) );
		
		addScreen( new DosScreenVESA103Nibble( 0x0B ) );
				
		addScreen( new DosScreenVESA103( 0x0C ) );

		addScreen( new DosScreenTextSliced( 80 , 25 , 0x0D , 2 ,  800 , 4 , vgaSoftFont ) );
		addScreen( new DosScreenTextSliced( 80 , 50 , 0x0E , 1 , 1000 , 8 , vgaSoftFont ) );
				
		addScreen( new DosScreenUnsupportedMode() );
		
		addScreen( new DosScreenUnsupportedModeOld());
	}

}
