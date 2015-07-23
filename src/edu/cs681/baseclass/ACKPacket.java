package edu.cs681.baseclass;

import edu.cs681.simulator.SimulationConfiguration;

public class ACKPacket extends Packet {
	private long expectedSequenceNumber;
	private boolean ece;
	private static int packetSizeInBytes = SimulationConfiguration.getInteger("NetworkSystem.ACKPacketSizeInBytes");

	public ACKPacket(Sender sender, Receiver receiver) {
		super(receiver.getNextSequenceNumber(), sender, receiver);
	}
	public int getPacketSizeInBytes() {
		return packetSizeInBytes;
	}
	public final long getExpectedSequenceNumber() {
		return expectedSequenceNumber;
	}
	public final boolean isEce() {
		return ece;
	}
	public void setExpectedSequenceNumber(long expectedSequenceNumber) {
		this.expectedSequenceNumber = expectedSequenceNumber;
	}
	public void setEce(boolean ece) {
		this.ece = ece;
	}
	public String toString() {
		return "ACK:" + getSequenceNumber() + "#sendermachine:" + getReceiver() + "#expectedSequenceNumber:"+expectedSequenceNumber+"#targetsender:"+getSender();
	}
}
