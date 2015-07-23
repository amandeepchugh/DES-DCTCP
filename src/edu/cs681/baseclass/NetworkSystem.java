package edu.cs681.baseclass;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class NetworkSystem {
	private static List<Sender> senderList = new ArrayList<Sender>();
	private static Receiver receiver;
	private static Switch nwswitch;
	
	static private int nextReceiverId = 0;
	static private int nextSenderId = 0;
	
	public static int getNextReceiverId() {
		return nextReceiverId++;
	}
	
	public static int getNextSenderId() {
		return nextSenderId++;
	}
	
	public static final List<Sender> getSenderList() {
		return senderList;
	}
	public static final void setSenderList(List<Sender> senderList) {
		NetworkSystem.senderList = senderList;
	}
	public static final void addSender(Sender sender) {
		NetworkSystem.senderList.add(sender);
	}
	public static Receiver getReceiver() {
		return receiver;
	}
	public static void setReceiver(Receiver receiver) {
		NetworkSystem.receiver = receiver;
	}
	public static Switch getNwswitch() {
		return nwswitch;
	}
	public static void setNwswitch(Switch nwswitch) {
		NetworkSystem.nwswitch = nwswitch;
	}
	
	public static void reset() {
		for(Sender sender : senderList) {
			sender.reset();
		}
		receiver.reset();
		nwswitch.reset();
	}

	public static void printOutput(PrintWriter output) throws FileNotFoundException {
		double totalThroughput = 0;
		for(Sender sender : senderList) {
			sender.printOutput(output);
			sender.getTotalPacketsSuccessfullyACKed().calculateConfidenceIntervals();
			totalThroughput += sender.getTotalPacketsSuccessfullyACKed().getMean();
//			if(sender.getMachineId() == 1) {
//				sender.printOutput(output);
//			}
		}
		output.println("Collective Throughput: " + totalThroughput);
		output.flush();
		receiver.printOutput(output);
		output.flush();
		nwswitch.printOutput(output);
		output.flush();
	}

	public static void recordCISample() {
		for(Sender sender : senderList) {
			sender.recordCISample();
		}
		receiver.recordCISample();
		nwswitch.recordCISample();
	}
}
