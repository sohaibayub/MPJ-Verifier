import mpi.*;
public class AvgDistributed {
	static float localAvg(String s) {
		String[] p = s.split(","); float sum = 0;
		for (String x : p) sum += Float.valueOf(x);
		return sum / p.length;
	}
	public static void main(String[] args) throws Exception {
		MPI.Init(args);
		int rank = MPI.COMM_WORLD.Rank();
		int tag = 100;
		if (rank == 0) {
			String work[] = { "", "1,2,3,4", "5,6,7,8", "9,10,11,12", "13,14,15,16" };
			for (int i = 1; i <= 4; i++) MPI.COMM_WORLD.Send(work, i, 1, MPI.OBJECT, i, tag);
			float res[] = new float[1];
			MPI.COMM_WORLD.Recv(res, 0, 1, MPI.FLOAT, 1, 115);
			System.out.println("Final Average:" + res[0]);
		} else {
			String in[] = new String[1];
			MPI.COMM_WORLD.Recv(in, 0, 1, MPI.OBJECT, 0, tag);
			float avg[] = { localAvg(in[0]) };
			float x[] = new float[1];
			if (rank == 2) MPI.COMM_WORLD.Send(avg, 0, 1, MPI.FLOAT, 1, 110);
			if (rank == 4) MPI.COMM_WORLD.Send(avg, 0, 1, MPI.FLOAT, 3, 105);
			if (rank == 3) {
				MPI.COMM_WORLD.Recv(x, 0, 1, MPI.FLOAT, MPI.ANY_SOURCE, 105);
				avg[0] = (avg[0] + x[0]) / 2;
				MPI.COMM_WORLD.Send(avg, 0, 1, MPI.FLOAT, 1, 110);
			}
			if (rank == 1) {
				for (int j = 0; j < 2; j++) {
					Status s = MPI.COMM_WORLD.Recv(x, 0, 1, MPI.FLOAT, MPI.ANY_SOURCE, 110);
					avg[0] = (avg[0] + x[0]) / 2;
				}
				MPI.COMM_WORLD.Send(avg, 0, 1, MPI.FLOAT, 0, 115);
			}
		}
		MPI.Finalize();
	}
}
