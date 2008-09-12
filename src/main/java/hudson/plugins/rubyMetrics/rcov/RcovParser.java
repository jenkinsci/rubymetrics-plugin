package hudson.plugins.rubyMetrics.rcov;

import hudson.plugins.rubyMetrics.rcov.model.RcovFileResult;
import hudson.plugins.rubyMetrics.rcov.model.RcovResult;
import hudson.util.IOException2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Text;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class RcovParser {

	private static final String REPORT_CLASS_VALUE = "report";
	private static final String TOTAL_LINES = "lines_total";
	private static final String CODE_LINES = "lines_code";
	private static final String TOTAL_COVERAGE = "coverage_total";
	private static final String CODE_COVERAGE = "coverage_code";
	
	private static final String TABLE_TAG_NAME = "table";
	private static final String TD_TAG_NAME = "td";
	private static final String TT_TAG_NAME = "tt";	
	private static final String CLASS_ATTR_NAME = "class";

	private final File rootFilePath;
	
	public RcovParser(File rootFilePath) {
		this.rootFilePath = rootFilePath;
	}
	
    public RcovResult parse(File file) throws IOException {        
        return parse(new FileInputStream(file));
    }
    
    public RcovResult parse(InputStream input) throws IOException {
    	try {            
    		RcovResult result = new RcovResult();
    		
        	Parser parser = initParser(getHtml(input));        	
        	TableTag report = getReportTable(parser);
        	
        	if (report.getRowCount() > 0) {        		
        		//row at 0 is the header row, so we have to get the row at 1 
        		TableRow totalRow = report.getRow(1);        		
        		
        		result.setTotalLines(getColumnByClassName(totalRow, TD_TAG_NAME, TOTAL_LINES));
        		result.setCodeLines(getColumnByClassName(totalRow, TD_TAG_NAME, CODE_LINES));
        		result.setTotalCoverage(getColumnByClassName(totalRow, TT_TAG_NAME, TOTAL_COVERAGE));
        		result.setCodeCoverage(getColumnByClassName(totalRow, TT_TAG_NAME, CODE_COVERAGE));
        		
	        	for (int i = 2; i < report.getRowCount(); i++) {
	        		result.addFile(parseRow(report.getRow(i)));
	        	}
        	}        	
        	
        	return result;
        } catch (Exception e) {
            throw new IOException2("cannot parse rcov report file", e);
        }        
    }
    
    private String getHtml(InputStream input) throws IOException {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	
    	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    	String line;
    	while ((line = reader.readLine()) != null) {
    		pw.write(line);
    	}
    	return sw.toString();
    }
    
    private Parser initParser(String html) throws ParserException {
    	final Parser htmlParser = new Parser();
    	htmlParser.setInputHTML(html);
    	return htmlParser;
    }
    
    private TableTag getReportTable(Parser htmlParser) throws ParserException {
    	final AndFilter filter = new AndFilter(new TagNameFilter(TABLE_TAG_NAME), 
    			new HasAttributeFilter(CLASS_ATTR_NAME, REPORT_CLASS_VALUE));
    	
    	NodeList reportNode = htmlParser.extractAllNodesThatMatch(filter);
    	if (!(reportNode != null && reportNode.size() > 0)) {
    		throw new ParserException("cannot parse rcov report file, report element wasn't found");
    	}
    	return (TableTag) reportNode.elements().nextNode();
    }
    
    private RcovFileResult parseRow(TableRow row) throws ParserException, IOException {
    	final RcovFileResult file = new RcovFileResult();
    	
    	NodeList nodeList = new NodeList();
    	row.collectInto(nodeList, new TagNameFilter("a"));
    	String linkPath = null;
    	if (nodeList.size() > 0) {
    		LinkTag link = (LinkTag) nodeList.elementAt(0);
    		linkPath = link.getLink();
    		file.setHref(link.getLink().replaceAll(".html", ""));
    		file.setName(link.getLinkText());
    	}
    	    		    
    	file.setTotalLines(getColumnByClassName(row, TD_TAG_NAME, TOTAL_LINES));
    	file.setCodeLines(getColumnByClassName(row, TD_TAG_NAME, CODE_LINES));
    	
    	file.setTotalCoverage(getColumnByClassName(row, TT_TAG_NAME, TOTAL_COVERAGE));
    	file.setCodeCoverage(getColumnByClassName(row, TT_TAG_NAME, CODE_COVERAGE));
    	
    	file.setSourceCode(parseSource(linkPath));
    	
    	return file;
    }
    
    private String getColumnByClassName(TableRow row, String tagName, String className) {
    	NodeList nodeList = new NodeList();
	    row.collectInto(nodeList, new AndFilter(new TagNameFilter(tagName), new HasAttributeFilter(CLASS_ATTR_NAME, className)));
	    
	    String text = null;
	    
	    if (nodeList.size() > 0) {
	    	Node first = nodeList.elementAt(0);	    
	    	nodeList = new NodeList();
	    	Node parent = first.getChildren() != null && first.getChildren().size() > 0?first:first.getParent();
	    	
	    	parent.collectInto(nodeList, new NodeClassFilter(Text.class));
    		text = nodeList.elementAt(0).getText();    		
	    }
	    
	    return text;
    }
    
    private String parseSource(String href) throws ParserException, IOException {    	    	
    	String html = null;
    	    	    	
    	File[] sourceFile = rootFilePath.listFiles(new RcovFilenameFilter(href));
    	    	
    	if (sourceFile != null && sourceFile.length > 0 && sourceFile[0].exists()) {
    		html = getHtml(new FileInputStream(sourceFile[0]));
    		Pattern pattern = Pattern.compile(".+<table class='report'>.+(<pre>(.+)</pre>).+");
    		Matcher matcher = pattern.matcher(html);
    		if (matcher.matches()) {
    			html = matcher.group(1);    			
    		}
    	}
    	
    	return html;
    }

    private static class RcovFilenameFilter implements FilenameFilter {
		
		String fileName;
		public RcovFilenameFilter(String fileName) {
			this.fileName = fileName;
		}
		
        public boolean accept(File dir, String name) {            
            return name.equalsIgnoreCase(fileName);
        }
    }
    
}
