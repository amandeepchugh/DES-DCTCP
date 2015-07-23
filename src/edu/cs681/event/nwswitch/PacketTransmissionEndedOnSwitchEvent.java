package edu.cs681.event.nwswitch;

import edu.cs681.baseclass.SwitchPort;
import edu.cs681.event.Event;
import edu.cs681.simulator.SimulationParameters;

public class PacketTransmissionEndedOnSwitchEvent extends Event {

	SwitchPort switchPort;
	public PacketTransmissionEndedOnSwitchEvent(double time, SwitchPort switchPort_) {
		super(time);
		switchPort = switchPort_;
		priority = 41;
	}

	@Override
	public void handleEvent() {
		switchPort.setTransmitting(false);

		//pick a packet from the queue, and transmit it (schedule packetTransmissionStartedOnSwitchEvent)
		// if port's queue is empty then do nothing, as there is nothing to transmit
		if (switchPort.getQueueLength() > 0) {
			switchPort.getAvgQueueLengthTimeDiff().recordValue(SimulationParameters.getSimualationTime());
			switchPort.getAvgQueueLength().recordValue(
					switchPort.getQueueLength() * switchPort.getAvgQueueLengthTimeDiff().getDifference());
			
			switchPort.setTransmitting(true);
			Event event = new PacketTransmissionStartedOnSwitchEvent(
					this.getTimeOfEventOccurance(), switchPort.getPacketFromQueue(), switchPort);
			SimulationParameters.getEventQueue().add(event);
		}
	}
}
