package edu.cs681.event.receiver;

import java.util.Set;

import edu.cs681.baseclass.ACKPacket;
import edu.cs681.baseclass.Receiver;
import edu.cs681.baseclass.TCPPacket;
import edu.cs681.event.Event;
import edu.cs681.event.nwswitch.PacketArrivalOnSwitchEvent;
import edu.cs681.simulator.SimulationParameters;

public class PacketReceivedOnReceiver extends Event {

	private TCPPacket dataPacket;
	private Receiver receiver;
	
	public PacketReceivedOnReceiver(double time, TCPPacket p, Receiver r) {
		super(time);
		dataPacket = p;
		receiver = r;
	}
	
	@Override
	public void handleEvent() {
		ACKPacket ackPacket = new ACKPacket(dataPacket.getSender(), dataPacket.getReceiver());

		//see the red marking, and accordingly set the ece flag
		ackPacket.setEce(dataPacket.isRedMarking());
		
		if(dataPacket.getSequenceNumber() > receiver.getLastSuccessfullyReceivedPacketIdForSender(dataPacket.getSender()) + 1) {
			//packet itself is out of order, so add it in the list, and move on
			receiver.addToOutOfOrderPackets(dataPacket.getSender(), dataPacket);
			ackPacket.setExpectedSequenceNumber(
					receiver.getLastSuccessfullyReceivedPacketIdForSender(dataPacket.getSender())+1);
		} else {
			//packet received is in the order, as expected
			//refer the outOfOrder list for the current sender, and determine the sequence number of the next packet required
			Set<TCPPacket> outOfOrderPacketSetForSender = receiver.getOutOfOrderPacketListForSender(dataPacket.getSender());
			long lastAcknowledged = receiver.getLastSuccessfullyReceivedPacketIdForSender(dataPacket.getSender());
			if(dataPacket.getSequenceNumber() == lastAcknowledged + 1) { //this will take care of old and already-acknowledged packets coming in
				lastAcknowledged++;
			}
			if(outOfOrderPacketSetForSender != null) {
				Object[] outOfOrderPacketArrayForSender = (Object[])outOfOrderPacketSetForSender.toArray();
				for(Object object : outOfOrderPacketArrayForSender ) {
					TCPPacket nextOutOfOrderPacket = (TCPPacket) object;
					if(lastAcknowledged + 1 == nextOutOfOrderPacket.getSequenceNumber()) {
						lastAcknowledged++;
						outOfOrderPacketSetForSender.remove(nextOutOfOrderPacket);
					} else {
						break;
					}
				}
			}
			receiver.addLastSuccessfullyReceivedPacketIdForSender(dataPacket.getSender(), lastAcknowledged);
			ackPacket.setExpectedSequenceNumber(lastAcknowledged+1);
		}
		
		//schedule the ACK packet's arrival on switch
		double bandwidth = receiver.getNetworkLink().getBandwidthInMBps();
		double transmissionDelay = ackPacket.getPacketSizeInBytes() / (bandwidth*Math.pow(2,20));
		Event event = new PacketArrivalOnSwitchEvent(
				this.getTimeOfEventOccurance() + transmissionDelay + receiver.getProcessingTime(),
				ackPacket, receiver.getNetworkLink().getSwitchPort());
		SimulationParameters.getEventQueue().add(event);
	}
	
	public final TCPPacket getDataPacket() {
		return dataPacket;
	}
	public final void setDataPacket(TCPPacket dataPacket) {
		this.dataPacket = dataPacket;
	}
	public final Receiver getReceiver() {
		return receiver;
	}
	public final void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}
	
	public String toString() {
		return super.toString() + "," + dataPacket;
	}
}
