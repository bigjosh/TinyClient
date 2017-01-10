package com.josh.tiny;
import java.util.*;

class ScreenRequestSenderList {
	
	public class ScreenRequestSenderItem {
		
		String fullName;
		ScreenRequestSender screenrequestSender;
		
		int index;
		
		ScreenRequestSenderItem( String fullName , ScreenRequestSender screenrequestSender , int index ) {
			
			this.fullName = fullName;
			this.screenrequestSender = screenrequestSender;
			this.index = index;
			
		}
		
		public int getIndex() {
			
			return index;
			
		}
		
		public String getFullName() {
			return fullName;
		}
		
		public ScreenRequestSender getScreenRequestSender() {
			return screenrequestSender;
		}
		
	}
	
	// A list of all the store screen request senders.
	// This exists mostly to support the ability to pick a screenrequestitem based on it's index
	
	ArrayList<ScreenRequestSenderItem> screenRequestSenderItems = new ArrayList<ScreenRequestSenderItem>();
	
	
	public ScreenRequestSenderItem getScreenRequestSenderItemFromIndex( int index ) {
		
		return screenRequestSenderItems.get( index );
		
	}
	
	
	
	
	abstract public class ScreenRequestSenderNode {
		
		String fullName;
		String name;
		
		public String getName() {
			
			return name;
		}
		
		public String getFullName() {
			return fullName;
			
		}
		
		abstract public boolean isBranch();
		abstract public boolean isLeaf();
		
	}
	
	
	public class ScreenRequestSenderNodeBranch extends ScreenRequestSenderNode
	{

		public boolean isBranch()
		{
			return true;
		}
		
		public boolean isLeaf()
		{
			return false;
		}
		
		
		ArrayList<ScreenRequestSenderNode> subNodes;
		
		ScreenRequestSenderNodeBranch( String name , String base ) {
			
			this.name = name;
			this.fullName = base+"-"+name;
			
			subNodes = new ArrayList<ScreenRequestSenderNode>();
			
		}
		
		
		void addLeaf( String newName , ScreenRequestSender screenRequestSender ) {
			
			int index = screenRequestSenderItems.size();

			ScreenRequestSenderNode newNode = new ScreenRequestSenderNodeLeaf(  newName , this.fullName , screenRequestSender , index );
						
			screenRequestSenderItems.add( new ScreenRequestSenderItem(  newNode.getFullName() , screenRequestSender ,  index ));
			
			subNodes.add( newNode );
			
		}
		
		ScreenRequestSenderNodeBranch addBranch( String newName ) {
			
			ScreenRequestSenderNodeBranch newNode = new ScreenRequestSenderNodeBranch( newName , this.fullName );
			
			newNode.subNodes = new ArrayList<ScreenRequestSenderList.ScreenRequestSenderNode>();
			
			subNodes.add( newNode );
			
			return( newNode );
			
		}
		
		
	}
		

	public class ScreenRequestSenderNodeLeaf extends ScreenRequestSenderNode
	{
		
		public boolean isBranch()
		{
			return false;
		}
		
		public boolean isLeaf()
		{
			return true;
		}
		
		int index;
		
		ScreenRequestSender screenRequestSender;
		
		ScreenRequestSenderNodeLeaf( String name ,  String base , ScreenRequestSender screenRequestSender , int index) {
			
			this.name = name;
			this.fullName = base+"-"+name;
			
			this.screenRequestSender = screenRequestSender;
			this.index = index;
			
		}
		
	}
	
		

	
	ScreenRequestSenderNodeBranch rootNode;
	
	ScreenRequestSenderNodeBranch getRootNode() {
		
		return rootNode;
		
	}
	
	ScreenRequestSenderList( ) {
		
		rootNode = new ScreenRequestSenderNodeBranch( "" , ""  );
		
		rootNode.subNodes = new ArrayList<ScreenRequestSenderNode>();
				
	}
	
	/*
	
	
	public void print() {
		
		System.out.println("Treeview:");
		
		getBaseNode().print();
		
		System.out.println("ListView:");
		
		int x = 0;
		
		for (ScreenRequestSenderItem i : screenRequestSenderItems ) {
			
			System.out.println( x +" - " + i.getFullName() );
			
			x++;
			
		}
		
	}
	
	/*
	public static void main( String args[] ) {
		
		
		
		ScreenRequestSenderList list = new ScreenRequestSenderList(  );
		
		ScreenRequestSenderNode base = list.getBaseNode();
		
		ScreenRequestSenderNode br1 = base.addBranch( "Branch 1" );
		
		br1.addLeaf( "Leaf One" , new ScreenRequestSender( 0x01 , 0x01 , 0 , 0 ) );
		br1.addLeaf( "Leaf Two" , new ScreenRequestSender( 0x01 , 0x01 , 0 , 0 ));
		
		ScreenRequestSenderNode br2 = base.addBranch( "Branch 2" );
		
		br2.addLeaf("Leaf A" , new ScreenRequestSender( 0x01 , 0x01 , 0 , 0 ) );
		br2.addLeaf("Leaf B" , new ScreenRequestSender( 0x01 , 0x01 , 0 , 0 ) );
		
		base.addLeaf( "exit" , new ScreenRequestSender( 0x01 , 0x01 , 0 , 0 ) );
		
		list.print();
				
	}
* */
	
}

