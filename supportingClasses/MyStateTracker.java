package gov.nasa.jpf.supportingClasses;

import java.util.ArrayList;

import gov.nasa.jpf.vm.ThreadInfo;

public class MyStateTracker {
	private class StateThreadInfo {
		public ArrayList<ThreadInfo> threads = new ArrayList<ThreadInfo>() ;
		public int state_id ;
		public int parent_state_id ;
		StateThreadInfo(ArrayList<ThreadInfo> _thread, int _state_id, int _parent_state_id) {
			this.state_id = _state_id ;
			this.parent_state_id = _parent_state_id ;
			if(_state_id != 0) {
				if(_thread.size() > 0) {
					for(int i = 0 ; i < _thread.size() ; i++) {
						threads.add(_thread.get(i)) ;
					}
				}
			}
		}
	}
	
	ArrayList<StateThreadInfo> Stack = new ArrayList<StateThreadInfo>() ;
	
	public void AddState(int _state_id) {
		if(_state_id == 0) {
			Stack.add(new StateThreadInfo(null, 0, -1)) ;
		} else {
			Stack.add(new StateThreadInfo(Stack.get(Stack.size() - 1).threads, _state_id, Stack.get(Stack.size() - 1).state_id)) ;	
		}
	}
	
	public void addThread(int _state_id, ThreadInfo _thread) {
		for(int i = 0 ; i < Stack.size() ; i++) {
			if(Stack.get(i).state_id == _state_id) {
				Stack.get(i).threads.add(_thread) ;
			}
		}
	}
	
	public ThreadInfo[] getThreads(int _state_id) {
		int j = -1 ;
		for(int i = 0 ; i < Stack.size() ; i++) {
			if(Stack.get(i).state_id == _state_id) {
				j = i ;
				break ;
			}
		}
		ThreadInfo[] Threads ;
		if(Stack.get(j).threads.size() > 0) {
			Threads = new ThreadInfo[Stack.get(j).threads.size()] ;
			for(int i = 0 ; i < Stack.get(j).threads.size() ; i++) {
				Threads[i] = Stack.get(j).threads.get(i) ;
			}
		} else {
			Threads = new ThreadInfo[0] ;
		}
		return Threads ;	
	}
	
	public void RemoveState(int _state_id) {
		Stack.remove(getIndex(_state_id)) ;
	}
	
	public String ThreadInfotoString(ThreadInfo[] choices) {
		String str = "{" ;
		for(int i = 0 ; i < choices.length; i++)
			str = str + String.valueOf(choices[i].getId()) + " " + choices[i].getState().toString() + " " ;
		str = str.trim() ;
		return str + "}" ;
	}
	
	private int getIndex(int _state_id) {
		for(int i = 0 ; i < Stack.size() ; i++) {
			if(Stack.get(i).state_id == _state_id) {
				return i ;
			}
		}
		return -1 ;
	}
}
