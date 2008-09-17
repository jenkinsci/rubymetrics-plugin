package hudson.plugins.rubyMetrics.railsStats;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsProjectAction;

public class RailsStatsProjectAction extends AbstractRubyMetricsProjectAction {

	public RailsStatsProjectAction(AbstractProject<?, ?> project) {
		super(project);
	}
	
	public String getDisplayName() {
		return "Rails stats report";
	}

	public String getUrlName() {		
		return "railsStats";
	}
	
	public RailsStatsBuildAction getLastResult() {
		for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
	        if (b.getResult() == Result.FAILURE)
	            continue;
	        RailsStatsBuildAction r = b.getAction(RailsStatsBuildAction.class);
	        if (r != null)
	            return r;
	    }
	    return null;
	}
	
	public Integer getLastResultBuild() {
		for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            RailsStatsBuildAction r = b.getAction(RailsStatsBuildAction.class);
            if (r != null)
                return b.getNumber();
        }
        return null;
	}

}
