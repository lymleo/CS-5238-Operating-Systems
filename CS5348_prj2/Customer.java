import java.util.concurrent.Semaphore;
import java.util.Random;
import java.util.ArrayList;

public class Customer implements Runnable
{
	private int number; // customer number that will be printed in the console for identification
	private int service; //	buying stamps, mailing a letter, or mailing a package
	private ArrayList<Semaphore> cleft; //customer left post office
	private Semaphore capacity, 
	                  wcapability, //3 workers can provide service
	                  cready, //customer is ready
	                  working, //postal worker is working or not
	                  staticMutex, // mutex protect static variables
	                  c_w, // Mutex that ensures customer requests assistance at appropriate time
	                  request; //customer service request
	private String serviceStr;
	
	
	//	Customer constructor
	Customer(int number, ArrayList<Semaphore> cleft, Semaphore capacity, Semaphore wcapability, Semaphore cready, Semaphore working, Semaphore staticMutex, Semaphore c_w, Semaphore request)
	{
		this.cleft = cleft;
		this.number = number;
		this.capacity  = capacity;
		this.wcapability = wcapability;
		this.cready  = cready;
		this.working  = working;
		this.staticMutex = staticMutex;
		this.c_w  = c_w;
		this.request  = request;
		
		Random randomGenerator = new Random();
	    this.service = randomGenerator.nextInt(3);
		
	    
		if(this.service == 0)
			this.serviceStr	= "buy stamps";
		else if(this.service == 1)
			this.serviceStr	= "mail a letter";
		else	
			this.serviceStr	= "mail a package";
		
		System.out.println("Customer " + number + " created");
	}
	
	public void run()
	{
		try
		{
			capacity.acquire();	//enter post office
		} catch(InterruptedException e){System.out.println(e);}
		
		try
		{
			wcapability.acquire(); //get one postal worker
		} catch(InterruptedException e){System.out.println(e);}
		
		try
		{
			staticMutex.acquire();	//customer and postal worker tied to each other, only this thread can write to global variables
		} catch(InterruptedException e){System.out.println(e);}
		
		PO.cnum = this.number;
		PO.service = this.service;

		cready.release();	//	allows worker to begin reading data
	
		try
		{
			c_w.acquire();			//	waits for worker to acquire data
		} catch(InterruptedException e){System.out.println(e);}
		
		
		System.out.println("Customer " + number + " asks postal worker " + PO.assignWoker[number] + " to " + this.serviceStr);
		
		request.release();	//	allows worker to begin working on service
		
		staticMutex.release();		//	other threads can now write to static variables
		
		try
		{
			cleft.get(this.number).acquire();	//	// wait until worker finish service to this customer 
		} catch(InterruptedException e){System.out.println(e);}

		working.release();		//this customer leaves and worker is not working now
		
		System.out.println( "Customer " + number + " leaves post office" );
		
		capacity.release();		//then allows one more customer to enter the post office
	}
}