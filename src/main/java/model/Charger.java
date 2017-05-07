package model;

import java.io.Serializable;

import model.dayphases.Activity;

/**
 * @author marc
 * Voltage in V, Power in kW and Current in Ampere
 */

public class Charger implements Serializable{
	
	private static final long serialVersionUID = 877210092117349155L;

	private String chargerId;
	private ChargerType chargerType;

	public Charger(String chargerId, ChargerType chargerType) {

		setChargerId(chargerId);
		setChargerType(chargerType);
		
	}
	
	/**
	 * Updates the chargeStatus according to runModel time and the car.
	 * 
	 * @param car Car Object
	 * @param time The time in minutes the car instance will be charged
	 */
	public void chargeCar(Car car, int time, Activity acticity){
				
		double charge = car.getChargeStatus() + chargerType.getChargerPower() * time/60;		
		car.setChargeStatus(charge, acticity);
		
	}

	public String getChargerId() {
		return chargerId;
	}

	public void setChargerId(String chargerId) {
		this.chargerId = chargerId;
	}

	public double getPower() {
		return chargerType.getChargerPower();
	}


	public ChargerType getChargerType() {
		return chargerType;
	}

	public void setChargerType(ChargerType chargerType) {
		this.chargerType = chargerType;
	}

	@Override
	public String toString() {
		return "charger with " + chargerType.getChargerPower() + " kW power of type: " + this.getChargerType().getChargerType();
	}
	
	


}
