package org.cloudbus.cloudsim.eicb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
//import java.util.Comparator;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHostUtilizationHistory;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class CloudletSchedulerEicb extends CloudletSchedulerSpaceShared {

	SimEntity entity = CloudSim.getEntity("Datacenter_0");
	PowerDatacenterEicb powerDs = (PowerDatacenterEicb) entity;
	// PowerDatacenterBroker broker =
	// (PowerDatacenterBroker)CloudSim.getEntity("Broker");

	// List<Host> hosts= powerDs.getHostList();

	@Override
	public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
		double total = getTotalUtilizationOfCpu(currentTime);
		setCurrentMipsShare(mipsShare);
		double timeSpam = currentTime - getPreviousTime(); // time since last update
		double capacity = 0.0;
		int cpus = 0;

		for (Double mips : mipsShare) { // count the CPUs available to the VMM
			capacity += mips;
			if (mips > 0) {
				cpus++;
			}
		}
		currentCpus = cpus;
		capacity /= cpus; // average capacity of each cpu

		// each machine in the exec list has the same amount of cpu
		for (ResCloudlet rcl : getCloudletExecList()) {
			// int vmid = rcl.getCloudlet().getVmId();
			// Cloudlet cl = rcl.getCloudlet();

			// List<Host> sortedHost=
			// powerDs.getHostList().stream().sorted().collect(Collectors.toList());

			// List<Host> hostls= powerDs.getHostList();
			//List<Host> hostls = new ArrayList<Host>();
			//hostls.addAll(powerDs.getHostList());
			// Collections.copy(hostls, powerDs.getHostList());

			// sort(PMs) by the CPU utilization in CRU
			/*
			 * try {
			 * 
			 * Collections.sort(hostls, new java.util.Comparator<Host>() { public int
			 * compare(Host h1, Host h2) { // return
			 * h1.getResourceUsage().compareTo(h2.getResourceUsage()); return
			 * Double.compare(h1.getResourceUsage(), h2.getResourceUsage()); } }); } catch
			 * (Exception e) { Log.printLine(); Log.printLine(e); }
			 */

			// ((PowerDatacenterEicb)powerDs).addHost();

			/*
			 * Vm selectedVm= powerDs.getVmList().stream().filter(v->vmid==v.getId())
			 * .findFirst().orElse(null); int selectedHostId= selectedVm.getHost().getId();
			 * 
			 * Host selectedHost=powerDs.getHostList().stream().filter(h->h.getId()==
			 * selectedHostId) .findFirst().orElse(null);
			 */

			// if(cl.getCloudletLength()+total<=)

			// for (Host host : hostls) {

			// if(rcl.getCloudlet().getCloudletLength()+host.getAvailableMips()+
			// <=)
			// }

			/*
			 * List<Host> hostls= powerDs.getHostList(); List<Double> dpm=new
			 * ArrayList<Double>(); int i = 0; for (Host host : hostls) { for (Vm vm :
			 * host.getVmList()) { double tmp = 0; try { tmp = dpm.get(i); } catch
			 * (Exception e) { // TODO: handle exception }
			 * 
			 * tmp += vm.getMips() / host.getTotalMips(); try { dpm.set(i, tmp); } catch
			 * (Exception e) { dpm.add(tmp); } } i++; }
			 */

			/*
			 * if(rcl.setMachineAndPeId(machineId, peId);)
			 * super.getTotalCurrentAvailableMipsForCloudlet(rcl, mipsShare)
			 */

			rcl.updateCloudletFinishedSoFar((long) (capacity * timeSpam * rcl.getNumberOfPes() * Consts.MILLION));
			// rcl.getCloudlet().getVmId()

			// if(rcl.getCloudlet().getWaitingTime()>0)
			// {
			// System.out.println("waiting time: " + rcl.getCloudlet().getWaitingTime());
			// }
		}

		// no more cloudlets in this scheduler
		if (getCloudletExecList().size() == 0 && getCloudletWaitingList().size() == 0) {
			setPreviousTime(currentTime);
			return 0.0;
		}

		// update each cloudlet
		int finished = 0;
		List<ResCloudlet> toRemove = new ArrayList<ResCloudlet>();
		for (ResCloudlet rcl : getCloudletExecList()) {
			// finished anyway, rounding issue...
			if (rcl.getRemainingCloudletLength() == 0) {
				toRemove.add(rcl);
				cloudletFinish(rcl);
				finished++;
			}
		}
		getCloudletExecList().removeAll(toRemove);

		manageWaitingList(currentTime);

		// added by hamed
		// check if there where no cloudlets to process
		if (getCloudletExecList().size() == 0) {
			return 0;
		}

		toRemove.clear();

		// estimate finish time of cloudlets in the execution queue
		double nextEvent = Double.MAX_VALUE;
		for (ResCloudlet rcl : getCloudletExecList()) {
			double remainingLength = rcl.getRemainingCloudletLength();
			double estimatedFinishTime = currentTime + (remainingLength / (capacity * rcl.getNumberOfPes()));
			if (estimatedFinishTime - currentTime < CloudSim.getMinTimeBetweenEvents()) {
				estimatedFinishTime = currentTime + CloudSim.getMinTimeBetweenEvents();
			}
			if (estimatedFinishTime < nextEvent) {
				nextEvent = estimatedFinishTime;
			}
		}

		setPreviousTime(currentTime);
		return nextEvent;
	}

	private void manageWaitingList(double currentTime) {
		List<Integer> gcToBatch = new ArrayList<Integer>(); // list of Grouping char to send to batch process
		List<ResCloudlet> toRemove = new ArrayList<ResCloudlet>();
		// for each finished cloudlet, add a new one from the waiting list
		if (!getCloudletWaitingList().isEmpty()) {
			int groupingChar = ((activity) ((getCloudletWaitingList().iterator().next()).getCloudlet())).groupingChar;
			// TODO: gharar shod inja list cloudlet ro ke filter konam bar asase
			// grouingchar va hameye unayee ke tu groupingchar yeksan gharar darand ro
			// beferestam baraye ejra
			// TODO: dar marhaleye bad bayad sharte waitingtime after deadline ro ezafe
			// konam
			for (ResCloudlet res : getCloudletWaitingList()) {
				activity ac = (activity) res.getCloudlet();
				// waiting time is over: Algorithm 1 line 6
				if (ac.getActivityWaitingTime(currentTime) >= ac.deadline || getCloudletExecList().size() == 0 // this
																												// added
																												// cause
																												// if
																												// there
																												// would
																												// not
																												// be
				// any cloudlet in exec list, nextEvent in updateVmProcessing will be a huge
				// number, and waiting list won't be executed

				) {
					if (!gcToBatch.contains(ac.groupingChar)) {
						gcToBatch.add(ac.groupingChar);
					}
				}
			}
		}

		for (Integer gc : gcToBatch) {
			toRemove.addAll(getWaitingToExec(gc));
		}

		getCloudletWaitingList().removeAll(toRemove);

		for (ResCloudlet res : toRemove) {
			res.setCloudletStatus(Cloudlet.INEXEC);
			getCloudletExecList().add(res);
		}

		/*
		 * 
		 * res.setCloudletStatus(Cloudlet.INEXEC); getCloudletExecList().add(res);
		 * //getCloudletWaitingList().remove(res); // TODO: add cloudlets with same GC
		 * in waiting list to exec // list and remove from waiting list for (ResCloudlet
		 * resGroup : getCloudletWaitingList()) { if (((activity)
		 * resGroup.getCloudlet()).groupingChar == ((activity) res
		 * .getCloudlet()).groupingChar) { resGroup.setCloudletStatus(Cloudlet.INEXEC);
		 * getCloudletExecList().add(resGroup); // commented becuase of concurrent
		 * modification error // getCloudletWaitingList().remove(resGroup);
		 * 
		 * toRemove.add(res); }
		 */

		/*
		 * List<ResCloudlet> ls=getCloudletWaitingList().stream()
		 * .filter(cl->((activity)cl.getCloudlet()).groupingChar==groupingChar)
		 * .collect(Collectors.toList());
		 * 
		 * 
		 * List<activity> lsActivity=new ArrayList<activity>();
		 * 
		 * for (ResCloudlet res : getCloudletWaitingList()) {
		 * 
		 * lsActivity.add((activity)res.getCloudlet()); }
		 * 
		 * List<activity> Edd=
		 * lsActivity.stream().sorted(Comparator.comparing(activity::getDeadline))
		 * .collect(Collectors.toList());
		 */

		// commented by hamed
		/*
		 * for (int i = 0; i < finished; i++) { toRemove.clear(); for (ResCloudlet rcl :
		 * getCloudletWaitingList()) { if ((currentCpus - usedPes) >=
		 * rcl.getNumberOfPes()) { rcl.setCloudletStatus(Cloudlet.INEXEC); for (int k =
		 * 0; k < rcl.getNumberOfPes(); k++) { rcl.setMachineAndPeId(0, i); }
		 * getCloudletExecList().add(rcl); usedPes += rcl.getNumberOfPes();
		 * toRemove.add(rcl); break; } } getCloudletWaitingList().removeAll(toRemove); }
		 */
	}

	/**
	 * Gets list of a grouping characteristic (GC) of an activity and return list of
	 * activities in waiting list with same GC in waiting queue to add them into
	 * executing queue for batch process
	 * 
	 * @param grouping characteristic (GC)
	 * @return activity waiting time in waiting queue
	 */
	private List<ResCloudlet> getWaitingToExec(int groupingChar) {
		List<ResCloudlet> result = new ArrayList<ResCloudlet>();
		for (ResCloudlet res : getCloudletWaitingList()) {
			if (((activity) res.getCloudlet()).groupingChar == groupingChar)
				result.add(res);
		}
		return result;
	}

	@Override
	public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
		// it can go to the exec list
		// added by Hamed, if it is normal activity,
		// if its batchactivity, send it waiting queue
		// if ((currentCpus - usedPes) >= cloudlet.getNumberOfPes())
		// {
		if (!((activity) cloudlet).acivityType) {

			ResCloudlet rcl = new ResCloudlet(cloudlet);
			rcl.setCloudletStatus(Cloudlet.INEXEC);

			for (int i = 0; i < cloudlet.getNumberOfPes(); i++) {
				rcl.setMachineAndPeId(0, i);
			}

			getCloudletExecList().add(rcl);
			usedPes += cloudlet.getNumberOfPes();

		
		} else {
			ResCloudlet rcl = new ResCloudlet(cloudlet);
			rcl.setCloudletStatus(Cloudlet.QUEUED);
			getCloudletWaitingList().add(rcl);

			// TODO: waiting time of cloudlets should be set somewhere
			// TODO: checking same batch characteristic has not been set yet
			/*
			 * for (ResCloudlet resCloudlet : getCloudletWaitingList()) {
			 * resCloudlet.getCloudlet().getWaitingTime() .getWaitingTime();
			 * cloudletSubmit(resCloudlet.getCloudlet(),0);
			 * 
			 * }
			 */
			
			return 0.0;
		}
		// }

		// calculate the expected time for cloudlet completion
		double capacity = 0.0;
		int cpus = 0;
		for (Double mips : getCurrentMipsShare()) {
			capacity += mips;
			if (mips > 0) {
				cpus++;
			}
		}

		currentCpus = cpus;
		capacity /= cpus;

		// use the current capacity to estimate the extra amount of
		// time to file transferring. It must be added to the cloudlet length
		double extraSize = capacity * fileTransferTime;
		long length = cloudlet.getCloudletLength();
		length += extraSize;
		cloudlet.setCloudletLength(length);
		return cloudlet.getCloudletLength() / capacity;
	}

	private double curResUsage() {
		return 0;
	}

