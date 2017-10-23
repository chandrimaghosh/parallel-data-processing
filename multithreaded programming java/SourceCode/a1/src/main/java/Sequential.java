package mr.assignment.a1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/***
 *  This program is used to calculate average TMAX temperature recorded from various weather stations.
 *  The program reads a csv file from the location specified as command line argument and calculates the
 *  average of the TMAX readings per station-id. 
 *  This program runs sequentially.
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
 *
 */

public class Sequential 
{
	
	static ArrayList<String> lines =new ArrayList<String>();
	static HashMap<String,Double>average=new HashMap<String, Double>();
	static HashMap<String,ArrayList<String>>accumulation =new HashMap();
	static ArrayList<Long> executionTimes=new ArrayList<Long>();
	static PrintWriter pw;
	static long start;
	static long end;
	static long minExecutionTime=0;
	static long maxExecutionTime=0;
	static long avgExecutionTime=0;
    static boolean fibFlag=false;
    static long executionTime=0;
	 
	//main metod :entry point 
	public static void main( String[] args )
    {
    	
    String filename=args[0];
    fibFlag=Boolean.parseBoolean(args[1]);
    boolean x=readTemperatureData(filename);
    Sequential ap=new Sequential();
    
    try {
    	if (fibFlag)
    	{
		pw = new PrintWriter(new FileWriter("outputSeqFib.txt"));}
    	else
    	{
    		pw = new PrintWriter(new FileWriter("outputSeq.txt"));
    	}

	}catch (FileNotFoundException fe) {
		fe.printStackTrace();
	}catch (IOException io){

	}
    
    for (int e=0;e<10;e++)
    {
    	accumulation.clear();
    	average.clear();
        start=System.currentTimeMillis();
    ap.sequentialExecution(lines);
   //Calculate average 
    for (String stationId :accumulation.keySet())
	{

		ArrayList<String> tempValues=accumulation.get(stationId);
		int count=tempValues.size();
		double tempSum=0.0;
		for(String value:tempValues)
		{

			tempSum=tempSum+Double.parseDouble(value);
		}

		double avgValue=tempSum/count;
		average.put(stationId,avgValue);
	}
    
    end =System.currentTimeMillis();
	executionTime=end-start;
	executionTimes.add(executionTime);
    
    }//10 executions
    
    printRoutine(pw,executionTimes,average);
    pw.close();
    
   }
	
	   /***
     *  readTemperatureData:
     *
     * The function  takes the following inputs
     * 
     *  @Inputs : filename : the name of the file to be processed.
     *            
     *            
     *  The function produces the following Output
     *            
     *  @Output : true if reading  data was successful
     * 
     */
	
	
	
    public static boolean readTemperatureData(String filename)
    {
    	try {
    	FileReader fr =new FileReader(filename);
    	BufferedReader br =new BufferedReader(fr);
    	String line;
    	
			while ((line=br.readLine())!=null)
			{
				lines.add(line);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return true;
    }
    
    
    /***
     *  sequentialExecution:
     *
     * The function  takes the following inputs
     * 
     *  @Inputs : lines : the chunk of the file to be processed.
     *            
     *            
     *  The function produces the following Output
     *            
     *  @Output : void
     * 
     */
    public void sequentialExecution(ArrayList<String> lines)
    {
    	for(String s :lines)
    	{
    	String[] separated = s.split(",");
    	
    	
    	String key=separated[0];
    	String tmax=separated[2];
    	String tmaxValue=separated[3];
    	
    	
    	
    	if (tmax.equalsIgnoreCase("TMAX"))
    	{
    	
    		if (fibFlag)
			{
    			fib(17);
    		}
			
    	if (accumulation.get(key)!=null) //if it already exists append to the value list 
    	{
    		accumulation.get(key).add(tmaxValue);
    		
    	}else
    	{
    		ArrayList<String> value=new ArrayList<String>();
    		value.add(tmaxValue);
    		accumulation.put(key,value);
    		}
    	
    	}
    	}
    	
    	
    	
    	
    }//end method

	
    
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
	  * A function print results to an output file 
	  * 
	  * 
	  * @param pwriter :The object of the pritwriter object 
	  * @param etimes :The arrayList with the execution times of all execution
	  * @param avg :The Hashmap containing the actual result:-the average temparature by stationId.
	  * 
	  * 
	  * 
	  */
	private static void printRoutine(PrintWriter pwriter,ArrayList<Long>etimes,HashMap<String,Double>avg) {
		PrintWriter pw=pwriter;
		for(long et:etimes)
		{
			pw.print("execution time:   ");
			pw.println(et);
			pw.flush();
		}
		pw.println(".................................................");
		Collections .sort(etimes);
		pw.print("min execution time:   ");
		pw.println(etimes.get(0));
		pw.print("max execution time:  ");
		pw.println(etimes.get(etimes.size()-1));
		long totalSum=0;
		for (long l:etimes)
		{
			totalSum=totalSum+l;
		}
		long averageTime=totalSum/etimes.size();
		pw.print("avg execution time:  ");
		pw.println(averageTime);
		pw.println(".................................................");
		pw.println(" Station ID       Average ");
		for(String key:avg.keySet())
		{
			pw.println();
			pw.println("    "+key+"   "+ avg.get(key)+"   ");
		}
		
		pw.println(avg.size()+"the number of records");

	}
    
    
}
