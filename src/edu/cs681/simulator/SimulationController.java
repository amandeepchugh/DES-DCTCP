package edu.cs681.simulator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.cs681.baseclass.Machine;
import edu.cs681.baseclass.NetworkLink;
import edu.cs681.baseclass.NetworkSystem;
import edu.cs681.baseclass.Receiver;
import edu.cs681.baseclass.Sender;
import edu.cs681.baseclass.Switch;
import edu.cs681.baseclass.SwitchPort;
import edu.cs681.event.Event;
import edu.cs681.event.SimulationStartEvent;


public class SimulationController {
	private static Logger logger = Logger.getLogger(SimulationController.class);
	public static void main(String[] args) throws IOException {
		createInputSystem(args);

		System.out.println("Simualtion started");
		for(; 
				SimulationParameters.getCurrentReplication() <SimulationParameters.getNumberOfReplications();
				SimulationParameters.incrementCurrentReplication()) {
			SimulationParameters.getEventQueue().add(new SimulationStartEvent(0));
			
			double lastCISampleTime = 0;
			double ciSampleDuration = SimulationParameters.getSimulationEndTime() / SimulationParameters.getTotalNumberOfSamples();
			
			double reportTimeDelta = SimulationParameters.getSimulationEndTime() / 100;
			double nextReportTime = reportTimeDelta;
			int progress = 0;
			
			while(true) {
				if(SimulationParameters.getSimualationTime() > nextReportTime) {
					System.out.print("\rReplication " + (SimulationParameters.getCurrentReplication()+1) + ": " + progress + "%");
					progress++;
					nextReportTime += reportTimeDelta;
				}
				
				if(SimulationParameters.getSimualationTime() > (lastCISampleTime+ciSampleDuration)) {
					lastCISampleTime = SimulationParameters.getSimualationTime();
					NetworkSystem.recordCISample();
					SimulationParameters.incrementCurrentSampleNumber();
				}
				
				Event event = SimulationParameters.getEventQueue().poll();
				if(event == null || SimulationParameters.getSimualationTime() > SimulationParameters.getSimulationEndTime()) {
					logger.debug("Simulation ended at time "  + SimulationParameters.getSimualationTime());
					System.out.print("\rReplication " + (SimulationParameters.getCurrentReplication()+1) + ": 100%");
					System.out.println();
					break;
				} else {
					SimulationParameters.setSimualationTime(event.getTimeOfEventOccurance());
					event.handleEvent();
				}
			}
			if(SimulationParameters.getCurrentReplication()+1 <SimulationParameters.getNumberOfReplications()) {
				NetworkSystem.reset();
				SimulationParameters.reset();
			}
		}
		printOutput();
	}
	
	public static void createInputSystem(String[] args) {
		int numSenders = SimulationConfiguration.getInteger("NetworkSystem.numSenders");
		
		NetworkSystem.setNwswitch(new Switch());
		NetworkSystem.setSenderList(new ArrayList<Sender>(numSenders));
		
		Receiver receiver = new Receiver();
		connectMachineToSwitch(receiver);
		NetworkSystem.setReceiver(receiver); //WISHLIST: while changing this to multiple receivers, and then pick a random receiver for each sender.
		
		Sender sender;
		for(int i=0;i<numSenders;i++) {
			sender = new Sender();
			connectMachineToSwitch(sender);
			sender.setTargetReceiver(NetworkSystem.getReceiver());
			NetworkSystem.addSender(sender);			
		}
	}

	private static void connectMachineToSwitch(Machine machine) {
		NetworkLink networkLink;
		SwitchPort switchPort;
		switchPort = new SwitchPort();
		
		networkLink = new NetworkLink();
		networkLink.setMachine(machine);
		networkLink.setSwitchPort(switchPort);
		
		switchPort.setNetworkLink(networkLink);
		switchPort.setParentSwitch(NetworkSystem.getNwswitch());
		NetworkSystem.getNwswitch().addPort(switchPort);
		
		machine.setNetworkLink(networkLink);
	}
	
	public static void printOutput() throws IOException {
		String fileName = SimulationConfiguration.getProperty("Simulation.outputFileName");
		PrintWriter output = null;
		if(fileName.equals("System.out")) {
			output = new PrintWriter(System.out);
		} else {
			File file = new File(fileName);
			if(!file.exists() && !file.createNewFile()) {
				System.err.println("Could not create output file " + file.getAbsolutePath() + ". Redirecting output to console.");
				output = new PrintWriter(System.out);
			} else {
				output = new PrintWriter(new PrintStream(file));
			}
		}
		NetworkSystem.printOutput(output);
		output.flush();
		output.close();
	}
}