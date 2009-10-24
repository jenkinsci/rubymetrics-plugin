package hudson.plugins.rubyMetrics.railsNotes;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsBuildAction;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsProjectAction;
import hudson.plugins.rubyMetrics.railsStats.RailsStatsBuildAction;

public class RailsNotesProjectAction extends AbstractRubyMetricsProjectAction {
    public RailsNotesProjectAction(AbstractProject<?, ?> project) {
        super(project);
    }
    
    public String getDisplayName() {
        return "Annotations report";
    }

    public String getUrlName() {        
        return "railsNotes";
    }
    
    public RailsNotesBuildAction getLastResult() {
        for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            RailsNotesBuildAction r = b.getAction(RailsNotesBuildAction.class);
            if (r != null)
                return r;
        }
        return null;
    }
    
    public Integer getLastResultBuild() {
        for (AbstractBuild<?, ?> b = project.getLastStableBuild(); b != null; b = b.getPreviousNotFailedBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            RailsNotesBuildAction r = b.getAction(RailsNotesBuildAction.class);
            if (r != null)
                return b.getNumber();
        }
        return null;
    }
}