//	
//	private static List<activity> BatchProcess2(List<activity> WQ) {		
//		List<activity> PI = new ArrayList<activity>(); // pending instances				
//		boolean waitTimeOut = false;
//
//		for (activity a : WQ) {
//			if (a.getWaitingTime() >= a.dealine) {
//				// waitTimeOut.set(true);
//				waitTimeOut = true;
//				break;
//			}
//		}
//
//		HashMap<Integer, List<activity>> hashMap = new HashMap<Integer, List<activity>>();
//
//		for (activity a : WQ) {
//			if (!hashMap.containsKey(a.groupingChar)) {
//				List<activity> list = new ArrayList<activity>();
//				list.add(a);
//
//				hashMap.put(a.groupingChar, list);
//			} else {
//				hashMap.get(a.groupingChar).add(a);
//			}
//		}
//
//		List<activity> AEIQ = new ArrayList<activity>(); // waiting queue
//		/*
//		 * line 6-7 algorithm 1 just batch process if the waiting time of activity
//		 * instance in WQ is over and activity instances in WQ with same GC and count
//		 * more than 1
//		 */
//		if (waitTimeOut) {
//			for (Entry<Integer, List<activity>> h : hashMap.entrySet()) {
//				if (h.getValue().size() > 1)
//					AEIQ = BatchProcess(h.getValue());
//			}
//		}
//
//		return PI;
//	}

