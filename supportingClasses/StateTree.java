package gov.nasa.jpf.supportingClasses;

import gov.nasa.jpf.vm.ThreadInfo;

import java.util.ArrayList;

public class StateTree
{
	private static StateNode Root ;
	private static StateNode CurrentNode ;

	public StateTree() {
		Root = new StateNode();
	    Root.Children = new ArrayList<StateNode>() ;
	    Root.Parent = null ;
	    Root.state_id = 0 ;
	    Root.threads = new ArrayList<ThreadInfo>() ;
	    Root.parent_threads = new ArrayList<ThreadInfo>() ;
	    Root.ThreadLog = new ArrayList<ThreadInfo>() ;
	    CurrentNode = Root ;
	}
	    
	public void StateAdvance(int _state_id, ArrayList<ThreadInfo> _threadLog) {
		StateNode node = new StateNode() ;
		node.Children = new ArrayList<StateNode>() ;
	    node.Parent = CurrentNode ;
	    node.parent_threads = new ArrayList<ThreadInfo>() ;
	    node.threads = new ArrayList<ThreadInfo>() ;
	    node.state_id = _state_id ;
	    node.ThreadLog = new ArrayList<ThreadInfo>(_threadLog) ;
	    Helper.PrintArrayList(node.ThreadLog);
	    getParentThreads(node) ;
	    CurrentNode.Children.add(node) ;
	    CurrentNode = node ;
	}
	    
	public void StateBackTrack(int _state_id) {
	    if(_state_id != -1) {
			CurrentNode = CurrentNode.Parent ;
		    CurrentNode.threads.clear();
		    if(CurrentNode.state_id != _state_id) {
		    	p("#### Error ####") ;
		    }
	    }
	}
	
	public ArrayList<ThreadInfo> getThreadLog() {
		Helper.PrintArrayList(CurrentNode.ThreadLog) ;
		pr("State Id: "+CurrentNode.state_id) ;
		return CurrentNode.ThreadLog ;
	}
	
	public ThreadInfo[] filterThreadsWithMessagesInQue(ThreadInfo[] _threadsInfo) {
		ArrayList<ThreadInfo> tempList = new ArrayList<ThreadInfo>() ;
		if(_threadsInfo.length > 0) {
			for(int i = 0 ; i < _threadsInfo.length ; i++) {
				if(WaitingListLog.isThreadwithMessage(_threadsInfo[i].getId())) {
					tempList.add(_threadsInfo[i]) ;
				}
			}
		}
		return Helper.ArrayListToArray(tempList) ;
	}
	
	public void StateProcessed(int _state_id) {
	
	}
	    
	public void AddThread(ThreadInfo _thread) {
		CurrentNode.threads.add(_thread) ;
	}
	
	private void getParentThreads(StateNode _node) {
		if(CurrentNode.parent_threads.size() > 0) {
			for(int i = 0 ; i < CurrentNode.parent_threads.size() ; i++) {
	    		_node.parent_threads.add(CurrentNode.parent_threads.get(i)) ;
	    	}
	    }
		if(CurrentNode.threads.size() > 0) {
	    	for(int i = 0 ; i < CurrentNode.threads.size() ; i++) {
	    		_node.parent_threads.add(CurrentNode.threads.get(i)) ;
	    	}
	    }
	}
	
	public void ClearList() {
		CurrentNode.threads.clear() ;
	}
	      
	public boolean isThreadExists(ThreadInfo _threadInfo) {
		return Helper.isArrayListContainThread(_threadInfo, getNodeThreadsArrayList(CurrentNode)) ;
	}
	
	public ThreadInfo[] filterThreads(ThreadInfo[] _threadsInfo) {
		ArrayList<ThreadInfo> threadList = getNodeThreadsArrayList(CurrentNode) ;
		ArrayList<ThreadInfo> filteredList = new ArrayList<ThreadInfo>() ;
		if(_threadsInfo.length > 0) {
			for(int i = 0 ; i < _threadsInfo.length ; i++) {
				if(!Helper.isArrayListContainThread(_threadsInfo[i], threadList)) {
					filteredList.add(_threadsInfo[i]) ;
				}
			}
		}
		return Helper.ArrayListToArray(filteredList) ;	
	}
	
	public ThreadInfo[] filterThreadsArrayList(ArrayList<ThreadInfo> threadList, ThreadInfo[] _threadsInfo) {
		ArrayList<ThreadInfo> filteredList = new ArrayList<ThreadInfo>() ;
		if(_threadsInfo.length > 0) {
			for(int i = 0 ; i < _threadsInfo.length ; i++) {
				if(!Helper.isArrayListContainThread(_threadsInfo[i], threadList)) {
					filteredList.add(_threadsInfo[i]) ;
				}
			}
		}
		return Helper.ArrayListToArray(filteredList) ;	
	}
	
	public ThreadInfo[] GetAllThreadsArray() {
		return getNodeThreadsArray(CurrentNode) ;
	}
	
	public ArrayList<ThreadInfo> GetAllThreadsArrayList() {
		return getNodeThreadsArrayList(CurrentNode) ;
	}
	    
	public void printCompleteTree() {
		printTree(Root, 0) ;
	}
	
	public void printTree(StateNode _node) {
		printTree(_node, 0) ;
	}
	
	private void printTree(StateNode _node, int level) {
		p(createSpace(level)+_node.state_id+" Choices: "+Helper.ThreadInfoArraytoString(getNodeThreadsArray(_node))) ;
	    if(_node.Children.size() > 0) {
	    	level = level + 1 ;
	    	for(int i = 0 ; i < _node.Children.size() ; i++)
	    		printTree(_node.Children.get(i), level) ;
	    } else {
	    	return ;
	    }
	}
	
	private String createSpace(int _level) {
		String str = "=> " ;
	    if(_level == 0) 
	    	return  str ;
	    else {
	    	for(int i = 0 ; i < _level ; i++) {
	    		str = "=" + str ;
	    	}
	    }
	    return str ;
	}
	
	private ThreadInfo[] getNodeThreadsArray(StateNode _node) {
		int size1 =  _node.parent_threads.size() ;
	    int size2 =  _node.threads.size() ;

	    ThreadInfo[] threads = new ThreadInfo[size1 + size2] ;
	    int j = 0 ;
	    if(size1 > 0) {
	    	for(int i = 0 ; i < size1 ; i++) {
	    		threads[j] = _node.parent_threads.get(i) ;
	    		j++ ;
	    	}
	    }
	    if(size2 > 0) {
	    	for(int i = 0 ; i < size2 ; i++) {
	    		threads[j] = _node.threads.get(i) ;
	    		j++ ;
	    	}
	    }
	    return threads ;
	}
	
	private ArrayList<ThreadInfo> getNodeThreadsArrayList(StateNode _node) {
		int size1 =  _node.parent_threads.size() ;
	    int size2 =  _node.threads.size() ;

	    ArrayList<ThreadInfo> tempThreads = new ArrayList<ThreadInfo>() ;
	    if(size1 > 0) {
	    	for(int i = 0 ; i < size1 ; i++) {
	    		tempThreads.add(_node.parent_threads.get(i)) ;
	    	}
	    }
	    if(size2 > 0) {
	    	for(int i = 0 ; i < size2 ; i++) {
	    		tempThreads.add(_node.threads.get(i)) ;
	    	}
	    }
	    return tempThreads ;
	}
	
	public static void pr(String str) {
		//System.out.println(str);
	}
	
	private void p(String str) {
		//System.out.println(str) ;
	}
}
