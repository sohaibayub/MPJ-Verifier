import java.util.ArrayList;
public class MPJ_Express_Model {
	public static void main(String[] args) {
		int NoOfProcesses = Integer.parseInt(args[0]) ;
		String cls = args[1] ;
		ArrayList<Threads> workers = new ArrayList<Threads>() ;
		for(int i = 0; i < NoOfProcesses; i++) {
			Threads worker = new Threads(NoOfProcesses, i, cls) ;
			worker.start();
			workers.add(worker) ;
		}
	}
}
