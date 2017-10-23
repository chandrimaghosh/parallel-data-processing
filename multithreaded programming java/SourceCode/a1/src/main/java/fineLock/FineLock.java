/*
 * 
 */
package fineLock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
/***
*  This program is used to calculate average TMAX temperature recorded from various weather stations.

*  The program reads a csv file from the location specified as command line argument and calculates the
*  average of the TMAX readings per station-id. 
*  This program runs parallely by applying Fine lock to the value object of the data structure.
* 
* The program takes the following inputs
* 
*  @Inputs : The inputs are to be passed through command line in the following format
*            filename fibFlag 
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
*/
public class FineLock implements Runnable {


	//arrayList of lines to process
	ArrayList<String> lines =new ArrayList<String>();

	//Concurrent HashMap allows segments within the HashMap to be altered by multiple threads 
	ConcurrentHashMap<String,ArrayList<String>>accumulation;

	int startIndex=0;
	int endIndex=0;
	Boolean fibFlag;

	public FineLock(ConcurrentHashMap<String,ArrayList<String>>accumulation,int start,int end,ArrayList<String> lines,Boolean fibflag) {
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
	  *Whenever a new thread is spawned and the start method is called on it , the thread processes 
	  *the chunk of the file mentioned between startIndex and endIndex,
	  *The HashMap accumulation is the share data structure where each thread groups part of the data
	  *into station id and its respective TMAX temperatures.
	  *Here :The lock has been applied to the value object of the  data structure
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

			if (tmax.equalsIgnoreCase("TMAX"))
			{
				
				//blocking only the value of object of one particular key 
				if (accumulation.get(key)!=null) //if it already exists append to the value list 
					{
						synchronized (accumulation.get(key)) {
							if (fibFlag)
							{
								fib(17);
							}
						accumulation.get(key).add(tmaxValue);
						}
					
					}
				else
					{
						ArrayList<String> value=new ArrayList<String>();
						value.add(tmaxValue);
						accumulation.put(key,value);
					}
				}

				}
			
		

	}

}