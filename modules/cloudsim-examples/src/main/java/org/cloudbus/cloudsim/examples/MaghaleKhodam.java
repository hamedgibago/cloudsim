package org.cloudbus.cloudsim.examples;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostDynamicWorkload;
import org.cloudbus.cloudsim.HostStateHistoryEntry;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.UtilizationModelNull;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.cloudbus.cloudsim.UtilizationModelThreshold;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.eicb.CloudletSchedulerEicb;
import org.cloudbus.cloudsim.eicb.PowerDatacenterEicb;
import org.cloudbus.cloudsim.eicb.PowerVmAllocationPolicyEicbThreshold;
import org.cloudbus.cloudsim.eicb.activity;
import org.cloudbus.cloudsim.lists.HostList;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerDatacenterBroker;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.power.PowerVm;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMinimumMigrationTime;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMinimumUtilization;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G5Xeon3075;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.org.apache.xpath.internal.operations.Bool;

import sun.util.logging.resources.logging;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.*;

/**
 * A simple example showing how to create a data center with one host and run
 * one cloudlet on it.
 */
public class MaghaleKhodam {
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList, cloudletListTest;
	/** The vmlist. */
	private static List<Vm> vmlist;

	/**
	 * Creates main() to run this example.
	 *
	 * @param args the args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		SimpsonIntegrator si = new SimpsonIntegrator();
		// final double result = si.integrate(50, x -> x, 1, 3);
		/*
		 * final double result = si.integrate(50, new UnivariateFunction() {
		 * 
		 * @Override public double value(double x) { return value2(x); } }, 1, 3);
		 * Log.printLine(result + " integral result");
		 */
		Log.printLine("Starting CloudSim Eicb Example...");

