package hudson.plugins.rubyMetrics.saikuro;

import hudson.model.BuildListener;
import hudson.plugins.rubyMetrics.HtmlParser;
import hudson.plugins.rubyMetrics.rcov.model.RcovFileResult;
import hudson.plugins.rubyMetrics.saikuro.model.SaikuroFileResult;
import hudson.plugins.rubyMetrics.saikuro.model.SaikuroResult;
import hudson.util.IOException2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Text;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class SaikuroParser extends HtmlParser {

	public SaikuroParser(File rootFilePath, BuildListener listener) {
		super(rootFilePath);
		this.listener = listener;
	}
	
	public SaikuroResult parse(File file) throws IOException {        
        return parse(new FileInputStream(file));
    }
	
    public SaikuroResult parse(InputStream input) throws IOException {
    	try {
    		SaikuroResult result = new SaikuroResult();
    		
    		Parser parser = initParser(getHtml(input));
    		TableTag report = getReportTable(parser);
    		
    		if (report.getRowCount() > 0) {
    			//row at 0 is the header row
    			for (int i = 1; i < report.getRowCount(); i++) {
    				result.addFile(parseRow(report.getRow(i)));
    			}
    		}
    		
    		
    		return result;
    	} catch (Exception e) {
    		throw new IOException2("cannot parse saikuro report file", e);
    	}
    }

	@Override
	protected TableTag getReportTable(Parser htmlParser) throws ParserException {
		NodeList node = htmlParser.extractAllNodesThatMatch(new TagNameFilter(TABLE_TAG_NAME));
		if (!(node != null && node.size() > 0)) {
    		throw new ParserException("cannot parse saikuro file, report element wasn't found");
    	}
    	return (TableTag) node.elements().nextNode();				
	}
	
	private SaikuroFileResult parseRow(TableRow row) throws ParserException, IOException {
    	final SaikuroFileResult file = new SaikuroFileResult();
    	
    	NodeList nodeList = new NodeList();
    	row.collectInto(nodeList, new TagNameFilter("a"));
    	
    	if (nodeList.size() > 0) {
    		LinkTag link = (LinkTag) nodeList.elementAt(0);    		
    		file.setHref(link.getLink()
    		    .replaceAll("_cyclo.html", "")
    		    .replaceAll("\\./", "")
    		    .replaceAll("/", "-").trim());
    		file.setClassName(link.getLinkText());
    	}
    	
    	NodeList columnList = new NodeList();
    	row.collectInto(columnList, new TagNameFilter(TD_TAG_NAME));
    	if (columnList.size() > 0) {	
    		file.setMethodName(getTextAtNode(columnList, 1));
    		file.setComplexity(getTextAtNode(columnList, 2));
    	}    	    		    
    	return file;
    }
}
