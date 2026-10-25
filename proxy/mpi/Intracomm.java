package mpi;
import java.util.ArrayList;

public class Intracomm extends Comm {

	private final int SENDER = 0 ;
	private final int RECEIVER = 1 ;
	
	private static int current_num_of_msgs = -1 ;
	private static int previous_num_of_msgs = -1 ;
	
	private static int current_Thread_id = -1 ;
	private static int previous_Thread_id = -1 ;
	private static int Size = 0 ; 
	int NO_OF_MESSAGES = 0 ;
	public ArrayList<WaitingObjectClass> waitingList = new ArrayList<WaitingObjectClass>() ;
	public static int waitingListCounter = 0 ;
	
	public static int Send_To = 0 ;
	public static int Send_To_Tag = 0 ;
	public static int Receive_From = 0 ;
	public static int Receive_From_Tag = 0 ;
	
	
	public Intracomm(int NoOfThreads) {
		super(NoOfThreads) ;
	}
	
	public void setSize(int setSize) {
		Size = setSize ;
	}
	
	public int Size() {
		return Size ;
	}
	
	public static void set_threads(int NoOfThreads) {
	}
	
	public int Rank() {
		return (int) MPI.threadMap.get(Thread.currentThread().getId()) ;
	}

	public void Send(Object buffer, int offset, int count, Datatype datatype, int destination, int tag) {
		Send_To = destination ;
		Send_To_Tag = tag ;
		Message msg = new Message(NO_OF_MESSAGES++, buffer, offset, count, datatype, (int) MPI.threadMap.get(Thread.currentThread().getId()), destination, tag);
		WaitingObjectClass obj1 = getobj((int) MPI.threadMap.get(Thread.currentThread().getId()), destination, tag, RECEIVER, msg, "SENDER", 0) ;
		synchronized(obj1) {
			obj1.set_flag(false) ;
			obj1.notify() ;
		}
	}
	
