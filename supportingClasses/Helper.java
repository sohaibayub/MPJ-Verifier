package gov.nasa.jpf.supportingClasses;

import gov.nasa.jpf.vm.ThreadInfo;

import java.util.ArrayList;

public class Helper {

	public static ArrayList<ThreadInfo> mergeArrayLists(ArrayList<ThreadInfo> _list1, ArrayList<ThreadInfo> _list2) {
		ArrayList<ThreadInfo> tempArrayList = new ArrayList<ThreadInfo>() ;
		
		for(int i = 0 ; i < _list1.size() ; i++)
			tempArrayList.add(_list1.get(i)) ;
		
		for(int i = 0 ; i < _list2.size() ; i++)
			tempArrayList.add(_list2.get(i)) ;
		
		return tempArrayList ;
	}
	
	public static ThreadInfo[] mergeArrays(ThreadInfo[] _list1, ThreadInfo[] _list2) {
		
		ThreadInfo[] tempArrayList = new ThreadInfo[_list1.length + _list2.length] ;
		
		int j = 0 ;
		
		for(int i = 0 ; i < _list1.length ; i++) {
			tempArrayList[j] = _list1[i] ;
			j++ ;
		}
		
		for(int i = 0 ; i < _list2.length ; i++) {
			tempArrayList[j] = _list2[i] ;
			j++ ;
		}
		
		return tempArrayList ;
	}
	
	public static ArrayList<ThreadInfo> mergeArrayList(ThreadInfo[] _list1, ArrayList<ThreadInfo> _list2) {
		
		ArrayList<ThreadInfo> tempArrayList = new ArrayList<ThreadInfo>() ;
		
		for(int i = 0 ; i < _list1.length ; i++)
			tempArrayList.add(_list1[i]) ;
		
		for(int i = 0 ; i < _list2.size() ; i++)
			tempArrayList.add(_list2.get(i)) ;
		
		return tempArrayList ;
		
	}
	
	public static ThreadInfo[] mergeArray(ThreadInfo[] _list1, ArrayList<ThreadInfo> _list2) {
		
		ThreadInfo[] tempArrayList = new ThreadInfo[_list1.length + _list2.size()] ;
		
		int j = 0 ;
		
		for(int i = 0 ; i < _list1.length ; i++) {
			tempArrayList[j] = _list1[i] ;
			j++ ;
		}
		
		for(int i = 0 ; i < _list2.size() ; i++) {
			tempArrayList[j] = _list2.get(i) ;
			j++ ;
		}
		
		return tempArrayList ;
	}
	
	
	
	public static boolean isArrayListContainThread(ThreadInfo _threadInfo, ArrayList<ThreadInfo> _list) {
		if(_list.size() > 0) {
			for(int i = 0 ; i < _list.size() ; i++) {
				if(_list.get(i).getId() == _threadInfo.getId()) {
					return true ;
				}
			}
		}
		return false ;
	}
	
	public static boolean isArrayContainThread(ThreadInfo _threadInfo, ThreadInfo[] _list) {
		if(_list.length > 0) {
			for(int i = 0 ; i < _list.length ; i++) {
				if(_list[i].getId() == _threadInfo.getId()) {
					return true ;
				}
			}
		}
		return false ;
	}
	
	public static ArrayList<ThreadInfo> ArrayToArrayList(ThreadInfo[] _threadsInfo) {
		ArrayList<ThreadInfo> tempList = new ArrayList<ThreadInfo>() ;
		if(_threadsInfo.length > 0) {
			for(int i = 0 ; i < _threadsInfo.length ; i++) {
				tempList.add(_threadsInfo[i]) ;
			}
		}
		return tempList ;
	}
	
	public static ThreadInfo[] ArrayListToArray(ArrayList<ThreadInfo> _threadsInfo) {
		ThreadInfo[] tempArray = new ThreadInfo[_threadsInfo.size()] ;
		if(_threadsInfo.size() > 0) {
			for(int i = 0 ; i < _threadsInfo.size() ; i++) {
				tempArray[i] = _threadsInfo.get(i) ;
			}
		}
		return tempArray ;
	}
	
	public static String ThreadInfoArraytoString(ThreadInfo[] choices) {
		String str = "" ;
		if(choices.length > 0) {
			str = str + "{" ;
			for(int i = 0 ; i < choices.length; i++) {
				str = str + String.valueOf(choices[i].getId()) + " <" + choices[i].getState().toString() + ">; " ;
			}
			str = str + "}" ;
		}
		return str ;
	}
	
	public static String ThreadInfoArrayListtoString(ArrayList<ThreadInfo> choices) {
		String str = "" ;
		if(choices.size() > 0) {
			str = str + "{" ;
			for(int i = 0 ; i < choices.size(); i++) {
				str = str + String.valueOf(choices.get(i).getId()) + " <" + choices.get(i).getState().toString() + ">; " ;
			}
			str = str + "}" ;
		}
		return str ;
	}
	
	public static ThreadInfo[] getAllThreadsExcept(ThreadInfo _thread, ThreadInfo[] _threadsInfo) {
		ThreadInfo[] threadArray = new ThreadInfo[0] ;
		int j = 0 ;
		if(_threadsInfo.length > 0) {
			threadArray = new ThreadInfo[_threadsInfo.length - 1] ;
			for(int i = 0 ; i < _threadsInfo.length ; i++) {
				if(_threadsInfo[i].getId() != _thread.getId()) {
					threadArray[j] = _threadsInfo[i] ;
					j++ ;
				}
			}
		}
		return threadArray ;
	}
	
	public static void PrintArrayList(ArrayList<ThreadInfo> _ThreadInfoArrayList) {
		ThreadInfo[] threadArray = ArrayListToArray(_ThreadInfoArrayList) ;
		PrintArray(threadArray) ;
	}
	
	public static void PrintArray(ThreadInfo[] choices) {
		String str = "{" ;
		for(int i = 0 ; i < choices.length; i++)
			str = str + String.valueOf(choices[i].getId()) + " " ;
		str = str.trim() ;
		pr(str + "}") ;
	}
	
	public static void pr(String str) {	
  		//System.out.println(str) ;
	}
	
	
}
