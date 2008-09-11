package hudson.plugins.rubyMetrics.rcov;

import hudson.Plugin;
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
    }
}
