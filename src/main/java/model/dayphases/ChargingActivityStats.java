package model.dayphases;

import java.util.LinkedList;

import model.CarTrip;

public class ChargingActivityStats {
	
	private String message;
	private String reason;
	private CarTrip carTrip;
	private ChargingActivity chargingActivity;
	
	public static LinkedList<ChargingActivityStats> listOfChargingEvents = new LinkedList<>();

	public ChargingActivityStats(ChargingActivity chargingActivity, CarTrip carTrip, String reason, String message) {
		this.chargingActivity = chargingActivity;
		this.carTrip = carTrip;
		this.reason = reason;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String getReason() {
		return reason;
	}

	public CarTrip getCarTrip() {
		return carTrip;
	}

	public ChargingActivity getChargingActivity() {
		return chargingActivity;
	}
	
	
	
	

}
