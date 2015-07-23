/*
 * Copyright (C) 2011-12  by Varsha Apte - <varsha@cse.iitb.ac.in>, et al.
 * This file is distributed as part of PerfCenter
 *
 *  This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package edu.cs681.metric;


/**
 * 
 * @author bhavin
 * 
 */

public class SummationMetric {
	// NOTE: the cyclic workload implementation is wierd from this context, and pretty-much non-intuitive.
	// Hence its not considered while designing this class
	private double totalValue = 0;

	public void clearEverything() {
		totalValue = 0;
	}

	public void recordValue(double sampleValue) {
		totalValue += sampleValue;
	}

	public double getTotalValue() {
		return totalValue;
	}
}