		try {
			// First step: Initialize the CloudSim package. It should be called before
			// creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current
														// date and time.
			boolean trace_flag = false; // trace events

			CloudSim.init(num_user, calendar, trace_flag);

			PowerDatacenter datacenter0 = (PowerDatacenter) createDatacenter("Datacenter_0");

			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			vmlist = new ArrayList<Vm>();

			// we try to create vms based on cloudlet size
			// or pm threshold
			// first we create one vm based on 70% pm cpu
			// and during the simulation and scheduling we add more vms based on
			// cloudlet size
			// we set 70% in creation of datacenter object,
			// in PowerVmAllocationPolicyMigrationStaticThreshold
			// before it was VmAllocationPolicySimple
			// VM description
			int vmid = 0;
			int mips =620;
			int pesNumber = 1; // number of cpus
			
			createMultipleSameVms(brokerId, mips, 3, pesNumber);

			//createOneVm(brokerId, mips, 1, pesNumber,vmid);
			//mips = 930;
			//createOneVm(brokerId, mips, 1, pesNumber,++vmid);
			//mips = 200;
			//createOneVm(brokerId, mips, 1, pesNumber,++vmid);
			

			// submit vm list to the broker
			broker.submitVmList(vmlist);

			cloudletList = new ArrayList<Cloudlet>();

			Random rand = new Random();

			// Cloudlet properties
			int id = 0;
			long length = 500;
			long fileSize = 0;
			long outputSize = 0;

			boolean acivityType = false;
			int groupingChar = 1;
			int arrivingTime = 1;
			int dealine = 120;
			int cloudletPesNumbers = 1;

			UtilizationModel thresholdUtilModel = new UtilizationModelThreshold(70);
			UtilizationModel nullutilizationModel = new UtilizationModelNull();
			UtilizationModel fullutilizationModel = new UtilizationModelFull();

			String filepath = "c:\\workloadlist-10000.txt";
			workloadFromFile(brokerId, cloudletPesNumbers, fileSize, outputSize, fullutilizationModel,
					nullutilizationModel, filepath);

			// Create Random cloudlets
			//createRandomClouldlets(30000,brokerId, rand, fileSize, outputSize,
			//nullutilizationModel, fullutilizationModel);

			//writeCloudletsToFile(cloudletList,filepath);
			
			//TODO: yadet naro chon az cloudletScheduler az spaceShared ert mibare pas be 
			//sorate first come first serviced hast, pas bayad vaghti cloudlet haye random ro
			//ke tolid shode load kardi, hamaro be tartibe zamane vorood moratab koni, bad
			// beferesti be cloudletList.
			//albate mi2ni bad az rikhtan to khodet cloudletList, hamun cloudletList ro moratab
			//koni bar asase time voorood va badesh 
			//broker.submitCloudletList(cloudletList);
			//ro ejra koni

			broker.submitCloudletList(cloudletList);
			long start = System.currentTimeMillis();
			CloudSim.startSimulation();

			CloudSim.stopSimulation();
			long finish = System.currentTimeMillis();
			long timeElapsed = finish - start;

			List<Object> energyList = new ArrayList();

			double DatacenterEnergy = calculateTotalEnergy(si, datacenter0);

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			printCloudletList(newList);
			Log.printLine("Number of cloudlets received: "+newList.size());

			Log.printLine("Datacenter Energy is: " + DatacenterEnergy);
			Log.printLine("time elapsed : " + timeElapsed / 1000);

			Log.printLine("CloudSimExample1 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	private static double calculateTotalEnergy(SimpsonIntegrator si, PowerDatacenter datacenter0) {
		// equation 4
		double DatacenterEnergy = 0;

		for (int i = 0; i < datacenter0.getHostList().size(); i++) {
			Host host = datacenter0.getHostList().get(i);
			List<HostStateHistoryEntry> stateList = ((HostDynamicWorkload) host).getStateHistory();

			double energy=0;
			for (int j = 0; j < stateList.size(); j++) {
				energy+=stateList.get(j).getEnegry();
			}
			
			/*
			final double energy = si.integrate(50, new UnivariateFunction() {
				@Override
				public double value(double time) {
					return ((PowerHost) host).getEnergyChing_Hsien(time);
				}
			}, stateList.get(0).getTime(), stateList.get(stateList.size() - 1).getTime());
			 *
			 */
			DatacenterEnergy += energy;
			Log.printLine("Total Energy for host " + i + " :  " + energy);

			/*
			 * for (int j = 0; j < stateList.size(); j++) {
			 * Log.printLine("Energy history for host "+i+" : at time "+stateList.get(j).
			 * getTime()+" is : " +stateList.get(j).getEnegry()); }
			 */
		}
		return DatacenterEnergy;
	}

	private static void writeCloudletsToFile(List<Cloudlet> cloudletList2, String filepath) throws IOException {
		StringBuilder builder = new StringBuilder();
		for (Cloudlet cl : cloudletList2) {
			builder.append(String.join(",", Integer.toString(cl.getCloudletId()),
					Integer.toString((int) cl.getCloudletLength()), Boolean.toString(((activity) cl).acivityType),
					Integer.toString(((activity) cl).groupingChar), Integer.toString(((activity) cl).arrivingTime),
					Integer.toString((int) ((activity) cl).deadline)));
			builder.append("\n");
		}

		byte[] strToBytes = builder.toString().getBytes();
		Path path = Paths.get(filepath);
		Files.write(path, strToBytes);
	}

	private static void createRandomClouldlets(int total, int brokerId, Random rand, long fileSize, long outputSize,
			UtilizationModel ramBwUtilModel, UtilizationModel cpuUtilModel) {
		long length;
		boolean acivityType;
		int groupingChar;
		int arrivingTime;
		int dealine;
		for (int i = 0; i < total; i++) {
			length = (long) (rand.nextInt(100000 - 50000 + 1) + 50000); // workload of activity
			acivityType = rand.nextBoolean(); // bool value, false=normal, true=batching
			groupingChar = rand.nextInt(100) + 1; // random between [1-100]

			arrivingTime = rand.nextInt(9) + 1; // random between [1-9]
			dealine = rand.nextInt(180 - 120 + 1) + 120; // random between [120-180]

			activity ac = new activity(i, length, 1, fileSize, outputSize, cpuUtilModel, ramBwUtilModel, ramBwUtilModel,
					rand.nextBoolean(), groupingChar, arrivingTime, dealine);
			ac.setUserId(brokerId);
			cloudletList.add(ac);
		}
	}

	public static double value2(double x) {
		return x;
	}

	private static void createMultipleSameVms(int brokerId, int mips, int numberofVms, int pesNumber) {
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;

		String vmm = "Xen"; // VMM name

		for (int i = 0; i < numberofVms; i++) {
			Vm vm = new PowerVm(i, brokerId, mips, pesNumber, ram, bw, size, 1, vmm, new CloudletSchedulerEicb(), 1000);
			 //Vm vm = new PowerVm(i, brokerId, mips, pesNumber, ram, bw,size, 1 ,vmm, new CloudletSchedulerSpaceShared(),1000);
			vmlist.add(vm);
		}
	}
	
	private static void createOneVm(int brokerId, int mips, int numberofVms, int pesNumber,int vmId) {
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;

		String vmm = "Xen"; // VMM name

		Vm vm = new PowerVm(vmId, brokerId, mips, pesNumber, ram, bw, size, 1, vmm, new CloudletSchedulerEicb(), 1000);
		// Vm vm = new PowerVm(vmId, brokerId, mips, pesNumber, ram, bw,size, 1 ,vmm,
		// new
		// CloudletSchedulerSpaceShared(),1000);
		vmlist.add(vm);
	}

	private static void workloadFromFile(int brokerId, int pesNumber, long fileSize, long outputSize,
			UtilizationModel cpuUtilModel, UtilizationModel ramBwUtilModel, String filename)
			throws IOException, FileNotFoundException {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				List<String> splited = Arrays.asList(line.split(","));

				activity ac = new activity(Integer.parseInt(splited.get(0)), Integer.parseInt(splited.get(1)),
						pesNumber, fileSize, outputSize, cpuUtilModel, ramBwUtilModel, ramBwUtilModel,
						Boolean.parseBoolean(splited.get(2)), Integer.parseInt(splited.get(3)),
						Integer.parseInt(splited.get(4)), Integer.parseInt(splited.get(5)));
				ac.setUserId(brokerId);
				cloudletList.add(ac);
			}
		}
	}

	/**
	 * Creates the datacenter.
	 *
	 * @param name the name
	 *
	 * @return the datacenter
	 */
	// private static PowerDatacenter createDatacenter(String name) {
	private static Datacenter createDatacenter(String name) {

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		// our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();
		// G4
		int mips = 1860;

		// 3. Create PEs and add these into a list.
		// dual core
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList.add(new Pe(1, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

		// 4. Create Host with its id and list of PEs and add them to the list
		// of machines
		/*
		 * commented by Hamed Akrami int hostId = 0; int ram = 2048; // host memory (MB)
		 * long storage = 1000000; // host storage int bw = 10000;
		 */

		// Added by Hamed Akrami
		// HP ProLiant ML110 G4
		int hostId = 0;
		int ram = 4096; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000;

		// add 26 host for minimum required 10000 instance
		 //for (int i = 0; i < 2; i++) {
		hostList.add(new PowerHostUtilizationHistory(
				// new Host(
				hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList,
				new VmSchedulerTimeShared(peList), 				
				new PowerModelSpecPowerHpProLiantMl110G4Xeon3040())); // This is our
																											// machine
		 //hostId++;
		// }

		// HP ProLiant ML110 G5
		// with same memory
		hostId = 1;

		// clean peList
		peList = new ArrayList<Pe>();
		// added dual core pe for G5
		mips = 2660;
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList.add(new Pe(1, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

		/*
		 * hostList.add( new PowerHostUtilizationHistory( hostId, new
		 * RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList, new
		 * VmSchedulerTimeShared(peList), new
		 * PowerModelSpecPowerHpProLiantMl110G5Xeon3075() ) ); // This is our machine
		 * 
		 */

		/*
		 * // Added by Hamed Akrami // HP ProLiant ML110 G4 hostId = 2; ram = 16384; //
		 * host memory (MB)
		 * 
		 * 
		 * hostList.add( new Host( hostId, new RamProvisionerSimple(ram), new
		 * BwProvisionerSimple(bw), storage, peList, new VmSchedulerTimeShared(peList) )
		 * ); // This is our machine
		 */

		// 5. Create a DatacenterCharacteristics object that stores the
		// properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
		// devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone,
				cost, costPerMem, costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		// PowerDatacenter datacenter = null;
		PowerDatacenter datacenter = null;
		try {
			// PowerVmSelectionPolicy vmSelectionPolicy=new
			// PowerVmSelectionPolicyMinimumUtilization();
			PowerVmSelectionPolicy vmSelectionPolicy = new PowerVmSelectionPolicyMinimumMigrationTime();

			datacenter = // new PowerDatacenter(name, characteristics,
					new PowerDatacenterEicb(name, characteristics,
							// new
							// PowerVmAllocationPolicyMigrationStaticThreshold(hostList,vmSelectionPolicy,0.7),
							// storageList, 1);
							new PowerVmAllocationPolicyEicbThreshold(hostList, vmSelectionPolicy, 0.7), storageList, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		datacenter.setDisableMigrations(false);

		return datacenter;
	}

	// We strongly encourage users to develop their own broker policies, to
	// submit vms and cloudlets according
	// to the specific rules of the simulated scenario
	/**
	 * Creates the broker.
	 *
	 * @return the datacenter broker
	 */
	private static DatacenterBroker createBroker() {
		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
						+ indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}
}