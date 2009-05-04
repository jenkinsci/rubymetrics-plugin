package hudson.plugins.rubyMetrics;

import hudson.Plugin;
import hudson.plugins.rubyMetrics.railsStats.RailsStatsPublisher;
import hudson.plugins.rubyMetrics.rcov.RcovPublisher;
import hudson.plugins.rubyMetrics.saikuro.SaikuroPublisher;
import hudson.tasks.BuildStep;

/**
 * Entry point of a plugin.
 *
 * @author David Calavera
 * @plugin
 */
public class PluginImpl extends Plugin {
    public void start() throws Exception {    	
        BuildStep.PUBLISHERS.addRecorder(RcovPublisher.DESCRIPTOR);        
        BuildStep.PUBLISHERS.addRecorder(RailsStatsPublisher.DESCRIPTOR);
//        BuildStep.PUBLISHERS.addRecorder(SaikuroPublisher.DESCRIPTOR);
    }
}
