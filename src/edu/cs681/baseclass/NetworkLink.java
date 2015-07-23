package edu.cs681.baseclass;

import edu.cs681.simulator.SimulationConfiguration;

public class NetworkLink {
	private double bandwidthInMBps = SimulationConfiguration.getDouble("NetworkLink.bandwidthInMBps");
	private SwitchPort switchPort;
	private Machine machine;
	
	public final double getBandwidthInMBps() {
		return bandwidthInMBps;
	}
	public final void setBandwidthInMBps(double bandwidthInMbps) {
		this.bandwidthInMBps = bandwidthInMbps;
	}
	public final SwitchPort getSwitchPort() {
		return switchPort;
	}
	public final void setSwitchPort(SwitchPort parentPort) {
		this.switchPort = parentPort;
	}
	public final Machine getMachine() {
		return machine;
	}
	public final void setMachine(Machine machine) {
		this.machine = machine;
	}
	public final double getTotalDelay(long numBytes) {
		return numBytes / (bandwidthInMBps*Math.pow(2,20));
	}
}