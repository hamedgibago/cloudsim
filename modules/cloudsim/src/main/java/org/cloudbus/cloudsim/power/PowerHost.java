/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.HostDynamicWorkload;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

import sun.misc.VM;

/**
 * PowerHost class enables simulation of power-aware hosts.
 * 
 * <br/>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:<br/>
 * 
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 * 
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class PowerHost extends HostDynamicWorkload {

	/** The power model used by the host. */
	private PowerModel powerModel;

	/**
	 * Instantiates a new PowerHost.
	 * 
	 * @param id the id of the host
	 * @param ramProvisioner the ram provisioner
	 * @param bwProvisioner the bw provisioner
	 * @param storage the storage capacity
	 * @param peList the host's PEs list
	 * @param vmScheduler the VM scheduler
	 */
	public PowerHost(
			int id,
			RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner,
			long storage,
			List<? extends Pe> peList,
			VmScheduler vmScheduler,
			PowerModel powerModel) {
		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
		setPowerModel(powerModel);
	}

	/**
	 * Gets the power. For this moment only consumed by all PEs.
	 * 
	 * @return the power
	 */
	public double getPower() {
		return getPower(getUtilizationOfCpu());
	}

	/**
	 * Gets the current power consumption of the host. For this moment only consumed by all PEs.
	 * 
	 * @param utilization the utilization percentage (between [0 and 1]) of a resource that
         * is critical for power consumption
	 * @return the power consumption
	 */
	protected double getPower(double utilization) {
		double power = 0;
		try {
			power = getPowerModel().getPower(utilization);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return power;
	}

	/**
	 * Gets the max power that can be consumed by the host.
	 * 
	 * @return the max power
	 */
	public double getMaxPower() {
		double power = 0;
		try {
			power = getPowerModel().getPower(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return power;
	}

	/**
	 * Gets the energy consumption using linear interpolation of the utilization change.
	 * 
	 * @param fromUtilization the initial utilization percentage
	 * @param toUtilization the final utilization percentage
	 * @param time the time
	 * @return the energy
	 */
	public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time) {
		if (fromUtilization == 0) {
			return 0;
		}
		double fromPower = getPower(fromUtilization);
		double toPower = getPower(toUtilization);
		return (fromPower + (toPower - fromPower) / 2) * time;
	}
	
	/**
	 * Gets the energy consumption using Ching-Hsien Hsu of the utilization change.
	 * 
	 * @param fromUtilization the initial utilization percentage
	 * @param toUtilization the final utilization percentage
	 * @param time the time
	 * @return the energy
	 */
	public double getEnergyChing_Hsien(double fromUtilization, double toUtilization, double time) {
		
		//Cpu utilization of PM at time t
		double umt=0;
		//running vm list, but here we calculate vm list Not running vm list
		//TODO: should get Running vm list
		List<Vm> vmList= getVmList();
		
		//TODO: farz kardim baraye baraye sigmaye dovom ke rnl hast
		//tedad running vm ba tedad vm ha yeki hast
		//dar surati ke bayar rnl tedad running az noe n bashe ke dar time t
		//bar roye pm dar hale ejrast
		int rnl= vmList.size();
		
		
		for (int i = 0; i < rnl; i++) {
			umt+= vmList.get(i).getMips()/this.getPeList().get(0).getMips();
		}
		
		//The energy consumption rate at time instant t for the pm
		//equation 1
		//basic enery consumption
		double phi=getPower(0);
		double alpha=phi/7;
		double pemt = 0;

		if(umt==0)
			pemt= phi;
		else if (umt>0 && umt<=0.2)
			pemt= phi+alpha;
		else if (umt>0.2 && umt<=0.5)
			pemt=  phi+(3*alpha);
		else if (umt>0.5 && umt<=0.7)
			pemt=  phi+(5*alpha);
		else if (umt>0.7 && umt<=0.8)
			pemt= phi+(8*alpha);
		else if (umt>0.8 && umt<=0.9)
			pemt= phi+(11*alpha);
		else if (umt>0.9 && umt<=1)
			pemt=  phi+(12*alpha);
		
		return pemt;							
	}

	/**
	 * Sets the power model.
	 * 
	 * @param powerModel the new power model
	 */
	protected void setPowerModel(PowerModel powerModel) {
		this.powerModel = powerModel;
	}

	/**
	 * Gets the power model.
	 * 
	 * @return the power model
	 */
	public PowerModel getPowerModel() {
		return powerModel;
	}

}
