package pagerank.pagerank;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PreProcessorReducer extends Reducer<Text,Node,Text,Node>  {

	public void reduce(Text key,Iterable<Node>values,Context context)
	{
		
		Boolean isReallyDangling =true;
		
			Node node=new Node();
	        node.pageLinks=new ArrayList<String>();
	        node.isDangling=true;
		
	        try {	

			for (Node n:values)
			{
				if(!n.isDangling){
			     n.pageLinks=n.pageLinks;
		         n.isDangling=false;
		         isReallyDangling =false;
		         context.getCounter(PageRankDriver.gloabalVariable.totalPages).increment(1);
		         context.write(key,n);
		         return;
				}
	
			}
			if (isReallyDangling)
			{
				 context.write(key,node);
		         context.getCounter(PageRankDriver.gloabalVariable.totalPages).increment(1);

			}
			
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
