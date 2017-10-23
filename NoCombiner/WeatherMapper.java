package NoCombiner.noCombiner;


import java.io.IOException;
import org.apache.hadoop.io.*;	
import org.apache.hadoop.mapred.*;

 //The mapper class emits the station id as key and WeatherRecordWritable which has min/max record with the value of temp
public class WeatherMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,WeatherRecordWritable> {

	public void map(LongWritable key, Text value, OutputCollector<Text, WeatherRecordWritable> output,
			Reporter reporter) throws IOException {// TODO Auto-generated method stub
		
	     String line = value.toString();
	     String [] values=line.split(",");
	     String station_id=values[0];
	     String min_or_max=values[2];
	     double val=Double.parseDouble(values[3]);
	     Text k2 = new Text(station_id);
	     WeatherRecordWritable v2 = new WeatherRecordWritable();
	    v2.temp = val;
	    v2.min_or_max=min_or_max;
		
	    //emit only if it is a min or max record 
	    if (min_or_max.equalsIgnoreCase("TMIN")||min_or_max.equalsIgnoreCase("TMAX")) 
			{
			output.collect(k2, v2);
				
			}
		
	
	} 


}
