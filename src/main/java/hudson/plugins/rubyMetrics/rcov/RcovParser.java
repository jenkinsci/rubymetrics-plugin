package hudson.plugins.rubyMetrics.rcov;

import hudson.plugins.rubyMetrics.HtmlParser;
import hudson.plugins.rubyMetrics.rcov.model.RcovFileResult;
import hudson.plugins.rubyMetrics.rcov.model.RcovResult;
import hudson.util.IOException2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
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
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class RcovParser extends HtmlParser {

	private static final String REPORT_CLASS_VALUE = "report";

	public RcovParser(File rootFilePath) {
		super(rootFilePath);
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
        		  TableColumn[] columns = totalRow.getColumns();

              result.setTotalLines(getTextFromTT(columns[1]));
              result.setCodeLines(getTextFromTT(columns[2]));
              result.setTotalCoverage(getTextFromTT(columns[3]));
              result.setCodeCoverage(getTextFromTT(columns[4]));

	        	  for (int i = 2; i < report.getRowCount(); i++) {
	        		  result.addFile(parseRow(report.getRow(i)));
	        	  }
        	}        	
        	
        	return result;
        } catch (Exception e) {
            throw new IOException2("cannot parse rcov report file", e);
        }        
    }
    
    protected TableTag getReportTable(Parser htmlParser) throws ParserException {
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

        TableColumn[] columns = row.getColumns();
        file.setTotalLines(getTextFromTT(columns[1]));
        file.setCodeLines(getTextFromTT(columns[2]));
        file.setTotalCoverage(getTextFromTT(columns[3]));
        file.setCodeCoverage(getTextFromTT(columns[4]));        
	
    	file.setSourceCode(parseSource(linkPath));
    	
    	return file;
    }
    
    private String getTextFromTT(TableColumn td) {
        NodeList nodeList = new NodeList();
        td.collectInto(nodeList, new TagNameFilter(TT_TAG_NAME));

        return getTextFromFirstNode(nodeList);
    }
    
    private String getTextFromFirstNode(NodeList nodeList) {
        String text = null;

        if (nodeList.size() > 0) {
            Node first = nodeList.elementAt(0);
            nodeList = new NodeList();
            Node parent = first.getChildren() != null && first.getChildren().size() > 0?first:first.getParent();

            parent.collectInto(nodeList, new NodeClassFilter(Text.class));
            text = nodeList.toHtml(true).replaceAll("&nbsp;", "").trim();
        }

        return text;
    }

    public String parseSource(String href) throws ParserException, IOException {    	
    	String source = null;
    	    	    	
    	File[] sourceFile = rootFilePath.listFiles(new RcovFilenameFilter(href));
    	    	
    	if (sourceFile != null && sourceFile.length > 0 && sourceFile[0].exists()) {
    		String html = getHtml(new FileInputStream(sourceFile[0]));
    		
    		String sourceFromDetails = parseSourceInTableDetails(html);
    		if (sourceFromDetails != null) {
    			source = sourceFromDetails;
    		} else {
	    		source = parseSourceFromRegularExpression(html);
    		}
    	}
    	
    	return source;
    }
    
    private String parseSourceInTableDetails(String html) throws FileNotFoundException, ParserException, IOException {
    	String source = null;

    	final Parser htmlParser = initParser(html);
		final AndFilter filter = new AndFilter(new TagNameFilter(TABLE_TAG_NAME), 
    			new HasAttributeFilter(CLASS_ATTR_NAME, "details"));
    	
    	NodeList reportNode = htmlParser.extractAllNodesThatMatch(filter);
    	if (reportNode != null && reportNode.size() > 0) {
    		source = ((TableTag) reportNode.elements().nextNode()).toHtml(true);
    	}
    	
    	return source;
    }
    
    private String parseSourceFromRegularExpression(String html) {
    	String source = null;
    	
    	Pattern pattern = Pattern.compile(".+<table class='report'>.+(<pre>(.+)</pre>).+");
		Matcher matcher = pattern.matcher(html);
		if (matcher.matches()) {
			source = matcher.group(1);    			
		}
    	
    	return source;
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
