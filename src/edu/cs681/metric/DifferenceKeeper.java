package edu.cs681.metric;

public class DifferenceKeeper {
	
	double value1 = 0;
	double value2 = 0;
	
	public void recordValue(double value) {
		if(value1 == 0) {
			value1 = value;
		} else if(value2 == 0) {
			value2 = value;
		} else {
			value1 = value2;
			value2 = value;
		}
	}
	
	public void clearEverything() {
		value1 = 0;
		value2 = 0;
	}
	
	public double getDifference() {
		return value2 - value1;
	}
}
