package model.dayphases;

import manager.ManageActivities;
import model.Charger;
import model.ChargerType;
import utils.dataAnalysisUtils;

public class Activity extends DayPhaseImpl{

	private static final long serialVersionUID = 6982593041634907664L;

	private String address;
	private Charger charger; 

	private boolean hasCharger = false;

	public Activity(String activityId, int activityStartTime, int activityEndTime, String activityType) {	
		super(activityId, activityStartTime, activityEndTime, activityType);

	}


	public void addCharger(String chargerId, ChargerType chargerType){

		// Checks if home has parking spaces
		if(!hasChargerAtHome()){
			removeCharger();
			return;
		}


		charger = new Charger(chargerId, chargerType);
		hasCharger = true;
	}


	public String getAddress() {
		return address;
	}

	public Charger getCharger() {
		return charger;
	}


	public boolean hasCharger() {
		return hasCharger;
	}


	private boolean hasChargerAtHome(){

		// Checks if home has parking spaces
		if(ManageActivities.isConsiderHouseholdParkingSpaces()){
			if (getActivityType().equals("Home") || getActivityType().equals("HomeCarCharging")){
				// Also includes -99 and -98 for not knowing
				if(this.getPerson().getHousehold().getParkingSpaces() <= 0 ){
					// No parking at home

					// But check, if all home activities without parking won't get a charger 
					// Factor = 1 -> no change still false
					if(dataAnalysisUtils.random().nextDouble() > ManageActivities.getHouseholdParkingSpacesFactor()){
						// has still a charger
						return true;
					}
					// Still no charger
					return false;

				}
			}
		}
		// No home -> consider as has one.
		return true;
	}


	// Cars can only be parked at an activity
	public void parkCar(){

		// if there is a charger -> charge vehicle.
		if(hasCharger){

			charger.chargeCar(this.getCar(), this.getTime(), this);
		} else {

			this.getCar().setChargeStatus(this.getCar().getChargeStatus(), this);

		}


	}


	public void removeCharger(){
		charger = null;
		hasCharger = false;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCharger(Charger charger) {
		this.charger = charger;
	}

	public void setHasCharger(boolean hasCharger) {
		this.hasCharger = hasCharger;
	}


	@Override
	public String toString(boolean moreInfo) {

		return super.toString(true) +
				"\n   Activity Info: \n"
				+ " - " + ((hasCharger)? charger : "no charger") + "\n"
				+ ((this.getAddress()!= null) ? " - address = " + this.getAddress() + "\n": "");
		

	}





}
