package hudson.plugins.rubyMetrics.rcov.model;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;

import hudson.plugins.rubyMetrics.rcov.RcovParser;

import java.io.IOException;
import java.io.Serializable;

import org.htmlparser.util.ParserException;

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

    public String loadSourceCode() throws IOException, ParserException {
        RcovParser parser = new RcovParser(owner.getRootDir());
        return parser.parseSource(getResult().getLinkPath());
    }


}
