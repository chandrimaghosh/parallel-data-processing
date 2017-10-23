package pagerank.pagerank;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.htrace.commons.logging.impl.Log4JLogger;
import org.apache.log4j.BasicConfigurator;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
/**
 * Driver Program
 *
 */
public class PageRankDriver extends Configuration
{
	// Global counters for TotalPages and delta 
	static enum gloabalVariable{
		totalPages,
		delta;
	}
	public static void main(String[] args)
	{
		try {
//			Configuration conf = new Configuration();
//			BasicConfigurator.configure();
//			GenericOptionsParser optionsParser = new GenericOptionsParser(conf,args);
//			String[] remainingArgs = optionsParser.getRemainingArgs();
			// Start preprecessing task
			System.out.println("print&&&&&&&&&&&");
			System.out.println("Input"+args[1]);
			System.out.println("Output"+args[2]);

			
			
			long preProcessStartTime=System.currentTimeMillis();
			Job preProcessJob = preprocessJob(args);
			preProcessJob.waitForCompletion(true);
			//get the total number of Nodes /Pages 
			long totalPages=preProcessJob.getCounters().findCounter(gloabalVariable	.totalPages).getValue();
			long preProcessEndTime=System.currentTimeMillis();
			long execTime=preProcessEndTime-preProcessStartTime;
			System.out.println("Preprocessing completed in "+execTime+" ms");
			//End preprocessing task


//			//Begin PageRank Computation 
//			//Run Page Rank for 10 times 
//			double delta=0.0;
//			for(int i=0;i<10;i++){
//
//				long PRStartTime=System.currentTimeMillis();
//				Job PRJob=runPageRank(remainingArgs, conf, i,totalPages,delta);
//				PRJob.waitForCompletion(true);
//				//upadate delta after one run;
//				delta=Double.longBitsToDouble(PRJob.getCounters().findCounter(gloabalVariable.delta).getValue());
//				long PREndTime=System.currentTimeMillis();
//				execTime=PREndTime-PRStartTime;
//				System.out.println("Page Rank computation completed in "+execTime+" ms");
//				//End of Page Rank Computation	
//
//			}
//			long topKStartTime=System.currentTimeMillis();
//			Job topKJob=findTopKNodes(remainingArgs,conf);
//			topKJob.waitForCompletion(true);
//			long topKEndTime=System.currentTimeMillis();
//			long topkTime=topKEndTime-topKStartTime;
//			System.out.println("Top 100 computation completed in "+topkTime+" ms");
//			//Top k end
			System.exit(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Job preprocessJob(String[] Args ) throws IllegalArgumentException, IOException {

		System.out.println("InPreprocess  Input"+Args[1]);
		System.out.println("InPreprocess  Output"+Args[2]);
		
	    Configuration conf = new Configuration();
        BasicConfigurator.configure();
		Job job = Job.getInstance(conf, "BZ2Parsing");
		job.setJarByClass(PageRankDriver.class);
		
		System.out.println("NoProblem Finding this");
		
		//set mapper,combiner and reducer
		job.setMapperClass(PreProcessorMapper.class);
		System.out.println("NoProblem Finding this");
		
		job.setCombinerClass(PreProcessorCombiner.class);
		job.setReducerClass(PreProcessorReducer.class);

		//set  output key and value class 
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Node.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Node.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		//for(int i=0;i<remainingArgs.length-1;i++) {
			FileInputFormat.addInputPath(job, new Path(Args[1]));
		//}
		FileOutputFormat.setOutputPath(job, new Path(Args[2]));
		return job;
	}
	private static Job runPageRank(String[] remainingArgs, Configuration conf,int currentRun,long totalPages,double delta) throws IllegalArgumentException, IOException {

		conf.setLong("TotalPages",totalPages);
		if(currentRun==0){
			conf.setBoolean("isFirst",true);
		}
		else{
			conf.setBoolean("isFirst",false);
		}
		conf.setDouble("Delta",delta);

		Job job = Job.getInstance(conf, "PageRank");
		job.setJarByClass(PageRankDriver.class);
		//set mapper,combiner and reducer
		job.setMapperClass(PageRankMapper.class);
		job.setReducerClass(PageRankReducer.class);
		job.setCombinerClass(PageRankCombiner.class);

		//set  output key and value class 
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Node.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Node.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path("PD"+currentRun));
		FileOutputFormat.setOutputPath(job, new Path("PD"+(currentRun+1)));
		return job;
	}

	//The TopKNodes job
	
	private static Job findTopKNodes(String[] remainingArgs, Configuration conf) throws IllegalArgumentException, IOException {

		Job job = Job.getInstance(conf, "PageRank");
		job.setJarByClass(PageRankDriver.class);
		//set mapper,combiner and reducer
		job.setMapperClass(TopKMapper.class);
		job.setReducerClass(TopKReducer.class);
		//set  output key and value class 
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(TopKRank.class);
		//for reducer
		job.setOutputValueClass(Text.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		FileInputFormat.addInputPath(job, new Path("PD10"));
		FileOutputFormat.setOutputPath(job, new Path(remainingArgs[remainingArgs.length-1]));
		return job;
	}

}
