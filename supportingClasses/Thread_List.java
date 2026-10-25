package gov.nasa.jpf.supportingClasses;

import gov.nasa.jpf.vm.ThreadInfo;

import java.util.ArrayList;


public class Thread_List {

	public static ArrayList<ThreadList1> threadlist = new ArrayList<ThreadList1>() ;
	public static int _choice = -1 ;
	
	public static void addThread(ThreadInfo _thread, int _line_number) {
		if(threadlist.size() == 0)
			threadlist.add(new ThreadList1()) ;
		
		int index = -1 ;
		for(int i = 0 ; i < threadlist.size() ; i++)
			if(threadlist.get(i).getListUsed() == false && threadlist.get(i).getListDone() == false)
				index = i ;

		if(index == -1)
			threadlist.add(new ThreadList1()) ;
		
		for(int i = 0 ; i < threadlist.size() ; i++) {
			if(threadlist.get(i).getListUsed() == false && threadlist.get(i).getListDone() == false) {
				threadlist.get(i).addThread(_thread, _line_number, false) ; 
				break ;
			}
		}
	}
	
	public static void addThread(ThreadInfo _thread) {
		if(threadlist.size() == 0)
			threadlist.add(new ThreadList1()) ;
		
		int index = -1 ;
		for(int i = 0 ; i < threadlist.size() ; i++)
			if(threadlist.get(i).getListUsed() == false && threadlist.get(i).getListDone() == false)
				index = i ;

		if(index == -1)
			threadlist.add(new ThreadList1()) ;
		
		for(int i = 0 ; i < threadlist.size() ; i++) {
			if(threadlist.get(i).getListUsed() == false && threadlist.get(i).getListDone() == false) {
				threadlist.get(i).addThread(_thread, false) ; 
				break ;
			}
		}
	}
	
	public static void UpdateThread(ThreadInfo _thread, int _line_number) {
		int index = -1 ;
		for(int i = 0 ; i < threadlist.size() ; i++) {
			if(threadlist.get(i).getListUsed() == false && threadlist.get(i).getListDone() == false) {
				index = i ;
				break ;
			}
		}
		
		if(index == -1) {
			threadlist.add(new ThreadList1()) ;
			for(int i = 0 ; i < threadlist.size() ; i++) {
				if(threadlist.get(i).getListUsed() == false && threadlist.get(i).getListDone() == false) {
					threadlist.get(i).addThread(_thread, _line_number, false) ;
					break ;
				}
			}
		} else {
			threadlist.get(index).UpdateState(_thread, _line_number) ;
		}
	}

	public static boolean isThreadInListB(ThreadInfo _thread, int _line_number) {
		if(threadlist.size() == 1) {
			return threadlist.get(0).IsThreadInListB(_thread, _line_number) ;	
		} else {
			for(int i = 0 ; i < threadlist.size() ; i++) {
				if(threadlist.get(i).getListUsed() == true && threadlist.get(i).getListDone() == false)
					return threadlist.get(i).IsThreadInListB(_thread, _line_number) ;
			}
		}
		return false ;
	}
	
	public static int isThreadInListI(ThreadInfo _thread, int _line_number) {
		if(threadlist.size() == 1) {
			return threadlist.get(0).IsThreadInListI(_thread, _line_number) ;	
		} else {
			for(int i = 0 ; i < threadlist.size() ; i++) {
				if(threadlist.get(i).getListUsed() == true && threadlist.get(i).getListDone() == false)
					return threadlist.get(i).IsThreadInListI(_thread, _line_number) ;
			}
		}
		return -1 ;
	}
	
	public static boolean isThreadInListB(ThreadInfo _thread) {
		if(threadlist.size() == 1) {
			return threadlist.get(0).IsThreadInListB(_thread) ;	
		} else {
			for(int i = 0 ; i < threadlist.size() ; i++) {
				if(threadlist.get(i).getListUsed() == true && threadlist.get(i).getListDone() == false)
					return threadlist.get(i).IsThreadInListB(_thread) ;
			}
		}
		return false ;
	}
	
	public static int isThreadInListI(ThreadInfo _thread) {
		if(threadlist.size() == 1) {
			return threadlist.get(0).IsThreadInListI(_thread) ;	
		} else {
			for(int i = 0 ; i < threadlist.size() ; i++) {
				if(threadlist.get(i).getListUsed() == true && threadlist.get(i).getListDone() == false)
					return threadlist.get(i).IsThreadInListI(_thread) ;
			}
		}
		return -1 ;
	}
	
	public static void printList() {
		for(int i = 0 ; i < threadlist.size() ; i++) {
			p("Index: "+i+"; List Used:"+String.valueOf(threadlist.get(i).getListUsed())+"; List Done:"+String.valueOf(threadlist.get(i).getListDone())) ;
			p("Threads: "+threadlist.get(i).printList()) ;
		}
	}
	
