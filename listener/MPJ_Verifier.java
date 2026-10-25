package gov.nasa.jpf.listener;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.supportingClasses.*;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.DynamicElementInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.Memento;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.ThreadChoiceFromSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.HashSet;
import java.util.IdentityHashMap;


class TempClass 
{
	public ArrayList<Memento<ThreadInfo>> threadInfo ;
	public Object RestorableSS ;
	TempClass(ArrayList<Memento<ThreadInfo>> _threadInfo, Object _RestorableSS) 
	{
		this.threadInfo = _threadInfo ;
		this.RestorableSS = _RestorableSS ;
	}
}

class PendingSend
{
	int threadId ;
	int receiverId ;
	int tag ;
	PendingSend(int _id, int _receiverId, int _tag) {
		threadId = _id ;
		receiverId = _receiverId ;
		tag = _tag ;
	}
}

public class MPJ_Verifier extends ListenerAdapter 
{
	/*
	 * Unsafe send check. Every completed send is kept as a pending send until a
	 * matching receive completes. Under synchronous (zero buffer) semantics a
	 * process is blocked on its earliest pending send until the destination
	 * posts a matching receive. If following the earliest pending sends from
	 * the destination leads back to the sending process, every process on that
	 * walk is blocked on a send whose destination is itself blocked on a send,
	 * so none of them can proceed. The pending list is saved per choice
	 * generator and restored on backtracking, like NumberOfMessages.
	 */
	private static final int ANY_SOURCE = 100 ;
	public static ArrayList<PendingSend> PendingSends = new ArrayList<PendingSend>() ;
	private IdentityHashMap<ChoiceGenerator<?>, ArrayList<PendingSend>> pendingMap = new IdentityHashMap<ChoiceGenerator<?>, ArrayList<PendingSend>>() ;
	public boolean checkUnsafeSend = true ;

	Random random;
	boolean singleChoice = true;
	protected VM vm;
	protected boolean breakSingleChoice;
	protected boolean breakLockRelease;
	public static int TerminateCounter = 0 ;
	public static int JustCounter = 0 ;
	public static int WaitingListCounter1 = 0 ;
	public static int WaitingListCounter2 = 0 ;
	public static int OFFSET = 2 ;
	public static ArrayList<ChoiceGenerator<?>> CGlist = new ArrayList<ChoiceGenerator<?>>() ;
	public static ArrayList<ChoiceGenerator<?>> LoggedCGlist = new ArrayList<ChoiceGenerator<?>>() ;
	public static CGClass3 CGList = new CGClass3() ;
	
	public static ArrayList<Integer> Receivers = new ArrayList<Integer>() ;
	public static boolean TerminatorFlag = false ;
	public static StateTree sTree ;
	public static boolean sTree_Init = false ;
	public static ArrayList<ThreadInfo> threadLog = new ArrayList<ThreadInfo>() ;
	public static Search sSearch = null ;
	
	public static int NumberOfMessages = 0 ;
	HashMap<String, Integer> myMap = new HashMap<String, Integer>();
	
	public MPJ_Verifier (Config config, JPF jpf) {
		random = new Random() ;
		breakSingleChoice = config.getBoolean("cg.break_single_choice", false) ;    
		breakLockRelease = config.getBoolean("cg.break_lock_release", true) ;
		vm = jpf.getVM() ;
		sSearch = jpf.getSearch(); 
	}

	@Override
	public void threadWaiting (VM vm, ThreadInfo waitingThread) {
		p("Thread Waiting ID: <"+waitingThread.getId()+">") ;
		
	}
	
	@Override
	public void stateAdvanced(Search search) {
		
		if(search.getVM().getStateId() == 0) {
			sTree = new StateTree() ;
			sTree_Init = true ;
		} else {
			sTree.StateAdvance(search.getVM().getStateId(), threadLog) ;
		}
	}

	@Override
	public void stateProcessed(Search search) {
		//p("State Processed -> State ID: <" + search.getVM().getStateId() + ">") ;
		sTree.StateProcessed(search.getVM().getStateId()) ;
		p("Error -- Main_waitingListCounter: " + String.valueOf(NumberOfMessages)) ;
	}
	
