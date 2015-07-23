package edu.cs681.simulator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class SimulationConfiguration {
	static Properties properties = new Properties();
	static {
		try {
			properties.load(new BufferedInputStream(new FileInputStream(new File("config.properties"))));
		} catch (FileNotFoundException e) {
			System.err.println("File config.properties not found.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("File config.properties could not be read.");
			System.exit(1);
		}
	}
	
	public static String getProperty(String name) {
		String value = properties.getProperty(name);
		if(value == null || value.trim().length()==0) {
			System.err.println("property "+name+" is not defined in the config.properties file.");
			System.exit(1);
		}
		return value;
	}
	
	public static int getInteger(String name) {
		String value = getProperty(name);
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException nfe) {
			System.err.println("Value of property " + name + " is not an integer. Config file has \"" + value + "\"");
			System.exit(1);
			return 0;
		}
	}
	
	public static double getDouble(String name) {
		String value = getProperty(name);
		try {
			return Double.parseDouble(value);
		} catch(NumberFormatException nfe) {
			System.err.println("Value of property " + name + " is not a double. Config file has \"" + value + "\"");
			System.exit(1);
			return 0;
		}
	}
}
