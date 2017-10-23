package InMapper.mapper;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class InMapperReduce extends Reducer<Text,WeatherRecorInMapperWritable , Text, Text> {

	public void reduce(Text key, Iterable<WeatherRecorInMapperWritable> values,Context context ) throws IOException, InterruptedException {
	
		double minSum=0;
		double maxSum=0;
		int minCount=0;
		int maxCount=0;
		double avgMax=0;
		double avgMin=0;
		

		
		for(WeatherRecorInMapperWritable w :values){
			maxSum+=w.getMaxSum();
			maxCount+=w.getMaxCount();
			minSum+=w.getMinSum();
			minCount+=w.getMinCount();}

		
		if (minCount!=0)
			avgMin=minSum/minCount;


		if ( maxCount!=0)
		avgMax=maxSum/maxCount;
		context.write(key, new Text("TMIN"+avgMin+"TMAX"+avgMax));
		
		
	}

}