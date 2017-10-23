package pagerank.pagerank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


//TopKReducer reducer is used to find global top100 from the local top100
public class TopKReducer extends Reducer <NullWritable,TopKRank,NullWritable,Text> {

	Comparator<TopKRank> comparator=new TopKMapper.TopKRankComparator();
	PriorityQueue<TopKRank> q=new PriorityQueue<TopKRank>(100,comparator);
	//TopKReducer reducer is used to find global top100 from the local top100
	public void reduce(NullWritable Key,Iterable<TopKRank> values,Context context) throws IOException, InterruptedException
		{

        for(TopKRank t :values){
    		System.out.println("key:"+t.pageName+"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&  pr:"+t.rank);
           q.add(new TopKRank(t.rank,t.pageName));
            
    		if (q.size()>100)
    		{
    			q.poll();
    		}
	
        }
	        TopKRank top100;
	        
	     
        ArrayList<String> reverseResults=new ArrayList<String>();
        while(!q.isEmpty()) {
        	top100=q.poll();
        	String result=top100.pageName+":"+top100.rank;
        	reverseResults.add(result);	}
        for (int i=reverseResults.size()-1;i>=0;i--)
        {
        	context.write(NullWritable.get(),new Text(reverseResults.get(i).toString()));
        	}
 		}
	}
