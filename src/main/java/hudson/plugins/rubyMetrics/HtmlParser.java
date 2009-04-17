package hudson.plugins.rubyMetrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import hudson.model.BuildListener;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Text;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public abstract class HtmlParser {

	protected static final String TABLE_TAG_NAME = "table";
	protected static final String TD_TAG_NAME = "td";
	protected static final String TT_TAG_NAME = "tt";	
	protected static final String CLASS_ATTR_NAME = "class";
	
	protected final File rootFilePath;
	protected BuildListener listener;
	
	public HtmlParser(File rootFilePath) {
		this.rootFilePath = rootFilePath;
	}
	
	protected String getHtml(InputStream input) throws IOException {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	
    	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    	String line;
    	while ((line = reader.readLine()) != null) {
    		pw.write(line);
    	}
    	return sw.toString();
    }
	
	protected Parser initParser(String html) throws ParserException {
    	final Parser htmlParser = new Parser();
    	htmlParser.setInputHTML(html);
    	return htmlParser;
    }
	
	protected String getTextAtNode(NodeList nodeList, int index) {
		Node node = nodeList.elementAt(index);
		
		NodeList textNode = new NodeList();
		node.collectInto(textNode, new NodeClassFilter(Text.class));
		
		return textNode.elementAt(0).getText();
	}
	
	protected abstract TableTag getReportTable(Parser htmlParser) throws ParserException;
}
