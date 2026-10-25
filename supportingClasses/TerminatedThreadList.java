package gov.nasa.jpf.supportingClasses;

import gov.nasa.jpf.vm.ThreadInfo ;

import java.util.ArrayList;

public class TerminatedThreadList {
	
	public static ArrayList<ThreadInfo> TerminatedThreads = new ArrayList<ThreadInfo>() ;
	
	static public void add_Thread(ThreadInfo _ti) {
		TerminatedThreads.add(_ti) ;
	}
	
	static public ThreadInfo[] Filter_Terminated_Threads(ThreadInfo[] _newChoices1) {
		ArrayList<ThreadInfo> tempList = new ArrayList<ThreadInfo>() ;
		for(int i = 0 ; i < _newChoices1.length ; i++) {
			if(!TerminatedThreads.contains(_newChoices1[i])) {
				tempList.add(_newChoices1[i]) ;
			}
		}
		ThreadInfo[] FinalArray = new ThreadInfo[tempList.size()] ;
		for(int i = 0; i < tempList.size(); i++)
			FinalArray[i] = tempList.get(i) ;
		
		return FinalArray ;
	}	
}
