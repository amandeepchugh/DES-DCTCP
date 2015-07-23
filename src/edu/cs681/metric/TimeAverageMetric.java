package edu.cs681.metric;

import edu.cs681.simulator.SimulationParameters;


/**
 * 
 * @author bhavin
 * 
 */

public class TimeAverageMetric extends MetricSim {
	private double totalValue = 0;
	private double totalDuration = 0;
	private boolean valuesCapturedSinceLastCI = false;
	
	private double timeOfLastSampleRecording = 0;

	public TimeAverageMetric(double probability) {
		super(probability);
	}
	public void recordCISample() {
		double sample = 0;
		if(totalDuration - confInterval.totalDurationPrev == 0) {
			sample = 0;
		} else {
			sample = (totalValue - confInterval.totalValuePrev) / (totalDuration - confInterval.totalDurationPrev);
			confInterval.totalDurationPrev = totalDuration;
			confInterval.totalValuePrev = totalValue;
		}
		confInterval.recordCISample(sample);
		valuesCapturedSinceLastCI = false;
	}

	public void clearValuesButKeepConfInts() {
		totalValue = 0;
		totalDuration = 0;
	}

	public void recordValue(double sampleValue, double timeElapsedForLastSample) {
		if(!Double.isNaN(sampleValue) && !Double.isNaN(timeElapsedForLastSample)) {
			totalValue += sampleValue;
		}
		if(!Double.isNaN(timeElapsedForLastSample)) {
			totalDuration += timeElapsedForLastSample;
		}
		valuesCapturedSinceLastCI = true;
	}
	
	public void recordValue(double sampleValue) {
		recordValue(sampleValue, SimulationParameters.getSimualationTime() - timeOfLastSampleRecording);
		timeOfLastSampleRecording = SimulationParameters.getSimualationTime();
	}

	public double getTotalValue() {
		return totalValue;
	}

	public double getTotalDuration() {
		return totalDuration;
	}
	
	public double getMean() {
		try {
			return confInterval.getMean();
		}catch(IndexOutOfBoundsException e) {
			System.out.println("\noops\n");
			throw new RuntimeException();
//			return totalValue / totalDuration;
		}
	}
	public boolean isValuesCapturedSinceLastCI() {
		return valuesCapturedSinceLastCI;
	}
}
