package InMapper.mapper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

class WeatherRecorInMapperWritable implements Writable{

	public DoubleWritable maxSum; 
	public IntWritable maxCount;
	public DoubleWritable minSum; 
	public IntWritable minCount;

	// default constructor
	public WeatherRecorInMapperWritable(){

		maxSum = new DoubleWritable();
		maxCount= new IntWritable();
		minSum = new DoubleWritable();
		minCount= new IntWritable();
	}

	//customized parametrized constructor
	public WeatherRecorInMapperWritable( DoubleWritable s, IntWritable c,DoubleWritable s2, IntWritable c2){
		maxSum=s;
		maxCount=c;
		minSum=s2;
		minCount=c2;

	}

	// method to serialize object
	public void write(DataOutput out) throws IOException {

		maxCount.write(out);
		maxSum.write(out);
		minCount.write(out);
		minSum.write(out);


	}
	// method to serialize object
	public void readFields(DataInput in) throws IOException {

		maxCount.readFields(in);
		maxSum.readFields(in);
		minCount.readFields(in);
		minSum.readFields(in);

	}


	//getters 
	public double getMaxSum() {
		return maxSum.get();
	}

	public int getMaxCount() {
		return maxCount.get();
	}

	public double getMinSum() {
		return minSum.get();
	}

	public int getMinCount() {
		return minCount.get();
	}
	//setters
	public void setMaxSum(double maxSum) {
		this.maxSum = new DoubleWritable(maxSum);
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = new IntWritable(maxCount);
	}

	public void setMinSum(double minSum) {
		this.minSum = new DoubleWritable(minSum);
	}

	public void setMinCount(int minCount) {
		this.minCount = new IntWritable(minCount);
	}




}

