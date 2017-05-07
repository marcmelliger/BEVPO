package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import model.dayphases.DayPhase;
import utils.dataAnalysisUtils;


public class Car implements Serializable{
	
	private static final long serialVersionUID = -8327295730854820126L;

	public static ArrayList<Car> allCars = new ArrayList<Car>();
	
	private String carId;
	
	/**
	 * Current charge in kWh. 
	 */
	private double chargeStatus;

	/**
	 * True if battery is empty
	 */
	private boolean emptyBattery;
	
	/**
	 * Charge at dayPhase in kWh. 
	 */
	private LinkedHashMap<DayPhase, Double> dayPhaseChargeStatus;
	
	private String carType;
	private CarModel carModel;
	
	private int timesAtChargingStation = 0;



	public Car(String carId, CarModel carModel) {
		
		this.carId = carId;
		this.setCarModel(carModel);
		
		chargeStatus = 0;
		setEmptyBattery(false); // Need to be false, otherwise first charge would not work

		dayPhaseChargeStatus = new LinkedHashMap<DayPhase, Double>();
		
	}
	

	
	/**
	 * Drives and uncharges the car
	 * 
	 * @param distance Distance in km
	 * @param dayPhaseID
	 * @return The driven Distance which is either equal to the distance or if empty, as far
	 * 	as the car go.
	 */
	public double driveCar(double distance, DayPhase dayPhase){
								
		
		double chargeOld = chargeStatus;
		double chargeNew = chargeOld - carModel.getEnergyConsumption() * distance;

		
		setChargeStatus(chargeNew, dayPhase);
		
		if (hasEmptyBattery()){
			return (getChargeStatus() - chargeOld) / (- carModel.getEnergyConsumption());
		} else {
			return distance; 
		}

	}


	public String getCarId() {
		return carId;
	}



	public void setCarId(String carId) {
		this.carId = carId;
	}



	public double getChargeStatus() {
		return chargeStatus;
	}
	
	public void setChargeStatus(double chargeStatus){
		this.chargeStatus = chargeStatus;
	}

	public void setChargeStatus(double chargeStatus, DayPhase dayPhase) {
		
		// maximum charge reached
		setEmptyBattery(false);
		if (chargeStatus >= carModel.getBatteryCapacity()){
			chargeStatus = carModel.getBatteryCapacity();
		}  
		else if(chargeStatus < 0){
			chargeStatus = 0;
			setEmptyBattery(true);
			
		}
		
		this.chargeStatus = chargeStatus;
		addDayPhaseChargeStatus(dayPhase, chargeStatus);
		
	}



	public LinkedHashMap<DayPhase, Double> getDayPhaseChargeStatus() {
		return dayPhaseChargeStatus;
		
	}
	
	public void setDayPhaseChargeStatus(LinkedHashMap<DayPhase, Double> dayPhaseChargeStatus) {
		this.dayPhaseChargeStatus = dayPhaseChargeStatus;
		
	}
	
	private void addDayPhaseChargeStatus(DayPhase dayPhase, double chargeStatus) {
		dayPhaseChargeStatus.put(dayPhase, chargeStatus);
	}

	

	public boolean hasEmptyBattery() {
		return emptyBattery;
	}

	public void setEmptyBattery(boolean emptyBattery) {
		this.emptyBattery = emptyBattery;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}
	

	public CarModel getCarModel() {
		return carModel;
	}

	public void setCarModel(CarModel carModel) {
		this.carModel = carModel;
	}

	public void resetTimeAtChargingStation(){
		timesAtChargingStation = 0;
	}
	
	public int getTimesAtChargingStation() {
		return timesAtChargingStation;
	}



	public void addTimeAtChargingStation() {
		this.timesAtChargingStation++;
	}



	@Override
	public String toString() {
		return carId + dataAnalysisUtils.printHashCode(this) + ": [" + carModel.toString() + "]";
	}

	
	
}
