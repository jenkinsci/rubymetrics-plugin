package hudson.plugins.rubyMetrics.rcov.model;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class RcovFileResult extends RcovAbstractResult implements Serializable {

    static final long serialVersionUID = 7875682204781173769L;

    private String name;
    private String href;
    @Deprecated
    private transient String sourceCode;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = href;
    }

    public String getLinkPath() {
        if (href == null || href.endsWith(".html")) {
            return href;
        } else {
            return href + ".html";
        }
    }

    // Ensure that the sourceCode field is discarded when loading old builds
    private Object readResolve() throws ObjectStreamException {
        this.sourceCode = null;
        return this;
    }
}
