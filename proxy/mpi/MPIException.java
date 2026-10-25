package mpi;

public class MPIException extends RuntimeException {

	public MPIException() {
		super();
	}

	public MPIException(String msg) {
		super(msg);
	}

	public MPIException(Throwable cause) {
		super(cause);
	}
}