package hudson.plugins.rubyMetrics.railsStats.model;

public enum RailsClassType {
	CONTROLLERS, HELPERS, MODELS, LIBRARIES, 
		INTEGRATION_TESTS, FUNCTIONAL_TESTS, UNIT_TESTS, TOTAL;
	
	public String prettyPrint() {
		String prettyString = this.toString().toLowerCase().replaceAll("_", " ");
		return prettyString.substring(0, 1).toUpperCase() + prettyString.substring(1);
	}
	
	public static boolean contains(String name) {
		return toRailsClassType(name) != null;
	}
	
	public static RailsClassType toRailsClassType(String name) {
		try {
			return RailsClassType.valueOf(name.toUpperCase().replaceAll(" ", "_"));
		} catch (Exception e) {
			return null;
		}
	}
}
