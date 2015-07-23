package edu.cs681.baseclass;

import java.io.PrintWriter;
import java.util.ArrayList;

import edu.cs681.metric.DifferenceKeeper;
import edu.cs681.metric.DiscreteSampleAverageMetric;
import edu.cs681.metric.TimeAverageMetric;
import edu.cs681.simulator.SimulationConfiguration;
import edu.cs681.simulator.SimulationParameters;

public class SwitchPort {
	private Switch parentSwitch;
	/** every port has constant maxQueueLength */
	private int maxQueueLength = SimulationConfiguration.getInteger("SwitchPort.maxQueueLength");
	private ArrayList<Packet> portQueue;
	private NetworkLink networkLink;
	private boolean transmitting;
	
	private TimeAverageMetric avgQueueLength = new TimeAverageMetric(0.95);
	private DifferenceKeeper avgQueueLengthTimeDiff = new DifferenceKeeper();
	private DiscreteSampleAverageMetric packetDropProbability = new DiscreteSampleAverageMetric(0.95);
	private DiscreteSampleAverageMetric avgWaitingTime = new DiscreteSampleAverageMetric(0.95);

	public SwitchPort() {
		init();
	}
	private void init() {
		 portQueue = new ArrayList<Packet>(maxQueueLength);
		 transmitting = false;
		 avgQueueLengthTimeDiff.clearEverything();
	}
	public boolean isTransmitting() {
		return transmitting;
	}
	public void setTransmitting(boolean transmitting) {
		this.transmitting = transmitting;
	}
	public final void addPacketToQueue(Packet packet) {
		if(portQueue.size() >= maxQueueLength) {
			//dont add packet to the queue, just drop it silently
			packetDropProbability.recordValue(1);
			return;
		} else {
			avgQueueLengthTimeDiff.recordValue(SimulationParameters.getSimualationTime());
			avgQueueLength.recordValue(portQueue.size() * avgQueueLengthTimeDiff.getDifference());
			portQueue.add(packet);
			packetDropProbability.recordValue(0);
			packet.getWaitingTimeDiff(1).recordValue(SimulationParameters.getSimualationTime());
		}
	}
	
	public final Switch getParentSwitch() {
		return parentSwitch;
	}
	public final void setParentSwitch(Switch parentSwitch) {
		this.parentSwitch = parentSwitch;
	}
	public final NetworkLink getNetworkLink() {
		return networkLink;
	}
	public final void setNetworkLink(NetworkLink networkLink) {
		this.networkLink = networkLink;
		networkLink.setSwitchPort(this);
	}

	public void reset() {
		init();
	}
	public TimeAverageMetric getAvgQueueLength() {
		return avgQueueLength;
	}
	public void recordCISample() {
		avgQueueLength.recordCISample();
		packetDropProbability.recordCISample();
		avgWaitingTime.recordCISample();
	}
	public void printOutput(PrintWriter output) {
		avgQueueLength.calculateConfidenceIntervals();
		packetDropProbability.calculateConfidenceIntervals();
		avgWaitingTime.calculateConfidenceIntervals();
		
		System.out.println();
		System.out.println("SwitchPort of " + networkLink.getMachine().getName() + ":");
		System.out.println("Average Queue Length: " + avgQueueLength);
		System.out.println("Packet Drop Prob    : " + packetDropProbability);
		System.out.println("Average Waiting Time: " + avgWaitingTime);
	}
	public DiscreteSampleAverageMetric getPacketDropProbability() {
		return packetDropProbability;
	}
	public DifferenceKeeper getAvgQueueLengthTimeDiff() {
		return avgQueueLengthTimeDiff;
	}
	public DiscreteSampleAverageMetric getAvgWaitingTime() {
		return avgWaitingTime;
	}
	public Packet getPacketFromQueue() {
		Packet packet = portQueue.remove(0);
		packet.getWaitingTimeDiff(2).recordValue(SimulationParameters.getSimualationTime());
		
		try {
			avgWaitingTime.recordValue(packet.getWaitingTimeDiff(0).getDifference());
		}catch(RuntimeException re) {
			System.err.println(packet);
			re.printStackTrace();
		}
		packet.setWaitingTimeDiff(null);
		return packet;
	}
	public int getQueueLength() {
		return portQueue.size();
	}
}
