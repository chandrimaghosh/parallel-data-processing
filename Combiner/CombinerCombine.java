package Combiner.combiner;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class CombinerCombine extends MapReduceBase implements Reducer<Text, WeatherRecordCombinerWritable, Text, WeatherRecordCombinerWritable>{

	public void reduce(Text key, Iterator<WeatherRecordCombinerWritable> values, OutputCollector<Text, WeatherRecordCombinerWritable> output, Reporter reporter)
			throws IOException {
		double minSum=0;
		double maxSum=0;
		int minCount=0;
		int maxCount=0;
		
		while(values.hasNext())
		{	WeatherRecordCombinerWritable w=values.next();
			
			if (w.min_or_max.toString().equals("TMIN"))
					{
				
				
				minCount++;
	 		    minSum=minSum+w.getSum();
					}
			
			else if (w.min_or_max.toString().equals("TMAX")){
				
			maxCount++;
 		    maxSum=maxSum+w.getSum();
				}
			
		}
		
		WeatherRecordCombinerWritable v2min=new WeatherRecordCombinerWritable(new Text("TMIN"),new DoubleWritable(minSum),new IntWritable(minCount));
		WeatherRecordCombinerWritable v2max=new WeatherRecordCombinerWritable(new Text("TMAX"),new DoubleWritable(maxSum),new IntWritable(maxCount));
		//Outputs one for min and one for max
		
		
		
		
		
		output.collect(key, v2min);
		output.collect(key, v2max);
		
		
	}
	
}
