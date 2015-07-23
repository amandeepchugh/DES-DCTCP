package edu.cs681.event.sender;

import edu.cs681.baseclass.Sender;
import edu.cs681.baseclass.TCPPacket;
import edu.cs681.event.Event;
import edu.cs681.simulator.SimulationParameters;

public class PacketGeneratedOnSenderEvent extends Event {
	TCPPacket packet;
	Sender sender;
	
	public PacketGeneratedOnSenderEvent(double time, TCPPacket p, Sender s) {
		super(time);
		packet = p;
		sender= s;
		priority = 1;
	}

	@Override
	public void handleEvent() {
		//enqueue in the sender's queue, or PacketSendEvent immediately if sender is not busy
		if(sender.isTransmitting() || sender.getPacketsInCurrentWindow().size() >= sender.getWindowSize()) {
			sender.getPacketsToBeTransmitted().add(packet);
		} else {
			Event event = new PacketTransmissionStartedOnSenderEvent(this.getTimeOfEventOccurance(), packet, sender);
			sender.setTransmitting(true);
			SimulationParameters.getEventQueue().add(event);
		}
		
		// schedule next packet generation
//		if (sender.getPacketsToBeTransmitted().size() < SimulationParameters.getMaxSenderBufferSize()) {
			double timeToSend = sender.getSendingRateDistribution().getNextExponential();
			TCPPacket tcpPacket = new TCPPacket(sender, sender.getTargetReceiver());
			Event event = new PacketGeneratedOnSenderEvent(this.getTimeOfEventOccurance() + timeToSend, tcpPacket, sender);
			SimulationParameters.getEventQueue().add(event);
//		}
	}

	public TCPPacket getPacket() {
		return packet;
	}

	public void setPacket(TCPPacket packet) {
		this.packet = packet;
	}

	public Sender getSender() {
		return sender;
	}

	public void setSender(Sender sender) {
		this.sender = sender;
	}
	
	public String toString() {
		return super.toString() + "," + packet;
	}

}
