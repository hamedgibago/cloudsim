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
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerDatacenterBroker;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G5Xeon3075;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;



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
		Log.printLine("Starting CloudSimExample1...");			

		try {
			// First step: Initialize the CloudSim package. It should be called before creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
 			boolean trace_flag = false; // trace events

			/* Comment Start - Dinesh Bhagwat 
			 * Initialize the CloudSim library. 
			 * init() invokes initCommonVariable() which in turn calls initialize() (all these 3 methods are defined in CloudSim.java).
			 * initialize() creates two collections - an ArrayList of SimEntity Objects (named entities which denote the simulation entities) and 
			 * a LinkedHashMap (named entitiesByName which denote the LinkedHashMap of the same simulation entities), with name of every SimEntity as the key.
			 * initialize() creates two queues - a Queue of SimEvents (future) and another Queue of SimEvents (deferred). 
			 * initialize() creates a HashMap of of Predicates (with integers as keys) - these predicates are used to select a particular event from the deferred queue. 
			 * initialize() sets the simulation clock to 0 and running (a boolean flag) to false.
			 * Once initialize() returns (note that we are in method initCommonVariable() now), a CloudSimShutDown (which is derived from SimEntity) instance is created 
			 * (with numuser as 1, its name as CloudSimShutDown, id as -1, and state as RUNNABLE). Then this new entity is added to the simulation 
			 * While being added to the simulation, its id changes to 0 (from the earlier -1). The two collections - entities and entitiesByName are updated with this SimEntity.
			 * the shutdownId (whose default value was -1) is 0    
			 * Once initCommonVariable() returns (note that we are in method init() now), a CloudInformationService (which is also derived from SimEntity) instance is created 
			 * (with its name as CloudInformatinService, id as -1, and state as RUNNABLE). Then this new entity is also added to the simulation. 
			 * While being added to the simulation, the id of the SimEntitiy is changed to 1 (which is the next id) from its earlier value of -1. 
			 * The two collections - entities and entitiesByName are updated with this SimEntity.
			 * the cisId(whose default value is -1) is 1
			 * Comment End - Dinesh Bhagwat 
			 */
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			// Datacenters are the resource providers in CloudSim. We need at
			// list one of them to run a CloudSim simulation
		    PowerDatacenter datacenter0 = (PowerDatacenter)createDatacenter("Datacenter_0");
			//Datacenter datacenter0 = createDatacenter("Datacenter_0");

			// Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();
			
			

			// Fourth step: Create one virtual machine
			vmlist = new ArrayList<Vm>();

			// VM description
			int vmid = 0;
			int mips = 500;
			long size = 10000; // image size (MB)
			int ram = 512; // vm memory (MB)
			long bw = 1000;
			int pesNumber = 1; // number of cpus
			String vmm = "Xen"; // VMM name

			// create VM
			//Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
			//Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
			//Vm vm2 = new Vm(vmid+1, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
			
			Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerEicb());
			Vm vm2 = new Vm(vmid+1, brokerId, 400, pesNumber, ram, bw, size, vmm, new CloudletSchedulerEicb());			
			
			/*
			for (int i = 0; i < 4; i++) {
				Vm vm = new Vm(i, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerEicb());
				vmlist.add(vm);
			}
			*/
			

			// add the VM to the vmList
		    vmlist.add(vm);
			//vmlist.add(vm2);

			// submit vm list to the broker
			broker.submitVmList(vmlist);			

			// Fifth step: Create one Cloudlet
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

			
			
			UtilizationModel stocasticUtilModel = new UtilizationModelStochastic(100000); //new UtilizationModelFull();
			
			UtilizationModel fullUtilModel = new UtilizationModelFull();
			
			//((UtilizationModelStochastic)stocasticUtilModel).setRandomGenerator(randomGenerator);
			
			UtilizationModel nullutilizationModel = new UtilizationModelNull();
			
			
			workloadFromFile(brokerId, pesNumber, fileSize, outputSize, fullUtilModel, nullutilizationModel);

										
