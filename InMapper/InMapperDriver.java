package InMapper.mapper;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.BasicConfigurator;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class InMapperDriver extends Configured {
	
	
	
 
	public static void main(String args[]) throws Exception
	{	
		  Configuration conf = new Configuration();
          BasicConfigurator.configure();
          Job job = Job.getInstance(conf, "InMapperDriver");
          job.setJarByClass(InMapperDriver.class);
          job.setMapperClass(InMapperMap.class);
          job.setReducerClass(InMapperReduce.class);
          job.setInputFormatClass(TextInputFormat.class);
          job.setMapOutputKeyClass(Text.class);
          job.setMapOutputValueClass(WeatherRecorInMapperWritable.class);
          job.setOutputFormatClass(TextOutputFormat.class);
          job.setOutputKeyClass(Text.class);
          job.setOutputValueClass(Text.class);
          FileInputFormat.setInputPaths(job,new Path(args[0]));
          FileOutputFormat.setOutputPath(job, new Path(args[1]));
          System.exit(job.waitForCompletion(true) ? 0 : 1);
		
}

	
}
