import mpi.*;
public class SimpleDeadlock {
	public static void main(String[] args) throws Exception {
		MPI.Init(args);
		int rank = MPI.COMM_WORLD.Rank();
		int tag = 100;
		int other = 1 - rank;
		int buf[] = new int[] { rank };
		int in[] = new int[1];
		MPI.COMM_WORLD.Recv(in, 0, 1, MPI.INT, other, tag);
		MPI.COMM_WORLD.Send(buf, 0, 1, MPI.INT, other, tag);
		MPI.Finalize();
	}
}
