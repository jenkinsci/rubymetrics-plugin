package hudson.plugins.rubyMetrics.railsNotes;

import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsProjectAction;

public class RailsNotesProjectAction<RailsNotesBuildAction> extends AbstractRubyMetricsProjectAction {
    public RailsNotesProjectAction(Job<?, ?> job) {
        super(job);
    }

    @Deprecated
    public RailsNotesProjectAction(AbstractProject<?, ?> project) {
        super(project);
    }

    public String getDisplayName() {
        return "Annotations report";
    }

    public String getUrlName() {
        return "railsNotes";
    }

    @Override
    protected Class getBuildActionClass() {
        return hudson.plugins.rubyMetrics.railsNotes.RailsNotesBuildAction.class;
    }
}
