package hudson.plugins.rubyMetrics.saikuro.model;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;

import java.io.Serializable;

public class SaikuroFileDetail implements ModelObject, Serializable  {

    private final AbstractBuild<?, ?> owner;
    private final SaikuroFileResult result;
    
    public SaikuroFileDetail(final AbstractBuild<?, ?> owner, final SaikuroFileResult result) {        
        this.owner = owner;
        this.result = result;
    }
    
    public AbstractBuild<?, ?> getOwner() {
		return owner;
	}

	public SaikuroFileResult getResult() {
		return result;
	}

	public String getDisplayName() {		
		return "Saikuro report for: " + result.getClassName();
	}
}