	public Status Recv(Object _buf, int _offset, int _count, Datatype _datatype, int _sender, int _tag) {
		int NoOfMessages = 0 ;
		
		Receive_From = _sender ;
		Receive_From_Tag = _tag ;
		
		int tempSender = _sender ;
		int tempTag = _tag ;
		previous_num_of_msgs = current_num_of_msgs ;
		
		while(true) {
			NoOfMessages = getNoOfMessages(_sender, 
										   (int) MPI.threadMap.get(Thread.currentThread().getId()), 
										   _tag, 
										   SENDER, 
										   null, 
										   "RECEIVER") ;
			current_num_of_msgs = NoOfMessages + 0 ;
			current_Thread_id = (int) MPI.threadMap.get(Thread.currentThread().getId()) ; 
			if(NoOfMessages > 0)  break ;
		}
		
		MPI.Static_special_flag = -2 ;
		previous_Thread_id = current_Thread_id ;
		
		if((previous_Thread_id == current_Thread_id) && (current_num_of_msgs == 1) && (previous_num_of_msgs == 2)) {
			MPI.Static_special_flag = 1 ;
			current_num_of_msgs = -2 ;
			previous_num_of_msgs = -2 ;
		}
		
		int b = NoOfMessages + 0 ;
		int offset = 0 ;
		offset = offset - 0 ;
		
		WaitingObjectClass _obj1_ = getobj(_sender, (int) MPI.threadMap.get(Thread.currentThread().getId()), _tag, SENDER, null, "RECEIVER", offset) ;
		synchronized(_obj1_) {
			while(_obj1_.get_flag()) {
				p("yes 5") ;
				try {
					_obj1_.wait() ;
					_obj1_.set_used(true) ;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		Message m = _obj1_.get_message() ;
		
		if(_datatype == MPI.INT) System.arraycopy((int[])m.buffer, m.offset, _buf, _offset, _count) ;
		else if(_datatype == MPI.FLOAT) System.arraycopy((float[])m.buffer, m.offset, _buf, _offset, _count) ;
		else if(_datatype == MPI.DOUBLE) System.arraycopy((double[])m.buffer, m.offset, _buf, _offset, _count) ;
		else if(_datatype == MPI.OBJECT) System.arraycopy((String[])m.buffer, m.offset, _buf, _offset, _count) ;
		
		Status status = new Status(_sender, _tag) ;
		return status ;
	}
	
	public synchronized int getNoOfMessages(int _sender, int _receiver, int _tag, int _added_by, Message _msg, String type) {
		
		int messageCounter = 0 ;
		
		if(type.equals("RECEIVER")) {
			if(_sender != MPI.ANY_SOURCE) {
				for(int i = 0; i < waitingList.size(); i++) {
					if(waitingList.get(i).sender == _sender && waitingList.get(i).receiver == _receiver && waitingList.get(i).tag == _tag && waitingList.get(i).added_by == _added_by) {
						messageCounter++ ;
					}
				}
			} else {
				for(int j = 0; j < waitingList.size(); j++) {
					if(waitingList.get(j).receiver == _receiver && waitingList.get(j).tag == _tag && waitingList.get(j).added_by == _added_by) {
						messageCounter++ ;
					}
				}
			}
		}
		return messageCounter ; 
	}
	
	public synchronized WaitingObjectClass getobj(int _sender, int _receiver, int _tag, int _added_by, Message _msg, String type, int _offset) {
		WaitingObjectClass obj = null ;
		int offset = -1 ;
		
		for(int i = 0; i < waitingList.size(); i++) {
			if(waitingList.get(i).sender == _sender && waitingList.get(i).receiver == _receiver && waitingList.get(i).tag == _tag && waitingList.get(i).added_by == _added_by) {
				offset++ ;
				if(offset == _offset) {
					obj = waitingList.get(i) ;
					if(_added_by == RECEIVER) obj.set_message(_msg) ;
					waitingList.remove(i) ;
					waitingListCounter = waitingListCounter - 1 ;
					break ;
				}
			}
		}
		
		if(obj == null && (_sender == MPI.ANY_SOURCE || iam_(_added_by).equals("SENDER"))) {
			for(int i = 0; i < waitingList.size(); i++) {
				if(waitingList.get(i).receiver == _receiver && waitingList.get(i).tag == _tag && waitingList.get(i).added_by == _added_by) {
					offset++ ;
					if(offset == _offset) {
						obj = waitingList.get(i) ;
						if(_added_by == RECEIVER) obj.set_message(_msg) ;
						waitingList.remove(i) ;
						waitingListCounter = waitingListCounter - 1 ;
						break ;
					}
				}
			}
		}

		if(obj == null) {
			obj = new WaitingObjectClass(_sender, _receiver, _tag, _added_by, _msg) ;
			waitingList.add(obj) ;
			waitingListCounter = waitingListCounter + 1 ;
		}
		return obj ;
	}
		
	public String get_type(int added_by, int receiver, int sender) {
		if(added_by == 0) return "SENDER, "+sender ;
		else return "RECEIVER, "+receiver ;
	}
	
	public String get_type_(int added_by) {
		if(added_by == 0) return "SENDER" ;
		else return "RECEIVER" ;
	}
		
	public String iam(int added_by, int receiver, int sender)
	{
		if(added_by == 0) return "RECEIVER, "+receiver ;
		else return "SENDER, "+sender ;
	}
	
	public String iam_(int added_by)
	{
		if(added_by == 0) return "RECEIVER" ;
		else return "SENDER" ;
	}
	
	public int get_my_type(int added_by)
	{
		if(added_by == 0) return RECEIVER ;
		else return SENDER ;
	}
	
	public static void p(String s) {
		//System.out.println(s) ;
	}
	
	public void printList() {
		
		for(int i = 0; i < waitingList.size(); i++) {
			/*
			p("Sender: "+waitingList.get(i).sender+
			  " Receiver: "+waitingList.get(i).receiver+
			  " Tag: "+waitingList.get(i).tag+
			  " Added_by: "+get_type_(waitingList.get(i).added_by)) ; /**/
		}
	}
}
