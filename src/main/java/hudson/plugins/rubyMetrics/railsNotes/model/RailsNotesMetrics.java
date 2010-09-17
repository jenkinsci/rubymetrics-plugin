package hudson.plugins.rubyMetrics.railsNotes.model;

import java.util.Arrays;
import java.util.Comparator;

public enum RailsNotesMetrics {
    TODO, FIXME, OPTIMIZE;

    public static RailsNotesMetrics toRailsNotesMetrics(String name) {
        try {
            return RailsNotesMetrics.valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    public int getOrder() {
        return Arrays.asList(RailsNotesMetrics.values()).indexOf(this);
    }

    public static class COMPARATOR implements Comparator<RailsNotesMetrics> {
        public int compare(RailsNotesMetrics o1, RailsNotesMetrics o2) {
            return new Integer(o1.getOrder()).compareTo(new Integer(o2.getOrder()));
        }

    }
}
