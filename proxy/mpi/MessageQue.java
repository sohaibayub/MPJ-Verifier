package mpi;

import java.util.ArrayList;

public class MessageQue {

	public ArrayList<WaitingObjectClass> que = new ArrayList<WaitingObjectClass>() ;
	public boolean flag = false ;
	
	public MessageQue() {
	}
	
	public void putMessage(WaitingObjectClass _obj) {
		que.add(_obj) ;
	}
	
	public WaitingObjectClass removeMessage(int _index) {
		WaitingObjectClass obj = null ;
		obj = que.get(_index) ;
		return obj ;
	}
	
	public WaitingObjectClass getMessage(int _index) {
		WaitingObjectClass obj = null ;
		obj = que.remove(_index) ;
		return obj ;
	}
}