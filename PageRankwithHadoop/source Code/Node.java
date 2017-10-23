package pagerank.pagerank;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.io.Writable;

public class Node implements Writable{
	
	double pageRank;
	public  ArrayList<String> pageLinks;
	Boolean isDangling;
	
	
	public Node()
	{
		isDangling=true;
		this.pageRank=0.0;
	}
	
	public void readFields(DataInput in) throws IOException {
		pageRank=in.readDouble();
		pageLinks=new ArrayList<String>();
		isDangling=in.readBoolean();
		int len=in.readInt();
		
		
			for(int i=0;i<len;i++)
			{
				pageLinks.add(in.readUTF());
			}
			
	}
	
	public void write(DataOutput out) throws IOException {
		
		out.writeDouble(pageRank);
		out.writeBoolean(isDangling);
		out.writeInt(pageLinks.size());
		 for(int i = 0 ; i < pageLinks.size(); i++){
		out.writeUTF(pageLinks.get(i));
	   }
}
	
	
	
}
