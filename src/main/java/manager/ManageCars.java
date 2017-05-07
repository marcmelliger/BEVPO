package manager;

import model.CarModel;
import model.CarTrip;
import model.dayphases.Activity;
import model.dayphases.ChargingActivity;
import model.dayphases.DayPhase;
import model.dayphases.DayPhaseImpl;
import model.dayphases.Leg;
import runmodel.RunAnalysis;
import utils.ProbabilityItemCar;
import utils.RandomSelector;

// Helper function
public class ManageCars {



	/**
	 * Factor between 0 and 1 which is relevant for first Charge.
	 * 1 equals a runModel time equal to the time of the last activity
	 */
	public static double startOfChargeFactor;

	public static boolean fullFirstCharge;


	private static RandomSelector random;




	public static void setRandom(RandomSelector random) {
		ManageCars.random = random;
	}


	public static CarModel selectRandomCarModel() {

		if(random == null)
			random = new RandomSelector(RunAnalysis.carModelsShare);

		int carModelID = ((ProbabilityItemCar)random.getRandom()).getCarModelID();

		return CarModel.allCarModels.get(carModelID);

	}


	public static double getStartOfChargeFactor() {
		return startOfChargeFactor;
	}


	/**
	 * Sets the charge of cars before midnight.<br> 
	 * The respective function takes the last activites' time multiplied by this factor to approximate this time.
	 * @param startOfChargeFactor Factor between 0 and 1.
	 */
	public static void setStartOfChargeFactor(double startOfChargeFactor) {
		ManageCars.startOfChargeFactor = startOfChargeFactor;
	}


	private static void setFirstCharge(CarTrip carTrip){

		// Apply Initial charge to first activity
		Activity firstActivity = ((Activity)carTrip.getDayPhases().getFirst());

		if(!ManageCars.fullFirstCharge){

			// Assumption: Only charging until midnight (there could be higher times)		
			int lastTime = carTrip.getDayPhases().getLast().getStartTime(); 
			int time = 1440 - ((lastTime <= 1440) ? lastTime : 1440);

			// Assumption: "startOfChargeFactor" of time of last activity as runModel.
			int chargingTime = (int) Math.round(startOfChargeFactor * time);

			if(firstActivity.getCharger() != null)
				firstActivity.getCharger().chargeCar(firstActivity.getCar(), chargingTime, firstActivity);

		} else {
			firstActivity.getCar().setChargeStatus(firstActivity.getCar().getCarModel().getBatteryCapacity());

		}

	}


	public static void runCarTrips() {

		for(CarTrip carTrip: CarTrip.allCarTrips){

			if(!carTrip.isIgnoreCarTrip()){

				setFirstCharge(carTrip);

				for(DayPhase dayPhase: carTrip.getDayPhases()){
					// Hack, because carTrip is not always set?
					((DayPhaseImpl)dayPhase).setCarTrip(carTrip);

					if(!dayPhase.getCar().hasEmptyBattery()){
						if(dayPhase instanceof Activity){

							boolean parkAtActivity = true;

							if(dayPhase instanceof ChargingActivity){

								// Park if active and positive decision to park
								parkAtActivity = ((ChargingActivity) dayPhase).getChargingStation().isActive() && 
										((ChargingActivity) dayPhase).decideToCharge();

								if(parkAtActivity) 
									((ChargingActivity) dayPhase).adjustChargingTime(); 


							}

							if(parkAtActivity)
								((Activity) dayPhase).parkCar();

						} else if(dayPhase instanceof Leg){
							((Leg) dayPhase).driveCar();

						}

					} 

					// Empty Battery -> trip failed
					else {

						dayPhase.getCar().setChargeStatus(0, dayPhase);
						carTrip.setFailedTrip(true);
					}

				}

				// Add to failed and successful CarTrips
				if(carTrip.isFailedTrip()){
					CarTrip.failedCarTrips.add(carTrip);
				} else {
					CarTrip.successfullCarTrips.add(carTrip);
				}

			} 
		}

	}




}
