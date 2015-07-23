package edu.cs681.baseclass;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.PriorityQueue;

import edu.cs681.metric.DifferenceKeeper;
import edu.cs681.metric.DiscreteSampleAverageMetric;
import edu.cs681.metric.TimeAverageMetric;
import edu.cs681.random.ExponentialDistribution;
import edu.cs681.simulator.SimulationConfiguration;
import edu.cs681.simulator.SimulationParameters;

public class Sender extends Machine {
	private int windowThreshold = SimulationConfiguration.getInteger("Sender.windowThreshold");
	private double timeoutDuration = SimulationConfiguration.getDouble("Sender.timeoutDuration");
	private ExponentialDistribution sendingRateDistribution = new ExponentialDistribution(1/SimulationConfiguration.getDouble("Sender.sendingRate"));
	private Receiver targetReceiver;
	
	private PriorityQueue<TCPPacket> packetsInCurrentWindow;
	private int windowSize;
	private long sequenceNumberOfLastACKedPacket;
	private boolean transmitting;
	private PriorityQueue<TCPPacket> packetsToBeTransmitted;
	/** count of continuous ACKs received without any timeout (used for increasing window size) */
	private int ackCounter;
	private int eceFlagCounter;
	private int eceFlagTotal;
	private double alpha;
	private double g = SimulationConfiguration.getDouble("Sender.g");
	/** this value is read by timeout event handling. value is valid only when the sender is transmitting. */
	private double willBeTransmittingTill;
	
	private DiscreteSampleAverageMetric packetTimeouts = new DiscreteSampleAverageMetric(0.95); //XXX: remove hardcoding
	private TimeAverageMetric avgWindowSize = new TimeAverageMetric(0.95);
	private DifferenceKeeper windowSizeTimeDifferenceKeeper = new DifferenceKeeper();
	private TimeAverageMetric totalPacketsSuccessfullyACKed = new TimeAverageMetric(0.95);
	
	public Sender() {
		init();
		machineId = NetworkSystem.getNextSenderId();
	}
	
	public ExponentialDistribution getSendingRateDistribution() {
		return sendingRateDistribution;
	}
	public final int getWindowThreshold() {
		return windowThreshold;
	}
	public final void setWindowThreshold(int windowThreshold) {
		this.windowThreshold = windowThreshold;
	}
	public final PriorityQueue<TCPPacket> getPacketsInCurrentWindow() {
		return packetsInCurrentWindow;
	}
	public final void setPacketsInCurrentWindow(
			PriorityQueue<TCPPacket> packetsInCurrentWindow) {
		this.packetsInCurrentWindow = packetsInCurrentWindow;
	}
	public final double getTimeoutDuration() {
		return timeoutDuration;
	}
	public final void setTimeoutDuration(double timeout) {
		this.timeoutDuration = timeout;
	}
	public long getSequenceNumberOfLastACKedPacket() {
		return sequenceNumberOfLastACKedPacket;
	}
	public void setSequenceNumberOfLastACKedPacket(long sequenceNumberOfLastACKedPacket) {
		if(this.sequenceNumberOfLastACKedPacket > sequenceNumberOfLastACKedPacket) {
			System.err.println("OOPS: last ACKed sequence number value in sender S"+machineId+" is decreasing. Going from " + this.sequenceNumberOfLastACKedPacket + " to " + sequenceNumberOfLastACKedPacket);
			System.exit(1);
		}
		this.sequenceNumberOfLastACKedPacket = sequenceNumberOfLastACKedPacket;
	}
	public Receiver getTargetReceiver() {
		return targetReceiver;
	}
	public void setTargetReceiver(Receiver targetReceiver) {
		this.targetReceiver = targetReceiver;
	}
	public boolean isTransmitting() {
		return transmitting;
	}
	public void setTransmitting(boolean transmitting) {
		this.transmitting = transmitting;
	}
	public PriorityQueue<TCPPacket> getPacketsToBeTransmitted() {
		return packetsToBeTransmitted;
	}
	public void setPacketsToBeTransmitted(PriorityQueue<TCPPacket> packetsToBeTransmitted) {
		this.packetsToBeTransmitted = packetsToBeTransmitted;
	}
	public int getWindowSize() {
		return windowSize;
	}
	public void incrementWindowSize() {
		windowSizeTimeDifferenceKeeper.recordValue(SimulationParameters.getSimualationTime());
		avgWindowSize.recordValue(windowSize * windowSizeTimeDifferenceKeeper.getDifference());
		windowSize++;
	}
	public int getAckCounter() {
		return ackCounter;
	}
	public void incrementAckCounter() {
		this.ackCounter++;
	}
	public void resetCounterToZero() {
		this.ackCounter = 0;
	}
	public void resetWindowSizeToOne() {
		windowSizeTimeDifferenceKeeper.recordValue(SimulationParameters.getSimualationTime());
		avgWindowSize.recordValue(windowSize * windowSizeTimeDifferenceKeeper.getDifference());
		this.windowSize = 1;
		this.resetEceFlagCounter();
		this.resetEceFlagTotal();
	}
	public int getEceFlagCounter() {
		return eceFlagCounter;
	}
	public void incrementEceFlagCounter() {
		this.eceFlagCounter++;
	}
	public void resetEceFlagCounter() {
		this.eceFlagCounter = 0;
	}
	public int getEceFlagTotal() {
		return eceFlagTotal;
	}
	public void incrementEceFlagTotal() {
		this.eceFlagTotal++;
	}
	public void resetEceFlagTotal() {
		this.eceFlagTotal = 0;
	}
	public void updateAlpha() {
		alpha = alpha * (1-g) + g * eceFlagCounter/(double)eceFlagTotal;
		windowSizeTimeDifferenceKeeper.recordValue(SimulationParameters.getSimualationTime());
		avgWindowSize.recordValue(windowSize * windowSizeTimeDifferenceKeeper.getDifference());
		windowSize = (int)(windowSize * (1-alpha/2));
		if(windowSize == 0) {
			windowSize++;
		}
	}
	public String toString() {
		return "S"+machineId+"#windowsize:"+windowSize+"#windowthreshold:"+windowThreshold+"#packetsAcknowledgedTill:"+sequenceNumberOfLastACKedPacket;
	}

