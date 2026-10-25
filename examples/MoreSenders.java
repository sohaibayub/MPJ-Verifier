import mpi.*;
public class MoreSenders {
	public static void main(String[] args) throws Exception {
		MPI.Init(args);
		int rank = MPI.COMM_WORLD.Rank();
		int tag = 100;
		int buf[] = new int[] { rank };
		int in[] = new int[1];
		if (rank == 0) {
			MPI.COMM_WORLD.Send(buf, 0, 1, MPI.INT, 1, tag);
		} else {
			MPI.COMM_WORLD.Recv(in, 0, 1, MPI.INT, 0, tag);
			MPI.COMM_WORLD.Send(buf, 0, 1, MPI.INT, 0, tag);
		}
		MPI.Finalize();
	}
}
