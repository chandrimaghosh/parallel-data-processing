package pagerank.pagerank;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PageRankMapper extends Mapper<Text, Node, Text, Node>{

	public void map(Text key,Node node,Context context) throws IOException, InterruptedException
	{
		boolean isFirst=context.getConfiguration().getBoolean("isFirst",true);
		double pageRank=0.0;
		long totalPages= context.getConfiguration().getLong("TotalPages",0);
		double Oneminusalpha=0.80;
		double delta=context.getConfiguration().getDouble("Delta",0.0);
		//for the first time the page Rank is calculated a 1/total number of nodes
		if (isFirst)
		{
			pageRank=1.0/totalPages;
		}//the dangling loss mass is equally divided to all other nodes 
		else
		{
			pageRank=node.pageRank+Oneminusalpha*(delta/totalPages);

	 	}
		//page rank is set 
		node.pageRank=pageRank;
		//emit the node with calculated pageRank 

		if (node.pageLinks.size()>0)
		context.write(key, node);

		//dangling node  page rank  will not contribute but nevertheless , its is extremely important 
		//we mark it as a dangling node and pass its PR value since it will used 
		//in the next iteration to calculate the delta value 
		if (node.pageLinks.size()==0)
		{
			context.write(new Text("~DNode"), node);

		}else{
			//calculate the contributing page rank the node contributes to all its 
			//linkedNodes so it can used in the forthcoming iterations.

			double contributingPR=node.pageRank/(node.pageLinks.size());
			for (String linkedNodes:node.pageLinks)
			{
				Node n =new Node();
				n.pageLinks=new ArrayList<String>();
				n.pageRank=contributingPR;
				context.write(new Text(linkedNodes.toString()),n);
			}

		}
		}
}
