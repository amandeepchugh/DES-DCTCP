package edu.cs681.event.sender;

import edu.cs681.baseclass.Packet;
import edu.cs681.baseclass.Sender;
import edu.cs681.baseclass.TCPPacket;
import edu.cs681.event.Event;
import edu.cs681.event.nwswitch.PacketArrivalOnSwitchEvent;
import edu.cs681.simulator.SimulationParameters;

public class PacketTransmissionStartedOnSenderEvent extends Event {
	private TCPPacket packet;
	private Sender sender;

	public PacketTransmissionStartedOnSenderEvent(double time, TCPPacket packet_, Sender sender_) {
		super(time);
		packet = packet_;
		sender = sender_;
		priority = 21;
	}
	
	@Override
	public void handleEvent() {
		sender.setTransmitting(true);
		sender.getPacketsInCurrentWindow().add(packet.getCopy());
		//add packet in the window
		if(sender.getPacketsInCurrentWindow().size() > sender.getWindowSize()) {
			System.err.println("BUG: more packets were sent than the allowed number by the window in sender S"+sender.getMachineId());
			System.err.println(sender.getWindowSize());
			for(Packet packet : sender.getPacketsInCurrentWindow()) {
				System.err.println(packet);
			}
			System.exit(1);
		}
		

		//schedule the timeout event for current packet
		Event event = new PacketTimeoutEvent(
				this.getTimeOfEventOccurance()+sender.getTimeoutDuration(), packet);
		SimulationParameters.getEventQueue().add(event);
		
		double linkDelay = sender.getNetworkLink().getTotalDelay(packet.getPacketSizeInBytes());
		
		//schedule transmission end of this sending after transmission delay
		event = new PacketTransmissionEndedOnSender(this.getTimeOfEventOccurance() + linkDelay,
				sender);
		sender.setWillBeTransmittingTill(this.getTimeOfEventOccurance() + linkDelay);
		SimulationParameters.getEventQueue().add(event);
		
		//schedule the packet on the switch port after transmission delay
		event = new PacketArrivalOnSwitchEvent(this.getTimeOfEventOccurance() + linkDelay,
				packet, sender.getNetworkLink().getSwitchPort());
		SimulationParameters.getEventQueue().add(event);

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