	@Override
	public void stateBacktracked(Search search) {
		p("State Backtracked -> State ID: <" + search.getVM().getStateId() + ">") ;
		sTree.StateBackTrack(search.getVM().getStateId()) ;
		threadLog = new ArrayList<ThreadInfo>(sTree.getThreadLog()) ;
		
		Helper.PrintArrayList(threadLog) ; 
	}

	@Override
	public void methodExited (VM vm, ThreadInfo currentThread, MethodInfo exitedMethod) {
		if(exitedMethod.toString().contains("mpi.Intracomm.Send")) {
			WaitingListLog.clear() ;
			WaitingListStatus(vm, currentThread) ;
			NumberOfMessages++ ;
			p("this is emd of send method ++++++++++++++++++++++++++++ NumberOfMessages: " + NumberOfMessages) ;
			if(checkUnsafeSend) {
				int rank = currentThread.getId() - 1 ;
				int sendTo = (int) currentThread.getTopFrame().getFieldValue("Send_To") ;
				int sendToTag = (int) currentThread.getTopFrame().getFieldValue("Send_To_Tag") ;
				PendingSends.add(new PendingSend(rank, sendTo, sendToTag)) ;
				if(isSendCycle(rank)) {
					System.out.println("Deadlock (unsafe send): cycle of blocked sends " + cycleToString(rank)) ;
					vm.getSearch().terminate() ;
				}
			}
		} else if(exitedMethod.toString().contains("mpi.Intracomm.Recv"))	{
			p("this is end of received method") ;
			NumberOfMessages-- ;
			p("this is emd of receive method ++++++++++++++++++++++++++++ NumberOfMessages: " + NumberOfMessages) ;
			if(checkUnsafeSend) {
				int rank = currentThread.getId() - 1 ;
				int from = (int) currentThread.getTopFrame().getFieldValue("Receive_From") ;
				int fromTag = (int) currentThread.getTopFrame().getFieldValue("Receive_From_Tag") ;
				removeMatchedSend(rank, from, fromTag) ;
			}
		}
	}

	private void removeMatchedSend(int receiverId, int senderId, int tag) {
		for(int i = 0 ; i < PendingSends.size() ; i++) {
			PendingSend s = PendingSends.get(i) ;
			if(s.receiverId == receiverId && s.tag == tag && (senderId == ANY_SOURCE || s.threadId == senderId)) {
				PendingSends.remove(i) ;
				return ;
			}
		}
	}

	private PendingSend earliestPendingSend(int threadId) {
		for(int i = 0 ; i < PendingSends.size() ; i++) {
			if(PendingSends.get(i).threadId == threadId) return PendingSends.get(i) ;
		}
		return null ;
	}

	public boolean isSendCycle(int startId) {
		PendingSend s = earliestPendingSend(startId) ;
		if(s == null) return false ;
		HashSet<Integer> seen = new HashSet<Integer>() ;
		seen.add(startId) ;
		int cur = s.receiverId ;
		while(true) {
			if(cur == startId) return true ;
			if(seen.contains(cur)) return false ;
			seen.add(cur) ;
			PendingSend next = earliestPendingSend(cur) ;
			if(next == null) return false ;
			cur = next.receiverId ;
		}
	}

	private String cycleToString(int startId) {
		StringBuilder sb = new StringBuilder() ;
		int cur = startId ;
		do {
			sb.append(cur).append(" -> ") ;
			PendingSend next = earliestPendingSend(cur) ;
			if(next == null) break ;
			cur = next.receiverId ;
		} while(cur != startId) ;
		sb.append(startId) ;
		return sb.toString() ;
	}
	
