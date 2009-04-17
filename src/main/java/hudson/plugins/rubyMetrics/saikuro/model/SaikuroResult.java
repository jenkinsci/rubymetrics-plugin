package hudson.plugins.rubyMetrics.saikuro.model;

import java.util.ArrayList;
import java.util.Collection;

public class SaikuroResult {

	private Collection<SaikuroFileResult> files = new ArrayList<SaikuroFileResult>();
	private String totalComplexity;
	
	public SaikuroFileResult getFile(String href) {
		SaikuroFileResult file = null;
		
		for (SaikuroFileResult it : files) {
			if (it.getHref().equalsIgnoreCase(href)) {
				file = it;
				break;
			}
		}
		
		return file;
	}

	public Collection<SaikuroFileResult> getFiles() {
		return files;
	}
	
	public void setFiles(Collection<SaikuroFileResult> files) {
		this.files = files;
	}
	
	public void addFile(SaikuroFileResult file) {
		this.files.add(file);
		sumComplexity(file.getComplexityAsInt());
	}

	public String getTotalComplexity() {
		return totalComplexity;
	}

	public void setTotalComplexity(String totalComplexity) {
		this.totalComplexity = totalComplexity;
	}
	
	public void sumComplexity(int complexity) {
		int curComplexity = totalComplexity == null?0 : Integer.parseInt(totalComplexity);
		
		totalComplexity = String.valueOf(curComplexity + complexity);
	}
	
	public Integer getTotalComplexityAsInt() {
		return totalComplexity != null?Integer.parseInt(totalComplexity) : 0;
	}
}
