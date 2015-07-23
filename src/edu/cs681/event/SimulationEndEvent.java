package edu.cs681.event;


public class SimulationEndEvent extends Event {
	
	public SimulationEndEvent(double time) {
		super(time);
	}

	@Override
	public void handleEvent() {
		System.out.println("Simulation complete!!");
	}
}