	@Override
	public void methodEntered (VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
		
		if(enteredMethod.toString().contains("mpi.Intracomm.Recv"))	{
			
			p("0. ****************************************** .0") ;
			SenderTag.set(currentThread) ;
			
			if(!sTree.isThreadExists(currentThread)) {
				
				p("1. ****************************************** .1") ;
				sTree.AddThread(currentThread) ;
				ThreadInfo[] Choices = currentThread.getVM().getThreadList().getTimeoutRunnables();
  				ThreadInfo[] newChoices = sTree.filterThreads(Choices) ;
  				
  				if(newChoices.length == 0) {
  					p("2. ****************************************** .2") ;
  					newChoices = sTree.GetAllThreadsArray() ;
	  			}
  				vm.getSystemState().setNextChoiceGenerator(new ThreadChoiceFromSet("Id "+ThreadId.getId(), newChoices, true)) ;
			} else {
				p("3. ****************************************** .3") ;
				WaitingListLog.clear() ;
				WaitingListStatus(vm, currentThread) ;
				WaitingListLog.print() ;
				//p(ThreadInfotoString(Helper.ArrayListToArray(threadLog))) ;
				if(WaitingListLog.isThreadwithMessageAndDecrementCounter(currentThread.getId(),
																	     SenderTag.getSender(),
																	     SenderTag.getTag())) {
					p("4. ****************************************** .4") ;
					threadLog.add(currentThread) ;
					//threadLogSize = threadLog.size() ;
					Helper.PrintArray(currentThread.getVM().getThreadList().getTimeoutRunnables());
					Helper.PrintArrayList(threadLog);
					ThreadInfo[] tThreads = sTree.filterThreadsArrayList(threadLog, currentThread.getVM().getThreadList().getTimeoutRunnables()) ;
					Helper.PrintArray(tThreads) ;
					ThreadChoiceFromSet CG = null ;
					if(tThreads.length == 0) {
						 CG = new ThreadChoiceFromSet("New Id "+ThreadId.getId(), Helper.ArrayListToArray(threadLog), true) ;
					} else {
						 CG = new ThreadChoiceFromSet("New Id "+ThreadId.getId(), tThreads, true) ;
					}
					vm.getSystemState().setNextChoiceGenerator(CG) ;
	  				TerminatorFlag = true ;
				} else {
					p("5. ****************************************** .5") ;
					int current_num_of_msgs = (int) currentThread.getTopFrame().getFieldValue("current_num_of_msgs") ;
					int previous_num_of_msgs = (int) currentThread.getTopFrame().getFieldValue("previous_num_of_msgs") ;
					System.out.println("previous_num_of_msgs: " + String.valueOf(previous_num_of_msgs) + "current_num_of_msgs: "+String.valueOf(current_num_of_msgs) ) ;
				}
			}
		} else if(enteredMethod.toString().contains("mpi.Intracomm.Send")) {
			//p("this is send method") ;
			//NumberOfMessages++ ;
			//p("NumberOfMessages: <" + NumberOfMessages) ;
		}
	}
		
	/*
	 * Getting WaitingList Status
	 */
	public void WaitingListStatus(VM vm, ThreadInfo currentThread) {
		int waitingListCounter = (int) currentThread.getTopFrame().getFieldValue("waitingListCounter") ;
		Object obj = currentThread.getTopFrame().getLocalOrFieldValue("waitingList") ;
		if(obj instanceof DynamicElementInfo) {
			DynamicElementInfo obj1 = (DynamicElementInfo)obj ;
			DynamicElementInfo obj2 = (DynamicElementInfo)obj1.getFieldValueObject("elementData") ;
			if(waitingListCounter > 0) {
				for(int i = 0 ; i < waitingListCounter ; i++) {
					int sender = (int) currentThread.getElementInfo(obj2.getReferenceElement(i)).getFieldValueObject("sender") ;
					int receiver = (int) currentThread.getElementInfo(obj2.getReferenceElement(i)).getFieldValueObject("receiver") ;
					int tag = (int) currentThread.getElementInfo(obj2.getReferenceElement(i)).getFieldValueObject("tag") ;
					WaitingListLog.add(sender, receiver, tag) ;
				}
			}
		}
	}
	
	
	
	@Override
	public void choiceGeneratorProcessed (VM vm, ChoiceGenerator<?> processedCG) {
		p(JustCounter+". Processed CG -> CG: <"+processedCG.getId()+"> ("+processedCG.getProcessedNumberOfChoices()+"/"+processedCG.getTotalNumberOfChoices()+") "+
				      "Thread ID: <"+processedCG.getThreadInfo().getId()+"> State No: "+vm.getStateId()+"> ThreadLog: "+threadLog.size()) ;
		JustCounter++ ;
	}

