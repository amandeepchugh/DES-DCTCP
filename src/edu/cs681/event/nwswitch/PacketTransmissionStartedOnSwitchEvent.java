package edu.cs681.event.nwswitch;

import edu.cs681.baseclass.ACKPacket;
import edu.cs681.baseclass.Packet;
import edu.cs681.baseclass.SwitchPort;
import edu.cs681.baseclass.TCPPacket;
import edu.cs681.event.Event;
import edu.cs681.event.receiver.PacketReceivedOnReceiver;
import edu.cs681.event.sender.ACKReceivedOnSender;
import edu.cs681.simulator.SimulationParameters;

public class PacketTransmissionStartedOnSwitchEvent extends Event {

	Packet packet;
	SwitchPort switchPort;
	
	public PacketTransmissionStartedOnSwitchEvent(double time, Packet packet_, SwitchPort port_) {
		super(time);
		packet = packet_;
		switchPort = port_;
		priority = 40;
	}

	@Override
	public void handleEvent() {
		switchPort.setTransmitting(true);
		double linkDelay = switchPort.getNetworkLink().getTotalDelay(packet.getPacketSizeInBytes());
		Event event = null;
		
		//schedule end of transmission on switch
		event = new PacketTransmissionEndedOnSwitchEvent(
				this.getTimeOfEventOccurance() + linkDelay, switchPort);
		SimulationParameters.getEventQueue().add(event);

		//schedule receipt of packet on receiver
		if(packet instanceof TCPPacket) {
			event = new PacketReceivedOnReceiver(this.getTimeOfEventOccurance() + linkDelay, 
					(TCPPacket)packet, packet.getReceiver());
		} else { //ACKPacket
			event = new ACKReceivedOnSender(this.getTimeOfEventOccurance() + linkDelay,
					(ACKPacket)packet, packet.getSender());
		}
		SimulationParameters.getEventQueue().add(event);

	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public SwitchPort getSwitchPort() {
		return switchPort;
	}

	public void setSwitchPort(SwitchPort port) {
		this.switchPort = port;
	}
	
	public String toString() {
		return super.toString() + "," + packet;
	}

}
