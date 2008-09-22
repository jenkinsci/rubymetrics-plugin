package hudson.plugins.rubyMetrics.rcov.model;

import org.apache.commons.beanutils.Converter;

public class MetricTarget {
	
	private final Targets metric;
	private final Integer healthy;
	private final Integer unhealthy;
	private final Integer unstable;
	
	public static final TargetConverter CONVERTER = new TargetConverter();
	
	/**
     * @param metric
     * @param healthy
     * @param unhealthy
     * @param unstable
     * @stapler-constructor
     */
	public MetricTarget(Targets metric, Integer healthy, Integer unhealthy, Integer unstable) {		
		this.metric = metric;
		this.healthy = healthy != null?healthy:80;
		this.unhealthy = unhealthy;
		this.unstable = unstable;
	}

	public Targets getMetric() {
		return metric;
	}

	public Integer getHealthy() {
		return healthy != null?healthy:0;
	}

	public Integer getUnhealthy() {
		return unhealthy != null?unhealthy:0;
	}

	public Integer getUnstable() {
		return unstable != null?unstable:0;
	}
	
	private static class TargetConverter implements Converter {
        public Object convert(Class type, Object value) {        	
            return Targets.resolve(value.toString());
        }
    }
	
}