	// First Method to be called
	@Override
	public void choiceGeneratorAdvanced (VM vm, ChoiceGenerator<?> currentCG) {
		
		p2("CG Advance -> CG: <" + currentCG.getId() + "> ("+currentCG.getProcessedNumberOfChoices()+"/"+currentCG.getTotalNumberOfChoices()+
		    ") Choice to be processed: <"+currentCG.getNextChoice().toString()+"> ") ;
		p("              Current Thread ID: <"+currentCG.getThreadInfo().toString()+"> State No: "+vm.getStateId()) ;
		
		if(!currentCG.getId().toString().contains("ROOT")) {
			NumberOfMessages = myMap.get(currentCG.getId().toString()) ;
		}
		if(pendingMap.containsKey(currentCG)) {
			PendingSends = new ArrayList<PendingSend>(pendingMap.get(currentCG)) ;
		}
		
		ArrayList<ThreadInfo> tempList = new ArrayList<ThreadInfo>() ;
		
		boolean tempFlag = true ;
		
		while(true) {
			
			if(currentCG.getNextChoice() instanceof ThreadInfo) {
				
				// Current Thread to be executed
				ThreadInfo ctempThreadInfo =  (ThreadInfo) currentCG.getThreadInfo() ;
				p("++++ Current Thread" + ctempThreadInfo.toString()) ;
					
				// Next Thread to be Executed
				ThreadInfo tempThreadInfo =  (ThreadInfo) currentCG.getNextChoice() ;
				p("++++ Next Thread" + tempThreadInfo.toString()) ;
				
				if(Helper.isArrayListContainThread(tempThreadInfo, tempList)) {
					p("Here 1") ; 
					p("Log Size:" + String.valueOf(threadLog.size())) ;
					Helper.PrintArrayList(tempList);
					threadLog.remove(tempThreadInfo) ;
					if(threadLog.size() == 0) 
						TerminatorFlag = false ;
				}
				
				if(Helper.isArrayListContainThread(tempThreadInfo, threadLog)) {
					p("Here 2") ;
					tempList.add(tempThreadInfo) ;
	 				currentCG.advance();
				} else if(vm.getStateId() != -1 && tempFlag && sTree.isThreadExists(tempThreadInfo)) {
					p("Here 3") ;
					currentCG.advance() ; // Main change
					tempList.add(tempThreadInfo) ;
					tempFlag = false ;
				} else {
					p("Here 4") ;
					break ;
				}
			}
		}
	}
	
	@Override
	public void threadBlocked (VM vm, ThreadInfo blockedThread, ElementInfo lock) {
		p("++++ blocked Thread" + blockedThread.toString()) ;
	}
	
	 @Override
	 public void instructionExecuted(VM vm, ThreadInfo ti, Instruction nextInsn, Instruction executedInsn) {
	    //p(nextInsn.toString()) ;
	    //p(executedInsn.toString()) ;
	    if(sTree_Init && sTree.isThreadExists(ti) && (threadLog.size() == 0) && executedInsn.toString().contains("getNoOfMessages")) {
	    	//p("++++" + ti.toString()) ;
			//int current_num_of_msgs = (int) ti.getTopFrame().getFieldValue("current_num_of_msgs") ;
			//int previous_num_of_msgs = (int) ti.getTopFrame().getFieldValue("previous_num_of_msgs") ;
			//System.out.println("previous_num_of_msgs: " + String.valueOf(previous_num_of_msgs) + "current_num_of_msgs: "+String.valueOf(current_num_of_msgs) ) ;
	    	//int waitingListCounter = (int) ti.getTopFrame().getFieldValue("waitingListCounter") ;
	    	//System.out.println("waitingListCounter: "+ String.valueOf(waitingListCounter));
	    	
	    	SenderTag.set(ti) ;
	    	
	    	//p(String.valueOf(ti.getId()) + " " + String.valueOf(SenderTag.getSender()) + " " + String.valueOf(SenderTag.getTag())) ;
	    	
	    	WaitingListLog2.clear() ;
			WaitingListStatus1(vm, ti) ;
			WaitingListLog2.print() ;
			if(WaitingListLog2.isThreadwithMessage(ti.getId(), SenderTag.getSender(), SenderTag.getTag())) {
				p("IExecuted if") ;
			} else {
				p("IExecuted else 1") ;
				//vm.getSearch().terminate() ;
				ti.breakTransition("Deadlock") ;
				vm.getSearch().terminate() ;
				//sSearch.terminate() ;
			}
		}
	   }
	 
