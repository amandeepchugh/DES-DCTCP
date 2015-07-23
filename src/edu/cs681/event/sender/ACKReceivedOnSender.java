package edu.cs681.event.sender;

import edu.cs681.baseclass.ACKPacket;
import edu.cs681.baseclass.Sender;
import edu.cs681.baseclass.TCPPacket;
import edu.cs681.event.Event;
import edu.cs681.simulator.SimulationParameters;

public class ACKReceivedOnSender extends Event {

	/* (non-Javadoc)
	 * @see edu.cs681.baseclass.event.Event#handleEvent()
	 */
	private ACKPacket ackPacket;
	private Sender sender;
	
	public ACKReceivedOnSender(double time, ACKPacket ap, Sender s) {
		super(time);
		ackPacket = ap;
		sender = s;
		priority = 30;
	}
	@Override
	public void handleEvent() {
		sender.setSequenceNumberOfLastACKedPacket(ackPacket.getExpectedSequenceNumber() - 1);
		
		//handle the ece flag of the ACK packet, and update the window size
		sender.incrementEceFlagTotal();
		if(ackPacket.isEce()) {
			sender.incrementEceFlagCounter();
		}
		
		//when sufficient samples are received, update alpha, and reset
		if(sender.getEceFlagTotal() >= sender.getWindowSize()) {
			sender.updateAlpha();
			sender.resetEceFlagCounter();
			sender.resetEceFlagTotal();
		}
		
		//first packet in the window buffer is still unacknowledged, so retransmit that packet directly
//		if(sender.getPacketsInCurrentWindow().peek() == null) {
//			System.err.println(sender.getWindowSize());
//			System.err.println(sender.getPacketsInCurrentWindow().size());
//			for(Packet p : sender.getPacketsInCurrentWindow()) {
//				System.err.println(p);
//			}
//			System.err.println("POSSIBLE BUG: sender window empty when ACK received on sender. "+ackPacket);
//			System.exit(1);
//		}
		if(sender.getPacketsInCurrentWindow().peek()!= null && ackPacket.getExpectedSequenceNumber() < sender.getPacketsInCurrentWindow().peek().getSequenceNumber()) { //similar to ==
			SimulationParameters.removeAllFutureTimeoutEventsFromEventQueue();
			
			//empty the current window buffer, and put all the "failed" packets back in sender queue
			sender.getPacketsToBeTransmitted().addAll(sender.getPacketsInCurrentWindow());
			while(sender.getPacketsInCurrentWindow().size() > 0) {
				sender.getPacketsInCurrentWindow().remove();
			}
		}
		
		//remove all the acknowledged packets from the window
		TCPPacket toBeRemovedFromWindow = null;
		int totalACKedPacketsInThisEvent = 0;
		while(true) {
			toBeRemovedFromWindow = sender.getPacketsInCurrentWindow().peek();
			if(toBeRemovedFromWindow != null && toBeRemovedFromWindow.getSequenceNumber() < ackPacket.getExpectedSequenceNumber()) {
				sender.getPacketsInCurrentWindow().remove();
				totalACKedPacketsInThisEvent++;
				
				//upon each "logical" ACK received, update the counter and/or window size
				if(sender.getWindowSize() >= sender.getWindowThreshold()) {
					sender.incrementAckCounter();
					if(sender.getAckCounter() >= sender.getWindowSize()) {
						sender.incrementWindowSize();
						sender.resetCounterToZero();
					}
				} else {
					sender.incrementWindowSize();
				}
			} else {
				break;
			}
		}
		sender.getTotalPacketsSuccessfullyACKed().recordValue(totalACKedPacketsInThisEvent);
		
		if(!sender.isTransmitting()) {
			//schedule the next packet to be sent, as now the window "fullness"
			//will decrease due to received ACKs
			if(sender.getPacketsToBeTransmitted().size() > 0 && sender.getPacketsInCurrentWindow().size() < sender.getWindowSize()) {
				Event event = new PacketTransmissionStartedOnSenderEvent(this.getTimeOfEventOccurance(),
						sender.getPacketsToBeTransmitted().remove(), sender);
				sender.setTransmitting(true);
				SimulationParameters.getEventQueue().add(event);
			}
		}		
	}
	
	public final ACKPacket getAckPacket() {
		return ackPacket;
	}
	public final void setAckPacket(ACKPacket ackPacket) {
		this.ackPacket = ackPacket;
	}
	public final Sender getSender() {
		return sender;
	}
	public final void setSender(Sender sender) {
		this.sender = sender;
	}
	public String toString() {
		return super.toString() + "," + ackPacket;
	}
	
}
