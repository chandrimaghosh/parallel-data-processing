package mr.assignment.a1;

import java.util.ArrayList;
import java.util.HashMap;

public class Nolock implements Runnable {

	/***
	 *  This program is used to calculate average TMAX temperature recorded from various weather stations.
	 *  The program reads a csv file from the location specified as command line argument and calculates the
	 *  average of the TMAX readings per station-id. 
	 *  This program runs parallely with no locks .
	 * 
	 * The program takes the following inputs
	 * 
	 *  @Inputs : The inputs are to be passed through command line in the following format
	 *            filePath fibonacciFlag 
	 *            
	 *            filename: path of the 1912.csv file
	 *            fibFlag: true if we want to introduce delay of fibonacci(17).
	 *            
	 *  The program produces the following Output
	 *            
	 *  @Output : The min,max and average run times for 10 computations
	 * 
	 */
	/**
	 * @author chandrimaghosh
	 *
	 */
	//arrayList of lines to process
	ArrayList<String> lines =new ArrayList<String>();

	HashMap<String,ArrayList<String>>accumulation;

	int startIndex=0;
	int endIndex=0;
	Boolean fibFlag;

	public Nolock(HashMap<String,ArrayList<String>>accumulation,int start,int end,ArrayList<String> lines,Boolean fibflag) {
		super();
		this.accumulation=accumulation;
		this.lines = lines;
		this.startIndex=start;
		this.endIndex=end;
		this.fibFlag=fibflag;

	}

	/**
	  * A function to calculate fibonacci number .This  function is used to introduce
	  * delays in the multithreded progrm 
	  * 
	  * @param n : an integer which indicates the position of fibonacci number
	  * 
	  */
	public void fib(int n)
	{
		int a =1;
		int b =1;
		int c =0;
		for (int fib=0;fib<n-2;fib++)
		{

			c=a+b;
			a=b;
			b=c;
		}

	}
	 /**
	  *Whenever a new thread is spawned and the start method is called on it ,
	  * the thread processes 
	  *the chunk of the file mentioned between startIndex and endIndex,
	  *The HashMap accumulation is the share data structure where each thread groups 
	  *part of the data
	  *into station id and its respective TMAX temperatures.
	  *Here :The lock has not been applied.
	  * 
	  * @param :none
	  * 
	  */
	public void run() {
		for (int i = startIndex; i < endIndex; i++) {
			String s=lines.get(i);

			String[] separated = s.split(",");


			String key=separated[0];
			String tmax=separated[2];
			String tmaxValue=separated[3];
			
			if (key==null)
			{System.out.println("yes null exists");}
			
			

			if (tmax.equalsIgnoreCase("TMAX"))
			{

				if (accumulation.get(key)!=null) //if it already exists append to the value list 
				{

					if (fibFlag)
					{
						fib(17);
					}
					accumulation.get(key).add(tmaxValue);
				}else
				{
					ArrayList<String> value=new ArrayList<String>();
					value.add(tmaxValue);
					accumulation.put(key,value);
				}

			}
		}
		

	}

}
