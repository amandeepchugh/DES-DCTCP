package edu.cs681.metric;

/**
 * 
 * @author bhavin
 * 
 */

public class DiscreteSampleAverageMetric extends MetricSim {

	double totalValue = 0;
	long totalSamples = 0;

	public DiscreteSampleAverageMetric(double probability) {
		super(probability);
	}
	
	public void recordValue(double sampleValue) {
		if(sampleValue < 0) {
			System.err.println("LOCHA: window size coming out to be negative: " + sampleValue);
			throw new RuntimeException();
		}
		totalValue += sampleValue;
		totalSamples++;
	}

	public void recordCISample() {
		double sample = 0;
		if((totalSamples - confInterval.totalSamplesPrev) == 0) {
			sample = 0;
		} else {
			sample = (totalValue - confInterval.totalValuePrev) / (totalSamples - confInterval.totalSamplesPrev);
		}
		confInterval.recordCISample(sample);
		confInterval.totalSamplesPrev = totalSamples;
		confInterval.totalValuePrev = totalValue;
	}

	public void clearValuesButKeepConfInts() {
		totalValue = 0;
		totalSamples = 0;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public long getTotalSamples() {
		return totalSamples;
	}
	
	public double getMean() {
		try {
			return confInterval.getCI();
		}catch(IndexOutOfBoundsException e) {
			return totalValue / totalSamples;
		}
	}
}
