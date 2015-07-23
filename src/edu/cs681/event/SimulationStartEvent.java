package edu.cs681.event;

import edu.cs681.baseclass.NetworkSystem;
import edu.cs681.baseclass.Sender;
import edu.cs681.baseclass.TCPPacket;
import edu.cs681.event.sender.PacketGeneratedOnSenderEvent;
import edu.cs681.simulator.SimulationParameters;

public class SimulationStartEvent extends Event {

	public SimulationStartEvent(double time) {
		super(time);
	}
	
	@Override
	public void handleEvent() {
		for(Sender sender : NetworkSystem.getSenderList()) {
			double timeToSend = sender.getSendingRateDistribution().getNextExponential();
			TCPPacket tcpPacket = new TCPPacket(sender, sender.getTargetReceiver());
			Event event = new PacketGeneratedOnSenderEvent(
					this.getTimeOfEventOccurance()+timeToSend, tcpPacket, sender);
			SimulationParameters.getEventQueue().add(event);
		}
	}
}
