package edu.cs681.baseclass;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.cs681.simulator.SimulationConfiguration;

public class Receiver extends Machine {
	Map<Sender, Long> lastSuccessfullyReceivedPacketIdMap;
	Map<Sender, Set<TCPPacket>> outOfOrderPackets;
	private final double processingTime = SimulationConfiguration.getInteger("Receiver.processingTime");
	
	/* receiver will NOT have a queue for ACKs (like sender has a queue for TCPPackets), because
	 * TCP data packets will be larger in size. If a receiver can receive at rate x, then it can
	 * always ACK at the rate x successfully. This fact, along with the fact that receiver will ACK
	 * what it receives, will eliminate the need of a queue at receiver end for queuing ACKs.
	 */

	public Receiver() {
		machineId = NetworkSystem.getNextReceiverId();
		init();		
	}
	public String toString() {
		return "R"+machineId;
	}
	public double getProcessingTime() {
		return processingTime;
	}

	public void addLastSuccessfullyReceivedPacketIdForSender(Sender s, long packetId) {
		Long oldPacketId = lastSuccessfullyReceivedPacketIdMap.get(s);
		if(oldPacketId != null && oldPacketId > packetId) {
			System.err.println("BABA RE: receiver's lastSuccessfullyReceived number for sender S"+s.machineId+" is decreasing from "+oldPacketId+" to "+packetId);
			System.exit(1);
		}
		lastSuccessfullyReceivedPacketIdMap.put(s, packetId);
	}
	
	public long getLastSuccessfullyReceivedPacketIdForSender(Sender s) {
		if(lastSuccessfullyReceivedPacketIdMap.containsKey(s)) {
			return lastSuccessfullyReceivedPacketIdMap.get(s);
		} else {
			lastSuccessfullyReceivedPacketIdMap.put(s, 0L);
			return 0;
		}
	}
	
	public Set<TCPPacket> getOutOfOrderPacketListForSender(Sender sender) {
		return outOfOrderPackets.get(sender);
	}
	
	public void addToOutOfOrderPackets(Sender sender, TCPPacket packet) {
		if(outOfOrderPackets.get(sender) == null) {
			outOfOrderPackets.put(sender, new TreeSet<TCPPacket>());			
		}
		outOfOrderPackets.get(sender).add(packet);
	}
	public void reset() {
		init();		
	}
	private void init() {
		nextSequenceNumber = 1;
		lastSuccessfullyReceivedPacketIdMap = new HashMap<Sender, Long>();
		outOfOrderPackets = new HashMap<Sender, Set<TCPPacket>>();
	}
	public void printOutput(PrintWriter output) {
		
	}
	public void recordCISample() {
		
	}
}
