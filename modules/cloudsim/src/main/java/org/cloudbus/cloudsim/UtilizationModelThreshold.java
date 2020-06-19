package org.cloudbus.cloudsim;

public class UtilizationModelThreshold implements UtilizationModel {

	private double threshold;
	
	public UtilizationModelThreshold(double threshold)
	{		
		this.threshold=threshold;
	}
	
	@Override
	public double getUtilization(double time) {	
		
		// TODO Auto-generated method stub
		return getThreshold();
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

}
