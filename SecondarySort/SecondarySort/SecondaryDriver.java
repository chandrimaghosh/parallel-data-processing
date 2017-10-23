package SecondarySort.SecondarySort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.log4j.BasicConfigurator;

public class SecondaryDriver {

	
	//the weather record writable class that passes from the mapper to the reducer 
	
	
	 public static class  WeatherRecord implements Writable{
		
		
		double minSum;
		double maxSum;
		int minCount;
		int maxCount;
		String year;
		
		//default constructor
		public WeatherRecord()
		{}
		//parametrized constructor
		public WeatherRecord(double minSum,double maxSum,int minCount,int maxCount,String year)
		{
			this.minSum=minSum;
			this.minCount=minCount;
			this.maxCount=maxCount;
			this.maxSum=maxSum;
			this.year=year;
		}

		public void write(DataOutput out) throws IOException {
			out.writeInt(minCount);
			out.writeInt(maxCount);
			out.writeDouble(maxSum);
			out.writeDouble(minSum);
			out.writeUTF(year);
			
		}

		public void readFields(DataInput in) throws IOException {
			minCount=in.readInt();
			maxCount=in.readInt();
			maxSum=in.readDouble();
			minSum=in.readDouble();
			year=in.readUTF();
			
		}}
	
	//Key which has station id and year 
	//the key comparator first compares the key and then the year 
	//The key is an object of CustomKey that’s overrides its own 
	//CompareTo method.
	//The Key is a combination of stationId and Year
	//First it compare stationId s , if same compares the year

	 public static	class StationYearKey implements Writable,WritableComparable
	{
       public String stationId;
       public String year;
		
		
       public StationYearKey(){
           year="";
           stationId="";
       }

       public StationYearKey(String stationId,String year){
           this.stationId = stationId;
           this.year = year;
       }
       
       
       
     //if the station ids  are equal compare the year 
		
      
       public int compareTo (Object o) {
		return this.compareTo((StationYearKey) o);
	}
      
       
       public int compareTo(StationYearKey o) {
   		StationYearKey k=(StationYearKey)o;
   		//if the station ids  are equal compare the year 
   		int result=this.stationId.compareTo(k.stationId);
   		if (result==0)
   			return this.year.compareTo(k.year);
   		return result;
   	}
       
       
		public void write(DataOutput out) throws IOException {
			
			out.writeUTF(stationId);
			out.writeUTF(year);
			
		}

		public void readFields(DataInput in) throws IOException {
			stationId=in.readUTF();
			year=in.readUTF();
			
		}
		
	}

	
	//we need this to ignore all the year value and group all the station IDs together
	//The grouping comparator groups data by station id ignoring all the year value , 
	 //this ensures all the records with same stationId goes to same reduce call

	  public static class GroupingComparator extends WritableComparator
	{
		public GroupingComparator(){
            super(StationYearKey.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2){
        	StationYearKey key1 = (StationYearKey)w1;
        	StationYearKey key2 = (StationYearKey)w2;
            return (key1.stationId.compareTo(key2.stationId));
        }
		
	}
	  
	  public static class CustomPartitioner extends Partitioner <StationYearKey,WeatherRecord>{
		  //@Override
		  public int getPartition(StationYearKey key,WeatherRecord temp,int noOfReducers){

			  return ((key.stationId.hashCode()&Integer.MAX_VALUE)%noOfReducers);
		  }
	  }

	  /*Mapper*/
	//map emits key ,value pair 
	//where key is an object of CustomKEY 
	//and value is //(year,TmaxValue,TmaxCount,TminValue,TminCount)


	  public static  class SecondarySortMap extends Mapper<Object,Text,StationYearKey,WeatherRecord>{

		    protected void map(Object key, Text value , Context context) throws IOException, InterruptedException{

		    	String line = value.toString();
		    	String [] values=line.split(",");
				String station_id=values[0];
				String min_or_max=values[2];
				String year=values[1].trim().substring(0, 4);
				double val=Double.parseDouble(values[3]);
             StationYearKey key2=new StationYearKey(station_id, year);
				 if (min_or_max.equalsIgnoreCase("TMIN")) 
					{
					 
					 WeatherRecord v2=new WeatherRecord(val,0.0,1,0,year);
					context.write(key2, v2);
						
					}
				 else if(min_or_max.equalsIgnoreCase("TMAX"))
				 {
					 WeatherRecord v2=new WeatherRecord(0.0,val,0,1,year);
					 context.write(key2, v2);
				 }
				
		        
		    }
		}
	  
