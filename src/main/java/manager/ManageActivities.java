package manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.stream.Collectors;

import analysis.Tests;
import geo.RouteLeg;
import geo.RoutedLeg;
import geo.TestPointOnLeg;
import geo.testpoints.ChargingStation;
import geo.testpoints.ExistingChargingStation;
import geo.testpoints.ServiceArea;
import geo.testpoints.TestPoint;
import model.Car;
import model.CarTrip;
import model.ChargerType;
import model.Person;
import model.dayphases.Activity;
import model.dayphases.ChargingActivity;
import model.dayphases.DayPhaseImpl;
import model.dayphases.Leg;
import runmodel.RunAnalysis;
import utils.ProbabilityItemCharger;
import utils.RandomSelector;
import utils.dataAnalysisUtils;

public class ManageActivities {
	
	private static int probabilityDenominator = 2;
	private static int probabilityDenominatorBeginning = 1;
	private static RandomSelector randomCharger;
	
	public static boolean useShareOfChargingStations = false;
	public static boolean useShareOfChargingStationsAtBeginning = false;
	public static boolean considerHouseholdParkingSpaces = false;
	public static boolean serviceAreasEnabled = false;

	
	/**
	 * householdParkingSpacesFactor: if 1 then all legs without parking space won't get a
	 * charger at the beginning. If 0.5, half ot f those whithout a parking space will get one.
	 */
	private static double householdParkingSpacesFactor;
	public static boolean serialiseTestPointsOnLegs;
	
	public static double scalingFactorThreshold;

	
	/**
	 * Generate activites out of legs.
	 * @param carTrip
	 */
	public static void addActivities(CarTrip carTrip){
				
		String currentLegId = "";
		String currentActivityId = "";
		String previousPurpose = "";
		Person previousPerson = null;
		int previousLegEndTime = 0; // Important to keep 0 for first activity of trip
		Car previousCar = null;
		
		// Loop through all legs. First add activities and then legs in between this and previous leg --> alway need previous leg.
		for(Leg leg: carTrip.getLegs()){
			
			currentLegId = leg.getDayPhaseID();
			
			
			// ----- Activity -----
			// First activity is probably at home, unless its purpose "go home" or time == 0
			// TODO: Assumption probably wrong, considering not all cars at home. 
			String purpose = leg.getActivityType();
			int legStartTime = leg.getStartTime();
			int legEndTime = leg.getEndTime();
			
			
			int ActivityStartTime;
			int ActivityEndTime;
			String ActivityPurpose;
			boolean addCar = true;
			//boolean firstActivity = false;
			
			// First activity is runModel -> add chargers
			if(carTrip.getActivities().isEmpty()){
				previousPurpose = getFirstChargingActivityPurpose(leg.getCar().getCarType());
			}
			
			ActivityStartTime = previousLegEndTime; // Is 0 at beginning -> init a top
			ActivityEndTime = legStartTime;
			ActivityPurpose = previousPurpose;
			
			previousLegEndTime = legEndTime;
			previousPurpose = purpose;
			previousPerson = leg.getPerson();
			
			currentActivityId = "a_" + currentLegId;
			
			Activity activity = new Activity(currentActivityId, ActivityStartTime, ActivityEndTime, ActivityPurpose);
			
			leg.setDayPhaseID("l_" + currentLegId); // Same ID format
			activity.setPerson(leg.getPerson());
			
			if(addCar){
				activity.setCar(leg.getCar());
				previousCar = leg.getCar();					
			}

			carTrip.addDayPhaseBefore(activity, leg);

		}
		
		// Add last Activity after Last Leg
		currentActivityId = "a_" + "last";
		String LastPurpose = previousPurpose;
		
		// Normally runModel until 1440, but not if leg is longer. plus 1 minute
		int lastActivityEndTime = (previousLegEndTime>1440) ? previousLegEndTime + 1 : 1440;
				
		Activity LastActivity = new Activity(currentActivityId, previousLegEndTime, lastActivityEndTime, LastPurpose);
		
		LastActivity.setCar(previousCar);
		LastActivity.setPerson(previousPerson);
		
		carTrip.addDayPhase(LastActivity);
		
	}
	
