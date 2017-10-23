package Combiner.combiner;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;




public class CombinerReducer  extends MapReduceBase implements Reducer<Text, WeatherRecordCombinerWritable, Text, Text>{

	public void reduce(Text key, Iterator<WeatherRecordCombinerWritable> values, OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {

		double minSum=0;
		double maxSum=0;
		int minCount=0;
		int maxCount=0;
		double avgMax=0;
		double avgMin=0;

		while(values.hasNext())
		{
			WeatherRecordCombinerWritable w = values.next();

			if (w.min_or_max.toString() .equals("TMIN"))
			{
				minSum+=w.getSum();
				minCount=minCount+w.getCount();

			}
			else if (w.min_or_max.toString().equals("TMAX")){

				maxSum+=w.getSum();
				maxCount=maxCount+w.getCount();
			}
	}

		
		if (minCount!=0)
			avgMin=minSum/minCount;


		if ( maxCount!=0)
		avgMax=maxSum/maxCount;
		
		output.collect(key, new Text("TMIN"+avgMin+"TMAX"+avgMax));

}
}
