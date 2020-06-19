/**
 * 
 */
package org.cloudbus.cloudsim.eicb;

import java.time.Clock;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;

/**
 * @author Hamed
 *
 */
public class PowerVmAllocationPolicyEicbThreshold extends PowerVmAllocationPolicyMigrationAbstract {
	
	private double utilizationThreshold = 0.7;
	
	public PowerVmAllocationPolicyEicbThreshold(
			List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy);
		setUtilizationThreshold(utilizationThreshold);
	}
	
	@Override
	protected boolean isHostOverUtilized(PowerHost host) {
		addHistoryEntry(host, getUtilizationThreshold());
		double totalRequestedMips = 0;
		
		for (Vm vm : host.getVmList()) {
			List<ResCloudlet> resList =vm.getCloudletScheduler().getCloudletExecList();
			for(ResCloudlet res:resList) {
				Cloudlet cl= res.getCloudlet();
				long clLen= cl.getCloudletLength();
				long tot=cl.getCloudletTotalLength();
				long p= res.getCloudletLength();
			}
			totalRequestedMips += vm.getTotalUtilizationOfCpu(CloudSim.clock());
			
		}
		double utilization = totalRequestedMips / host.getTotalMips();
		return utilization > getUtilizationThreshold();
	}
	
	
	protected void setUtilizationThreshold(double utilizationThreshold) {
		this.utilizationThreshold = utilizationThreshold;
	}
	
	protected double getUtilizationThreshold() {
		return utilizationThreshold;
	}
}
