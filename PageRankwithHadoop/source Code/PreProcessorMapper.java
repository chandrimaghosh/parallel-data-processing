package pagerank.pagerank;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParserFactory;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapreduce.Mapper;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


public class PreProcessorMapper extends Mapper<Object, Text, Text, Node>{


	private static Pattern namePattern;

	static {
		// Keep only html pages not containing tilde (~).
		namePattern = Pattern.compile("^([^~]+)$");

	} 

	public void map(Object key,Text value,Context context)

	{
		ArrayList<String> linkPageNames = new ArrayList<String>();
		String page=value.toString();
		int pagenameLength=page.indexOf(':');
		String pageName=page.substring(0,pagenameLength);
		String pageContent=page.substring(pagenameLength+1);
		pageContent=pageContent.replaceAll(" & ","&amp;");
		//source:https://nuonline.neu.edu/bbcswebdav/pid-9701819-dt-content-rid-14794910_1/courses/CS6240.33175.201730/CS6240.33175.201730_ImportedContent_20170107022625/Bz2WikiParser.java
		Matcher matcher=namePattern.matcher(pageName);
		if (!matcher.find()) {
			// Skip this html file, name contains (~).
			return;
		}

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			// Parser fills this list with linked page names.
			xmlReader.setContentHandler(new WikiParser(linkPageNames));
			//parses the html content and finds the link pages 
			xmlReader.parse(new InputSource(new StringReader(pageContent)));
			
			Node node=new Node();
			node.isDangling=false;
			node.pageLinks=linkPageNames;
			context.write(new Text(pageName),node);
			
			
			//emit all the nodes that appear in the linkpages as sink nodes 
			for (String anodes:linkPageNames)
			{
				Node n =new Node();
				n.isDangling=true;
				n.pageLinks=new ArrayList<String>();
				context.write(new Text(anodes),node);
			}
		
			
			
		} catch (SAXNotRecognizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			//ignore invalid  html
			e.printStackTrace();
		}
	}
}