	  /*Reducer*/

	//The key to the reduce is our custom type (StationId,Year)
	//and value is a list having same keys 
	//Because of the grouping Comparator year is ignored and same //stationId s with diferent year are clubbed in the same call 
	//Value consists of  minSum maxSum, minCount,maxCount, //year
	//Because we exploited MR frameworks sorting  of keys the //important thing to notice is that the records to the reducer call //is sorted in ascending order of year .

//	  We make use of map reduces sorting mechanism to avoid explicit sorting in the reducer  .
//	  We achieve this by adding year as a part of the key and then use Key comparator and grouping comparator 
//	  The keycomprator emits records with ascending order of both station id and year and group comparator ignores the year and sends all records with same station id to one reduce call 
//	  Thus the input to the reducer is a list of 
//	  Records for one stationId in increasing order year eg:
//
//	  (A 1998)(A 1998)(A 1999)( A 2000) in one reduce call

	  
	  public static class SecondarySortReducer extends Reducer<StationYearKey, WeatherRecord, NullWritable, Text>
	  {
		  
	        protected void reduce(StationYearKey key, Iterable<WeatherRecord>  values,Context context) throws IOException, InterruptedException {
	        double minSum=0;
	        double maxSum=0;
	        int minCount=0;
	        int maxCount=0;
	        String year=null;
	        String v3="["+key.stationId+":";
	        for (WeatherRecord w:values)
	        {
	        
	         //if not the first time and year value changes 
	        	if (year!=null&& !year.equalsIgnoreCase(w.year))
	        	{
	        		
	        		//flush
	        		 v3 += "" + year + ", ";
	                 v3 += ( minCount > 0) ?(minSum / minCount)+ "," : "Nan ,";
	                 v3 += (maxCount > 0) ?(maxSum / maxCount)+ ",)]" : "Nan ,)]";
	             	//clean
		        	maxSum = 0;
	                maxCount = 0;
	                minSum =0;
	                minCount = 0;
	        		
	        	}
	        	year=w.year;
	        	maxSum += w.maxSum;
                maxCount += w.maxCount;
                minSum += w.minSum;
                minCount += w.minCount;
            			
	        }
	        
	       

            v3 += "" + year + ", ";
            v3 += ( minCount > 0) ?(minSum / minCount)+ "," : "Nan ,";
            v3 += (maxCount > 0) ?(maxSum / maxCount)+ ",)]" : "Nan ,)]";
            context.write(NullWritable.get(), new Text(v3));
           }
	        
	  }
	  
	  
	  
	  public static void main( String[] args )
	    {

	        try {
	            Configuration conf = new Configuration();
	            BasicConfigurator.configure();
	            GenericOptionsParser optionsParser = new GenericOptionsParser(conf,args);
	            String[] remainingArgs = optionsParser.getRemainingArgs();
	            if (remainingArgs.length < 2){
	                System.err.println("Expecting Inputs");
	                System.exit(2);
	            }
	            Job job = Job.getInstance(conf, "SecSort");
	            job.setJarByClass(SecondaryDriver.class);
	            
	            job.setMapperClass(SecondarySortMap.class);
	            
	            job.setGroupingComparatorClass(GroupingComparator.class);
	            
	            job.setPartitionerClass(CustomPartitioner.class);
	           
	            job.setReducerClass(SecondarySortReducer.class);
	            
	            job.setOutputKeyClass(StationYearKey.class);
	            job.setOutputValueClass(WeatherRecord.class);
	            for(int i=0;i<remainingArgs.length-1;i++) {
	                FileInputFormat.addInputPath(job, new Path(args[i]));
	            }
	            FileOutputFormat.setOutputPath(job, new Path(remainingArgs[remainingArgs.length-1]));
	            System.exit(job.waitForCompletion(true) ? 0 : 1);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    }
	}
	  

	  
	
	  

	



