/*
 * 
 */
package fineLock;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import coarseLock.CoarseLockMain;
import mr.assignment.a1.Nolock;

// TODO: Auto-generated Javadoc
/**
 * The Class FineLockMain.
 */
public class FineLockMain {

	/** The chunk of data to be processed */
	ArrayList<String> lines=new ArrayList<String>();
	
	/** The accumulation data structure */
	ConcurrentHashMap<String,ArrayList<String>>accumulation =new ConcurrentHashMap();
	
	/** The average values of TMAX by station id */
	HashMap<String,Double>average=new HashMap<String, Double>();
	
	/** The printer object to write the output files locally */
	PrintWriter pw;
	
	/** The execution times of 10 executions */
	ArrayList<Long> executionTimes=new ArrayList<Long>();
  

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	//first 
	public static void main(String[] args) {

		
		String s = null;
		FineLockMain nlm=new FineLockMain();
		long executionTime=0;
		long start=0;
		String filename=args[0];
		int numberOfexecution=10;
		long averageTime=0;
		int startFrom=0;
		int processTill=0;
		long minExecutionTime=0;
		long maxExecutionTime=0;
		long avgExecutionTime=0;
		Boolean fibFlag=false;


		//system uses all available cores to perform multithreading 
		int numberOfThreads=Runtime.getRuntime().availableProcessors();
		String fileName=args[0];
		fibFlag=Boolean.parseBoolean(args[1]);


		//Read from file and store locally 
		try{

			BufferedReader br =new BufferedReader(new FileReader(fileName));
			String line="";
			while ((line=br.readLine())!=null)
			{
				nlm.lines.add(line);

			}
		}
		catch(FileNotFoundException f){

		}catch(IOException e)
		{

		}

		int partitionSize=nlm.lines.size()/numberOfThreads;



		try {
			nlm.pw = new PrintWriter(new FileWriter("outputFine.txt"));

		}catch (FileNotFoundException fe) {
			fe.printStackTrace();
		}catch (IOException io){

		}

		//running the process 10 times 
		for (int e=0;e<10;e++)
		{
			
			
			nlm.accumulation.clear();
			nlm.average.clear();
			startFrom=0;
			start=System.currentTimeMillis();

			//spawn  maximum available threads 
			ArrayList<Thread> executionThreads=new ArrayList<Thread>();

			for (int i=0;i<numberOfThreads;i++)
			{


				//creates a new thread that sends the Shared accumulation Data Structure nlm.accumulation
				//nlm.accumulation is the reference and thus the threads spawned all access the same data structure and also the chunk of the lines 
				//it needs to deal with 
				//a new object for the Nolock Data Structre is created but the new thread spawned are all in the same memory space 

				Thread t=new Thread(new FineLock(nlm.accumulation,startFrom,startFrom+partitionSize,nlm.lines,fibFlag));
				startFrom=startFrom+partitionSize;
				executionThreads.add(t);
		
			}


			for(Thread t :executionThreads)
			{
				t.start();
			}
			for (Thread t : executionThreads)
			{

				try {
					t.join();// wait for all thread to write into accumulation data structure


				} catch (InterruptedException interrupted ) {
					// TODO Auto-generated catch block
					interrupted.printStackTrace();
				}
			}
			//calculate average 
			for (String stationId :nlm.accumulation.keySet())
			{

				ArrayList<String> tempValues=nlm.accumulation.get(stationId);
				int count=tempValues.size();
				double tempSum=0.0;
				for(String value:tempValues)
				{

					tempSum=tempSum+Double.parseDouble(value);
				}

				double avgValue=tempSum/count;
				nlm.average.put(stationId,avgValue);

			}
			long end =System.currentTimeMillis();
			executionTime=end-start;
			nlm.executionTimes.add(executionTime);

		}
		printRoutine(nlm.pw,nlm.executionTimes,nlm.average);
		nlm.pw.close();
	}
	
	/**
	 * Prints the routine.
	 *
	 * @param pwriter the pwriter
	 * @param etimes the etimes
	 * @param avg the avg
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



	}


}
