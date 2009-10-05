package hudson.plugins.rubyMetrics.rcov.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import org.jvnet.localizer.Localizable;

public class RcovResult extends RcovAbstractResult {
	
	private Collection<RcovFileResult> files = new LinkedHashSet<RcovFileResult>();	

	public RcovFileResult getFile(String href) {
		RcovFileResult file = null;
		
		for (RcovFileResult it : files) {
			if (it.getHref().equalsIgnoreCase(href)) {
				file = it;
				break;
			}
		}
		
		return file;
	}
	
	public Collection<RcovFileResult> getFiles() {
		return files;
	}

	public void setFiles(Collection<RcovFileResult> files) {
		this.files = files;
	}
	
	public void addFile(RcovFileResult file) {
		this.files.add(file);
	}
	
	public Float getRatioFloat(Targets metric) {
		return metric.equals(Targets.TOTAL_COVERAGE)? getTotalCoverageFloat():getCodeCoverageFloat();
	}
	
	public String getRatio(Targets metric) {
		return metric.equals(Targets.TOTAL_COVERAGE)? getTotalCoverage():getCodeCoverage();
	}
	
	public Localizable getHealthDescription(Targets metric) {
		return Messages._RcovResult_HealthDescription(metric.getName(), getRatio(metric), getRatioFloat(metric));
	}
}
