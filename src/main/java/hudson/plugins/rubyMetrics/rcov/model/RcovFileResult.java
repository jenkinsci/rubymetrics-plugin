package hudson.plugins.rubyMetrics.rcov.model;

import static hudson.plugins.rubyMetrics.Utils.newString;

public class RcovFileResult extends RcovAbstractResult {

    private String name;
    private String href;
    private String sourceCode;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = newString(name);
    }
    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = newString(href);
    }
    public String getSourceCode() {
        return sourceCode;
    }
    public void setSourceCode(String sourceCode) {
        this.sourceCode = newString(sourceCode);
    }
}
