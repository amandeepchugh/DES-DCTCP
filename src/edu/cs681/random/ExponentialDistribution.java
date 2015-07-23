package edu.cs681.random;

import java.util.Random;

public class ExponentialDistribution {
	private Random r = new Random();
	private final double parameter;
	public double getNextExponential() {
		double x;
		while ((x = r.nextDouble()) == 0)
			; // loop until x!=0
		return (-parameter * Math.log(x));
	}

	/**
	 * Create a new random number generator based on the current time.
	 */
	public ExponentialDistribution(double p) { // initializes r based on the current time
		r = new Random();
		parameter = p;
	}

	/**
	 * Create a new random number generator using <TT>seed</TT> as a seed. A seed is a number used to generate a certain pattern of random numbers. It
	 * is <B>NOT</B> the upper or lower bound, nor the expected value.
	 * 
	 * @param seed
	 *            the seed for the random number generator
	 */
	public ExponentialDistribution(long seed, double parameter) {
		r = new java.util.Random(seed);
		this.parameter = parameter;
	}

	/**
	 * Select a number from a uniform distribution.
	 * 
	 * @post returns a random int uniformly distributed between low and high inclusive.
	 * @param low
	 *            the minimum random integer to return
	 * @param high
	 *            the maximum random integer to return
	 * @return a random integer between low and high
	 */
	public int nextUniformInt(int low, int high) {
		double x;
		while ((x = r.nextDouble()) == 1)
			; // loop until x!=1;
		return (int) Math.floor(x * (high - low + 1) + low);
	}

}
