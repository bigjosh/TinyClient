package com.josh.tiny;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class TinyClient  {
	
	static final String VERSION = "3.60";
	static final String COPYRIGHT = "2010";
				
	DatagramSocket socket;
		
	byte receiveBuffer[] ; // Used for receiving incoming screen snapshot which may be (text plus cursor info) or (graphics plus bit plane info)
				
	DatagramPacket inPacket;
	
	String hostName;
	
	ClassLoader cl = this.getClass().getClassLoader();
	
	URL tinyClientIconURLs[] = {
		
		cl.getResource("TinyIcon0.gif"),
		cl.getResource("TinyIcon1.gif")
		
	};
	
			
	
	class FinishedFlag {
		
		boolean finished = false;
		
		
		public synchronized void setFinished() {
			
			finished = true;
			
			notifyAll();
			
		}
		
		public synchronized void waitUntilFinished() {
			
			while (!finished ) {
				
				try
				{
					wait();
					
				}
				catch (InterruptedException e)
				{}
				
			}
			
		}
	}
	
	FinishedFlag finishedFlag = new FinishedFlag();
	
	
	private String makeTitle( String state ) {
		
		return( "TINY to " + hostName + ":" + Integer.toHexString(hostPort) + " ("+state+")" );
		
	}
	
	public void showAboutBox(Icon icon) {
		
		JOptionPane.showMessageDialog(null, "TINY\r\nVersion " + VERSION +"\r\n(c)" + COPYRIGHT +" Josh Levine\r\nhttp://josh.com/tiny" , "About TINY" , JOptionPane.INFORMATION_MESSAGE , icon);
		
	}
	
	class StatusBar extends Frame {		// Represents and generates the optional status window
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3550704869644924264L;

		boolean changed = true;
		
		boolean displayed = false;
		
		
		public synchronized void setChanged() {
			
			changed = true;
			
			if (displayed) {
			
				repaint();
				
			}
			
		}
				
		public synchronized void setDisplayed( boolean displayed ) {
			
			if (displayed == this.displayed) return;
			
			if (displayed) {
							
				setFocusTraversalKeysEnabled(false);
				setResizable(false);
				setVisible(true);
				
				this.displayed = true;
				
				repaint();
				
			} else {
				
				setVisible( false );
				
				this.displayed = false;
				
			}
			
		}
		
		public void toggleDisplayedState()
		{
			
			setDisplayed( ! displayed );
			
		}
		
		@SuppressWarnings("deprecation")
		public boolean handleEvent(Event evt) {		// Make the close box work
			
 			if (evt.id == Event.WINDOW_DESTROY)	{
				
				setDisplayed( false );
				
 				return true;
				
			} else {
				
				return super.handleEvent(evt);
				
			}
		}
		
		
		int viewPacketsreceived = 0;
		
		public synchronized void incrementViewPacketCount() {
			
			viewPacketsreceived++;
			setChanged();
			
		}
		
		int viewRequestCount = 0;
		
		public synchronized void incrementViewRequestCount() {
			
			viewRequestCount++;
			setChanged();
			
		}
		
		int pendingkeyStrokeCount = 0;
		
		public synchronized void incrementpendingKeyStrokeCount() {
			
			pendingkeyStrokeCount++;
			setChanged();
			
		}

		public synchronized void decrementpendingKeyStrokeCount() {
			
			pendingkeyStrokeCount--;
			setChanged();
			
		}
		
		int keystrokeSendCount = 0;
		
		public synchronized void incrementKeystrokeSendCount() {
			
			keystrokeSendCount++;
			setChanged();
			
		}
		
		int keystrokeAckCount = 0;
		
		public synchronized void incrementKeystokeAckCount() {
			
			keystrokeAckCount++;
			setChanged();
			
		}
		
		
		int bootSendCount = 0;
		
		public synchronized void incrementBootSendCount() {
			
			bootSendCount++;
			setChanged();
			
		}
		
		String displayedModeName="NONE";
		
		public synchronized void setDisplayedModeName( String modeName ){
			
			this.displayedModeName = modeName;
			setChanged();
			
		}
		
					
		
		int screenRequestSenderIndex;
		
		public synchronized void setScreenRequestSenderIndex( int index ) {
			
			if (this.screenRequestSenderIndex != index ) {
			
				this.screenRequestSenderIndex = index;
				setChanged();
				
			}
			
		}
				
		
		public StatusBar() {
			
			super("TINY Stats");
			
			super.setIconImage( this.getToolkit().getImage( tinyClientIconURLs[0] ) );
			
			
		}
		
		boolean sizeSetFlag = false;
		
		BufferedImage bufferedImage;
		
		Graphics bg;
		
		Insets insets;
		
		FontMetrics fm;
	
			
		static final int LINE_COUNT = 17;
		
		static final int PADDING = 10;
		
		private void clear() {
			
			bg.clearRect( 0 , 0 , bufferedImage.getWidth() , bufferedImage.getHeight() );
			
		}
		
		private void printline( int line , String left , String right , Color color ) {
			
			int baseline = (line * fm.getHeight()) + fm.getHeight();
			
			bg.setColor( color );
			
			bg.drawString( left , PADDING , baseline );
			
			bg.drawString( right , bufferedImage.getWidth() - PADDING - fm.stringWidth( right ) , baseline );
			
		}
		
		synchronized public void update( Graphics g ) {
			
			if (!sizeSetFlag) {
				
				fm = g.getFontMetrics();
				
				insets = this.getInsets();
				
				bufferedImage = new BufferedImage( fm.stringWidth( " SEND PACKET COUNT:                         99999999 " ) , fm.getHeight() * LINE_COUNT , BufferedImage.TYPE_INT_RGB );
				
				bg = bufferedImage.getGraphics();
											
				setSize( bufferedImage.getWidth() + insets.left + insets.right , bufferedImage.getHeight() + insets.bottom +insets.top );
				
				sizeSetFlag = true;
			
			}
			
			if (changed) {
				
				// Draw the current buffered image onto the screen
				
				clear();

				printline( 1 , "Host IP"          , hostAddress.getHostAddress() , Color.WHITE );
				printline( 2 , "Host Port"        , "0x"+Integer.toHexString(hostPort) , Color.WHITE );

				printline( 4 , "Requested Mode"   , Integer.toString(screenRequestSenderIndex) , Color.WHITE );
				
				printline( 5 , "Displayed Mode"   , displayedModeName  , Color.WHITE );
				
				printline( 7 , "View requests"    , Integer.toString( viewRequestCount) , Color.WHITE );
				printline( 8 , "View responses"   , Integer.toString( viewPacketsreceived) , Color.WHITE );
				printline( 9 , "Lost Views"       , Integer.toString( viewRequestCount - viewPacketsreceived ) , Color.RED );

				printline(11 , "Key requests"     , Integer.toString( keystrokeSendCount ) , Color.WHITE );
				printline(12 , "Key ACKs"         , Integer.toString( keystrokeAckCount ) , Color.WHITE );
				printline(13 , "Lost Key Packets" , Integer.toString( keystrokeSendCount-keystrokeAckCount ) , Color.RED );
				
				
				printline(15 , "Pending keys"     , Integer.toString( pendingkeyStrokeCount ) , Color.WHITE );
				
				changed = false;
				
			}
			
			g.drawImage( bufferedImage , insets.left , insets.top , null );
				
		}
			
		
		public void paint( Graphics g ) {
			
			update( g );
		}
		
		
								
	}
	
	StatusBar statusBar;
	
	class KeyboardRequestThread extends Thread {

		static final int KEYSTROKE_QUEUE_SIZE = 20;		// Max number of keystrokes that will be queued - after this many oldest will be discarded
		
		static final int TOKEN_VALUE_MAX = 10000;		// Recycle tokens back to zero when we get here - host only uses 16 bit signed ints...
		
		static final int TOKEN_VALUE_REJECT = 10001; 	// Indicates that the host rejected the keybaord request due to view password
	
		// Keypress request - 'K' + 8 char control password + 2 byte keycode
		
		// Note that a keycode with FF in the high byte will be interpreted as a Type 2 scan code and will be stuffed into the 8042 keyboard controller
		
		byte keyRequestBuffer[] = { (byte) 'L' , 0x00, 0x00, 0x00, 0x00, 0x00 , 0x00 , 0x00 , 0x00 , (byte) 0 , (byte) 0 , (byte) 0 , (byte) 0 };
		
		DatagramPacket keyRequestPacket;
		
		int delay;
		
		public KeyboardRequestThread( String password, InetAddress hostAddress , int hostPort ,  int delay ) {
		
			keyRequestPacket = new DatagramPacket( keyRequestBuffer , keyRequestBuffer.length , hostAddress , hostPort );
			
			copyBytes( password , 0 , 8 , keyRequestBuffer , 1 );		// Copy password into key request
			
			this.delay = delay;
				
		}

									 
		
		private void sendKeyboardRequestPacket( int code , int token ) {

			keyRequestBuffer[ 9] = (byte) ( token & 0xff );
			keyRequestBuffer[10] = (byte) (( token >>> 8 ) & 0xff );
			
			keyRequestBuffer[11] = (byte) (code & 0xff );
			keyRequestBuffer[12] = (byte) ((code >>> 8) & 0xff );
	
			try {
				
				socket.send( keyRequestPacket );
				statusBar.incrementKeystrokeSendCount();
				
				
			} catch (IOException ex) {
				
				System.out.println("Error sending keystroke:"+ex.toString());
				
			}
			
		}
		
		
		int[] queue = new int[KEYSTROKE_QUEUE_SIZE];
		
		int head=0;
		int tail=0;
		
		int pendingToken = 0;						// Used by the protocol to help avoid dupped keystrokes
													// This is the token used by the last keystroke sent
		
		private int incptr( int x ) {		// increment a queue pointer wrapping at end of queue
			
			x++;
			
			if ( x==KEYSTROKE_QUEUE_SIZE ) {
				return( 0 );
			}
			
			return( x );
			
		}
		
		public synchronized void putKeystroke( int code ) {
			
			boolean wasEmpty;
			
			queue[ head ] = code;				// Add new code to head of queue
			
			wasEmpty = (head == tail);			// Remember if the queue was empty
							
			head = incptr( head );				// Bump the head forward
			
			statusBar.incrementpendingKeyStrokeCount();
			
			if (head == tail) {					// If we hit the tail, then the queue is full
			
				tail = incptr( tail );			// so bump the tail and drop the least recent element
				
				statusBar.decrementpendingKeyStrokeCount();
				
			}
			
			if (wasEmpty) {						// If the queue was empty, then wake up the send thread, which will be waiting
				
				notify();
				
			}
						
		}
		
		public void putScancode( int code ) {
			
			// set the high byte to FF to mark as a scancode and send it.
			
			putKeystroke( 0xff00 | code );
			
			System.out.println( "Put " + Integer.toHexString(0xff00 | code ) +" in buffer" );
	
		}
		
		public synchronized void ackKeyStroke( int token ) {
			
			
			if ( head != tail ) {	// Is there anything in the queue waiting for an ack?
				
				
				if ( token == pendingToken || token == TOKEN_VALUE_REJECT ) {	// Is this the one we were waiting for or was the request rejected?
														
					pendingToken++;				// Increment and wrap token
					
					statusBar.incrementKeystokeAckCount();
					
					if ( pendingToken == TOKEN_VALUE_MAX ) {
						
						pendingToken = 0;
						
					}
					
					tail = incptr( tail );		// Bump tail
					
					statusBar.decrementpendingKeyStrokeCount();
					
					if (tail != head ) {		// If there is anything still in the queue, we can send it out now
												
						notify();
						
					}
					
				}
				
			}
						
		}
		
		synchronized public void run() {
			
			putKeystroke( 0x0000 );			// Always send a magic zero first - this will reset the token on the host
			
			try {
			
				while ( true ) {
					
					
					if ( head != tail ) {
												
						sendKeyboardRequestPacket( queue[ tail ] , pendingToken );
						
					}
					
					if (head == tail ) {
													
						this.wait();
						
					} else {

						this.wait(delay);
						
						
					}
										

					
				}
				
			} catch (InterruptedException ex) {
				
				System.out.println("Keyboard send thread interrupted:"+ex.toString());
				
			}
							
		}
		
	}
	

	KeyboardRequestThread keyboardRequestThread;
	
	// Boot request packet - 'B' + 8 char control password
	
	byte bootRequestBuffer[] = { (byte) 'B' , 0x00, 0x00, 0x00, 0x00, 0x00 , 0x00 , 0x00 , 0x00 };
	
	DatagramPacket bootRequestPacket;
	
	synchronized void sendBootRequest() {
				
		try {
			
			socket.send( bootRequestPacket );
			statusBar.incrementBootSendCount();
			
		} catch (IOException ex) {
			
			System.out.println("Error sending boot request:"+ex.toString());
			
		}
	}
	
	DosScreenManager dosScreenManager;
	
	ScreenRequestSender currentScreenRequestSender;
	

	synchronized void setScreenRequestSender( int index ) {
		
		ScreenRequestSenderList.ScreenRequestSenderItem i = dosScreenManager.getScreenRequestSenderItemFromIndex(index);
		
		currentScreenRequestSender = i.getScreenRequestSender();
		statusBar.setScreenRequestSenderIndex( i.index );
						
	}
	
	
	class DosFrame extends Frame {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7441562285336664017L;

		boolean newModeFlag = true;		// Should we resize window on next repaint?
								
		boolean allowLeftClick;		// Should a left mouse click popup a menu, or only a right?
		
								
		public DosFrame( boolean allowLeftClick , boolean showStatusBar , int defaultScreenRequestSenderIndex , int scale , VgaSoftFont vgaSoftFont ) throws IOException {
						
			super( makeTitle( "(Waiting)" ));
			
			dosScreenManager = new DosScreenManager( vgaSoftFont );
			
			dosPopupMenu = new DosPopupMenu();
			
			currentDosScreen = dosScreenManager.getNoneScreen();			
			
			this.allowLeftClick = allowLeftClick;
			
			this.scale = scale;
							
			statusBar = new StatusBar();
			statusBar.setDisplayed( showStatusBar );

			setScreenRequestSender( defaultScreenRequestSenderIndex );
						
		}
		
		
				
		abstract class ModifierLookup {
			
			abstract int getCode( PcKeyboard.LocalKey key );
			
		}
		
		class NormalModifierLookup extends ModifierLookup {
			
			int getCode( PcKeyboard.LocalKey key ) {
				
				return( key.normal );
			}
			
		}
	
		class ShiftModifierLookup extends ModifierLookup {
			
			int getCode( PcKeyboard.LocalKey key ) {
				
				return( key.shift );
			}
			
		}
		
		class ControlModifierLookup extends ModifierLookup {
			
			int getCode( PcKeyboard.LocalKey key ) {
				
				return( key.control );
			}
			
		}
		class AltModifierLookup extends ModifierLookup {
			
			int getCode( PcKeyboard.LocalKey key ) {
				
				return( key.alt );
			}
			
		}
		
		
		class LocalKeystrokeMenu extends Menu {
			
								
			/**
			 * 
			 */
			private static final long serialVersionUID = -1374175584324850127L;


			class LocalKeystrokeMenuChoiceActionListener implements ActionListener {
				
				int dosKeyCode;
				
				LocalKeystrokeMenuChoiceActionListener(  int dosKeyCode ) {
					
					this.dosKeyCode = dosKeyCode;
					
				}
				
				public void actionPerformed(ActionEvent e)
				{
					keyboardRequestThread.putKeystroke( dosKeyCode );
				}
				
			}
			
									
			public LocalKeystrokeMenu( String modifierName , ModifierLookup modifierLookup ) {

				super( modifierName );
							
				for( int i = 0 ; i < PcKeyboard.keys.length ; i++ ) {
					
					MenuItem mi = new MenuItem( PcKeyboard.keys[i].name );
					
					LocalKeystrokeMenuChoiceActionListener l = new LocalKeystrokeMenuChoiceActionListener( modifierLookup.getCode( PcKeyboard.keys[i] ) );
					
					mi.addActionListener( l );
					
					this.add( mi );
					
				}
								
			}
			
		}


		
		
		class CustomKeystrokeMenu extends Menu {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 4518348993592410556L;


			class CustomKeystrokeMenuChoiceListener implements ActionListener {
				
				int dosKeyCode;
				
				CustomKeystrokeMenuChoiceListener(  int dosKeyCode ) {
					
					this.dosKeyCode = dosKeyCode;
					
				}
				
				public void actionPerformed(ActionEvent e)
				{
					keyboardRequestThread.putKeystroke( dosKeyCode );
				}
				
				
			}
			
						
			public CustomKeystrokeMenu( String name , KeyLookupTable.CustomKey[] keys ) {
				
				super( name );
				
				for( KeyLookupTable.CustomKey k : keys ) {
					
					MenuItem mi;
					
					mi = new MenuItem( k.name );
					
					mi.addActionListener( new CustomKeystrokeMenuChoiceListener( k.doscode ) );
					
					this.add( mi );
					
				}
								
			}
										
			
		}
		

		class ScreenModeMenuChoiceActionListener implements ActionListener {
			
				int index;
				
			ScreenModeMenuChoiceActionListener(  int index ) {
				
				this.index = index;
				
			}
			
			public void actionPerformed(ActionEvent e)
			{
				
				setScreenRequestSender( index );

				
			}
			
		}
		
		
		Menu makeSreenModesMenu( String name , ArrayList<ScreenRequestSenderList.ScreenRequestSenderNode> subNodes  ) {
			
			Menu m = new Menu( name );
			
			for( ScreenRequestSenderList.ScreenRequestSenderNode node : subNodes ) {
					
					if (node.isLeaf() ) {
						
						ScreenRequestSenderList.ScreenRequestSenderNodeLeaf leaf = (ScreenRequestSenderList.ScreenRequestSenderNodeLeaf) node;
						
						MenuItem mi = new MenuItem( leaf.name );
						
						mi.addActionListener( new ScreenModeMenuChoiceActionListener( leaf.index ));
						
						m.add( mi );
						
					} else {

						ScreenRequestSenderList.ScreenRequestSenderNodeBranch branch = (ScreenRequestSenderList.ScreenRequestSenderNodeBranch) node;
						
						m.add( makeSreenModesMenu( branch.name , branch.subNodes ));
						
					}
										
				}
			
			return m;
			
			}
		
			
		
		class DosPopupMenu extends PopupMenu implements ActionListener {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -4645202737649525385L;
			
			

			class RebootMenu extends Menu implements ActionListener {

				/**
				 * 
				 */
				private static final long serialVersionUID = -8396045238012850293L;


				public RebootMenu() {
	
					super("Reboot Host");
					
					this.add( "Cancel" );
					this.add( "Reboot Now" );
					
					this.addActionListener( this );
						
				}
				
				
				public void actionPerformed(final ActionEvent e) {
											
					if ( e.getActionCommand().equalsIgnoreCase( "Reboot Now" )) {
						
						sendBootRequest();
											
					}
					
				}
				
			}
			
			
			
					
			class ScaleMenu extends Menu {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1758581801833112956L;

				class ScaleMenuItem extends MenuItem implements ActionListener
				{
					
					/**
					 * 
					 */
					private static final long serialVersionUID = -2704269948739739801L;
					int scale;
					
					public ScaleMenuItem( String label , int scale ) {
						
						super( label );
						
						this.scale = scale;
						
						this.addActionListener( this );
						
					}
					
					public void actionPerformed(ActionEvent e)
					{
						
						setScale( this.scale );
					
					}
							
				}
				
				public ScaleMenu() {
					
					super("Scale Screen");
					
					this.add( "Cancel" );
					this.add( new ScaleMenuItem("1x",1 ));
					this.add( new ScaleMenuItem("2x",2 ));
					this.add( new ScaleMenuItem("3x",3 ));
					this.add( new ScaleMenuItem("4x",4 ));
					this.add( new ScaleMenuItem("Full Screen", 0 ));
					
				}
				
			
			}

						
			public DosPopupMenu() {

				
				super();
				
				this.add( "Cancel");
				
				Menu keystrokeMenus = new Menu( "Send Keystroke" );
				
				keystrokeMenus.add( new LocalKeystrokeMenu( "Normal"  , new NormalModifierLookup() ) );
				keystrokeMenus.add( new LocalKeystrokeMenu( "Shift"   , new ShiftModifierLookup() ) );
				keystrokeMenus.add( new LocalKeystrokeMenu( "Control" , new ControlModifierLookup() ) );
				keystrokeMenus.add( new LocalKeystrokeMenu( "Alt"     , new AltModifierLookup() ) );
				
				if (KeyLookupTable.customKeys != null ) {
					
					keystrokeMenus.add( new CustomKeystrokeMenu( "Custom Keys"    , KeyLookupTable.customKeys  ) );
					
				}
				
				this.add( keystrokeMenus );

				this.add( "Send Keyboard Scancode" );				
				
				this.add( new RebootMenu() );
				
				this.addSeparator();
				
				this.add( makeSreenModesMenu( "Host Screen Mode" , dosScreenManager.getScreenRequestSenderRootNode().subNodes ) );
				
				this.add( new ScaleMenu() );

				this.add( "Toggle Stats" );
				
				this.addSeparator();

				this.add( "Hang up" );

				this.add( "About" );
				
				this.addActionListener( this );
	
				
			}
			
			
			public void actionPerformed(ActionEvent e) {
							

				if ( e.getActionCommand().equalsIgnoreCase( "Send Keyboard Scancode" )) {
					
					
					String s = (String)JOptionPane.showInputDialog(
		                    dosFrame,
		                    "Enter a series of 2 digit hex type II keyboard\n"+
		                    "scan codes optionally separated by spaces.\n"+
		                    "example: 24 A4 would press and release the 'J' key"
		                    );

					//If a string was returned, say so.
					
					if ( (s != null) ) {
						
						StringTokenizer t = new StringTokenizer(s );
						
						while ( t.hasMoreTokens() ) {
						
							int code = Integer.parseInt( t.nextToken() , 16 );
									
							System.out.println("Sending scancode "+ Integer.toHexString(code));
						
							keyboardRequestThread.putScancode(code);
						
						}
										
					}					
					
					
					return;
					
				}

	
				if ( e.getActionCommand().equalsIgnoreCase( "Toggle Stats")) {
					
					statusBar.toggleDisplayedState();
										
					repaint();
					
					return;
					
				}
				
				
				if ( e.getActionCommand().equalsIgnoreCase( "Hang up")) {

					
					finishedFlag.setFinished();
														
					return;
					
				}
				
				if ( e.getActionCommand().equalsIgnoreCase( "About")) {
					
				
					
					
					showAboutBox( new ImageIcon( DosFrame.this.getToolkit().getImage( tinyClientIconURLs[0] )));
					
					return;
					
				}
				
				return;
				
			}
					
		}
		
		DosPopupMenu dosPopupMenu;
				
			
		public void display() {

			add( dosPopupMenu );

			setFocusTraversalKeysEnabled(false);
			setResizable(false);
			setVisible(true);
																											
		}
				
		public void start() {
			
			
			this.setIconImage( this.getToolkit().getImage( tinyClientIconURLs[0] ) );
			
			
//			if (keyScanDebug) {
//
//				this.addKeyListener( new DiagnosticKeyListener() );
//
//			}
				
			
	//		IconImageUpdaterThread = new IconUpdaterThread();
	//		iconImageUpdaterThread.start();
			
			display();
			
		}
		
				
		public int getMaxiumDataSize() {
			
			return( dosScreenManager.getMaxiumDataSize() );
			
		}
		
		DosScreen currentDosScreen;			// The most recently received screen
		
		DosScreen displayedDosScreen = null ;		// The most recently displayed screen
		
		long nextIconChange = 0;						// Next time to blink the icon
		int iconIndex = 0;							// Next cell to display
		static final long ICON_UPDATE_DELAY = 1000; 	// Min time between icon updates in milliseconds
		
		int scale = 1; 								// Scale size
		
		synchronized public void setScale( int scale ) {
			
			this.scale = scale;
			
			newModeFlag = true;
			
			repaint();
						
		}
		
				
		synchronized public void updateScreen( byte[] data , int len ) {	// called whenever a new data packet comes in that changes the contents of the dos screen
						
			currentDosScreen = dosScreenManager.findDosScreen( len );
			
			if ( currentDosScreen != displayedDosScreen ) {		// New screen type received
				
				newModeFlag = true;
				
			}
			
			if ( currentDosScreen.update( data , len ) || newModeFlag ) {
				
				repaint();
				
				long now = System.currentTimeMillis();
				
				if (  now > nextIconChange ) {
					
					iconIndex++;
					
					if (iconIndex >= tinyClientIconURLs.length ) {
						iconIndex = 0 ;
						
					}
	
					setIconImage( this.getToolkit().getImage( tinyClientIconURLs[iconIndex] ) );
					
					nextIconChange = now + ICON_UPDATE_DELAY;
					
				}
				
			}
						
		}
		
	
		
		synchronized public void update( Graphics g ){
			
			BufferedImage bufferedImage = currentDosScreen.getBufferedImage();

			Insets insets = getInsets();
			
			// First decide if we need to resize the window...
			
			if ( newModeFlag ) {
				
				statusBar.setDisplayedModeName( currentDosScreen.getShortName() );
				
				setTitle( makeTitle( currentDosScreen.getName() ) );
				
				if (scale == 0 ) { // Full screen mode?
					
					if ( GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getFullScreenWindow() == null ) {
						
						GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(DosFrame.this);
						
					}
					
				} else {
				
				
					if ( GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getFullScreenWindow() != null ) {
						
						GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
										
					}
						
					setSize( (bufferedImage.getWidth() * scale ) + insets.left + insets.right , (bufferedImage.getHeight() * scale ) + insets.bottom +insets.top );
										
				}
				
				displayedDosScreen = currentDosScreen;
				
				newModeFlag = false;
												
			}
			
																							
			// Draw the current buffered image onto the screen
					
			
			synchronized (bufferedImage) {
			
				if (scale ==1 ) {
					
					g.drawImage( bufferedImage , insets.left , insets.top , null );
					
				} else if ( scale ==0 ) {  // Scale 0 = full screen
						
					g.drawImage( bufferedImage , insets.left , insets.top , this.getWidth() - insets.left - insets.right, this.getHeight() -insets.top - insets.bottom  , null );
					
					
				} else {
					
					g.drawImage( bufferedImage , insets.left , insets.top , bufferedImage.getWidth() * scale , bufferedImage.getHeight() * scale , null );
					
				}
				
			}
			 												
		}
		
		public void paint( Graphics g ) {
			
			update( g ) ;
			
		}
		
/*
		 
		// Used to see if the new style listener could catch keys like german \ better than the old. It couldn't.
		 
		class DiagnosticKeyListener implements KeyListener
		{
			
				

			public void keyTyped(KeyEvent e)
			{
				if (keyScanDebug) {
					
					System.out.println("keyTyped getKeyChar="+e.getKeyChar());
										
				}
			}
			

			public void keyPressed(KeyEvent e)
			{
				if (keyScanDebug) {
					
					System.out.println("keyPressed getKeyCode="+e.getKeyCode()+ " getModifiers="+e.getModifiers()+ " getModifiersEx"+e.getModifiersEx()+" getKeyLocation="+e.getKeyLocation());
					
				}
			}

			public void keyReleased(KeyEvent e)
			{
			
			}
			
			
		}
		
*/
							
		public boolean keyDown(Event evt, int key ) {
		
			
			if (keyScanDebug)
			{
				
				System.out.println("KeyDown modifiers = " + Integer.toHexString( evt.modifiers ) + " key="+key ) ;
				
			}
			
			int code;
			
			code = KeyLookupTable.customTableLookup( evt.modifiers , key );			// Process custom translation table
			
			
			if ( code >= 0 ) {
				
				keyboardRequestThread.putKeystroke( code );
				
				return true;
								
			}
			
			
			for( int i = 0 ; i < PcKeyboard.keys.length ; i++ ) {
								
				if ( key == PcKeyboard.keys[i].lookup ) {
										
					if ( (evt.modifiers & Event.ALT_MASK ) != 0 ) {
						
						code = PcKeyboard.keys[i].alt;
						
					} else if ( (evt.modifiers & Event.CTRL_MASK ) != 0 ) {
						
						code = PcKeyboard.keys[i].control;
						
					} else if ( (evt.modifiers & Event.SHIFT_MASK ) != 0 ) {
						
						code = PcKeyboard.keys[i].shift;
						
					} else {
						
						code = PcKeyboard.keys[i].normal;
						
					}
					
					keyboardRequestThread.putKeystroke( code );
					
																
					return true;
					
				}
				
			}
			
			return false;
		}
		
				
		@SuppressWarnings("deprecation")
		public boolean handleEvent(Event evt) {		// Make the close box work
			
 			if (evt.id == Event.WINDOW_DESTROY)	{
				
				finishedFlag.setFinished();
				return true;
				
			} else {
 
				 return super.handleEvent(evt);
				
			}
		}
				
		
		public boolean mouseDown( Event evt , int x, int y ) {
			
			class StupidPopupThread extends Thread {
				
				int x,y;
				
				StupidPopupThread( int x, int y ) {
					
					this.x = x;
					this.y = y;
					
				}
																
				public void run() {
					
					dosPopupMenu.show( dosFrame , x , y );
					
				}
						
			}
			
			if (allowLeftClick) {
				
				new StupidPopupThread( x ,y ).start();
				
			} else {
				
				if ( evt.metaDown() ) {		// Check for right click only
					
					new StupidPopupThread( x ,y ).start();
				}
				
			}
			
			return true;
		}
		
	}
	
	DosFrame dosFrame;
	
	class ProcessreceivedPacketsThread extends Thread {
		
		public void run() {
			
			try {
				
				while ( !socket.isClosed() ) {
				
					inPacket.setLength( receiveBuffer.length );
										
					socket.receive( inPacket );
					
					if ( inPacket.getLength() == 2 ) {		// Is this a keystroke ACK packet?
						
						int token = ( receiveBuffer[0] & 0xff ) | (( receiveBuffer[1] & 0xff ) << 8 );
						
						keyboardRequestThread.ackKeyStroke( token );
						
					} else {
					
						statusBar.incrementViewPacketCount();
																															
						dosFrame.updateScreen( receiveBuffer , inPacket.getLength());
												
						dosFrame.repaint();
						
					}
																				
				}
				
			} catch (IOException ex ) {
				
				System.out.println("Error reading recieved packet:"+ex.toString());
				
			}
			
		}
		
	}
	
	// Continually sends view requests to the host
	
	class ViewRequestThread extends Thread {
		
		byte viewRequestBuffer[] = new byte[ScreenRequestSender.BUFFER_SIZE];
		
		DatagramPacket viewRequestPacket;
		
		int delay;
		
		public void run() {
								
			try {
			
				while ( !socket.isClosed() ){
					
					currentScreenRequestSender.fillRequestPacket( viewRequestBuffer , dosFrame.currentDosScreen.getLastBitPlane() , dosFrame.currentDosScreen.getLastSlice() );
																					
					socket.send( viewRequestPacket );
					
					statusBar.incrementViewRequestCount();
										
					Thread.sleep( delay );
						
				}
					
			} catch (IOException ex) {
				
				System.out.println("Error sending view request:"+ex.toString());
				
			} catch (InterruptedException ex) {
			}
				
		}
		
		ViewRequestThread( String password , InetAddress hostAddress , int hostPort , int delay ) {
			
			this.delay = delay;
			
			viewRequestPacket = new DatagramPacket( viewRequestBuffer , viewRequestBuffer.length , hostAddress , hostPort );
			
			copyBytes( password , 0 , 8 , viewRequestBuffer , 1 );		// Copy password into view request
						
		}
		
	}
	
	
	// Copy the bytes from the string into the byte array stop if end of string hit
	
	static void copyBytes( String s , int sStart , int sEnd, byte[] d , int dStart ) {
	
		for( int x = sStart ; x < sEnd && x < s.length() ; x++ ) {
			
			d[dStart++] = (byte) s.charAt( x );
			
		}
	}
	
	String password;
	InetAddress hostAddress;
	int hostPort;
	int screenRate;
	int keyRate;
	boolean keyScanDebug;		// For finding keycodes on forgien keyboards
	
		
	public TinyClient( String hostName , int port , String password , int screenRate , int keyRate , boolean allowLeftClick , boolean showStatusBar , boolean keyScanDebug , int screenMode , int scale , VgaSoftFont vgaSoftFont) throws IOException {
		
		this.password = password;
		this.hostName = hostName;
		this.hostPort = port;
		this.keyRate = keyRate;
		this.hostName = hostName;
		this.keyScanDebug = keyScanDebug;
		
		hostAddress = InetAddress.getByName( hostName );
		
		dosFrame = new DosFrame( allowLeftClick , showStatusBar , screenMode , scale , vgaSoftFont );
				
		socket = new DatagramSocket();
		
		receiveBuffer = new byte[ dosFrame.getMaxiumDataSize() ];
		
		inPacket = new DatagramPacket( receiveBuffer , receiveBuffer.length );
						
		bootRequestPacket = new DatagramPacket( bootRequestBuffer , bootRequestBuffer.length , hostAddress , port );
		
		copyBytes( password , 0 , 8 , bootRequestBuffer , 1 );		// Copy password into boot request
	
		this.screenRate = screenRate;
				
	}
	
	ViewRequestThread viewRequestThread;
	
	public void start() {
						
		dosFrame.start();
			
		new ProcessreceivedPacketsThread().start();
				
		viewRequestThread = new ViewRequestThread( password , hostAddress , hostPort , this.screenRate );

		viewRequestThread.start();
		
		keyboardRequestThread = new KeyboardRequestThread( password , hostAddress , hostPort , keyRate );
		
		keyboardRequestThread.start();
		
	}
	
	public void waitUntilFinished() {
		
		finishedFlag.waitUntilFinished();
		
	}
	


	public static void main( String args[] ) throws Exception {
		
		
		int UDPPort = 0x6363;
		int screenRate = 200;
		int keyRate = 500;
		
		String host;
		String password;
		
		boolean allowLeftClick = true;
		boolean ShowStatusBar = false;
		boolean keyScanDebug = false;
		
		VgaSoftFont vgaSoftFont = new VgaSoftFont();  // use the default font unless they load a different one 
		
		int screenMode = 0;
		
		int scale = 1;
		
		TinyClient tinyClient;
		
		System.out.println("TINY Client Version "+VERSION+"(a)"+ COPYRIGHT+ " Josh Levine [tiny"+'@'+"support.josh.com]");
		
		int arg= 0;
		

			
		while ( arg < args.length && args[arg].charAt( 0 ) == '/' ) {
			
			if (args[arg].length() < 2) {
				
				System.out.println( "Malformed argument, try TinyClient /? for help.");
				System.exit(1);
				
			}
			
			switch ( Character.toUpperCase( args[arg].charAt( 1 ) ) ) {
					
				case '?':
					System.out.println("Syntax:");
					System.out.println("TinyClient [/Dn] [/Kn] [/R] [/C] [/S] [/Px] [/Xf] [/Mn] [/Ln] [/Ff] host pass");
					System.out.println("");
					System.out.println("Where:");
					System.out.println("        /Dn sets delay between screen requests in milliseconds (default="+screenRate+", 0=none)");
					System.out.println("        /Kn sets the delay between key requests in milliseconds (default="+keyRate+")");
					System.out.println("        /R  disable popup menus on left click (default=left or right click)");
					System.out.println("        /Px sets target UDP port in hex (default="+Integer.toHexString(UDPPort)+")");
					System.out.println("        /S show status window on startup (default is don't show)");
					System.out.println("        /C show key scan codes to console for diagnosic purposes");
					System.out.println("        /X read in a custom key translation table from filename (can repeat)");
					System.out.println("        /M startup in screen mode n (omit n for mode list)");
					System.out.println("        /L startup with screen scaling n (default=1, 0=Full Screen)");
					System.out.println("        /F load text screen font from file");
					System.out.println("        host is the IP address or name of the host machine (required)");
					System.out.println("        pass is the password for the host machine (required)");
					System.exit(1);
					break;
					
				case 'M':
					if ( args[arg].length() < 3 ) {
						
						System.out.println("Available screen modes:");
						
						DosScreenManager d = new DosScreenManager( vgaSoftFont );
						
						int x=0;
						
						for( ScreenRequestSenderList.ScreenRequestSenderItem i : d.getScreenRequestSenderList().screenRequestSenderItems ) {
							
							System.out.println( x + i.getFullName() );
							x++;
						}

						System.exit(1);
						
					}
					
					screenMode = Integer.parseInt( args[arg].substring( 2 ) );
					System.out.println( "Initial screen request mode set to "+screenMode);
					break;
					
				case 'D':
					screenRate = Integer.parseInt( args[arg].substring( 2 ) );
					System.out.println( "Delay between screen requests set to "+screenRate+" milliseconds.");
					break;

				case 'K':
					keyRate = Integer.parseInt( args[arg].substring( 2 ) );
					System.out.println( "Delay between keyboard requests set to "+keyRate+" milliseconds.");
					break;

				case 'L':
					scale = Integer.parseInt( args[arg].substring( 2 ) );
					System.out.println( "Initial screen scale set to "+scale+"X");
					break;
					
				case 'S':
					ShowStatusBar = true;
					System.out.println("Enabled inital display of status bar");
					break;
					
				case 'R':
					allowLeftClick = false;
					System.out.println("Set menu popups only on right click.");
					break;
					
				case 'C':
					keyScanDebug = true;
					System.out.println("Will print diagnostic key scancodes to the console.");
					break;
					
				case 'P':
					UDPPort = Integer.parseInt( args[arg].substring( 2 ) , 16 );
					System.out.println( "Targeting UDP port " + Integer.toHexString( UDPPort ) + " on host.");
					break;
					
				case 'X':
					String filename = args[arg].substring(2);
					int count;
					System.out.println( "Reading custom key translation table from file '"+filename+"'...");
					count = KeyLookupTable.readTranslationTable( filename );
					System.out.println("Read " + count + " key translations.");
					break;
					
				case 'F':
					String fontfilename = args[arg].substring(2);
					
					System.out.println( "Loading custom text font from file '"+fontfilename+"'...");
			
					vgaSoftFont = new VgaSoftFont( new File(fontfilename) );
					
					System.out.println("Custom font loaded");
					
					break;
						
					
				default:
					System.out.println("Unrecognized argument: "+args[arg]+", try TinyClient /? for help.");
					System.exit(1);
					
			}
			
			arg++;
			
			
		}
		
		if ( arg + 2 != args.length ) {
			
			System.out.println( "Incorrect command line format, try TinyClient /? for help.");
			System.exit(1);
			
		}
		
		host = args[arg];
		password = args[arg+1];
			
		
		tinyClient = new TinyClient( host , UDPPort , password, screenRate , keyRate ,allowLeftClick , ShowStatusBar , keyScanDebug , screenMode , scale , vgaSoftFont );
		
		tinyClient.start();
		
		tinyClient.waitUntilFinished();
		
		System.exit(0);
						
	}
	
}
