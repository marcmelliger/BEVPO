package analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map.Entry;

import geo.RouteLeg;
import model.CarModel;
import model.CarTrip;
import model.Household;
import model.dayphases.DayPhase;
import model.filteredlegs.RemovedLeg;
import model.filteredlegs.TrimmedLeg;
import runmodel.RunAnalysis;
import utils.dataAnalysisUtils;

public class Tests {
	
	public static boolean debug = false;
	
	public static ArrayList<Integer> tripsToPrintToConsole = new ArrayList<>();

	public static void analysis() {
		
		//Tests.printCarModelInfo(0);
		
		Tests.printNumberOfFilteredLegs();
		Tests.NumberOfTrips();
		//Tests.printErrors();
		
		for(int id: tripsToPrintToConsole)
			Tests.testRunAnalysis(id);

		
		//Tests.printScalingMap();

		//Tests.countNrOfHouseholdsWithParking();		
		//Tests.printFilteredLegs();
		//Tests.printDayPhases(0);
		//Tests.printFailedTrips();
	
	}
	
	@SuppressWarnings("unused")
	private static void printErrors() {
		// TODO Auto-generated method stub
		
		Tests.print(Integer.toString(RouteLeg.googleErrors));
		
	}

	@SuppressWarnings("unused")
	private static void printScalingMap() {
		// TODO Auto-generated method stub

		Tests.print("Scaling factors");

		if(RouteLeg.scalingFactorMap != null){
			
			for(Entry<String, Double> entry : RouteLeg.scalingFactorMap.entrySet()){
				Tests.print(entry.getValue().toString());
				
			}
		}
		
		Tests.print(Tests.horizontalLine());
		
		Tests.print("Scaling factors Belwo Threshold");

		if(RouteLeg.scalingFactorMap != null){
			
			for(Entry<String, Double> entry : RouteLeg.scalingFactorMapBelow.entrySet()){
				Tests.print(entry.getValue().toString());
				
			}
		}
		
		Tests.print(Tests.horizontalLine());

		
	}

	public static void testRunAnalysis(int nr){
		
		ArrayList<CarTrip> allTrips = CarTrip.allCarTrips;
		
		
		print(allTrips.get(nr).toString());
		
		for(DayPhase dayPhases : allTrips.get(nr).getDayPhases()){
			print(dayPhases.toString(true));
		}
		
		allTrips.get(nr).getCar();
		
		
		print("---------------");
		
		
	}


	public static void printFailedTrips() {
		
		for(CarTrip failedCarTrip: CarTrip.failedCarTrips){
			print("____________________________________");
			print(failedCarTrip.toString());
			print("____________________________________");
			print(failedCarTrip.getDayPhases().toString());
			print(horizontalLine());		

			
		}
		
	}
	
	public static void printDayPhases(int TripNr){
		
		print(CarTrip.allCarTrips.get(TripNr).getCar().getDayPhaseChargeStatus().toString());
		print(horizontalLine());		

		
	}
	
	public static void printFilteredLegs(){
	
		print("Filtered Legs:");
		for(RemovedLeg removedLeg: RemovedLeg.allRemovedLegs){
			print(removedLeg.toString());
		}
		for(TrimmedLeg trimmedLeg: TrimmedLeg.allTrimmedLegs){
			print(trimmedLeg.toString());
		}
		print(horizontalLine());		

	}
	
	public static void printNumberOfFilteredLegs(){
		
		print("Filtered Legs:");
		print("Number of removed Legs:" + RemovedLeg.allRemovedLegs.size());
		print("Number of trimmed Legs:" + TrimmedLeg.allTrimmedLegs.size());
		print(horizontalLine());		

	}
	
	public static void printChargerPower(int carTripID){
		
		print(CarTrip.allCarTrips.get(0).getActivities().get(0).getCharger().toString());
	}
	
	
	public static void print(String string){
		System.out.println(string);
		
	}
	
	public static String horizontalLine(){
		return ("____________________________________");
	}


	public static void debug(String string) {
		
		if(debug){
			print(string);
		}
		
	}
	
	public static void printCarModelInfo(int carModelID){
		
		print(CarModel.allCarModels.get(carModelID).toString());
		print(horizontalLine());		


	}



	public static void monteCarloAnalysis() {
	
	}
	
	public static void countNrOfHouseholdsWithParking(){
		
		int noParking = 0;
		int parking = 0;
		
		
		for(Entry<Integer, Household> householdEntry : Household.getAllHouseholds().entrySet()){
			
			if(householdEntry.getValue().getParkingSpaces() <= 0){
				noParking++;
			} else {
				parking++;
			}
			
		}
		

		debug("Number of NoParking:" +  Integer.toString(noParking));
		debug("Number of Parking:" +  Integer.toString(parking));
		
		int startWith0OfFailed = 0;
		int startChargedOfFailed = 0;
		
		// Only failed CarTrips:
		for(CarTrip failedTrip : CarTrip.failedCarTrips){
			
			DayPhase firstPhase = failedTrip.getDayPhases().getFirst();
			double charge = failedTrip.getCar().getDayPhaseChargeStatus().get(firstPhase);
			
			if(charge == 0){
				startWith0OfFailed++;
			} else {
				startChargedOfFailed++;
			}
			
		}
		
		debug("Failed Trips that start with 0: " + startWith0OfFailed);
		debug("Failed Trips that start charged: " + startChargedOfFailed);
		
		
		
	}


	public static void NumberOfTrips() {
		
		double failed = CarTrip.failedCarTrips.size();
		double succeded = CarTrip.successfullCarTrips.size();
		double tot = failed + succeded;
		double shareFailed = (failed / tot * 100);
		double shareSucceded =  (succeded / tot * 100);
		
		int totHH = Household.getAllHouseholds().size();

				
		print("Failed trips: " + dataAnalysisUtils.formatString("%.0f",failed) + " (" + dataAnalysisUtils.formatString("%.1f",shareFailed) + " %)");
		print("Sucessfull trips: " + dataAnalysisUtils.formatString("%.0f",succeded) +  " (" + dataAnalysisUtils.formatString("%.1f",shareSucceded) + " %)");
		print("Total trips: " + dataAnalysisUtils.formatString("%.0f",tot));
		print("Total households: " + totHH + " of " + CarTrip.allCarTrips.size());

		print(horizontalLine());
		
		
		if(AnalyzeFailedLegs.printCSVOutput){
			
			File fileSummary = new File(RunAnalysis.basePathOutput + "summary/" + RunAnalysis.getScenarioName() + "_summary.csv");
			PrintWriter output;
			
			try {
				
				if(RunAnalysis.getMonteCarloIteration() == 1){
					output = new PrintWriter(new FileOutputStream(
						    fileSummary, 
						    false /* append = false */));
				
					output.println("iteration,failed,successfull");
					output.close();
				}

				output = new PrintWriter(new FileOutputStream(
					    fileSummary, 
					    true /* append = true */)); 
					
				output.append(String.join(",", 
						Integer.toString(RunAnalysis.getMonteCarloIteration()), 
						Double.toString(failed),
						Double.toString(succeded) +	System.lineSeparator()));

				output.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
	}


	public static void elapsedTime(long startTime, long endTime) {
		
		long elapsedTime = endTime - startTime;
		double seconds = (double)elapsedTime / 1000000000.0;
				
		Tests.debug("Time for processing legs:" + seconds);
		
	}



	

}
