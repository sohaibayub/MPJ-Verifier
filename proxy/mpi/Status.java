package mpi;

public class Status {
	public int source;
	public int tag;
	public int index;
	public int count;
	public Object buffer ;
	public int offset ;
	public Datatype datatype ;
    public int peer ;
    public int destination ;
    
    public Status(int _source, int _tag) {
    	this.source = _source ;
    	this.tag = _tag ;
    }
}
