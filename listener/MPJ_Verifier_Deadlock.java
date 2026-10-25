package gov.nasa.jpf.listener;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.supportingClasses.*;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.ThreadChoiceFromSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * Deadlock detection for MPI Java programs under synchronous (zero buffer)
 * send semantics.
 *
 * A blocking send completes only when the destination process posts a
 * matching receive. A process whose earliest unmatched send is addressed to
 * a process that is itself blocked on an unmatched send cannot make progress.
 * A deadlock exists when these wait-for edges form a cycle. The listener logs
 * every completed send and receive, removes a send from the log as soon as a
 * matching receive completes, and after each send walks the chain of earliest
 * unmatched sends starting at the destination. If the walk returns to the
 * sending process, the cycle is reported.
 */

class SenderLogClass {

	ThreadInfo thread ;
	int threadId ;
	int receiverId ;
	int tag ;

	SenderLogClass(ThreadInfo _thread, int _id, int _receiverId, int _tag) {
		thread = _thread ;
		threadId = _id ;
		receiverId = _receiverId ;
		tag = _tag ;
	}
}

class ReceiverLogClass {

	int threadId ;
	int senderId ;
	int tag ;

	ReceiverLogClass(int _id, int _senderId, int _tag) {
		threadId = _id ;
		senderId = _senderId ;
		tag = _tag ;
	}
}

public class MPJ_Verifier_Deadlock extends ListenerAdapter
{
	private static final int ANY_SOURCE = 100 ;

	protected VM vm ;
	public static Search sSearch = null ;

	public static ArrayList<SenderLogClass> SenderLog = new ArrayList<SenderLogClass>() ;
	public static ArrayList<ReceiverLogClass> ReceiverLog = new ArrayList<ReceiverLogClass>() ;

	// Logs are saved per choice generator so that backtracking restores them.
	private HashMap<String, ArrayList<SenderLogClass>> senderLogMap = new HashMap<String, ArrayList<SenderLogClass>>() ;
	private HashMap<String, ArrayList<ReceiverLogClass>> receiverLogMap = new HashMap<String, ArrayList<ReceiverLogClass>>() ;

	public MPJ_Verifier_Deadlock (Config config, JPF jpf) {
		vm = jpf.getVM() ;
		sSearch = jpf.getSearch() ;
	}

	@Override
	public void methodExited (VM vm, ThreadInfo currentThread, MethodInfo exitedMethod) {

		int threadId = currentThread.getId() - 1 ;

		if(exitedMethod.toString().contains("mpi.Intracomm.Send")) {

			int SendTo = (int) currentThread.getTopFrame().getFieldValue("Send_To") ;
			int SendToTag = (int) currentThread.getTopFrame().getFieldValue("Send_To_Tag") ;

			p("+++++++ Send_To : " + SendTo + " Send_To_Tag : " + SendToTag + " +++++++") ;
			p("currentThread: " + threadId) ;

			SenderLog.add(new SenderLogClass(currentThread, threadId, SendTo, SendToTag)) ;

			if(isSendCycle(threadId)) {
				p("Deadlock") ;
				p("Cycle of blocked sends: " + cycleToString(threadId)) ;
				vm.getSearch().terminate() ;
				return ;
			}

			ThreadInfo[] runableChoices = Helper.getAllThreadsExcept(currentThread, currentThread.getVM().getThreadList().getTimeoutRunnables()) ;
			if(runableChoices.length > 0) {
				vm.getSystemState().setNextChoiceGenerator(new ThreadChoiceFromSet("Id " + currentThread.getId(), runableChoices, true)) ;
			}

		} else if(exitedMethod.toString().contains("mpi.Intracomm.Recv")) {

			int ReceiveFrom = (int) currentThread.getTopFrame().getFieldValue("Receive_From") ;
			int ReceiveFromTag = (int) currentThread.getTopFrame().getFieldValue("Receive_From_Tag") ;

			p("******* Receive_From : " + ReceiveFrom + " receive from tag " + ReceiveFromTag + " *******") ;

			ReceiverLog.add(new ReceiverLogClass(threadId, ReceiveFrom, ReceiveFromTag)) ;
			removeMatchedSend(threadId, ReceiveFrom, ReceiveFromTag) ;
		}
	}

	/*
	 * A completed receive consumes the earliest unmatched send addressed to
	 * this process with the same tag and, unless the receive is a wildcard,
	 * the same source.
	 */
	private void removeMatchedSend(int receiverId, int senderId, int tag) {
		for(int i = 0 ; i < SenderLog.size() ; i++) {
			SenderLogClass s = SenderLog.get(i) ;
			if(s.receiverId == receiverId && s.tag == tag && (senderId == ANY_SOURCE || s.threadId == senderId)) {
				SenderLog.remove(i) ;
				return ;
			}
		}
	}

	/*
	 * Earliest unmatched send of a process, or null when the process has none.
	 */
	private SenderLogClass earliestPendingSend(int threadId) {
		for(int i = 0 ; i < SenderLog.size() ; i++) {
			if(SenderLog.get(i).threadId == threadId) {
				return SenderLog.get(i) ;
			}
		}
		return null ;
	}

	/*
	 * Follow wait-for edges from the destination of the current process's
	 * earliest unmatched send. Every process on the walk is blocked on an
	 * unmatched send, so if the walk returns to the starting process none of
	 * them can proceed.
	 */
	public boolean isSendCycle(int startId) {
		SenderLogClass s = earliestPendingSend(startId) ;
		if(s == null) return false ;
		HashSet<Integer> seen = new HashSet<Integer>() ;
		seen.add(startId) ;
		int cur = s.receiverId ;
		while(true) {
			if(cur == startId) return true ;
			if(seen.contains(cur)) return false ;
			seen.add(cur) ;
			SenderLogClass next = earliestPendingSend(cur) ;
			if(next == null) return false ;
			cur = next.receiverId ;
		}
	}

	private String cycleToString(int startId) {
		StringBuilder sb = new StringBuilder() ;
		int cur = startId ;
		do {
			sb.append(cur).append(" -> ") ;
			SenderLogClass next = earliestPendingSend(cur) ;
			if(next == null) break ;
			cur = next.receiverId ;
		} while(cur != startId) ;
		sb.append(startId) ;
		return sb.toString() ;
	}

	@Override
	public void choiceGeneratorRegistered (VM vm, ChoiceGenerator<?> nextCG, ThreadInfo ti, Instruction executedInsn) {
		senderLogMap.put(nextCG.getId().toString(), new ArrayList<SenderLogClass>(SenderLog)) ;
		receiverLogMap.put(nextCG.getId().toString(), new ArrayList<ReceiverLogClass>(ReceiverLog)) ;
	}

	@Override
	public void choiceGeneratorAdvanced (VM vm, ChoiceGenerator<?> currentCG) {
		String id = currentCG.getId().toString() ;
		if(senderLogMap.containsKey(id)) {
			SenderLog = new ArrayList<SenderLogClass>(senderLogMap.get(id)) ;
			ReceiverLog = new ArrayList<ReceiverLogClass>(receiverLogMap.get(id)) ;
		}
	}

	public void p(String str)
	{
		System.out.println(str) ;
	}
}
