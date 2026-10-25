package gov.nasa.jpf.supportingClasses;

import gov.nasa.jpf.vm.ThreadInfo;

class ThreadStructure {
	public ThreadInfo thread ;
	public int line_number ;
	boolean processed ;
	public ThreadStructure(ThreadInfo _threadInfo, int _line_number, boolean _processed) {
		this.thread = _threadInfo ;
		this.line_number = _line_number ; 
		this.processed = _processed ;
	}
	
	public ThreadStructure(ThreadInfo _threadInfo, boolean _processed) {
		this.thread = _threadInfo ;
		this.line_number = -1 ; 
		this.processed = _processed ;
	}
}
