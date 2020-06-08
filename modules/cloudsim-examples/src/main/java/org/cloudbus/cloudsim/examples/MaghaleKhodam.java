package org.cloudbus.cloudsim.examples;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

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
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.eicb.CloudletSchedulerEicb;
import org.cloudbus.cloudsim.eicb.activity;
import org.cloudbus.cloudsim.lists.HostList;
import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerDatacenterBroker;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G5Xeon3075;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import com.sun.corba.se.spi.orbutil.fsm.State;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.*;





/**
 * A simple example showing how to create a data center with one host and run one cloudlet on it.
 */
public class MaghaleKhodam {
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList,cloudletListTest;
	/** The vmlist. */
	private static List<Vm> vmlist;

	
	
	/**
	 * Creates main() to run this example.
	 *
	 * @param args the args
	 */	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		SimpsonIntegrator si=new SimpsonIntegrator();
		//final double result = si.integrate(50, x -> x, 1, 3);
		/*
		 * final double result = si.integrate(50, new UnivariateFunction() {
		 * 
		 * @Override public double value(double x) { return value2(x); } }, 1, 3);
		 * Log.printLine(result + " integral result");
		 */		
		Log.printLine("Starting CloudSimExample1...");			

		try {
			// First step: Initialize the CloudSim package. It should be called before creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
 			boolean trace_flag = false; // trace events

			CloudSim.init(num_user, calendar, trace_flag);

		    PowerDatacenter datacenter0 = (PowerDatacenter)createDatacenter("Datacenter_0");


			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();
						
			vmlist = new ArrayList<Vm>();

			// VM description
			int vmid = 0;
			int mips =900;
			int pesNumber = 1; // number of cpus
			
			
			//Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerEicb());
			//Vm vm2 = new Vm(vmid+1, brokerId, 400, pesNumber, ram, bw, size, vmm, new CloudletSchedulerEicb());			
			// add the VM to the vmList
			
		    //vmlist.add(vm);
			//vmlist.add(vm2);
			
			//4 vm for 1860 
			createVms(brokerId, mips,1,pesNumber);
			 
			mips=1330;
			//4 vm for 2660 
			/*
			 * for (int i = 4; i < 8; i++) { Vm vm = new Vm(i, brokerId, mips, pesNumber,
			 * ram, bw, size, vmm, new CloudletSchedulerEicb());
			 * vm.setHost(datacenter0.getHostList().get(1)); vmlist.add(vm); }
			 */
			 							
			// submit vm list to the broker
			broker.submitVmList(vmlist);
			

	
			cloudletList = new ArrayList<Cloudlet>();
									
			Random rand = new Random();

			// Cloudlet properties
			int id = 0;
			long length = 500;
			long fileSize = 0;
			long outputSize = 0; 
			
			boolean acivityType=false;
			int groupingChar=1;
			int arrivingTime=1;
			int dealine=120;					
			
			UtilizationModel fullUtilModel = new UtilizationModelFull();					
			UtilizationModel nullutilizationModel = new UtilizationModelNull();
						
			workloadFromFile(brokerId, pesNumber, fileSize, outputSize, fullUtilModel, nullutilizationModel);
			
			/* Random 500 cloudlets
			 * for (int i = 0; i < 500; i++) { //rand.nextInt(100)+1; //random between
			 * [1-100] activity ac=new activity(i,rand.nextInt(500)+1,1, fileSize,
			 * outputSize, fullUtilModel, nullutilizationModel,
			 * nullutilizationModel,rand.nextBoolean() ,rand.nextInt(10)+1,0,6);
			 * ac.setUserId(brokerId); cloudletList.add(ac); }
			 */
			
			broker.submitCloudletList(cloudletList);
			
			CloudSim.startSimulation();


			CloudSim.stopSimulation();
			
			
			List<Object> energyList=new ArrayList();
			
			//final double result = si.integrate(50, new UnivariateFunction() {
//	        @Override public double value(double x) {
//	            return x;
//	        }
//	    },0, 3.01);
			
			for (int i = 0; i < datacenter0.getHostList().size(); i++) {
				Host host= datacenter0.getHostList().get(i);
				List<HostStateHistoryEntry> stateList=((HostDynamicWorkload)host).getStateHistory();
				
				final double energy = si.integrate(50, new UnivariateFunction() {
			        @Override public double value(double time) {
						return ((PowerHost)host).getEnergyChing_Hsien(time);
			        }
			    },stateList.get(0).getTime(), stateList.get(stateList.size()-1).getTime());
				
				 Log.printLine("Total Energy for host "+i+" :  "+energy);

				
				/*
				 * for (int j = 0; j < stateList.size(); j++) {
				 * Log.printLine("Energy history for host "+i+" : at time "+stateList.get(j).
				 * getTime()+" is : " +stateList.get(j).getEnegry()); }
				 */												
			}
			
			
			
			//Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			printCloudletList(newList);

			Log.printLine("CloudSimExample1 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}
	
	public static double value2(double x) {
        return x;
	}

	private static void createVms(int brokerId, int mips, int numberofVms,int pesNumber) {
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		
		String vmm = "Xen"; // VMM name
		
		
		 for (int i = 0; i < numberofVms; i++) { 
			 Vm vm = new Vm(i, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerEicb());				
			 //vm.setHost(datacenter0.getHostList().get(0));
			 vmlist.add(vm); 
		 }
	}


	private static void workloadFromFile(int brokerId, int pesNumber, long fileSize, long outputSize,
			UtilizationModel fullUtilModel, UtilizationModel nullutilizationModel)
			throws IOException, FileNotFoundException {
		try (BufferedReader br = new BufferedReader(new FileReader("c:/workloadlist.txt"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	Log.printLine(line);
		    	List<String> splited = Arrays.asList(line.split(","));
		    	Log.printLine(splited.get(0));
				
				  activity ac=new activity(Integer.parseInt(splited.get(0)),Integer.parseInt(splited.get(1))
						  , pesNumber, fileSize, outputSize, fullUtilModel, nullutilizationModel,
				  nullutilizationModel,Boolean.parseBoolean(splited.get(2)) ,
				  Integer.parseInt(splited.get(3)),Integer.parseInt(splited.get(4)),Integer.parseInt(splited.get(5))); 
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
	//private static PowerDatacenter createDatacenter(String name) {
	private static Datacenter createDatacenter(String name) {

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		// our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();
		//G4 
		int mips = 1860;

		// 3. Create PEs and add these into a list.
		//dual core
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList.add(new Pe(1, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

		// 4. Create Host with its id and list of PEs and add them to the list
		// of machines
		/* commented by Hamed Akrami
		int hostId = 0;
		int ram = 2048; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000;
		*/
		
		// Added by Hamed Akrami
		// HP ProLiant ML110 G4
		int hostId = 0;
		int ram = 4096; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000;
		
		hostList.add(
				new PowerHost(
				//new Host(
					hostId,
					new RamProvisionerSimple(ram),
					new BwProvisionerSimple(bw),
					storage,
					peList,
					new VmSchedulerTimeShared(peList),
					new PowerModelSpecPowerHpProLiantMl110G4Xeon3040() 
				)
			); // This is our machine
		
		// HP ProLiant ML110 G5 
		// with same memory
		hostId = 1;			
		
		//clean peList
		peList = new ArrayList<Pe>();
		//added dual core pe for G5
		mips=2660;
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList.add(new Pe(1, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating


		hostList.add(
				new PowerHost(
					hostId,
					new RamProvisionerSimple(ram),
					new BwProvisionerSimple(bw),
					storage,
					peList,
					new VmSchedulerTimeShared(peList),
				new PowerModelSpecPowerHpProLiantMl110G5Xeon3075() 
				)
			); // This is our machine
		
		
		/*
		// Added by Hamed Akrami
		// HP ProLiant ML110 G4
		hostId = 2;
		ram = 16384; // host memory (MB)				
		
		
		hostList.add(
			new Host(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw),
				storage,
				peList,
				new VmSchedulerTimeShared(peList)
			)
		); // This is our machine
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

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);
				
		
		

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			//for power datacenter need to set scheduling interval 
			
			datacenter = new PowerDatacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		
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
			broker = new PowerDatacenterBroker("Broker");
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
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}
}