package hudson.plugins.rubyMetrics.saikuro;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsProjectAction;

public class SaikuroProjectAction extends AbstractRubyMetricsProjectAction {

	public SaikuroProjectAction(AbstractProject<?, ?> project) {
		super(project);
	}
	
	public String getDisplayName() {
		return "Saikuro report";
	}

	public String getUrlName() {
		return "saikuro";
	}	
	
	public SaikuroBuildAction getLastResult() {
		for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
	        if (b.getResult() == Result.FAILURE)
	            continue;
	        SaikuroBuildAction r = b.getAction(SaikuroBuildAction.class);
	        if (r != null)
	            return r;
	    }
	    return null;
	}
	
	public Integer getLastResultBuild() {
		for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            SaikuroBuildAction r = b.getAction(SaikuroBuildAction.class);
            if (r != null)
                return b.getNumber();
        }
        return null;
	}

}
