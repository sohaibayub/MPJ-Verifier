package mpi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MPI {

	public static Intracomm COMM_WORLD = new Intracomm(Integer.valueOf(0)) ;
	public static int ANY_SOURCE = 100 ;
	public static boolean COMM_WORLD_FLAG = true ;
	private final static Object LOCK1 = new Object();
	public static Map <Long, Integer> threadMap = new HashMap<Long, Integer>();
	public static ArrayList<MessageQue> Que = new ArrayList<MessageQue>() ;
	public static ArrayList<ThreadParking> ParkingQue = new ArrayList<ThreadParking>() ;
	public static int COMM_WORLD_SIZE = 0 ;
	public static int Static_special_flag = -2 ; 
	
	public static int UNDEFINED = -1 ;
	public static Datatype NULL = new Datatype(0);
	public static Datatype BYTE = new Datatype(1);
	public static Datatype CHAR = new Datatype(2);
	public static Datatype SHORT = new Datatype(3);
	public static Datatype BOOLEAN = new Datatype(4);
	public static Datatype INT = new Datatype(5);
	public static Datatype LONG = new Datatype(6);
	public static Datatype FLOAT = new Datatype(7);
	public static Datatype DOUBLE = new Datatype(8);
	public static Datatype PACKED = new Datatype(9);
	public static Datatype LB = new Datatype(10);
	public static Datatype UB = new Datatype(11);
	public static Datatype OBJECT = new Datatype(12);
	
	public static String[] Init(String[] argv) {
		synchronized(LOCK1) {
			threadMap.put(Long.valueOf(argv[2]), Integer.valueOf(argv[1])) ;
			COMM_WORLD.setSize(Integer.valueOf(argv[0])) ;
		}
		String[] nargs = null ;
		if(Que.size() == 0) {
			for(int i = 0 ; i < Integer.valueOf(argv[0]) ; i++) {
				Que.add(new MessageQue()) ;
			}
		}
		return nargs ;
	}
	
	public MPI() {
	}
	
	public static void Finalize() {
		int Special_flag = Static_special_flag ;
		int a = Special_flag + 0 ; 
		Static_special_flag = -2 ;
		Special_flag = -2 ; 
	}
	
	public static void p(String s) {
		System.out.println(s) ;
	}
}