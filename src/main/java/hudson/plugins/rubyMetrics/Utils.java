package hudson.plugins.rubyMetrics;

import hudson.FilePath;
import hudson.Util;
import hudson.model.TaskListener;

import java.io.File;
import java.io.IOException;

public class Utils {
    public static boolean moveReportsToBuildRootDir(FilePath workspace, File buildRootDir, TaskListener listener, String monitorDirectory, String copiesPattern) throws InterruptedException {
        return moveReportsToBuildRootDir(workspace, buildRootDir, listener, monitorDirectory, copiesPattern, false);
    }

    public static boolean moveReportsToBuildRootDir(FilePath workspace, File buildRootDir, TaskListener listener, String monitorDirectory, String copiesPattern, boolean sameLocation) throws InterruptedException {
        try {
            FilePath coverageDir = workspace.child(monitorDirectory);

            if (!coverageDir.exists()) {
                listener.getLogger().println("file not found: " + coverageDir);
                return false;
            }
            FilePath copiesLocation = new FilePath(buildRootDir);
            if (sameLocation) {
                copiesLocation = new FilePath(new File(buildRootDir, monitorDirectory));
            }

            coverageDir.copyRecursiveTo(copiesPattern, copiesLocation);

            return true;
        } catch (IOException e) {
            Util.displayIOException(e, listener);
            e.printStackTrace(listener.fatalError("Unable to find coverage results"));
        }
        return false;
    }

    /**
     * Creates a copy of a String that ensures the underyling character array
     * is not oversized; akin to <code>new String(String)</code> while handling
     * <code>null</code>.
     */
    public static String newString(String input) {
        return input == null ? null : new String(input);
    }
}
