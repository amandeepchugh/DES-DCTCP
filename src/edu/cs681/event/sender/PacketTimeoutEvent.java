package edu.cs681.event.sender;

import edu.cs681.baseclass.Sender;
import edu.cs681.baseclass.TCPPacket;
import edu.cs681.event.Event;
import edu.cs681.simulator.SimulationParameters;

public class PacketTimeoutEvent extends Event {
	private TCPPacket packet;
	
	public PacketTimeoutEvent(double time, TCPPacket p) {
		super(time);
		packet = p;
		priority = 25;
	}
	@Override
	public void handleEvent() {
		Sender sender = packet.getSender();
		if(sender.isTransmitting()) {
			this.setTimeOfEventOccurance(sender.getWillBeTransmittingTill());
			SimulationParameters.getEventQueue().add(this);
			return;
		}
		
		if(packet.getSender().getSequenceNumberOfLastACKedPacket() >= packet.getSequenceNumber()) {
			sender.getPacketTimeouts().recordValue(0);
			return;
		}
		
		sender.getPacketTimeouts().recordValue(1);
		//update window size: set the window threshold to halve the window size as timeout has occured
		//reset window size to 1
		sender.setWindowThreshold(sender.getWindowSize() / 2); //integer division intended
		sender.resetWindowSizeToOne();
		
		//empty the current window buffer, and put all the "failed" packets back in sender queue
		sender.getPacketsToBeTransmitted().addAll(sender.getPacketsInCurrentWindow());
		while(sender.getPacketsInCurrentWindow().size() > 0) {
			sender.getPacketsInCurrentWindow().remove();
		}
		
		//we assume here that a transmission will never be in progress,
		//and then send the timed-out packet again
		if(sender.getPacketsToBeTransmitted().size() > 0) {
			Event event = new PacketTransmissionStartedOnSenderEvent(this.getTimeOfEventOccurance(),
					sender.getPacketsToBeTransmitted().remove(), sender);
			SimulationParameters.getEventQueue().add(event);
		} else {
			System.err.println("PROB: timeout retransmission could not be scheduled.");
		}
	}

	public final TCPPacket getPacket() {
		return packet;
	}

	public final void setPacket(TCPPacket packet) {
		this.packet = packet;
	}
	
	public String toString() {
		return super.toString() + "," + packet;
	}
	
}
