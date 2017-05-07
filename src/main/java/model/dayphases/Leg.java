package model.dayphases;

import runmodel.RunAnalysis;
import utils.dataAnalysisUtils;

public class Leg extends DayPhaseImpl{
	
	private static final long serialVersionUID = 2791447302892834307L;

	private boolean personIsCoPassenger;

	private double distance;
	private double distanceDriven;
	
	private String addressStart;
	private String addressEnd;
	
	private String addressStartAlt;
	private String addressEndAlt;
	
	private boolean failedLeg;
	 
	
	
	public Leg(String legId){
		super(legId, 0, 0, "");
	}

	public Leg(String legId, int startTime, int endTime, String activityType, double distance) {
		super(legId, startTime, endTime, activityType);

		this.distance = distance; // need to transform to text
		this.setFailedLeg(false);
		
		personIsCoPassenger = false;

	}
	
	/**
	 * Copy the Leg with new LegId
	 * @param legId
	 * @param leg
	 */
	public Leg(String legId, Leg leg){
		this(legId, leg.getStartTime(), leg.getEndTime(), leg.getActivityType(), leg.getDistance());
		
		this.personIsCoPassenger = leg.personIsCoPassenger();
		this.addressStart = leg.getAddressStart();
		this.addressEnd = leg.getAddressEnd();
		
		this.setCarTrip(leg.getCarTrip());
		this.setCar(leg.getCar());
		this.setPerson(leg.getPerson());
				
	}
	
	
	public void driveCar(){
		
		setDistanceDriven(this.getCar().driveCar(distance, this));
		
		if(this.getCar().hasEmptyBattery()){
			setFailedLeg(true);
		}
		
		
	}

	/**
	 * @return distance in km
	 */
	public double getDistance() {
		return distance;
	}


	public void setDistance(double distance) {
		this.distance = Math.abs(distance);
	}


	public double getDistanceDriven() {
		return distanceDriven;
	}

	public void setDistanceDriven(double distanceDriven) {
		this.distanceDriven = distanceDriven;
		
		// also add to CarTrip
		this.getCarTrip().addToTotalDrivenDistance(distanceDriven);
		
	}
	


	public String getAddressStart() {
		return addressStart;
	}
	
	
	/**
	 * @param type with 
	 * <p><ul>
	 * <li>1 = address,</li>
	 * <li>2 = address + country,</li> 
	 * <li>3 = addressAlt,</li>
	 * <li>4 = addressAlt + country,</li>
	 * <li>5 = address + addressAlt + country</li>
	 * </p></ul>
	 * @return String
	 */
	public String getAddressStart(int type) {
		
		String address = addressStart;
		
		switch(type){
		
		case 2: address = addressStart + ", " + RunAnalysis.countryNameLong; break;
		case 3: address = addressStartAlt; break;
		case 4: address = addressStartAlt + ", " + RunAnalysis.countryNameLong; break;
		case 5: address = addressStart + ", " + addressStartAlt + ", " + RunAnalysis.countryNameLong; break;
		}
		
		return address;
	}


	
	public String getAddressEnd() {
		return addressEnd;
	}
	
	/**
	 * @param type with 
	 * <p><ul>
	 * <li>1 = address,</li>
	 * <li>2 = address + country,</li> 
	 * <li>3 = addressAlt,</li>
	 * <li>4 = addressAlt + country,</li>
	 * <li>5 = address + addressAlt + country</li>
	 * </p></ul>
	 * @return AddresString
	 */
	public String getAddressEnd(int type) {
		
		String address = addressEnd;
		
		switch(type){
		
		case 2: address = addressEnd + ", " + RunAnalysis.countryNameLong; break;
		case 3: address = addressEndAlt; break;
		case 4: address = addressEndAlt + ", " + RunAnalysis.countryNameLong; break;
		case 5: address = addressEnd + ", " + addressEndAlt + ", " + RunAnalysis.countryNameLong; break;
		}
		
		return address;
	}

	
	public void setAddressStart(String addressStart) {
		this.addressStart = addressStart;
	}


	public void setAddressEnd(String addressEnd) {
		this.addressEnd = addressEnd;
	}


	public void setAddressStartAlt(String addressStartAlt) {
		this.addressStartAlt = addressStartAlt;
	}

	public void setAddressEndAlt(String addressEndAlt) {
		this.addressEndAlt = addressEndAlt;
	}
	
	public boolean isFailedLeg() {
		return failedLeg;
	}

	public void setFailedLeg(boolean failedLeg) {
		this.failedLeg = failedLeg;
	}

	public boolean personIsCoPassenger() {
		return personIsCoPassenger;
	}


	public void setPersonAsCoPassenger(boolean personIsCoPassenger) {
		this.personIsCoPassenger = personIsCoPassenger;
	}


	@Override
	public String toString(boolean moreInfo) {
		
		return super.toString(true) +
				"\n   Leg Info: \n"
				+ " - failed = " + this.isFailedLeg() + " \n"
				+ " - distance = " + dataAnalysisUtils.formatString("%.5f",distance) + " km, driven distance = " + dataAnalysisUtils.formatString("%.5f",distanceDriven) + " km \n"
				+ " - route = " + this.getAddressStart() + " to " + this.getAddressEnd() + " \n"  
				+ " - " + ((personIsCoPassenger) ? "Person is a co-passenger" : "Person is the main driver") + "\n";
				
	}


	





	
	

}
