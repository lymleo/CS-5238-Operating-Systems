import java.util.concurrent.Semaphore;
import java.util.ArrayList;

public class PWorker implements Runnable
{
	private int wnum;
	private int cnum;
	private int service;
	private ArrayList<Semaphore> cleft; //customers left post office
	private Semaphore wcapability, //3 workers can provide service
					  cready, //customer is ready	
					  working, //postal worker is working or not
	                  c_w, // Mutex that ensures customer requests assistance at appropriate time
	                  request, //customer service request
	                  scale; //only one worker can use the scale
	
   //Postal worker constructor
	PWorker(int wnum, ArrayList<Semaphore> cleft, Semaphore wcapability, Semaphore cready, Semaphore working, Semaphore c_w, Semaphore request, Semaphore scale)
	{
		this.cleft = cleft;
		this.wnum = wnum;
		this.wcapability = wcapability;
		this.cready = cready;
		this.working = working;
		this.c_w = c_w;
		this.request = request;
		this.scale = scale;
		
		System.out.println( "Postal worker " + wnum + " created." );
	}
	
	public void run()
   // signal a ready customer
	{
		while(true)	
		{
			
			try	
			{
				cready.acquire(); // finds a customer that can be served
			} catch(InterruptedException e){System.out.println(e);}
			
			//	get static value 
			this.cnum = PO.cnum;
			this.service = PO.service;
			PO.assignWoker[this.cnum] = this.wnum;
			
			c_w.release();
			
			try
			{
				request.acquire();
			} catch(InterruptedException e){System.out.println(e);}
			     
			// customer requests mailing package
			if(this.service == 2)	
			{
				// request occupy scale
				try
				{
					scale.acquire();
				} catch(InterruptedException e){System.out.println(e);}
				
				System.out.println("scale in use by postal worker " + wnum);
				
				//free the scale
				try
				{
					
					Thread.sleep(PO.sleep(this.service));
				} catch(InterruptedException e){System.out.println(e);}
				
				System.out.println("scale released by postal worker " + wnum);
				scale.release();
			}
			
			//otherwise would be mail letter and buy stamps
			else	
			{
				try
				{
					Thread.sleep(PO.sleep(this.service));
				} catch(InterruptedException e){System.out.println(e);}
			}
			
			System.out.println("Postal worker " + wnum + " finished serving customer " + this.cnum);
			
			cleft.get(this.cnum).release();
			
			//waits for customer to exit
			try
			{
				working.acquire();		
			} catch(InterruptedException e){System.out.println(e);}
			
			//allows another customer to be helped
			wcapability.release();	
		}
	}
}