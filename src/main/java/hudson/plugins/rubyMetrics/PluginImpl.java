package hudson.plugins.rubyMetrics;

import hudson.Plugin;
import hudson.plugins.rubyMetrics.railsStats.RailsStatsPublisher;
import hudson.plugins.rubyMetrics.rcov.RcovPublisher;
import hudson.tasks.BuildStep;

/**
 * Entry point of a plugin.
 *
 * @author David Calavera
 * @plugin
 */
public class PluginImpl extends Plugin {
    public void start() throws Exception {        
        BuildStep.PUBLISHERS.add(RcovPublisher.DESCRIPTOR);        
        BuildStep.PUBLISHERS.add(RailsStatsPublisher.DESCRIPTOR);
    }
}