	 public void WaitingListStatus1(VM vm, ThreadInfo currentThread) {
			int waitingListCounter = (int) currentThread.getTopFrame().getFieldValue("waitingListCounter") ;
			System.out.println("waitingListCounter: " + String.valueOf(waitingListCounter));
			Object obj = currentThread.getTopFrame().getLocalOrFieldValue("waitingList") ;
			if(obj instanceof DynamicElementInfo) {
				DynamicElementInfo obj1 = (DynamicElementInfo)obj ;
				DynamicElementInfo obj2 = (DynamicElementInfo)obj1.getFieldValueObject("elementData") ;
				if(waitingListCounter > 0) {
					for(int i = 0 ; i < waitingListCounter ; i++) {
						int sender = (int) currentThread.getElementInfo(obj2.getReferenceElement(i)).getFieldValueObject("sender") ;
						int receiver = (int) currentThread.getElementInfo(obj2.getReferenceElement(i)).getFieldValueObject("receiver") ;
						int tag = (int) currentThread.getElementInfo(obj2.getReferenceElement(i)).getFieldValueObject("tag") ;
						System.out.println("sender: " + String.valueOf(sender) + " receiver: " + String.valueOf(receiver) + " tag: " + String.valueOf(tag));
						WaitingListLog2.add(sender, receiver, tag) ;
					}
				}
			}
	}
	/**/

	// Second Method to be Called
	@Override
	public void choiceGeneratorRegistered (VM vm, ChoiceGenerator<?> nextCG, ThreadInfo ti, Instruction executedInsn) {
		
		p2("New CG Registered -> CG: <"+nextCG.getId()+"> ("+nextCG.getProcessedNumberOfChoices()+"/"+nextCG.getTotalNumberOfChoices()+") "
		 + "Thread ID: <"+ti.getId() +"> Type: <"+nextCG.getClass()+"> State No: "+vm.getStateId()) ;
		
		pendingMap.put(nextCG, new ArrayList<PendingSend>(PendingSends)) ;
		
		if(nextCG.getId().contains("TERMINATE ")) {
			p("Stopped Threads: "+ThreadInfotoString(Helper.ArrayListToArray(threadLog)) + 
			  " Runnable Threads: "+ThreadInfotoString(ti.getVM().getThreadList().getTimeoutRunnables()) + 
			  " TerminatorFlag: <"+TerminatorFlag+">") ;
			myMap.put(nextCG.getId().toString(), NumberOfMessages) ;
		} else if(nextCG.getId().contains("MAX_TRANSITION_LENGTH")) {
			nextCG.setDone() ;
			vm.getSearch().terminate() ;
		} else if(nextCG.getId().toString().contains("Id ")) {
			myMap.put(nextCG.getId().toString(), NumberOfMessages) ;
		}
	}
	
	public String ThreadInfotoString(ThreadInfo[] choices) {
		String str = "{" ;
		for(int i = 0 ; i < choices.length; i++)
			str = str + String.valueOf(choices[i].getId()) + " " ;
		str = str.trim() ;
		return str + "}" ;
	}
	
	public void print(int source_line_number, String current_CG_id, String cg_created, int current_thread_id, 
			  int current_choices_length, String current_choices, int new_choices_length, String _new_choices) {
		p("/*******************************INVOKEVIRTUAL***************************************/") ;
		p("/**** Source Line: "+source_line_number+" --- Current CG: "+current_CG_id+" --- Current Thread Id: "+current_thread_id+" ****/") ;
		p("/**** Current Choices Length: "+current_choices_length+" --- Current Choices: "+current_choices+" ****/") ;
		p("/**** New CG Id: "+cg_created+" --- New Choices Length: "+new_choices_length+" --- New Choices: "+_new_choices+" ****/") ;
		p("/**********************************************************************************/") ;
	}
	
	public void p(String str) 
	{	
		System.out.println(str) ;
	}
	public void p2(String str) 
	{	
		System.out.println(str) ;
	}
	
	
	public void p(int str) 
	{	
		System.out.println(String.valueOf(str)) ;
	}
	
	public void psi(String str, int a)
	{	
		System.out.println(str + String.valueOf(a)) ;
	}
		
}
