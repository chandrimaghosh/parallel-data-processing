package InMapper.mapper;

import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


class InMapperMap extends Mapper <LongWritable, Text, Text, WeatherRecorInMapperWritable> {
	//the inmapper hashmap which will aggregate all the station ids in the same map task through multiple map calls
			HashMap<String,WeatherRecorInMapperWritable> map=new HashMap<String, WeatherRecorInMapperWritable>();
			
	public void map(LongWritable key, Text value, Context context ) throws IOException {


		String line = value.toString();
		String [] values=line.split(",");
		String station_id=values[0];
		String min_or_max=values[2];
		double val=Double.parseDouble(values[3]);
		Text k2 = new Text(station_id);
	
		//process only inputs with min or max 
		if (min_or_max.equalsIgnoreCase("TMIN") ) 
		{

			//the station id already exists 
			if( map.containsKey(k2.toString()))
			{

				//get object
				WeatherRecorInMapperWritable v2=map.get(k2.toString());
				//add count and sum 
				v2.minCount.set(v2.getMinCount()+1);
				v2.minSum.set(v2.getMinSum()+val);
			}else//station Id does not exist create new record in HashMap 
			{
				WeatherRecorInMapperWritable v2 = new WeatherRecorInMapperWritable(new DoubleWritable(0.0),new IntWritable(0),new DoubleWritable(val),new IntWritable(1));
				map.put(k2.toString(), v2); 
			}

		}//for Tmax 
		else if ( min_or_max.equalsIgnoreCase("TMAX"))
		{
			if( map.containsKey(k2.toString()))
			{
				WeatherRecorInMapperWritable v2=map.get(k2.toString());
				//add count and sum 
				v2.maxCount.set(v2.getMaxCount()+1);
				v2.maxSum.set(v2.getMaxSum()+val); 

			}else
			{
				WeatherRecorInMapperWritable v2 = new WeatherRecorInMapperWritable(new DoubleWritable(val),new IntWritable(1),new DoubleWritable(0.0),new IntWritable(0));
				map.put(k2.toString(), v2);
			}

		}
	}
	
	protected  void cleanup (Context context) throws IOException, InterruptedException
	{
		
		for(String key:map.keySet() )
		{
			 context.write(new Text(key),map.get(key));
		}
	}

	
	

}