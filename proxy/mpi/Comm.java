package mpi;

public abstract class Comm 
{
	private int NoOfThreads ;
	
	Comm(int noOfThreads) {
		NoOfThreads = noOfThreads ;
	}
	
	public int Size() {
		return NoOfThreads ;
	}
	
	public abstract int Rank() ;
}
