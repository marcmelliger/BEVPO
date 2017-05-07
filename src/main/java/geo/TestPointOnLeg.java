package geo;

import java.io.Serializable;


public class TestPointOnLeg implements Serializable{


	private static final long serialVersionUID = -8856229982655812435L;

	private String legID;
	private String testPointID;

	private double distanceToPoint;
	private double timeToPoint;
	private String address;
	
	private double scalingFactor;
	
	
	public TestPointOnLeg(double distanceToPoint, String testPointID) {
		this.distanceToPoint = distanceToPoint;
		this.testPointID = testPointID;
	}
	
	public TestPointOnLeg(double distanceToPoint, String testPointID, String legID, double timeToPoint, double scalingFactor) {
		this(distanceToPoint, testPointID);
		this.timeToPoint = timeToPoint;
		this.legID = legID;
		this.scalingFactor = scalingFactor;
	}
	
	public TestPointOnLeg(String testPointID, String legID) {
		this.testPointID = testPointID;
		this.legID = legID;
	}



	public String getLegID() {
		return this.legID;
	}


	public double getDistanceToPoint(boolean scaled) {
		if(scaled){
			return distanceToPoint * scalingFactor;
		} else {
			return distanceToPoint;
		}
		
	}

	/**
	 * @return Time in minutes
	 */
	public double getTimeToPoint() {
		return timeToPoint;
	}


	public String getTestPointID() {
		return testPointID;
	}

	public double getScalingFactor() {
		return scalingFactor;
	}

	public void setScalingFactor(double scalingFactor) {
		this.scalingFactor = scalingFactor;
	}

	public void setDistanceToPoint(double distanceToPoint) {
		this.distanceToPoint = distanceToPoint;
	}

	public void setTimeToPoint(double timeToPoint) {
		this.timeToPoint = timeToPoint;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	



}
