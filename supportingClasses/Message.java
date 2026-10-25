package gov.nasa.jpf.supportingClasses;

public class Message
{
	public int index ;
	public Object buffer ;
	public int offset ;
	public int count ;
	public Datatype datatype ;

	public int source ;
	public int destination ;
	public int tag ;
    
    Message(int index, Object buffer, int offset, int count, Datatype datatype, int source, int destination, int tag) {
    	this.index = index ;
    	this.buffer = buffer ;
    	this.offset = offset ;
    	this.count = count ;
    	this.datatype = datatype ;
    	this.source = source ;
    	this.destination = destination ;
        this.tag = tag ;
        /*
        p("Index: "+String.valueOf(this.index)
          +" Offset: "+String.valueOf(this.offset)
          +" Count: "+String.valueOf(this.count)
          +" Source: "+String.valueOf(this.source)
          +" Destination: "+String.valueOf(this.destination)
          +" Tag: "+String.valueOf(this.tag)) ;
        /**/
    }
    
    public void p(String s) {
		//System.out.println(s) ;
	}
}