	public void reset() {
		init();		
	}

	private void init() {
		alpha = 0;
		eceFlagTotal = 0;
		eceFlagCounter = 0;
		ackCounter = 0;
		packetsInCurrentWindow = new PriorityQueue<TCPPacket>();
		windowSize = 1;
		sequenceNumberOfLastACKedPacket = 0;
		transmitting = false;
		packetsToBeTransmitted = new PriorityQueue<TCPPacket>();
		willBeTransmittingTill = 0;
		
		nextSequenceNumber = 1;
		windowSizeTimeDifferenceKeeper.clearEverything();
	}

	public double getWillBeTransmittingTill() {
		return willBeTransmittingTill;
	}

	public void setWillBeTransmittingTill(double willBeTransmittingTill) {
		this.willBeTransmittingTill = willBeTransmittingTill;
	}

	public DiscreteSampleAverageMetric getPacketTimeouts() {
		return packetTimeouts;
	}

	public void printOutput(PrintWriter output) throws FileNotFoundException {
		packetTimeouts.calculateConfidenceIntervals();
		avgWindowSize.calculateConfidenceIntervals();
		totalPacketsSuccessfullyACKed.calculateConfidenceIntervals();
		output.println();
		output.println("Sender S" + machineId + ":");
//		output.println("Total ACKs recd: " + this.sequenceNumberOfLastACKedPacket);
		output.println("Packet Timeouts: " + this.packetTimeouts);
		output.println("Avg Window Size: " + this.avgWindowSize);
		output.println("Avg Throughput : " + this.totalPacketsSuccessfullyACKed);
	}

	public void recordCISample() {
		packetTimeouts.recordCISample();
		if(!avgWindowSize.isValuesCapturedSinceLastCI()) {
			windowSizeTimeDifferenceKeeper.recordValue(SimulationParameters.getSimualationTime());
			avgWindowSize.recordValue(windowSize * windowSizeTimeDifferenceKeeper.getDifference());
		}
		avgWindowSize.recordCISample();
		totalPacketsSuccessfullyACKed.recordCISample();
	}

	public TimeAverageMetric getAvgWindowSize() {
		return avgWindowSize;
	}

	public TimeAverageMetric getTotalPacketsSuccessfullyACKed() {
		return totalPacketsSuccessfullyACKed;
	}

	public DifferenceKeeper getWindowSizeTimeDifferenceKeeper() {
		return windowSizeTimeDifferenceKeeper;
	}
}