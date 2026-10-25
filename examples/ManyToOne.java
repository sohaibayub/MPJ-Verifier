import mpi.*;
public class ManyToOne {
	public static void main(String[] args) throws Exception {
		MPI.Init(args);
		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		int tag = 100;
		if (rank == 0) {
			int order[] = new int[1];
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < size; i++) {
				Status s = MPI.COMM_WORLD.Recv(order, 0, 1, MPI.INT, MPI.ANY_SOURCE, tag);
				sb.append(order[0]).append(' ');
			}
			System.out.println("ORDER " + sb);
		} else {
			int buf[] = new int[] { rank };
			MPI.COMM_WORLD.Send(buf, 0, 1, MPI.INT, 0, tag);
		}
		MPI.Finalize();
	}
}
