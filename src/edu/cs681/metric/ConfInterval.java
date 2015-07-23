package edu.cs681.metric;

/**
 * Used to calculate confidence intervals.
 */
import java.util.ArrayList;

import umontreal.iro.lecuyer.probdist.StudentDist;
import cern.colt.list.DoubleArrayList;
import edu.cs681.simulator.SimulationParameters;

public class ConfInterval {
	public double totalDurationPrev = 0;
	public double totalValuePrev = 0;
	public double data[][];
	public ArrayList<Double> ci;
	public double prob;
	public double totalSamplesPrev = 0;

	public ConfInterval(double respProb) {
		prob = respProb;
		data = new double[(int) SimulationParameters.getNumberOfReplications()][(int) SimulationParameters.getTotalNumberOfSamples()];
		ci = new ArrayList<Double>();
	}

	public double mean(DoubleArrayList data) {
		return cern.jet.stat.Descriptive.mean(data);
	}

	public double sampleVar(DoubleArrayList data, double mean) {
		return Math.sqrt(cern.jet.stat.Descriptive.sampleVariance(data, mean));
	}

	/**
	 * here we save the performance measures in corresponding matrix cells (based on replicationNumber & sampleNumber)
	 * 
	 * These matrices are later used to calculate the confidence intervals
	 */

	public void recordCISample(double val) {
		data[SimulationParameters.getCurrentReplication()][SimulationParameters.getCurrentSampleNumber() - 1] = val;
	}

	/**
	 * calls functions to calculate confidence intervals for different performance measures
	 * 
	 */
	public void calculateConfidenceIntervals() {
		DoubleArrayList dataMeanVector = new DoubleArrayList();

		// use welches procedure to decide the data vector over which confidence
		// interval to be calculated

		dataMeanVector = welchsProcedure(data);

		ci = confIntervals(dataMeanVector, prob);
	}

	/**
	 * here we 1. smooth the high frequency occilations in data 2. decide the cutoff points from the resulting vector
	 * 
	 * 
	 * @param data
	 *            = matrix[replication number][sample number] containing performance measure values
	 * @return
	 */

	public int decideCutOff(double data[][]) {
		int index = 0;
		double tolerance = 0.05;

		data[0][0] = 0;

		double temp[] = new double[(int) SimulationParameters.getTotalNumberOfSamples()];
		for (int i = 0; i < SimulationParameters.getTotalNumberOfSamples(); i++) {
			double sum = 0;
			for (int j = 0; j < SimulationParameters.getNumberOfReplications(); j++) {
				sum = sum + data[j][i];
			}
			temp[i] = sum / SimulationParameters.getNumberOfReplications();
		}

		int window = 10;
		for (int i = 0; i < SimulationParameters.getTotalNumberOfSamples(); i++) {
			if (i < window) {
				double sum = 0;
				for (int j = 0; j <= 2 * i; j++) {
					sum = sum + temp[j];
				}
				temp[i] = sum / (2 * i + 1);
			} else {
				double sum = 0;
				for (int j = i - window; j <= i + window && j < SimulationParameters.getTotalNumberOfSamples(); j++) {
					sum = sum + temp[j];
				}
				temp[i] = sum / (2 * window + 1);
			}
		}

		boolean indexFound = false;

		for (int i = window + 1; i < SimulationParameters.getTotalNumberOfSamples() && !indexFound; i++) {
			boolean flag = true;

			for (int j = i - window; j < i; j++) {
				double tempVal = (temp[i] - temp[j]) / temp[i];
				if (tempVal > temp[i] * tolerance)
					flag = false;
			}
			if (flag) {
				indexFound = true;
				index = i;
			}
		}
		return (index);
	}

	/**
	 * 1.we take as input the performance measure value matrix (data[][]) 2.decide which portions of matrix to ignore(to account for warm up and cool
	 * off effect) (as indicated by the indices begin and end(decide cutt off used for the purpose)) 3.from the resulting matrix portion we calculate
	 * the mean of values for each replication and store the mean values in the vector that is returned
	 * 
	 * this vector is used for calculating confidence intervals later
	 * 
	 * @param data
	 *            = matrix[replication number][sample number] containing performance measure values
	 * @return = vector containing
	 */
	public DoubleArrayList welchsProcedure(double data[][]) {
		// call decideCutOff() to decide the suitable portion of data after
		// ignoring the parts affected by warmup or cool off effect

		int begin = (int) SimulationParameters.getStartUpSampleNumber();// decideCutOff(data);
		int end = (int) SimulationParameters.getTotalNumberOfSamples() - SimulationParameters.getCoolDownSampleNumber();// PerfSim.numOfSampleIntervals;
		double sum = 0;

		// this vector contains the means of replications of the portion of
		// matrix selected above
		DoubleArrayList returnVal = new DoubleArrayList();

		// int counter=0;
		// calculate mean of replications
		// double prevsum=0;
		for (int i = 0; i < SimulationParameters.getNumberOfReplications(); i++) {

			// System.out.println("Replication number:"+i);
			for (int j = begin; j < end; j++) {
				// prevsum = sum;
				sum = sum + data[i][j];
				// System.out.println("Data["+i+"]["+j+"]:"+data[i][j]);

			}
			returnVal.add(sum / (end - begin));// add the means to the returnval vector
			sum = 0;
			// counter=0;
			// System.out.println();
		}

		return (returnVal);
	}

	// calculate confidence interval here
	public ArrayList<Double> confIntervals(DoubleArrayList dataMeanVector, double probability) {

		ArrayList<Double> temp = new ArrayList<Double>();

		double mean = mean(dataMeanVector);

		double a = ((1 - probability) / 2) + probability;

		double tempA = StudentDist.inverseF(dataMeanVector.size(), a);

		double tempB = tempA * sampleVar(dataMeanVector, mean) / Math.sqrt(dataMeanVector.size());

		temp.add(new Double(mean));
		temp.add(new Double(tempB));
		return (temp);
	}

	public double getCI() {
		return ((Double) ci.get(1)).doubleValue();
	}

	public double getMean() {
		return ((Double) ci.get(0)).doubleValue();
	}
}