//	private static List<activity> BatchProcessMain(List<activity> AIQ) {
//		List<activity> PI = new ArrayList<activity>(); // pending instances
//		List<activity> WQ = new ArrayList<activity>(); // waiting queue
//		
//		/* commented because doing this outside of method
//		AIQ.forEach(a -> {
//			if (a.acivityType) // batch activity
//				WQ.add(a);
//			else
//				PI.add(a);
//		});
//		*/
//
//		/*
//		 * for test ArrayList<Boolean> ar=new ArrayList<Boolean>(); ar.add(true);
//		 * ar.add(false); ar.add(true);
//		 * 
//		 * 
//		 * for (boolean i:ar) { if(!i) { break; } }
//		 */
//		/*
//		 * ar.forEach(a->{ if(!a) { break; } });
//		 */
//
//		/*
//		 * WQ.forEach(a->{ if(a.getWaitingTime()>=a.dealine) { waitTimeOut.set(true);
//		 * break; } });
//		 */
//
//		// AtomicBoolean waitTimeOut=new AtomicBoolean(false);//=new boolean();
//		boolean waitTimeOut = false;
//
//		for (activity a : WQ) {
//			if (a.getWaitingTime() >= a.dealine) {
//				// waitTimeOut.set(true);
//				waitTimeOut = true;
//				break;
//			}
//		}
//
//		HashMap<Integer, List<activity>> hashMap = new HashMap<Integer, List<activity>>();
//
//		for (activity a : WQ) {
//			if (!hashMap.containsKey(a.groupingChar)) {
//				List<activity> list = new ArrayList<activity>();
//				list.add(a);
//
//				hashMap.put(a.groupingChar, list);
//			} else {
//				hashMap.get(a.groupingChar).add(a);
//			}
//		}
//
//		List<activity> AEIQ = new ArrayList<activity>(); // waiting queue
//
//		// test
//		//waitTimeOut = true;
//
//		/*
//		 * line 6-7 algorithm 1 just batch process if the waiting time of activity
//		 * instance in WQ is over and activity instances in WQ with same GC and count
//		 * more than 1
//		 */
//		if (waitTimeOut) {
//			for (Entry<Integer, List<activity>> h : hashMap.entrySet()) {
//				if (h.getValue().size() > 1)
//					AEIQ = BatchProcess(h.getValue());
//			}
//		}
//
//		return PI;
//	}

}
