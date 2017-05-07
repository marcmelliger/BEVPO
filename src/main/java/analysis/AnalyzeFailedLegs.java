package analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.TreeSet;

import geo.SerObjects;
import geo.StepOnLeg;
import model.Car;
import model.CarTrip;
import model.dayphases.Leg;
import runmodel.RunAnalysis;
import utils.dataAnalysisUtils;

public class AnalyzeFailedLegs {


	//private static HashSet<String> activities;
	public static boolean printCSVOutput;
	public static boolean printPointsCSV;

	public static void analysis() throws FileNotFoundException {

		if(printCSVOutput){
			exportShareOfFailedLegsToCSV(RunAnalysis.getScenarioName());
			AnalyzeChargingActivities.exportChargingActivitiesToCSV();
			exportRunInfo();
		}

		if(printPointsCSV)
			exportPointsOfFailedLegsToCSV(RunAnalysis.getScenarioName());


	}

	private static void exportRunInfo() {
		// Like failed trip share, successfull trips
		// for each MC iteration



	}

	/**
	 * Writes a matrix of shareOfFailedLegs in percent and km
	 * @throws Exception 
	 */
	public static void exportShareOfFailedLegsToCSV(String filename) throws FileNotFoundException{


		// need a HashSet of activities.
		int IterationNr = RunAnalysis.getMonteCarloIteration();


		File fileDistances = new File(RunAnalysis.basePathOutput + "scenarios/" + filename + "/distances_it" + IterationNr + ".csv");

		if(!fileDistances.getParentFile().exists())
			fileDistances.getParentFile().mkdirs();

		File fileShares = new File(RunAnalysis.basePathOutput + "scenarios/" + filename + "/shares_it" + IterationNr + ".csv");

		if(!fileShares.getParentFile().exists())
			fileShares.getParentFile().mkdirs();

		File fileLegs = new File(RunAnalysis.basePathOutput + "scenarios/" + filename + "/legs_it" + IterationNr + ".csv");

		if(!fileLegs.getParentFile().exists())
			fileLegs.getParentFile().mkdirs();
		
		File fileHouseholds = new File(RunAnalysis.basePathOutput + "scenarios/" + filename + "/households_it" + IterationNr + ".csv");

		if(!fileLegs.getParentFile().exists())
			fileLegs.getParentFile().mkdirs();

		PrintWriter outputDistances = new PrintWriter (fileDistances);
		PrintWriter outputShares = new PrintWriter (fileShares);
		PrintWriter outputLegs = new PrintWriter (fileLegs);
		PrintWriter outputHouseholds = new PrintWriter (fileHouseholds);


		TreeSet<String> activities = dataAnalysisUtils.getSetOfActivities();

		// Titles
		String csvTitle = "carTripID";
		for(String activity: activities){
			csvTitle = csvTitle + "," + activity;
		}

		outputDistances.println(csvTitle);
		outputShares.println(csvTitle);
		outputLegs.println("carTripID, ID, distance, covered_distance");
		outputHouseholds.println("carTripID,HouseholdID,nrCars,carType,carTypesHH");


		// Body
		for(CarTrip carTrip : CarTrip.failedCarTrips){


			// Shares in distance
			HashMap<String, Double> sharesInDistance = shareOfFailedLegs(carTrip);

			String csvCarTripDistanceRow = Integer.toString(carTrip.getCarTripID());
			String csvCarTripShareRow = Integer.toString(carTrip.getCarTripID());

			for(String activity: activities){

				if(sharesInDistance.containsKey(activity)){
					csvCarTripDistanceRow = csvCarTripDistanceRow + "," + sharesInDistance.get(activity);
					csvCarTripShareRow = csvCarTripShareRow + "," + sharesInDistance.get(activity) / carTrip.getTotalDrivenDistance();
				} else {
					csvCarTripDistanceRow = csvCarTripDistanceRow + "," + 0;
					csvCarTripShareRow = csvCarTripShareRow + "," + 0;
				}

			}

			outputDistances.println(csvCarTripDistanceRow);
			outputShares.println(csvCarTripShareRow);

			for(Leg leg: carTrip.getLegs()){
				outputLegs.println(
						Integer.toString(leg.getCarTrip().getCarTripID()) + "," +
								leg.getDayPhaseID().toString() + "," + 
								Double.toString(leg.getDistance()) + "," + 
								Double.toString(leg.getDistanceDriven()));
			}
			
			int carTypes = 0;
			for(Entry<String, Car> car : carTrip.getHousehold().getCars().entrySet()){
				
				switch (car.getValue().getCarType()){
				case "BusinessCar":
					carTypes += 10;
					break;
				case "RentalCar":
					carTypes += 100;
					break;
				case "CarsharingCar":
					carTypes += 1000;
					break;
				case "HomeCar":
					carTypes += 1;
					break;
				default:
					carTypes += 10000;
				}
			}
			
			

			outputHouseholds.println(String.join(",", 
					Integer.toString(carTrip.getCarTripID()),
					Integer.toString(carTrip.getHousehold().getHouseholdId()),
					Integer.toString(carTrip.getHousehold().getCars().size()),
					carTrip.getCar().getCarType(),
					Integer.toString(carTypes)
				));

		}

		outputDistances.close();
		outputShares.close();
		outputLegs.close();
		outputHouseholds.close();

	}



