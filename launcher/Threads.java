import java.lang.reflect.Method;
public class Threads extends Thread {
	private int NO_OF_THREADS ;
	private int ThreadId ;
	private long realThreadID ;
	private String cls ;
	public Threads(int NoOfThreads, int threadID, String cls) {
		this.NO_OF_THREADS = NoOfThreads ;
		this.ThreadId = threadID ;
		this.realThreadID = this.getId() ;
		this.cls = cls ;
	}
	public void run() {
		String [] str = new String[3] ;
		str[0] = String.valueOf(this.NO_OF_THREADS) ;
		str[1] = String.valueOf(this.ThreadId) ;
		str[2] = String.valueOf(this.realThreadID) ;
		try {
			Class<?> c = Class.forName(cls) ;
			Method m = c.getMethod("main", String[].class) ;
			m.invoke(null, (Object) str) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
