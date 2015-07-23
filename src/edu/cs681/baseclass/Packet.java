package edu.cs681.baseclass;

import edu.cs681.metric.DifferenceKeeper;

public abstract class Packet implements Comparable<Packet> {
	protected Sender sender;
	protected Receiver receiver;
	protected int sequenceNumber;
	/** marking done by switch in DCTCP */
	protected boolean redMarking = false;
	
	private DifferenceKeeper waitingTimeDiff;
	
	public Packet(int sequenceNumber_, Sender sender_, Receiver receiver_) {
		sequenceNumber = sequenceNumber_;
		sender = sender_;
		receiver = receiver_;
	}
	public final Sender getSender() {
		return sender;
	}
	public final Receiver getReceiver() {
		return receiver;
	}
	public final long getSequenceNumber() {
		return sequenceNumber;
	}
	public abstract int getPacketSizeInBytes();
	
	public final boolean isRedMarking() {
		return redMarking;
	}
	public final void setRedMarking(boolean redMarking) {
		this.redMarking = redMarking;
	}
	@Override
	public int compareTo(Packet other) {
		return new Integer(this.sequenceNumber).compareTo(other.sequenceNumber);
	}
	
	@Override
	public int hashCode() {
		if(this instanceof TCPPacket) {
			return sequenceNumber;
		} else {
			return -sequenceNumber;
		}
	}
	public DifferenceKeeper getWaitingTimeDiff(int firstOrSecond) {
		if(firstOrSecond == 1 && waitingTimeDiff!=null) {
			System.err.println("OOOOOOOPS ");
			throw new RuntimeException("first time");
		}
		
		if(firstOrSecond == 2 && waitingTimeDiff == null) {
			System.err.println("OOOOOOOOOOOOOOOOOOOOOOOPS");
			throw new RuntimeException("second time");
		}
		if(waitingTimeDiff == null) {
			waitingTimeDiff = new DifferenceKeeper();
		}
		return waitingTimeDiff;
	}
	public void setWaitingTimeDiff(DifferenceKeeper waitingTimeDiff) {
		this.waitingTimeDiff = waitingTimeDiff;
	}
}
