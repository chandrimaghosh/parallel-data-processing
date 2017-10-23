package pagerank.pagerank;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;

public class TopKRank implements Writable {
	
	double rank;
	String pageName;
	
	
	public TopKRank()
	{
		rank=0.0;
		pageName="";
		
	}
	
	public TopKRank(double rank,String pageName)
	{
		this.rank=rank;
		this.pageName=pageName;
	}
	
	public void readFields(DataInput arg0) throws IOException {
		
	rank=arg0.readDouble();
	pageName=arg0.readUTF();
		
	}
	public void write(DataOutput arg0) throws IOException {
	
		arg0.writeDouble(rank);
		arg0.writeUTF(pageName);
		
	}
	
	
}
