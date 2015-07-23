package edu.cs681.event;

abstract public class Event implements Comparable<Event> {
	private double timeOfEventOccurance;
	/** priority 1 is highest, 100 is lowest */
	protected int priority = 50;
	
	public Event(double time) {
		timeOfEventOccurance = time;
	}
	
	abstract public void handleEvent();
	
	public String toString() {
		return timeOfEventOccurance + "," + this.getClass().getSimpleName();
	}
	
	@Override
	public int compareTo(Event other) {
		if(this.timeOfEventOccurance > other.timeOfEventOccurance) {
			return 1;
		} else if(this.timeOfEventOccurance < other.timeOfEventOccurance) {
			return -1;
		} else {
			return new Integer(this.priority).compareTo(other.priority);
		}
	}

	public final double getTimeOfEventOccurance() {
		return timeOfEventOccurance;
	}

	public final void setTimeOfEventOccurance(double timeOfEventOccurance) {
		this.timeOfEventOccurance = timeOfEventOccurance;
	}
	
}
