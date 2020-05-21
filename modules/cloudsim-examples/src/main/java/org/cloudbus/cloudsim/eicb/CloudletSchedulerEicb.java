package org.cloudbus.cloudsim.eicb;

import java.io.Console;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;

public class CloudletSchedulerEicb extends CloudletSchedulerSpaceShared {
	

	@Override
	public double updateVmProcessing(double currentTime, List<Double> mipsShare)
	{
		/*
		List<ResCloudlet> execlist= getCloudletExecList();
		List<ResCloudlet> failist= getCloudletFailedList();
		List<ResCloudlet> finishlist= getCloudletFinishedList();
		List<ResCloudlet> pauseclist= getCloudletPausedList();
		List<ResCloudlet> waitlist= getCloudletWaitingList();*/
		
		setCurrentMipsShare(mipsShare);
		//commented by hamed
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
			//if((int)currentTime==rcl.getCloudletArrivalTime())
							rcl.updateCloudletFinishedSoFar(
		                   (long) (capacity * timeSpam * rcl.getNumberOfPes() * Consts.MILLION));	
			
			//Log.printLine("waiting exec: " +rcl.getCloudletId() +" - " + rcl.getCloudlet().getWaitingTime()
				//	+ " - "+  (long) (capacity * timeSpam * rcl.getNumberOfPes() * Consts.MILLION));				
		}
		
		
		/*Log.printLine("exec size: " +getCloudletExecList().size());
		Log.printLine("waiting list size: " +getCloudletWaitingList().size());
							
		for (ResCloudlet rcl : getCloudletWaitingList()) {
			Log.printLine("waiting time: " +rcl.getCloudletId()+ " - " + rcl.getCloudlet().getWaitingTime());
		}
		*/
		
			
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

		// for each finished cloudlet, add a new one from the waiting list
		if (!getCloudletWaitingList().isEmpty()) {
 
			int groupingChar= ((activity)((getCloudletWaitingList().iterator().next()).getCloudlet())).groupingChar;
			//TODO: gharar shod inja list cloudlet ro ke filter konam bar asase
			//grouingchar va hameye unayee ke tu groupingchar yeksan gharar darand ro
			//beferestam baraye ejra
			//TODO: dar marhaleye bad bayad sharte waitingtime after deadline ro ezafe konam			
			List<ResCloudlet> ls=getCloudletWaitingList().stream()
					.filter(cl->((activity)cl.getCloudlet()).groupingChar==groupingChar)
					.collect(Collectors.toList());
			
			List<activity> lsActivity=new ArrayList<activity>();
			
			for (ResCloudlet res : getCloudletWaitingList()) {
				lsActivity.add((activity)res.getCloudlet());
			}
			
			List<activity> Edd= lsActivity.stream().sorted(Comparator.comparing(activity::getDeadline))
			.collect(Collectors.toList());
			
			//getCloudletWaitingList().sort(Comparator.comparing(activity::));
			
			for (int i = 0; i < finished; i++) {
				toRemove.clear();
				for (ResCloudlet rcl : getCloudletWaitingList()) {					
					if ((currentCpus - usedPes) >= rcl.getNumberOfPes()) {
						rcl.setCloudletStatus(Cloudlet.INEXEC);
						for (int k = 0; k < rcl.getNumberOfPes(); k++) {
							rcl.setMachineAndPeId(0, i);
						}
						getCloudletExecList().add(rcl);
						usedPes += rcl.getNumberOfPes();
						toRemove.add(rcl);
						break;
					}
				}
				getCloudletWaitingList().removeAll(toRemove);
			}
		}

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
	
	@Override
	public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
		// it can go to the exec list
		// added by Hamed, if it is normal activity,
		// if its batchactivity, send it waiting queue
		if (!((activity) cloudlet).acivityType) {
			ResCloudlet rcl = new ResCloudlet(cloudlet);		
			rcl.setCloudletStatus(Cloudlet.READY);

			for (int i = 0; i < cloudlet.getNumberOfPes(); i++) {
				rcl.setMachineAndPeId(0, i);
			}
			getCloudletExecList().add(rcl);
			usedPes += cloudlet.getNumberOfPes();
		}
		else {
			ResCloudlet rcl = new ResCloudlet(cloudlet);
			// rcl.setCloudletStatus(Cloudlet.QUEUED);
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
