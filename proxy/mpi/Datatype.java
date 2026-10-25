package mpi;

public class Datatype implements Freeable {
	
	private int datatype ; 

	final static int UNDEFINED = -1;
	final static int NULL = 0;
	final static int BYTE = 1;
	final static int CHAR = 2;
	final static int SHORT = 3;
	final static int BOOLEAN = 4;
	final static int INT = 5;
	final static int LONG = 6;
	final static int FLOAT = 7;
	final static int DOUBLE = 8;
	final static int PACKED = 9;
	final static int LB = 10;
	final static int UB = 11;
	final static int OBJECT = 12;

	final static int SHORT2 = 3;
	final static int INT2 = 5;
	final static int LONG2 = 6;
	final static int FLOAT2 = 7;
	final static int DOUBLE2 = 8;

	public Datatype(int i) {
		setDatatype(i) ;
	}

	public int getDatatype() {
		return datatype;
	}

	public void setDatatype(int datatype) {
		this.datatype = datatype;
	}
	
	public void free() throws MPIException {	
		
	}
}
