package NoCombiner.noCombiner;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.DoubleWritable;;

public class WeatherRecordWritable implements Writable {
	
	
	public String min_or_max;
	public double temp;
	 
	public WeatherRecordWritable()
	{
		this.min_or_max = "";
		this.temp = 0;
	}
	
	public WeatherRecordWritable(String min_or_max, Double temp)
	{
		
		this.min_or_max=min_or_max;
		this.temp=temp;
	}
	
	

	public void write(DataOutput out) throws IOException {
		out.writeUTF(min_or_max);
		out.writeDouble(temp);
		
	}
	

	public void readFields(DataInput in) throws IOException {
        min_or_max = in.readUTF();
        temp = in.readDouble();
      }
      
      public static WeatherRecordWritable read(DataInput in) throws IOException {
    	  WeatherRecordWritable w = new WeatherRecordWritable();
    	  w.readFields(in);
    	  return w;
      }	
	
      

}
