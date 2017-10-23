package NoCombiner.noCombiner;


import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.io.*;	
import org.apache.hadoop.mapred.*;

public class WeatherReducer extends MapReduceBase implements Reducer<Text,WeatherRecordWritable , Text,Text >{

	public void reduce(Text key, Iterator<WeatherRecordWritable> values, OutputCollector<Text, Text> output,
			Reporter reporter) throws IOException {
		
		
		double minSum=0;
		double maxSum=0;
		int minCount=0;
		int maxCount=0;
		
		while(values.hasNext())
		{
			WeatherRecordWritable w=values.next();
			
			if (w.min_or_max.equals("TMIN"))
					{
				
				minSum+=w.temp;
				minCount++;
		
					}
			else if (w.min_or_max.equals("TMAX")){
				
				maxCount++;
 				maxSum+=w.temp;
 				}
			
		}
		double avgMax=0;
		double avgMin=0;
		if (minCount!=0)
		{
		 avgMin=minSum/minCount;
		}
		if (maxCount!=0)
		{
		 avgMax=maxSum/maxCount;
		}
		
		
		output.collect(key, new Text("TMIN"+avgMin+"TMAX"+avgMax+"MinCount"+minCount+"MaxCount"+maxCount));
		
		

	}
	
	
	

}
