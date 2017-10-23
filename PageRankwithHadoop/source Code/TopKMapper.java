package pagerank.pagerank;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
// TopKMapper is used to locally accumulate the top 100 nodes 
public class TopKMapper extends Mapper<Text,Node,NullWritable,TopKRank>{

	private PriorityQueue<TopKRank> q;


	public void setup(Context context)
	{
		Comparator<TopKRank> comparator =new TopKRankComparator();
		//set the capacity of the Priority queue to 100 
		q=new PriorityQueue<TopKRank>(100,comparator);
	}
	 
	
	//clean up function flushes out the top 100 pages in the map call
	public void cleanup(Context context) throws IOException,InterruptedException{
		  TopKRank topPage;
	        while(!q.isEmpty()){
	        	topPage=q.poll();
	            context.write(NullWritable.get(),topPage);
	        }

	    }

	public void map(Text Key,Node value, Context context) 
	{	
		//locally accumulate the top 100 nodes 
		TopKRank v=new TopKRank();
		v.pageName=Key.toString();
		v.rank=value.pageRank;
		
		
		q.add(v);

		//locally accumulate the top 100 nodes 
		if (q.size()>100)
		{ 
			q.poll();
		}

	}
	
	//custom comparator for natural order order
	
	
	 public static class TopKRankComparator implements Comparator<TopKRank>{
		//Compares its two arguments for order.
		//Returns a negative integer, zero, or a positive integer as the
		//first argument is less than, equal to, or greater than the second.
		 public int compare(TopKRank o1, TopKRank o2) {

			if (o1.rank < o2.rank)
			{
				return -1;
			}if (o1.rank > o2.rank)
			{
				return +1 ;
			}
			return 0;
		}
	}

}
