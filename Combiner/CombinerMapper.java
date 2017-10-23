package Combiner.combiner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;



import org.apache.hadoop.io.Writable;



 class WeatherRecordCombinerWritable implements Writable{
    public Text min_or_max;  
    public DoubleWritable sum; 
    public IntWritable count;

   // default constructor
   public WeatherRecordCombinerWritable(){
	   min_or_max = new Text();
       sum = new DoubleWritable();
       count= new IntWritable();
   }

   //customized parametrized constructor
    public WeatherRecordCombinerWritable(Text m, DoubleWritable s, IntWritable c){
    	min_or_max = m;
    	sum = s;
        count= c;
    }
  
    // method to serialize object
    public void write(DataOutput dataOutput) throws IOException {
    	min_or_max.write(dataOutput);
    	sum.write(dataOutput);
    	count.write(dataOutput);
    }
    // method to serialize object
    public void readFields(DataInput dataInput) throws IOException {
    	min_or_max.readFields(dataInput);
    	sum.readFields(dataInput);
        count.readFields(dataInput);
    }
    
    //getters
    public String getMin_or_Max(){
    	return min_or_max.toString();
    }
    public double getSum(){
    	return Double.parseDouble(sum.toString());
    }
    public int getCount(){ return Integer.parseInt(count.toString());}
}



//Mapper Class emit the the stationId as key and Weather Record Writable as value
public class CombinerMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, WeatherRecordCombinerWritable> {

	public void map(LongWritable key, Text value, OutputCollector<Text, WeatherRecordCombinerWritable> output, Reporter reporter) throws IOException {
		
	
	 String line = value.toString();
     String [] values=line.split(",");
     String station_id=values[0];
     String min_or_max=values[2];
     double val=Double.parseDouble(values[3]);
     Text k2 = new Text(station_id);
     
     WeatherRecordCombinerWritable v2 = new WeatherRecordCombinerWritable(new Text(min_or_max),new DoubleWritable(val),new IntWritable(1));
   
	//send only inputs with min or max 
	if (min_or_max.equalsIgnoreCase("TMIN") || min_or_max.equalsIgnoreCase("TMAX")) 
		{
		output.collect(k2, v2);
		}
	
	}


}
