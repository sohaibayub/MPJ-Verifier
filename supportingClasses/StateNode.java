package gov.nasa.jpf.supportingClasses;
import java.util.ArrayList;
import gov.nasa.jpf.vm.ThreadInfo;

public class StateNode {
	
	public int state_id ;

	public ArrayList<StateNode> Children ;
	public StateNode Parent ;
	
	public ArrayList<ThreadInfo> ThreadLog  ;
	public ArrayList<ThreadInfo> threads  ;
	public ArrayList<ThreadInfo> parent_threads  ;
	
}