//			for (int i = 1; i <= 5; i++) {
//				//if(i==1 || i==2)
//				if(i == 1)
//				{
//					acivityType=false; //bool value, false=normal, true=batching
//					groupingChar = 1; //random between [1-100]
//				}	
//				else if(i==5)
//				{
//					acivityType=true; //bool value, false=normal, true=batching
//					groupingChar = 3; //random between [1-100]
//				}
//				else
//				{
//					acivityType=true; //bool value, false=normal, true=batching
//					groupingChar = 2; //random between [1-100]
//				}
//				
//				////////////////////implementation maghale////////////////////////////
//				//length = rand.nextInt(100000-50000+1)+50000; //workload of activity
//				//acivityType=rand.nextBoolean(); //bool value, false=normal, true=batching
//				//groupingChar = rand.nextInt(100)+1; //random between [1-100]
//				////////////////////////
//				
//				arrivingTime = rand.nextInt(10)+1; //random between [1-9]
//				dealine= rand.nextInt(180-120+1)+120; //random between [120-180]
//				
//				activity ac=new activity(id, length, pesNumber, fileSize, outputSize, fullUtilModel,
//						nullutilizationModel, nullutilizationModel,acivityType,groupingChar,arrivingTime,dealine);
//				
//				ac.setUserId(brokerId);
//				//ac.setVmId(vmid);
//				
//				/*							
//				Cloudlet cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel,
//						nullutilizationModel, nullutilizationModel);
//				String s= Cloudlet.getStatusString(id);
//				cloudlet.setUserId(brokerId);
//				cloudlet.setVmId(vmid);
//				
//				cloudlet.setClassType(rand.nextInt(2));
//				
//				// add the cloudlet to the list
//				cloudletList.add(cloudlet);
//				*/										
//				cloudletList.add(ac);
//				id++;
//				
//			}
			
			/* test
			for (int i = 0; i < 10; i++) {
				length = rand.nextInt(100000-50000+1)+50000; //workload of activity
				acivityType=rand.nextBoolean(); //bool value, false=normal, true=batching
				groupingChar = rand.nextInt(3)+1; //random between [1-100]
				arrivingTime = rand.nextInt(10)+1; //random between [1-9]
				dealine= rand.nextInt(180-120+1)+120; //random between [120-180]
				
				activity ac=new activity(id, length, pesNumber, fileSize, outputSize, stocasticUtilModel,
						nullutilizationModel, nullutilizationModel,acivityType,groupingChar,arrivingTime,dealine);
				
				ac.setUserId(brokerId);
				cloudletListTest.add(ac);
			}			
			BatchProcess((List<activity>)(List<?>)cloudletListTest);
			
			*/
			
			
			//BatchProcess((List<activity>)(List<?>)cloudletList);
			
			// submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);
			
			
			
			//added by hamed for get status of all cloudlets
			cloudletList.forEach(cloudlet->{
				//Log.printLine("cloudlet list status: ");
				//Log.printLine(cloudlet.getCloudletStatusString());
				//Log.printLine(cloudlet.getClassType());
				//Log.printLine(String.format("type %s - grp %d - ",((activity)cloudlet).acivityType ,((activity)cloudlet).groupingChar));
				Log.printLine(String.format("(%d,%d,%s,%d,%d,%d)",
						cloudlet.getCloudletId(),cloudlet.getCloudletLength(),
						((activity)cloudlet).acivityType ,((activity)cloudlet).groupingChar,
						((activity)cloudlet).arrivingTime ,((activity)cloudlet).deadline
						));
				//Log.printLine(String.format("acivityType %s - groupingChar %d - arrivingTime %d - dealine %d",
						//((activity)cloudlet).acivityType,((activity)cloudlet).groupingChar,((activity)cloudlet).arrivingTime,((activity)cloudlet).dealine));
			});
			
			
			
			
			//added by hamed Algorithm1 Batch Process Activity Instances
			/*cloudletList.forEach(cloudlet->{
				cloudlet.getClassType()
			});*/
		
			

			// Sixth step: Starts the simulation
			CloudSim.startSimulation();
			
			//Log.printLine("test");

			CloudSim.stopSimulation();

			//Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			printCloudletList(newList);

			Log.printLine("CloudSimExample1 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
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
					//new VmSchedulerSpaceShared(peList),
					new PowerModelSpecPowerHpProLiantMl110G4Xeon3040() 
				)
			); // This is our machine
		
		// HP ProLiant ML110 G5 
		// with same memory
		hostId = 1;			
		/*
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
		*/
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