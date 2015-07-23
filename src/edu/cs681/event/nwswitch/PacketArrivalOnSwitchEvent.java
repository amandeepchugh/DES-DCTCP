package edu.cs681.event.nwswitch;

import edu.cs681.baseclass.Packet;
import edu.cs681.baseclass.SwitchPort;
import edu.cs681.baseclass.TCPPacket;
import edu.cs681.event.Event;
import edu.cs681.simulator.SimulationParameters;

public class PacketArrivalOnSwitchEvent extends Event {
	private Packet packet;
	private SwitchPort arrivingPort;
	
	public PacketArrivalOnSwitchEvent(double time, Packet p, SwitchPort s) {
		super(time);
		arrivingPort = s;
		packet = p;
		priority = 42;
	}
	@Override
	public void handleEvent() {
		SwitchPort targetPort = null;
		if(packet instanceof TCPPacket) {
			targetPort = arrivingPort.getParentSwitch().getPortOfTheMachine(packet.getReceiver());
		} else { //ACKPacket
			targetPort = arrivingPort.getParentSwitch().getPortOfTheMachine(packet.getSender());
		}
		int queueLength = targetPort.getQueueLength();
		if(queueLength > targetPort.getParentSwitch().getMarkingThreshold()) {
			packet.setRedMarking(true);
		}
		
		if(targetPort.isTransmitting() || targetPort.getQueueLength() > 0 ) {
			// enqueue in the port queue if port is busy
			targetPort.addPacketToQueue(packet);
		} else {
			// if switch is free, then start transmission
			targetPort.getPacketDropProbability().recordValue(0);
			targetPort.getAvgWaitingTime().recordValue(0);
			Event event = new PacketTransmissionStartedOnSwitchEvent(
					this.getTimeOfEventOccurance(), packet, targetPort);
			SimulationParameters.getEventQueue().add(event);
		}
	}
	public final Packet getPacket() {
		return packet;
	}
	public final void setPacket(Packet packet) {
		this.packet = packet;
	}
	public final SwitchPort getArrivingPort() {
		return arrivingPort;
	}
	public final void setArrivingPort(SwitchPort switchPort) {
		this.arrivingPort = switchPort;
	}
	public String toString() {
		return super.toString() + "," + packet;
	}
}
