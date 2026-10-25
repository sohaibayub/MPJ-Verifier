package mpi;

public class ThreadParking {

	private boolean flag ;
	private long threadId ;
	
	ThreadParking(long l) {
		this.flag = true ;
		this.threadId = l ;
	}
	
	public long getThreadId() {
		return this.threadId ;
	}
	
	public boolean getFlag() {
		return this.flag ;
	}
	
	public void setFlag() {
		this.flag = false ;
	}
}
