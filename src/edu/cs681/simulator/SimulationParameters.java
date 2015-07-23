package edu.cs681.simulator;

import java.util.PriorityQueue;

import edu.cs681.event.Event;
import edu.cs681.event.sender.PacketTimeoutEvent;

public class SimulationParameters {
	private static final int totalNumberOfSamples = SimulationConfiguration.getInteger("Simulation.numberOfSamples");
	static private final double simulationEndTime = SimulationConfiguration.getDouble("Simulation.endTime");
	static private final int numberOfReplications = SimulationConfiguration.getInteger("Simulation.numberOfReplications");
	private static final int startUpSampleNumber = SimulationConfiguration.getInteger("Simulation.startUpSampleNumber");
	private static final int coolDownSampleNumber = SimulationConfiguration.getInteger("Simulation.coolDownSampleNumber");
	private static final int maxSenderBufferSize = SimulationConfiguration.getInteger("Simulation.maxSenderBufferSize");
	
	static private int currentReplication = 0;
	
	static private double simualationTime = 0;
	static private PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();
	private static int currentSampleNumber = 1;
	
	public static void reset() {
		currentSampleNumber = 1;
		eventQueue.clear();
		simualationTime = 0;
	}
	
	public static void incrementCurrentReplication() {
		currentReplication++;
	}
	public static PriorityQueue<Event> getEventQueue() {
		return eventQueue;
	}
	static public final double getSimualationTime() {
		return simualationTime;
	}
	public final static double getSimulationEndTime() {
		return simulationEndTime;
	}
	public final static void removeAllFutureTimeoutEventsFromEventQueue() {
		Object[] eventArray = eventQueue.toArray();
		while(eventQueue.size()>0) {
			eventQueue.remove();
		}
		for(Object object : eventArray) {
			Event event = (Event)object;
			if(!(event instanceof PacketTimeoutEvent)) {
				eventQueue.add(event);
			}
		}
	}
	public static int getNumberOfReplications() {
		return numberOfReplications;
	}
	public static int getCurrentReplication() {
		return currentReplication;
	}
	public static void setSimualationTime(double simualationTime) {
		SimulationParameters.simualationTime = simualationTime;
	}
	public static int getTotalNumberOfSamples() {
		return totalNumberOfSamples;
	}
	public static int getCurrentSampleNumber() {
		return currentSampleNumber;
	}
	public static void incrementCurrentSampleNumber() {
		currentSampleNumber++;
	}

	public static int getStartUpSampleNumber() {
		return startUpSampleNumber;
	}

	public static int getCoolDownSampleNumber() {
		return coolDownSampleNumber;
	}

	public static int getMaxSenderBufferSize() {
		return maxSenderBufferSize;
	}

}