	public static int getLineNumber(ThreadInfo _thread) {
		int index = -1 ;
		for(int i = threadlist.size() -1 ; i >= 0  ; i--) {
			if(threadlist.get(i).getListUsed() == true && threadlist.get(i).getListDone() == false) {
				index = i ;
				break ;
			}
		}
		if(index != -1) {
			int temp = threadlist.get(index).IsThreadInListIWithoutProcessedFlag(_thread) ;
			return threadlist.get(index).getThreadStructure(temp).line_number ;
		} else {
			return -1  ;
		}
	}
	
	public static ThreadInfo[] GetChoices() {
		int index = -1 ;
		for(int i = 0 ; i < threadlist.size() ; i++) {
			if(threadlist.get(i).getListUsed() == false && threadlist.get(i).getListDone() == false) {
				index = i ;
				break ;
			}
		}
		if(index != -1) {
			_choice = index ;
			ArrayList<ThreadStructure> runningThreads = new ArrayList<ThreadStructure>() ;
			
			for(int i = 0 ; i < threadlist.get(index).size() ; i++)
				runningThreads.add(threadlist.get(index).getThreadStructure(i)) ;
			
			ThreadInfo[] FinalChoices = new ThreadInfo[runningThreads.size()] ;
			
			for(int i = 0 ; i < runningThreads.size(); i++)
				FinalChoices[i] = runningThreads.get(i).thread ;
			
			threadlist.get(index).setListUsed(true) ;
			return FinalChoices ;
		} else {
			ThreadInfo[] FinalChoices = new ThreadInfo[0] ;
			return FinalChoices ;
		}
	}
	
	public static ThreadInfo[] JustGetChoices() {
		int index = -1 ;
		for(int i = 0 ; i < threadlist.size() ; i++) {
			if(threadlist.get(i).getListUsed() == false && threadlist.get(i).getListDone() == false) {
				index = i ;
				break ;
			}
		}
		
		if(index != -1) {
			_choice = index ;
			ArrayList<ThreadStructure> runningThreads = new ArrayList<ThreadStructure>() ;
		
			for(int i = 0 ; i < threadlist.get(index).size() ; i++)
				runningThreads.add(threadlist.get(index).getThreadStructure(i)) ;
			
			ThreadInfo[] FinalChoices = new ThreadInfo[runningThreads.size()] ;
			
			for(int i = 0 ; i < runningThreads.size(); i++)
				FinalChoices[i] = runningThreads.get(i).thread ;
			
			return FinalChoices ;
		} else {
			ThreadInfo[] FinalChoices = new ThreadInfo[0] ;
			return FinalChoices ;
		}
	}
	
	public static ThreadInfo[] FilterChoices(ThreadInfo[] _choices) {
		int index = -1 ;
		if(threadlist.size() != 0) {
			for(int i = 0 ; i < threadlist.size() ; i++) {
				if(threadlist.get(i).getListUsed() == false && threadlist.get(i).getListDone() == false) {
					index = i ;
					break ;
				}
			}
		}
		if(index == -1) {
			ThreadInfo[] FinalChoices = new ThreadInfo[0] ;
			return FinalChoices ;
		}
		ArrayList<ThreadInfo> Choices = new ArrayList<ThreadInfo>() ;
		for(int i = 0 ; i < _choices.length ; i++) {
			if(threadlist.get(index).IsThreadInListB(_choices[i]) == false) {
				Choices.add(_choices[i]) ;
			}
		}
		ThreadInfo[] FinalChoices = new ThreadInfo[Choices.size()] ;
		for(int i = 0 ; i < FinalChoices.length ; i++)
			FinalChoices[i] = Choices.get(i) ;
		
		return FinalChoices ;
	}
	
	public static void setListDone(int _index) {
		threadlist.get(_index).setListDone(true) ;
	}

	public static void setListUsed(int _index) {
		threadlist.get(_index).setListUsed(true) ;
	}
	
	public static void ThreadProcessed(ThreadInfo ti) {
		int index = 0 ;
		if(threadlist.size() != 1) {
			for(int i = 0 ; i < threadlist.size() ; i++) {
				if(threadlist.get(i).getListUsed() == true && threadlist.get(i).getListDone() == false) {
					index = i ;
					break ;
				}
			}
		}
		for(int i = 0 ; i < threadlist.get(index).size(); i++)
			if(threadlist.get(index).getThread(i).getId() == ti.getId())
				threadlist.get(index).ThreadProcessed(i) ;
	}
	
	public static void p(String str) {
		//System.out.println(str) ; 
	}
}
