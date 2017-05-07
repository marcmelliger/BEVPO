package objectanalysis;

import model.dayphases.Leg;

public class FailedLeg extends Leg {
	
	private static final long serialVersionUID = -2099802683005394931L;
	
	private double shareOfFailedLeg; 
	
	public FailedLeg(String legId, int startTime, int endTime, String activityType, double distance) {
		super(legId, startTime, endTime, activityType, distance);
		
	}

	public double getShareOfFailedLeg() {
		return shareOfFailedLeg;
	}

	public void setShareOfFailedLeg(double shareOfFailedLeg) {
		this.shareOfFailedLeg = shareOfFailedLeg;
	}
	
	

}
