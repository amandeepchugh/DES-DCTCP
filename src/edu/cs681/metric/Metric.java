package edu.cs681.metric;

public class Metric {
	private double value;
	private double confidenceInterval;
	private double confidenceProbability;

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getConfidenceInterval() {
		return confidenceInterval;
	}

	public void setConfidenceInterval(double confidenceInterval) {
		this.confidenceInterval = confidenceInterval;
	}

	public double getConfidenceProbability() {
		return confidenceProbability;
	}

	public void setConfidenceProbability(double confidenceProbability) {
		this.confidenceProbability = confidenceProbability;
	}

	public void setValue(double value, double confidenceInterval) {
		setValue(value);
		setConfidenceInterval(confidenceInterval);
	}
	
	public void clear() {
		setValue(0);
		setConfidenceInterval(0);
		//not resetting the confidence probability
	}
	
	public String toString() {
		return value + "+-" + confidenceInterval;
	}
}
