

import mpi.* ;

/**
 * Compile: javac -cp $MPJ_HOME/lib/mpj.jar:. RunningAverage.java
 * Execute: mpjrun.sh -np 9 -dport 11000 RunningAverage
 */

public class RunningAverage {
	
	public static void main(String[] args) throws Exception {
		
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank() ;
        int size = MPI.COMM_WORLD.Size() ;
        
        int tag = 100 ;

        if(rank == 0) {
        	
        	String buffer[] = new String[8] ;
             
            buffer[0] = "1" ;
            buffer[1] = "2" ;
            buffer[2] = "3,4" ;
            buffer[3] = "5,6,7,8" ;
            buffer[4] = "9" ;
            buffer[5] = "10" ;
            buffer[6] = "11,12" ;
            buffer[7] = "13,14,15,16" ;
        	
        	for(int i = 1 ; i <= 8; i++) {
        		MPI.COMM_WORLD.Send(buffer, (i-1), 1, MPI.OBJECT, i, tag) ;
        		System.out.println("########### process <" + rank + "> sent a msg to " + "process <" + i + ">") ;
        	}
        	
        	float[] xbuffer = new float[1] ;
        	Status status1 = MPI.COMM_WORLD.Recv(xbuffer, 0, xbuffer.length, MPI.FLOAT, 1, 115) ;
        	System.out.println("########### Final Average:" + xbuffer[0]) ;
    
        } else {
        	
        	String buffer1[] = new String[1] ;
            
        	Status status = MPI.COMM_WORLD.Recv(buffer1, 0, buffer1.length, MPI.OBJECT, 0, tag);
            
            String str = buffer1[0] ;
            String[] numbers = str.split(",") ;
            int counter = 0 ;
            float summation = 0 ;
            
            for(int i = 0; i < numbers.length ; i++) {
            	summation = summation + Float.valueOf(numbers[i]) ;
            	counter = counter + 1 ;
            }
            
            float avg_buff[] = new float[1] ;
            avg_buff[0] = summation/counter ;
            
            if(rank == 2 || rank == 3 || rank == 4) {
            	MPI.COMM_WORLD.Send(avg_buff, 0, 1, MPI.FLOAT, 1, 110) ;
            	System.out.println("########### process <" + rank + "> sent a msg to " + "process <1>") ;
            }
            
            if(rank == 6 || rank == 7 || rank == 8) {
            	MPI.COMM_WORLD.Send(avg_buff, 0, 1, MPI.FLOAT, 5, 105) ;	
            	System.out.println("########### process <" + rank + "> sent a msg to " + "process <1>") ;
            }
            
            if(rank == 5) {
            	float[] xbuffer = new float[1] ;
            	float avg = avg_buff[0] ;
            	for(int j=0 ; j<3 ; j++) {
            		Status status1 = MPI.COMM_WORLD.Recv(xbuffer, 0, xbuffer.length, MPI.FLOAT, MPI.ANY_SOURCE, 105) ;
            		System.out.println("########### Process <"+rank+"> recv'ed a msg from Process <"+status1.source+"> with tag <"+status1.tag+">"+" Payload: " + xbuffer[0]);
            		avg = (avg + xbuffer[0])/2 ;
            	}
            	float[] b = new float[1] ;
            	b[0] = avg ;
            	MPI.COMM_WORLD.Send(b, 0, 1, MPI.FLOAT, 1, 110) ;  
            	System.out.println("########### process <" + rank + "> sent a msg to " + "process <1>") ;
            }
            
            if(rank == 1) {
            	float[] xbuffer = new float[1] ;
            	float avg = avg_buff[0] ;
            	for(int j=0 ; j<4 ; j++) {
            		Status status1 = MPI.COMM_WORLD.Recv(xbuffer, 0, xbuffer.length, MPI.FLOAT, MPI.ANY_SOURCE, 110) ;
            		System.out.println("########### Process <"+rank+"> recv'ed a msg from Process <"+status1.source+"> with tag <"+status1.tag+">"+" Payload: " + xbuffer[0]);
            		avg = (avg + xbuffer[0])/2 ;
            	}
            	float[] b = new float[1] ;
            	b[0] = avg ;
            	MPI.COMM_WORLD.Send(b, 0, 1, MPI.FLOAT, 0, 115) ;  
            	System.out.println("########### process <" + rank + "> sent a msg to " + "process <0>") ;
            }
        }
        
        MPI.Finalize() ;
    }
}