	private static String getFirstChargingActivityPurpose(String carType) {
		
		
		switch (carType){
			case "BusinessCar":
			case "RentalCar":
			case "CarsharingCar":
			case "UnknownCar":
			case "HomeCar":
			default:
				return carType + "Charging";
		}
		
	}
	
	/**
	 * Function to add charging stations in between activities.
	 * Splits up existing legs if charging station of charging stations List is on leg.
	 * Analysis is based on Google and should be chached after it has been requested from Google.
	 * 
	 * @param carTrip
	 */
	public static void addChargingStations(CarTrip carTrip){		
		
		
		if(RunAnalysis.georouteTrips){	
					
			
			if(RunAnalysis.replanTrips && RunAnalysis.tripsToReplan.contains(carTrip.getCarTripID())){
				RouteLeg.setReplan(true);
			} else {
				RouteLeg.setReplan(false);
			}
					
			
			LinkedList<Leg> listOfLegs = carTrip.getLegs();
			
			LinkedList<Leg> filteredList = listOfLegs
					.stream()
					.filter(p -> (!p.getAddressStart().equals(p.getAddressEnd()) 
							|| p.getAddressStart().equals("-97") 
							|| p.getAddressEnd().equals("-97")) )
					.collect(Collectors.toCollection(LinkedList::new));
			
			// required to keep references. Assuming ID is unique -> check
			HashMap<String, Leg> filteredListMap = new HashMap<>();
			// String for ID
			String addressString = "";
			for(Leg i : filteredList){
				filteredListMap.put(i.getDayPhaseID(), i);
				addressString += i.getAddressStart() + i.getAddressEnd();
			}

			
			if(filteredList.size() > 0){
				try {
					
					
					ArrayList<TestPoint> testPoints = new ArrayList<>(ChargingStation.allChargingStations.values());
					
					
					LinkedList<RoutedLeg> testPointsOnLegsResult = null;
					
					
					String uniqueID = carTrip.getHousehold().getHouseholdId() + "_"
					+ (addressString + filteredList.size()).hashCode() + "_"
					+ ManageDatafile.getChargingStationList().hashCode() + "_"
					+ RouteLeg.getTestRadius();
					
					String serFilename = RunAnalysis.basePathSer + 
							"TestPointsOnLegs" + File.separator + 
							RunAnalysis.getCountryNameShort() + File.separator + 
							RouteLeg.getTestRadius() + File.separator + 
							uniqueID  + ".ser";
					
					File file = new File(serFilename);
					
					if(!file.getParentFile().exists())
						file.getParentFile().mkdirs();

					if(serialiseTestPointsOnLegs  && file.exists()){

						boolean onlyGenerate = false;
						if(!onlyGenerate){
							try {

								FileInputStream fileIn = new FileInputStream(file);
								ObjectInputStream in = new ObjectInputStream(fileIn);

								SerObjects serObjects = (SerObjects)in.readObject();
								testPointsOnLegsResult = serObjects.testPointsOnLegs;

								Tests.debug("Loaded serialized data from " + serFilename);

								in.close();
								fileIn.close();


							} catch (IOException i) {
								i.printStackTrace();
								return;
							} catch(ClassNotFoundException c) {
								Tests.debug("testPointsOnLegs class not found");
								c.printStackTrace();
								return;
							}
						} 

					} else {

						if(RouteLeg.queryNr < RouteLeg.queryLimit){
							
							RouteLeg.logger.log(Level.FINE,"Route CarTrip " + carTrip.getCarTripID() + 
									" in iteration " + RunAnalysis.getMonteCarloIteration());
							testPointsOnLegsResult = RouteLeg.getTestPointsOnLegs(filteredList, testPoints);
							

							// Write to serFile
							if(serialiseTestPointsOnLegs && testPointsOnLegsResult != null){
								try {
									FileOutputStream fileOut = new FileOutputStream(file);
									ObjectOutputStream out = new ObjectOutputStream(fileOut);

									SerObjects serObjects = new SerObjects();
									serObjects.testPointsOnLegs = testPointsOnLegsResult;

									out.writeObject(serObjects);
									out.close();
									fileOut.close();

									Tests.debug("Serialized data is saved in " + serFilename);

								} catch(IOException i) {
									i.printStackTrace();
								}
							}
						} else {
							Tests.debug("queryLimit exceeded with " + RouteLeg.queryNr);
						}

					}
					

					for(RoutedLeg routedLeg : testPointsOnLegsResult){	
						
						if(!routedLeg.getTestPointsOnLeg().isEmpty()){
							
							Leg oldLeg = filteredListMap.get(routedLeg.getLegId());
							
							LinkedList<DayPhaseImpl> dayPhases = ManageActivities.splitLegByTestPoint(routedLeg, oldLeg);
							
							carTrip.replaceDayPhase(oldLeg, dayPhases);
						}
					}
					

				} catch (Exception e) {
					RouteLeg.logger.log(Level.SEVERE,"Exception while adding chargingstations for carTrip " + carTrip.getCarTripID() + ". CarTrip is ignored from model.",e);
					carTrip.setIgnoreCarTrip(true); 
				}
			}

		}
		
		

	}
	
