package hudson.plugins.rubyMetrics.rcov.model;

import java.math.BigDecimal;

public class RcovAbstractResult {
	
	private String totalLines;
	private String codeLines;
	private String totalCoverage;
	private String codeCoverage;
	
	public Integer getTotalLinesInteger() {
		return Integer.valueOf(getTotalLines());
	}
	
	public String getTotalLines() {
		return totalLines;
	}
	public void setTotalLines(String totalLines) {
		this.totalLines = totalLines;
	}
	
	public Integer getCodeLinesInteger() {
		return Integer.valueOf(getCodeLines());
	}
	
	public String getCodeLines() {
		return codeLines;
	}
	public void setCodeLines(String codeLines) {
		this.codeLines = codeLines;
	}
	
	public Float getTotalCoverageFloat() {
		return Float.valueOf(totalCoverage.replaceAll("%", ""));
	}
	
	public String getTotalCoverage() {
		return totalCoverage;
	}
	public void setTotalCoverage(String totalCoverage) {
		this.totalCoverage = totalCoverage;
	}
	
	public Float getCodeCoverageFloat() {
		return Float.valueOf(codeCoverage.replaceAll("%", ""));
	}
	
	public String getCodeCoverage() {
		return codeCoverage;
	}
	public void setCodeCoverage(String codeCoverage) {
		this.codeCoverage = codeCoverage;
	}
	
	public String getTotalCoveredWidth() {
		return new BigDecimal(getTotalCoverageFloat()).setScale(0, BigDecimal.ROUND_HALF_DOWN).toString();
	}
	
	public String getTotalUncoveredWidth() {
		return String.valueOf(100 - Integer.valueOf(getTotalCoveredWidth()));
	}
	
	public String getCodeCoveredWidth() {
		return new BigDecimal(getCodeCoverageFloat()).setScale(0, BigDecimal.ROUND_HALF_DOWN).toString();
	}
	
	public String getCodeUncoveredWidth() {
		return String.valueOf(100 - Integer.valueOf(getCodeCoveredWidth()));
	}
}
