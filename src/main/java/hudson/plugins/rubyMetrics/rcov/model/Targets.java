package hudson.plugins.rubyMetrics.rcov.model;

public enum Targets {
	TOTAL_COVERAGE, CODE_COVERAGE;
	
	public String getName() {
		String name = this.toString().toLowerCase().replaceAll("_", " ");
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public static Targets resolve(String name) {
		return Targets.valueOf(name.toUpperCase().replaceAll(" ", "_"));
	}
}
