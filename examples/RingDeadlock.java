import mpi.*;
public class RingDeadlock {
	public static void main(String[] args) throws Exception {
		MPI.Init(args);
		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		int tag = 100;
		int buf[] = new int[] { rank };
		int in[] = new int[1];
		MPI.COMM_WORLD.Send(buf, 0, 1, MPI.INT, (rank + 1) % size, tag);
		MPI.COMM_WORLD.Recv(in, 0, 1, MPI.INT, (rank + size - 1) % size, tag);
		MPI.Finalize();
	}
}
