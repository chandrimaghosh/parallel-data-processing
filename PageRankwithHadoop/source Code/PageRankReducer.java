package pagerank.pagerank;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

import pagerank.pagerank.PageRankDriver.gloabalVariable;

public class PageRankReducer extends Reducer<Text,Node,Text,Node>{
	private static double a=0.20;

	public void reduce(Text key,Iterable<Node> values,Context context) throws IOException, InterruptedException
	{
		long totalPages =  context.getConfiguration().getLong("TotalPages",0);
		double PageRankSoFar=0.0;
		double deltaTotal=0.0;
		
		//Dangling node handle
		//Calculate the total delta from all the dangling nodes 
		if (key.toString().equalsIgnoreCase("~Dnode"))
		{  
			for (Node n:values)
			{

				deltaTotal=deltaTotal+n.pageRank;
			}
			
			//store delta  in Context  variable 
			  long longDelta=Double.doubleToLongBits(deltaTotal);
			  //System.out.println("longDelta:"+longDelta+"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			  context.getCounter(gloabalVariable.delta).setValue(longDelta);          
	          return;

		}
		//Calculate the page Rank for all the other nodes 
		Node n =new Node();
		n.isDangling=false;
		n.pageLinks=new ArrayList<String>();
		n.pageRank=0.0;
		for(Node node:values)
		{
			if (!node.isDangling)
			{
				n.pageLinks=node.pageLinks;
			}
			PageRankSoFar=PageRankSoFar+node.pageRank;
		}
		//reducer all the contributiong pageRanks are added up and the new 
		//page rank is calculated with the formula 
		double pageRank=(a/totalPages)+((1-a)*(PageRankSoFar));
		n.pageRank=pageRank;
	
		//System.out.println("pageRank:"+pageRank+"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+"Key"+key);
		context.write(key, n);

	}
}
