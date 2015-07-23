package edu.cs681.metric;

abstract public class MetricSim {

	protected ConfInterval confInterval;
	
	public MetricSim(double probability) {
		super();
		confInterval = new ConfInterval(probability);
	}

	abstract public void recordCISample();

	public final void clearEverything() {
		clearValuesButKeepConfInts();
		confInterval = null;
	}

	abstract public void clearValuesButKeepConfInts();

	public final void calculateConfidenceIntervals() {
		confInterval.calculateConfidenceIntervals();
	}

	public double getCI() {
		return confInterval.getCI();
	}

	public abstract double getMean();
	
	public String toString() {
		return getMean() + "+-" + getCI();
	}
}