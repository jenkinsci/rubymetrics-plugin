package hudson.plugins.rubyMetrics.rcov;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsProjectAction;

public class RcovProjectAction extends AbstractRubyMetricsProjectAction {

	public RcovProjectAction(AbstractProject<?, ?> project) {
		super(project);
	}
	
	public String getDisplayName() {
		return "Rcov report";
	}

	public String getUrlName() {
		return "rcov";
	}	
	
	public RcovBuildAction getLastResult() {
		for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
	        if (b.getResult() == Result.FAILURE)
	            continue;
	        RcovBuildAction r = b.getAction(RcovBuildAction.class);
	        if (r != null)
	            return r;
	    }
	    return null;
	}
	
	public Integer getLastResultBuild() {
		for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            RcovBuildAction r = b.getAction(RcovBuildAction.class);
            if (r != null)
                return b.getNumber();
        }
        return null;
	}

}
