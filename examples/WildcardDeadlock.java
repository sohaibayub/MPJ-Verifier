import mpi.*;
public class WildcardDeadlock {
	public static void main(String[] args) throws Exception {
		MPI.Init(args);
		int rank = MPI.COMM_WORLD.Rank();
		int tag = 55;
		int buf[] = new int[] { rank };
		int in[] = new int[1];
		if (rank == 0) {
			MPI.COMM_WORLD.Send(buf, 0, 1, MPI.INT, 1, tag);
			MPI.COMM_WORLD.Send(buf, 0, 1, MPI.INT, 2, tag);
		} else if (rank == 1) {
			Status s = MPI.COMM_WORLD.Recv(in, 0, 1, MPI.INT, MPI.ANY_SOURCE, tag);
			System.out.println("wildcard matched " + in[0]);
			MPI.COMM_WORLD.Recv(in, 0, 1, MPI.INT, 2, tag);
		} else if (rank == 2) {
			MPI.COMM_WORLD.Send(buf, 0, 1, MPI.INT, 1, tag);
			MPI.COMM_WORLD.Recv(in, 0, 1, MPI.INT, 0, tag);
		}
		MPI.Finalize();
	}
}
