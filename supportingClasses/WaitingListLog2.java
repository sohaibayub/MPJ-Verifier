package gov.nasa.jpf.supportingClasses;

import java.util.ArrayList;

/*Added for deadlock detection on 20-Dec-2017*/
class Log2 {
	public int sender ;
	public int receiver ;
	public int counter ;
	public int tag ;
	Log2(int _sender, int _receiver, int _tag) {
		sender = _sender ;
		receiver = _receiver ;
		tag = _tag ;
		counter = 1 ;
	}
}

public class WaitingListLog2 {
	
	public static ArrayList<Log2> List = new ArrayList<Log2>() ;
	
	public static void add(int _sender, int _receiver, int _tag) {
		boolean flag = true ;
		if(List.size() > 0) { 
			for(int i = 0 ; i < List.size() ; i++) {
				if(List.get(i).sender == _sender && List.get(i).receiver == _receiver && List.get(i).tag == _tag) {
					List.get(i).counter =  List.get(i).counter + 1 ;
					flag = false ;
				}
			}
		}
		if(flag)
			List.add(new Log2(_sender, _receiver, _tag)) ;
	}
	
	public static void clear() {
		List.clear() ;
	}
	
	public static boolean isThreadwithMessage(int _threadId) {
		_threadId = _threadId - 1 ;
		if(List.size() > 0) {
			for(int i = 0 ; i < List.size() ; i++) {
				if(List.get(i).receiver == _threadId && List.get(i).counter > 0) {
					return true ;
				}
			}
		}
		return false ;
	}
	
	public static boolean isThreadwithMessage(int _threadId, int _sender, int _tag) {
		_threadId = _threadId - 1 ;
		if(List.size() > 0) {
			for(int i = 0 ; i < List.size() ; i++) {
				if(_sender == 100) {
					if(List.get(i).receiver == _threadId && List.get(i).tag == _tag && List.get(i).counter > 0) {
						List.get(i).counter = List.get(i).counter - 1 ;
						return true ;
					}
				} else {
					if(List.get(i).receiver == _threadId && List.get(i).sender == _sender && List.get(i).tag == _tag && List.get(i).counter > 0) {
						List.get(i).counter = List.get(i).counter - 1 ;
						return true ;
					}
				}
			}
		}
		return false ;
	}
	
	public static boolean isThreadwithMessageAndDecrementCounter(int _threadId, int _sender, int _tag) {
		_threadId = _threadId - 1 ;
		if(List.size() > 0) {
			for(int i = 0 ; i < List.size() ; i++) {
				if(_sender == 100) {
					if(List.get(i).receiver == _threadId && List.get(i).tag == _tag && List.get(i).counter > 0) {
						List.get(i).counter = List.get(i).counter - 1 ;
						return false ;
					}
				} else {
					if(List.get(i).receiver == _threadId && List.get(i).sender == _sender && List.get(i).tag == _tag && List.get(i).counter > 0) {
						List.get(i).counter = List.get(i).counter - 1 ;
						return false ;
					}
				}
				
			}
		}
		return true ;
	}
	
	public static boolean isThreadwithMessageAndDecrementCounter(int _threadId) {
		_threadId = _threadId - 1 ;
		if(List.size() > 0) {
			for(int i = 0 ; i < List.size() ; i++) {
				if(List.get(i).receiver == _threadId && List.get(i).counter > 0) {
					List.get(i).counter = List.get(i).counter - 1 ;
					return true ;
				}
			}
		}
		return false ;
	}
	
	public static void print() {
		if(List.size() > 0) {
			p("/--------------------------------------------------------------------------/") ;
			for(int i = 0 ; i < List.size() ; i++) {
				p("/---------------- "+"Sender: "+(List.get(i).sender + 1)+" Receiver: "+(List.get(i).receiver + 1)+" Tag: "+(List.get(i).tag)+" Counter: "+(List.get(i).counter)+" ---------------/") ;
			}
			p("/--------------------------------------------------------------------------/") ;
		}
	}
	
	public static void p(String str) {
		//System.out.println(str) ;
	}
}
