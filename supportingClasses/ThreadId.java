package gov.nasa.jpf.supportingClasses;

public class ThreadId {

	static public int threadId = -1 ;
	
	public static String getId() {
		threadId = threadId + 1 ;
		return String.valueOf(threadId) ;
	}
	
	public static String getCurrentId() {
		return String.valueOf(threadId) ;
	}
	
	public static String getNextId() {
		return String.valueOf(threadId + 1) ;
	}
	
	public static String getPreviousId() {
		return String.valueOf(threadId - 1) ;
	}
}
