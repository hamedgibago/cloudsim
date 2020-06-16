package org.cloudbus.cloudsim.eicb;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

import com.oracle.webservices.internal.api.message.PropertySet.Property;

public class activity extends Cloudlet {
//public class activity extends ResCloudlet {
	
	/**
	 * false means Normal activity and true means Batch activity
	 */
	public boolean acivityType=false;
	
	  /**
     * Grouping characteristic of activity 
     * which is between 1 to 100
     */
	public int groupingChar=1;
	
	 /**
     * Arriving Time of activity 
     * which is between 1 to 9
     */
	public int arrivingTime=1;
	
	 /**
     * Deadline of activity 
     * which is between 120 to 180
     */	
	public int deadline=120;	
	
	public int getDeadline() {
		return deadline;
	}
	
	/*
	 * public activity(int cloudletId, long cloudletLength, int pesNumber, long
	 * cloudletFileSize, long cloudletOutputSize, UtilizationModel
	 * utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel
	 * utilizationModelBw) {
	 * 
	 * super(cloudletId, cloudletLength, pesNumber, cloudletFileSize,
	 * cloudletOutputSize, utilizationModelCpu, utilizationModelRam,
	 * utilizationModelBw);
	 * 
	 * }
	 * 
	 * public activity(int cloudletId, long cloudletLength, int pesNumber, long
	 * cloudletFileSize, long cloudletOutputSize, UtilizationModel
	 * utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel
	 * utilizationModelBw, boolean record, List<String> fileList) {
	 * super(cloudletId, cloudletLength, pesNumber, cloudletFileSize,
	 * cloudletOutputSize, utilizationModelCpu, utilizationModelRam,
	 * utilizationModelBw, record, fileList);
	 * 
	 * }
	 * 
	 * public activity(int cloudletId, long cloudletLength, int pesNumber, long
	 * cloudletFileSize, long cloudletOutputSize, UtilizationModel
	 * utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel
	 * utilizationModelBw, List<String> fileList) { super(cloudletId,
	 * cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize,
	 * utilizationModelCpu, utilizationModelRam, utilizationModelBw, fileList); }
	 * 
	 * public activity(int cloudletId, long cloudletLength, int pesNumber, long
	 * cloudletFileSize, long cloudletOutputSize, UtilizationModel
	 * utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel
	 * utilizationModelBw, boolean record) { super(cloudletId, cloudletLength,
	 * pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu,
	 * utilizationModelRam, utilizationModelBw, record); }
	 */    
    public activity(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw,boolean acivityType,int groupingChar,int arrivingTime,int dealine) {

        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw);

        this.acivityType=acivityType;
        this.groupingChar=groupingChar;
        
        this.arrivingTime=arrivingTime;        
        setSubmissionTime(arrivingTime);
        
        this.deadline=dealine;        
    }
}