	private static LinkedList<DayPhaseImpl> splitLegByTestPoint(RoutedLeg routedLeg, Leg oldLeg) {

		LinkedList<DayPhaseImpl> dayPhases = new LinkedList<DayPhaseImpl>();

		int i = 1;

		String addressPrev = oldLeg.getAddressStart(); 
		for(TestPointOnLeg testPointOnLeg : routedLeg.getTestPointsOnLeg()){
			

			if(belowScalingFactorThreshold(testPointOnLeg)){

				TestPoint testPoint = ChargingStation.allChargingStations.get(testPointOnLeg.getTestPointID());

				if(testPoint instanceof ChargingStation){
					ChargingStation chargingStation = (ChargingStation)testPoint;

					Leg newLeg = new Leg(oldLeg.getDayPhaseID() + "_" + Integer.toString(i) , oldLeg);
					dayPhases.add(newLeg);

					// Configure new Leg	
					//newLeg.setDistance(testPointOnLeg.getDistanceToPoint(!RouteLeg.isReplan()));
					newLeg.setDistance(testPointOnLeg.getDistanceToPoint(false));
					
					// Addresses
					String addressThisPoint = testPointOnLeg.getAddress();
					newLeg.setAddressStart(addressPrev);
					newLeg.setAddressEnd(addressThisPoint);					
					addressPrev = addressThisPoint;

					// Insert ChargingStation
					double timeToPoint = testPointOnLeg.getTimeToPoint();
					String 	activityId = "c_" + newLeg.getDayPhaseID();
					int 	activityStartTime = (int) (newLeg.getStartTime() + timeToPoint);
					int 	activityEndTime = activityStartTime;
					String 	activityType = "charging";

					ChargingActivity chargingActivity = 
							new ChargingActivity(activityId, activityStartTime, activityEndTime, 
									activityType,chargingStation);

					chargingActivity.setCar(newLeg.getCar());
					chargingActivity.setPerson(newLeg.getPerson());
					i++;

					dayPhases.add(chargingActivity);
				}


			}
		}
		
		
		// Last Leg or unchanged ?
		
		Leg newLeg = new Leg(oldLeg.getDayPhaseID() + "_" + Integer.toString(i) , oldLeg);
		
		newLeg.setAddressStart(addressPrev);
		newLeg.setAddressEnd(oldLeg.getAddressEnd());
		
		double lastDistance = routedLeg.getLastLeg().getDistanceToPoint(false);
		newLeg.setDistance(lastDistance);
		dayPhases.add(newLeg);

		return dayPhases;

	}

	private static boolean belowScalingFactorThreshold(TestPointOnLeg testPointOnLeg) {

		// Scaling: if google routed way is larger than scalingFactorThreshold, don't split
		
		RouteLeg.scalingFactorMap.put(testPointOnLeg.getLegID(), testPointOnLeg.getScalingFactor());
		double scalingFactor = testPointOnLeg.getScalingFactor();
		
		if(testPointOnLeg.getScalingFactor() < 1)
			scalingFactor = 1 / scalingFactor;
		
		if(scalingFactor > scalingFactorThreshold){
			return false;
		} else {
			RouteLeg.scalingFactorMapBelow.put(testPointOnLeg.getLegID(), testPointOnLeg.getScalingFactor());
			return true;

		}
		

	}