	public static void exportPointsOfFailedLegsToCSV(String filename) throws FileNotFoundException{


		int IterationNr = RunAnalysis.getMonteCarloIteration();
		File filePoints = new File(RunAnalysis.basePathOutput + "scenarios/" + filename + "/points_it" + IterationNr + ".csv");

		if(!filePoints.getParentFile().exists())
			filePoints.getParentFile().mkdirs();

		PrintWriter outputPoints = new PrintWriter (filePoints);
		outputPoints.println("carTripID,distanceTotal,distanceDrivenTotal,pointsDriven,pointsFailure,pointsNotDriven");

		for(CarTrip carTrip : CarTrip.failedCarTrips){

			StringJoiner sjDriven = new StringJoiner("|");
			StringJoiner sjFailure = new StringJoiner("|");
			StringJoiner sjNotDriven = new StringJoiner("|");

			for(Leg leg: carTrip.getLegs()){

				if(!leg.getAddressStart().equals(leg.getAddressEnd())){



					File file = new File(RunAnalysis.basePathSer + 
							"ListOfSteps" + File.separator +
							RunAnalysis.countryNameShort + File.separator +
							leg.getAddressStart() + "_" + leg.getAddressEnd()  + ".ser");

					if(file != null){

						try {
							FileInputStream fileIn;
							fileIn = new FileInputStream(file);
							ObjectInputStream in = new ObjectInputStream(fileIn);
							SerObjects serObjects = (SerObjects) in.readObject();
							Tests.debug("Loaded serialized data from " + file.getName());
							in.close();
							fileIn.close();

							for(StepOnLeg steps : serObjects.listOfAllSteps){

								String s = dataAnalysisUtils.formatString("%.5f",steps.getLatLng().lat) +
										";"+ dataAnalysisUtils.formatString("%.5f",steps.getLatLng().lng);

								if(leg.isFailedLeg()){

									if(leg.getDistanceDriven() < 0.000001){
										sjNotDriven.add(s);
									} else {
										sjFailure.add(s);

									}
								} else {
									sjDriven.add(s);
								}
							}
						} catch (Exception e) {

							Tests.debug("Error loading " + file.getName());
						}
					}
				}

			}
			StringJoiner sjOutputPoints = new StringJoiner(",");

			sjOutputPoints.add(Integer.toString(carTrip.getCarTripID()));
			sjOutputPoints.add(Double.toString(carTrip.getTotalDistance()));
			sjOutputPoints.add(Double.toString(carTrip.getTotalDrivenDistance()));
			sjOutputPoints.add(sjDriven.toString());
			sjOutputPoints.add(sjFailure.toString());
			sjOutputPoints.add(sjNotDriven.toString());



			outputPoints.println(sjOutputPoints.toString());
		}


		outputPoints.close();
	}

	/**
	 * Returns an Map with distances and purposes that contributed to the failure of the trip
	 * @param failedCarTrip
	 * @return A Maps with activityType as String and the distance as Value
	 */
	public static HashMap<String, Double> shareOfFailedLegs(CarTrip failedCarTrip){

		// 1. Take failed legs or single failed leg
		// 2. sum up all covered distances of the legs

		HashMap<String, Double> sharesInDistance = new HashMap<>(); 

		for(Leg leg: failedCarTrip.getLegs()){

			// Only consider legs that were actually driven or partially driven --> bigger than 0 
			if(leg.getDistanceDriven() > 0){

				// get the km and add it to the respective purpose. If purpose already in map -> add it to existing purpose

				double newDistanceDriven = 0;
				if(sharesInDistance.containsKey(leg.getActivityType())){
					// add to it
					newDistanceDriven = sharesInDistance.get(leg.getActivityType());
				}

				sharesInDistance.put(leg.getActivityType(), newDistanceDriven + leg.getDistanceDriven());				
			}
		}




		// 3. store share in object, best if I extend current object and save it as DayPhase
		// 4. make function that outputs analysis data to csv file for R analysis

		// return ?
		// need to understand R format.
		// it is probably easier and sufficient now to write to a csv file.
		// HashMap of carTrip and all activities. Take activities from list.

		return sharesInDistance;
	}





}
