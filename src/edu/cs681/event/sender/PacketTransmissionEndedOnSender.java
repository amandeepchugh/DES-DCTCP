package edu.cs681.event.sender;

import edu.cs681.baseclass.Sender;
import edu.cs681.event.Event;
import edu.cs681.simulator.SimulationParameters;

public class PacketTransmissionEndedOnSender extends Event {

	private Sender sender;
	public PacketTransmissionEndedOnSender(double time, Sender sender_) {
		super(time);
		sender = sender_;
		priority = 20;
	}

	@Override
	public void handleEvent() {
		sender.setTransmitting(false);
		if(sender.getPacketsInCurrentWindow().size() >= sender.getWindowSize()) {
			//more packets cannot be sent. wait for ACKs from receiver before proceeding.
			return;			
		} else if(sender.getPacketsToBeTransmitted().size() > 0) {
			Event event = new PacketTransmissionStartedOnSenderEvent(this.getTimeOfEventOccurance(),
					sender.getPacketsToBeTransmitted().remove(), sender);
			sender.setTransmitting(true);
			SimulationParameters.getEventQueue().add(event);
		}
	}
	
	public String toString() {
		return super.toString() + "#sender:" + sender;
	}
}
