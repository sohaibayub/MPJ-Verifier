package gov.nasa.jpf.supportingClasses;

import gov.nasa.jpf.vm.ThreadInfo;

public class SenderTag {
	
	static int sender ;
	static int tag ;
	static int receiver ;
	
	public static void set(ThreadInfo _ti) {
		sender = (int) _ti.getTopFrame().getLocalVariable("_sender") ;
		tag = (int) _ti.getTopFrame().getLocalVariable("_tag") ;
		receiver = _ti.getId() ;
		print() ;
	}
	
	static public int getTag() {
		return tag ;
	}
	
	static public int getSender() {
		return sender ;
	}
	
	static public void print() {
		String tempStr = "" ;
		tempStr = tempStr + "/--------------------- " ;
		if(sender == 100)
			tempStr = tempStr + "Sender: " + sender + " Receiver: " + receiver + " Tag: " + tag ; 
		else
			tempStr = tempStr + "Sender: " + (sender + 1) + " Receiver: " + receiver + " Tag: " + tag ;
		tempStr = tempStr + " ---------------------/" ;
		p(tempStr) ;
	}
	
	public static void p(String s) {
		//System.out.println(s) ;
	}
}
