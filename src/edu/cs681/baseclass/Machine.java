package edu.cs681.baseclass;

public abstract class Machine {
	protected int machineId;
	private NetworkLink networkLink;
	/** assumption: this will go to infinity, this is not tied with windowSize */
	protected int nextSequenceNumber = 1;
	
	public NetworkLink getNetworkLink() {
		return networkLink;
	}
	public void setNetworkLink(NetworkLink networkLink) {
		this.networkLink = networkLink;
		networkLink.setMachine(this);
	}
	public final int getMachineId() {
		return machineId;
	}
	public int getNextSequenceNumber() {
		return nextSequenceNumber++;
	}
	public int compareTo(Machine other) {
		return new Integer(this.machineId).compareTo(other.machineId);
	}
	public String getName() {
		if(this instanceof Sender) {
			return "S"+machineId;
		} else {
			return "R"+machineId;
		}
	}
}
