import mpi.*;
public class OneToMany {
	public static void main(String[] args) throws Exception {
		MPI.Init(args);
		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		int tag = 100;
		if (rank == 0) {
			String buffer[] = new String[size];
			for (int i = 1; i < size; i++) {
				buffer[i] = "work " + i;
				MPI.COMM_WORLD.Send(buffer, i, 1, MPI.OBJECT, i, tag);
			}
		} else {
			String buffer1[] = new String[1];
			MPI.COMM_WORLD.Recv(buffer1, 0, 1, MPI.OBJECT, 0, tag);
		}
		MPI.Finalize();
	}
}
