package edu.cs681.baseclass;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cs681.simulator.SimulationConfiguration;

public class Switch {
	private String name;
	private int lastServicedPort;
	private List<SwitchPort> ports = new ArrayList<SwitchPort>();
	private Map<Machine, SwitchPort> machineToPortMapping = new HashMap<Machine, SwitchPort>();
	private int markingThreshold = SimulationConfiguration.getInteger("Switch.markingThreshold");

	public Switch() {
		init();
	}
	private void init() {
		lastServicedPort = -1;
	}
	
	public SwitchPort getPortOfTheMachine(Machine machine) {
		return machineToPortMapping.get(machine);
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final List<SwitchPort> getPorts() {
		return ports;
	}
	public final void setPorts(List<SwitchPort> ports) {
		this.ports = ports;
	}
	public final void addPort(SwitchPort port) {
		this.ports.add(port);
		machineToPortMapping.put(port.getNetworkLink().getMachine(), port);
	}
	public int getLastServicedPort() {
		return lastServicedPort;
	}
	public void setLastServicedPort(int lastServicedPort) {
		this.lastServicedPort = lastServicedPort;
	}
	public void reset() {
		init();
		for(SwitchPort switchPort : ports) {
			switchPort.reset();
		}
	}
	public int getMarkingThreshold() {
		return markingThreshold;
	}
	public void printOutput(PrintWriter output) {
		for(SwitchPort switchPort : ports) {
			if(switchPort.getNetworkLink().getMachine().getName().equals("R0")) {
				switchPort.printOutput(output);
			}
		}
	}
	public void recordCISample() {
		for(SwitchPort switchPort : ports) {
			switchPort.recordCISample();
		}
	}
}
