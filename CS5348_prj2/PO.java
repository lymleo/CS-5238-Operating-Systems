import java.util.concurrent.Semaphore;
import java.util.ArrayList;

public class PO
{
	public static int 	cnum;
	public static int 	service;
	public static int[] assignWoker = new int[50];
	
	public static void main(String args[])
	{
		int customers	= 50;
		int workers 	= 3;
		
		ArrayList<Semaphore> cleft 	= new ArrayList<Semaphore>(); //customers left post office
		Semaphore 	capacity = new Semaphore(10, true), //at most 10 customers are allowed in the post office
					wcapability = new Semaphore(3, true), //3 workers can provide service 
					cready = new Semaphore(0, true), //customer is ready	
					staticMutex	= new Semaphore(1, true), //mutex protect static variables
					c_w = new Semaphore(0, true), //mutex that ensures customer requests assistance at appropriate time
					request = new Semaphore(0, true), //customer service request
					scale = new Semaphore(1, true), //only one worker can use the scale
					working = new Semaphore(0 , true); //postal worker is working or not
		
		System.out.println("Simulating Post Office with 50 customers and 3 postal workers");
		
		//	initialize cleft ArrayList
		for(int i=0; i < customers; i++)
		{
			cleft.add(i, new Semaphore(0, true));
		}
		
		//	initialize customer threads
		Customer CustomerThread[] = new Customer[customers];
		Thread Cthreads[] = new Thread[customers];
		
		//	initialize worker threads
		PWorker WorkerThread[] = new PWorker[workers];
		Thread Wthreads[] = new Thread[workers];
		
		for(int i = 0; i < customers; i++)
		{
			CustomerThread[i] = new Customer(i, cleft, capacity, wcapability,  cready, working, staticMutex, c_w, request);
			Cthreads[i] = new Thread(CustomerThread[i]);
			Cthreads[i].start();
		}
		
		for(int i = 0; i < workers; i++)
		{
			WorkerThread[i] = new PWorker(i, cleft, wcapability, cready, working, c_w, request, scale);
			Wthreads[i] = new Thread(WorkerThread[i]);
			Wthreads[i].start();
		}		
		
		// join customers into original process once they have finished
		for(int i = 0; i < customers; i++)
		{
			try
			{
				Cthreads[i].join();
				System.out.println("Joined customer " + i);
			} catch(InterruptedException e){System.out.println(e);}
		}
		
		System.exit(0);
	}
	
	//	return time to be consumed based on the service type
	public static int sleep(int service)
	{
		switch(service)
		{			
			case 0: return 1000; // buy stamps
			case 1: return 1500; // mail a letter			
			case 2: return 2000; // mail a package
			default: 
				System.exit(1);
				return 9001; // in case customer request a service that is not provided
		}
		
	}
}