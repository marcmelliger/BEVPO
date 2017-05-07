package model.dayphases;

import geo.testpoints.ChargingStation;
import model.CarTrip;

public class ChargingActivity extends Activity {

	private static final long serialVersionUID = -313473072547116942L;

	private ChargingStation chargingStation = null;

	public static int maxTimesAtStation;
	public static int maxChargingTime;
	public static int enableChargingDecisionLevel = 0; 

	public ChargingActivity(String activityId, int activityStartTime, int activityEndTime, 
			String activityType, ChargingStation chargingStation) {
		super(activityId, activityStartTime, activityEndTime, activityType);

		this.setChargingStation(chargingStation);			
		this.addCharger(this.getDayPhaseID() + "_ch", chargingStation.getChargerType());

	}



	public void adjustChargingTime(){
		this.setEndTime(this.getStartTime() + determineChargingTime());
	}

	/**
	 * Decision if charging at the statio should take place
	 * @return
	 */
	public boolean decideToCharge(){

		// Only park twice
		if(enableChargingDecisionLevel > 0){

			// Either if lowerThreshold is reached: in km.
			// or If charge is not sufficient for the remaining distance
			double lowerThreshold = 30;
			double remainingRange = (this.getCar().getChargeStatus() / this.getCar().getCarModel().getEnergyConsumption()); 

			double relativeThreshold = 0.25;
			double percentageChargeLeft = this.getCar().getChargeStatus() / this.getCar().getCarModel().getBatteryCapacity(); //kWh

			double relativeThreshold2 = 0.8;


			double totalTripDistance = 0;
			// TODO: figure out why null pointer?
			if(this.getCarTrip() != null){
				CarTrip carTrip = this.getCarTrip();
				totalTripDistance = carTrip.getTotalDistance() ;
			}

			// logical order
			String message = null;
			String reason = null;
			if((remainingRange < lowerThreshold) && enableChargingDecisionLevel >= 1){
				message = "Charge below threshold of " + lowerThreshold + "km. Person decided to charge at charging station.";
				reason = "1";

			} else if ((percentageChargeLeft < relativeThreshold)  && enableChargingDecisionLevel >= 2){
				message =("Charge below relative threshold of " + relativeThreshold + "%. Person decided to charge at charging station.");
				reason = "2";


			} else if ((remainingRange < totalTripDistance) 
					&& (percentageChargeLeft < relativeThreshold2) 
					&& (this.getCar().getTimesAtChargingStation() < maxTimesAtStation) // TODO: make a variable
					&& enableChargingDecisionLevel >= 3){
				message = "Charge below total Trip distance of " + totalTripDistance + ". Person decided to charge at charging station.";
				reason = "3";
			}

			if(message!=null){
				
				ChargingActivityStats.listOfChargingEvents.add(
						new ChargingActivityStats(this, this.getCarTrip(), reason, message));
		
				this.getCar().addTimeAtChargingStation();
				return true;
			}
		}
		return false;
	}


	private int determineChargingTime(){

		double requiredCharge = this.getCar().getCarModel().getBatteryCapacity() - this.getCar().getChargeStatus(); //kWh 
		int timeRequired = (int) (requiredCharge / this.getCharger().getPower() * 60); // min time in minutes

		if(timeRequired > maxChargingTime){
			return maxChargingTime;
		} else {
			return timeRequired;
		}



	}

	@Override
	public String getAddress() {
		return chargingStation.getAddress();
	}


	public ChargingStation getChargingStation() {
		return chargingStation;
	}

	public String getName() {
		return chargingStation.getName();
	}

	private void setChargingStation(ChargingStation chargingStation) {
		this.chargingStation = chargingStation;
	}


	public String toString(boolean moreInfo) {

		if(this.chargingStation.isActive()){
			return super.toString(true)
					+ " - active charging station \n"
					+ ((this.getName() != null)? " - " + this.getName() + " \n": "");
		} else {
			return "\nInactive charging station\n";
		}

	}

}
