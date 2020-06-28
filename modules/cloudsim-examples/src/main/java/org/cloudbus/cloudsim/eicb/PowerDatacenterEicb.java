package org.cloudbus.cloudsim.eicb;

import java.util.List;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.power.PowerDatacenter;

public class PowerDatacenterEicb extends PowerDatacenter {

	public PowerDatacenterEicb(String name, DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval)
			throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
		// TODO Auto-generated constructor stub
	}
	public void addHost()
	{
		
		Object x= getHostList().get(0).getClass();								
		this.getHostList().add((Host) x);
		
		
	}

}
