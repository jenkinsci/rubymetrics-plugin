package hudson.plugins.rubyMetrics.saikuro.model;

public class SaikuroFileResult {
	private String className;
	private String href;
	private String methodName;
	private String complexity;
	private String totalComplexity;
	private String totalLines;
	private String lines;
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getComplexity() {
		return complexity;
	}
	public void setComplexity(String complexity) {
		this.complexity = complexity;
	}
	public String getTotalComplexity() {
		return totalComplexity;
	}
	public void setTotalComplexity(String totalComplexity) {
		this.totalComplexity = totalComplexity;
	}
	public String getTotalLines() {
		return totalLines;
	}
	public void setTotalLines(String totalLines) {
		this.totalLines = totalLines;
	}
	public String getLines() {
		return lines;
	}
	public void setLines(String lines) {
		this.lines = lines;
	}
	
	public int getComplexityAsInt() {
		return totalComplexity != null? Integer.parseInt(totalComplexity) :
			complexity != null? Integer.parseInt(complexity) : 0;
	}
}
