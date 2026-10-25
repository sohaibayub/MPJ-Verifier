package gov.nasa.jpf.supportingClasses;

public class Singleton {
	
	public static int secondLastValue = -1 ;
	public static int lastValue = -1 ;
	public static int currentValue = -1 ;
	
	public static int secondLastThreadid = -1 ;
	public static int lastThreadid = -1 ;
	public static int currentThreadid = -1 ;
	
	public static boolean flag = false ;
	
	public static void setFlag(int _value, int _threadId) {
		
		secondLastValue = lastValue ;
		lastValue = currentValue ;
		currentValue = _value ;
		
		secondLastThreadid = lastThreadid ;
		lastThreadid = currentThreadid ;
		currentThreadid = _threadId ;
		
		if((secondLastValue > 1) && 
		   (lastValue == 1) && 
		   (currentValue == 0) && 
		   (secondLastThreadid == lastThreadid) && 
		   (lastThreadid == currentThreadid)) {
			flag = true ;
		} else {
			flag = false ;
		}
		
	}
	
	public static boolean getFlag(int _threadId) {
		p("secondLastValue: " + secondLastValue + ", " + 
		  "lastValue: " + lastValue + ", " +
		  "currentValue: " + currentValue + ", " +
		  "secondLastThreadid: " + secondLastThreadid + ", " + 
		  "lastThreadid: " + lastThreadid + ", " +
		  "currentThreadid: " + currentThreadid + ", " +
		  "_threadId: " + _threadId + ", " +
		  "Flag: " + flag) ;
		
		if(flag == true) {
			flag = false ;
			return true ;
		} else {
			return false ;
		}
	}
	
	public static void p(String a) {
		// System.out.println(a) ;
	}
}
