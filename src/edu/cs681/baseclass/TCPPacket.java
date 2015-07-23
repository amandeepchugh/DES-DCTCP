package edu.cs681.baseclass;

import edu.cs681.simulator.SimulationConfiguration;

public class TCPPacket extends Packet {
	private static int packetSizeInBytes = SimulationConfiguration.getInteger("NetworkSystem.TCPPacketSizeInBytes");
	private boolean retransmitting = false;

	public TCPPacket getCopy() {
		TCPPacket copy;
		copy = new TCPPacket();
		copy.sender = this.sender;
		copy.receiver = this.receiver;
		copy.retransmitting = this.retransmitting;
		copy.redMarking = this.redMarking;
		copy.setWaitingTimeDiff(null);
		copy.sequenceNumber = this.sequenceNumber;
		return copy;
	}
	
	private TCPPacket() {
		super(-1, null, null);
	}
	
	public TCPPacket(Sender sender_, Receiver receiver_) {
		super(sender_.getNextSequenceNumber(), sender_, receiver_);
	}
	
	public int getPacketSizeInBytes() {
		return packetSizeInBytes;
	}
	
	public String toString() {
		return "TCP:" + getSequenceNumber() + "#sendermachine:" + getSender();
	}

	public boolean isRetransmitting() {
		return retransmitting;
	}

	public void setRetransmitting(boolean retransmitting) {
		this.retransmitting = retransmitting;
	}
}
