package hudson.plugins.rubyMetrics.rcov.model;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;

import java.io.Serializable;

public class RcovFileDetail implements ModelObject, Serializable  {
   
	private static final long serialVersionUID = -3496008428347123532L;
	
	private final AbstractBuild<?, ?> owner;
    private final RcovFileResult result;
    
    public RcovFileDetail(final AbstractBuild<?, ?> owner, final RcovFileResult result) {        
        this.owner = owner;
        this.result = result;
    }

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public AbstractBuild<?, ?> getOwner() {
		return owner;
	}

	public RcovFileResult getResult() {
		return result;
	}

	public String getDisplayName() {		
		return "Rcov report for: " + result.getName();
	}

    
}
