package gov.nasa.jpf.supportingClasses;

import gov.nasa.jpf.vm.ThreadInfo;

import java.util.ArrayList;

public class ThreadList1 {
	
	private boolean ListUsed = false ;
	private boolean ListDone = false ;
	private ArrayList<ThreadStructure> threads = new ArrayList<ThreadStructure>() ;
	
	public void addThread(ThreadInfo _thread, int _line_number, boolean _processed) {
		threads.add(new ThreadStructure(_thread, _line_number, _processed)) ;
	}
	
	public void addThread(ThreadInfo _thread, boolean _processed) {
		threads.add(new ThreadStructure(_thread, _processed)) ;
	}
	
	public boolean IsThreadInListB(ThreadInfo _thread, int _line_number) {
		for(int i = 0 ; i < threads.size(); i++) {
			if(threads.get(i).thread.getId() == _thread.getId() 
			   && threads.get(i).line_number == _line_number 
			   && threads.get(i).processed == false)
				return true ;
		}
		return false ;
	}
	
	public int IsThreadInListI(ThreadInfo _thread, int _line_number) {
		for(int i = 0 ; i < threads.size(); i++) {
			if(threads.get(i).thread.getId() == _thread.getId() 
			   && threads.get(i).line_number == _line_number 
			   && threads.get(i).processed == false)
				return i ;
		}
		return -1 ;
	}
		
	public boolean IsThreadInListB(ThreadInfo _thread) {
		for(int i = 0 ; i < threads.size(); i++) {
			if(threads.get(i).thread.getId() == _thread.getId() 
					   && threads.get(i).processed == false)
				return true ;
		}
		return false ;
	}
	
	public void UpdateState(ThreadInfo _thread, int _lineNo) {
		for(int i = 0 ; i < threads.size(); i++) {
			if(threads.get(i).thread.getId() == _thread.getId()) {
				threads.get(i).thread = _thread ;
				threads.get(i).line_number = _lineNo ;
			}
		}
	}
	
	public int IsThreadInListI(ThreadInfo _thread) {
		for(int i = 0 ; i < threads.size(); i++) {
			if(threads.get(i).thread.getId() == _thread.getId() 
					   && threads.get(i).processed == false)
				return i ;
		}
		return -1 ;
	}
	
	public int IsThreadInListIWithoutProcessedFlag(ThreadInfo _thread) {
		for(int i = 0 ; i < threads.size(); i++) {
			if(threads.get(i).thread.getId() == _thread.getId())
				return i ;
		}
		return -1 ;
	}
	
	public int size() {
		return threads.size() ;
	}
	
	public ThreadInfo getThread(int index) {
		return threads.get(index).thread ;
	}
	
	public ThreadStructure getThreadStructure(int index) {
		return threads.get(index) ;
	}
	
	public void ThreadProcessed(int index) {
		threads.get(index).processed = true ;
	}
		
	public String printList() {
		String str = "" ;
		for(int i = 0; i < threads.size() ; i++) {
			str = str + "(Thread Id:" + threads.get(i).thread.getId() + ",Line No:" + String.valueOf(threads.get(i).line_number) + ",Processed:"+  String.valueOf(threads.get(i).processed) + "); " ;
		}
		return str ;
	}
	
	public boolean getListUsed() {
		return this.ListUsed ;
	}
	
	public void setListUsed(boolean _listUsed) {
		this.ListUsed = _listUsed ;
	}
	
	public boolean getListDone() {
		return this.ListDone ;
	}
	
	public void setListDone(boolean _listDone) {
		this.ListDone = _listDone ;
	}
	
	public static void p(String str) {
		//System.out.println(str) ; 
	}
}
