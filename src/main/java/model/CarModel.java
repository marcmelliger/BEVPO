package model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import utils.dataAnalysisUtils;

public class CarModel implements Serializable {


	private static final long serialVersionUID = 5759630357667532643L;

	public static Map<Integer, CarModel> allCarModels;
	private static boolean calculationBasedOnRange = false;
	public static boolean probabilisticConsumption = false; 
	private static double chargerLossesFactor;
	


	private int carModelID;
	private String modelName;
	private int modelYear;
	
	/**
	 * Max. range according to range estimation list in km
	 */
	private double range;
	
	/**
	 * Max Capacity of Battery in kWh
	 */
	private double batteryCapacity;
	
	/**
	 * EnergyConsumption of the car in kWh per km
	 */
	private double energyConsumption;

	private String[] outletTypes;
	private int price;


	
	public static double defaultEnergyConsumption = 0.5;
	public static String[] defaultOutletTypes = {"single"}; 
	public static double defaultRange = 150;
	public static double defaultBatteryCapacity= 20;
	
	public CarModel() {
		
		batteryCapacity = defaultBatteryCapacity;
		energyConsumption = defaultEnergyConsumption;
		outletTypes = defaultOutletTypes;
		range = defaultRange;
		
	}
	
	public int getCarModelID() {
		return carModelID;
	}

	public void setCarModelID(int carModelID) {
		this.carModelID = carModelID;
	}

	public double getRange() {
		return range;
	}



	public void setRange(double range) {
		this.range = range;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public int getModelYear() {
		return modelYear;
	}

	public void setModelYear(int modelYear) {
		this.modelYear = modelYear;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public double getEnergyConsumption() {
		
		// Probabilistic model
		
		if(probabilisticConsumption){
			double randGaus = dataAnalysisUtils.random().nextGaussian();
			double sd = energyConsumption * 0.1;
			double mean = energyConsumption;
			
			double propConsumption = randGaus * sd + mean;
			return propConsumption;
					
		}
		
		
		return energyConsumption;
	
	}



	public void setEnergyConsumption(double energyConsumption) {
		this.energyConsumption = energyConsumption;
	}


	public static boolean isCalculationBasedOnRange() {
		return calculationBasedOnRange;
	}

	/**
	 * Set's if the calculation should be based only on the energy consumption data or the range data.
	 * @param calculationBasedOnRange 
	 */
	public static void setCalculationBasedOnRange(boolean calculationBasedOnRange) {
		CarModel.calculationBasedOnRange = calculationBasedOnRange;
	}

	public static double getChargerLossesFactor() {
		return chargerLossesFactor;
	}

	public static void setChargerLossesFactor(double chargerLossesFactor) {
		CarModel.chargerLossesFactor = chargerLossesFactor;
	}

	public String[] getOutletTypes() {
		return outletTypes;
	}

	public void setOutletTypes(String[] outletTypes) {
		this.outletTypes = outletTypes;
	}

	public double getBatteryCapacity() {
		
		if(calculationBasedOnRange){
			double batteryCapacityCalc = this.getRange() * this.getEnergyConsumption() * getChargerLossesFactor() ;
			return batteryCapacityCalc;
		}
		
		return batteryCapacity;
	}

	public void setBatteryCapacity(double batteryCapacity) {
		this.batteryCapacity = batteryCapacity;
	}
	
	/**
	 * @param defaultEnergyConsumption Energy Consumption in kWh/km
	 */
	public static void setDefaultEnergyConsumption(double defaultEnergyConsumption) {
		CarModel.defaultEnergyConsumption = defaultEnergyConsumption;
	}


	/**
	 * @param defaultOutletType
	 */
	public static void setDefaultOutletType(String[] defaultOutletTypes) {
		CarModel.defaultOutletTypes = defaultOutletTypes;
	}


	/**
	 * @param defaultRange Range in km
	 */
	public static void setDefaultRange(double defaultRange) {
		CarModel.defaultRange = defaultRange;
	}


	/**
	 * @param defaultBatteryCapacity Capacity in kWh
	 */
	public static void setDefaultBatteryCapacity(double defaultBatteryCapacity) {
		CarModel.defaultBatteryCapacity = defaultBatteryCapacity;
	}
	
	@Override
	public String toString() {
		return this.getModelName() + " (" + this.getModelYear() + ")" + dataAnalysisUtils.printHashCode(this) + ": [batteryCapacity=" + dataAnalysisUtils.formatString("%.1f",this.getBatteryCapacity())
				+ " kWh, energyConsumption=" + energyConsumption + " kWh/km, range = " + dataAnalysisUtils.formatString("%.1f",this.getRange())
				+ ", outletTypes: " + Arrays.toString(this.getOutletTypes()) + "]";
	}

	

}