	/**
	 * @param activity
	 * @param totalShareFromMatrix If true then it is assumed that shares in matrix add up to 1. Allows for null returns.
	 * @return
	 */
	private static ChargerType selectRandomChargerType(Activity activity, Boolean totalShareFromMatrix){
		
		if(RunAnalysis.chargerTypesShare.containsKey(activity.getActivityType())){
			randomCharger = new RandomSelector(RunAnalysis.chargerTypesShare.get(activity.getActivityType()));
		} else {
			System.err.println("Check if the following purpose is in the charger matrix: " + activity.getActivityType().toString());
		}
		
		
		if(totalShareFromMatrix){
			randomCharger.setCustomTotalSum(100);
		} else {
			randomCharger.resetCustomTotalSum();
		}
		
		ProbabilityItemCharger item = (ProbabilityItemCharger)randomCharger.getRandom();
		
		if(item != null){
			String chargerType = item.getChargerType();
			return ChargerType.allChargerTypes.get(chargerType);
		} else {
			return null;
		}
		
		
	}
	
	public static void randomlyAssignCharger(Activity activity, boolean firstActivity){
		
		// Overall probability
		boolean hasCharger = true;
		boolean totalShareFromMatrix = true;
		
		if(!firstActivity){
			if(useShareOfChargingStations){
				hasCharger = dataAnalysisUtils.random().nextInt(probabilityDenominator)==0;
				totalShareFromMatrix = false;		
			}
		} else {
			if(useShareOfChargingStationsAtBeginning){
				hasCharger = dataAnalysisUtils.random().nextInt(probabilityDenominatorBeginning)==0;
				totalShareFromMatrix = false;
			}
		}
		
		// Get ChargerType
		ChargerType chargerType = selectRandomChargerType(activity, totalShareFromMatrix);
		
		if(hasCharger && chargerType != null){
			activity.addCharger(activity.getDayPhaseID() + "_charger", chargerType);
			
		} else {
			activity.removeCharger();
			
		}
	}
	
	/**
	 * Activate and deactivate charging activities, based on options
	 * @param  
	 */
	public static void processChargingActivities(){
		
		// Activate existing chargers
		// Activate Service Areas

		ChargingStation.allChargingStations.forEach((k,v) -> {
			if(v instanceof ExistingChargingStation){
				v.setActive(true);
			} else if(v instanceof ServiceArea){
				if(ManageActivities.serviceAreasEnabled)
					v.setActive(true);	
			} 
		}
		);
		
	}
	

	
	/**
	 * Determines the share of runModel stations at activities
	 * @param probability The probability that a runModel station gets assigned 
	 */
	public static void setShareOfChargingStations(double probability) {
		if(probability<=1){
			probabilityDenominator = (int) (1 / probability);
		}
		
	}

	/**
	 * Determines the share of runModel stations at the first activity
	 * @param probability The probability that a runModel station gets assigned to the first leg
	 */
	public static void setShareOfChargingStationsAtBeginning(double probability) {
		if(probability<=1){
			probabilityDenominatorBeginning = (int) (1 / probability);
		}		
	}
	
	public static void useShareOfChargingStations(boolean use){
		useShareOfChargingStations = use;
	}
	
	public static void useShareOfChargingStationsAtBeginning(boolean use){
		useShareOfChargingStationsAtBeginning = use;
	}

	public static boolean isConsiderHouseholdParkingSpaces() {

		
		return considerHouseholdParkingSpaces;
	}


	public static void setHouseholdParkingSpacesFactor(double parseDouble) {
		householdParkingSpacesFactor =  parseDouble;
		
	}

	public static double getHouseholdParkingSpacesFactor() {
		return householdParkingSpacesFactor;
	}
	
	
	

}


class SerObjects implements Serializable{
	
	private static final long serialVersionUID = 2756652311273525217L;

	public LinkedList<RoutedLeg> testPointsOnLegs = new LinkedList<RoutedLeg>();
	
	public SerObjects() {
		
	}
	
}
