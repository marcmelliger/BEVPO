package runmodel;

import java.io.IOException;

import manager.ManageDatafile;

public class RunAnalysisFinnish extends RunAnalysis{
	
	public static void main(String[] args) throws IOException {

		CONFIG_SCENARIO = args[0];
		
		countryNameLong = "Finland";
		countryNameShort = "FIN";
		countryToplevel = "fn";
		
		countriesAllowedInGoogleResult.add(countryNameLong);
		countriesAllowedInGoogleResult.add("Åland Islands");
		
		// Filenames are defaults and can be adjusted in the scenario config file
		LEG_DATA_FILENAME = "FIN/travel_survey.csv";		
		CHARGING_STATIONS_FILENAME = "FIN_stations.csv";		
		CAR_MODEL_FILENAME = "carmodels"; // don't write csv ending
		CHARGER_TYPE_FILENAME = "chargers"; // don't write csv ending
		
		householdParkingSpacesFactor = 0.3;	
	
		init();
		
		HOUSEHOLD_ID 	= "HHNR";
		PERSON_ID 		= "respondentNumber";
		LEG_ID 			= "LegID";
		CAR_TYPE 		= "carType";
		HOMECAR_ID 		= "carID"; 
		DISTANCE 		= "distanceCorrected";
		LEG_STARTTIME 	= "legStartTimeMin";
		LEG_ENDTIME 	= "legEndTimeMin";
		LEG_VEHICLETYPE	= "vehicleType"; // Copassenger
		PARKING_SPACES	= "parkingSpaces"; 
		LEG_PURPOSE 	= "legPurposeAdjusted";
		RETURNHOME_PURPOSE = "wayPurpose";
		LEG_STARTADDRESS = "legStartAddress";
		LEG_ENDADDRESS  = "legEndAddress";
		
		CODE_RETURN_HOME  = 90; // Not set
		activityStrings.put(CODE_RETURN_HOME, "Home:Return");
		
		activityStrings.put(1, 	"Work");	
		activityStrings.put(2, 	"Business:Drive");
		activityStrings.put(3, 	"Business:Drive");
		activityStrings.put(4, 	"Shopping");
		activityStrings.put(5, 	"Shopping");
		activityStrings.put(6, 	"Services");
		activityStrings.put(7, 	"Escort:Misc");
		activityStrings.put(8, 	"Education");
		activityStrings.put(9, 	"Escort:Kids");
		activityStrings.put(10, "Services");
		activityStrings.put(11, "Cottage");

		activityStrings.put(21, "Pseudoleg");
		
		if(ManageDatafile.isParseLeisurePurpose()){
			activityStrings.put(12, "Leisure:Visits");
			activityStrings.put(13,	"Leisure:Sports");
			activityStrings.put(14,	"Leisure:Outdoor");
			activityStrings.put(15,	"Leisure:Walk");
			activityStrings.put(16,	"Leisure:Cultural");
			activityStrings.put(17,	"Leisure:Hobbies");
			activityStrings.put(18,	"Leisure:Joyride");
			activityStrings.put(19,	"Leisure:Holidays");
			activityStrings.put(20,	"Leisure:Misc");
			
		} else {
			activityStrings.put(12, "Leisure");
			activityStrings.put(13,	"Leisure");
			activityStrings.put(14,	"Leisure");
			activityStrings.put(15,	"Leisure");
			activityStrings.put(16,	"Leisure");
			activityStrings.put(17,	"Leisure");
			activityStrings.put(18,	"Leisure");
			activityStrings.put(19,	"Leisure");
			activityStrings.put(20,	"Leisure");
		}
		
		carTypeStrings.put(0, 	"HomeCar");	 		
		carTypeStrings.put(1, 	"BusinessCar");		
		carTypeStrings.put(2, 	"UnknownCar");	//(i.e. the household doesn’t have any cars)	

		legCoPassenger.put(1, false); // Driver
		legCoPassenger.put(2, true); // Co-passenger
		
		ManageDatafile.setCharset("MacRoman");
		processData();
		
		// ----- Run -----
		
		monteCarloRun();
		
		// ----- Analysis ----
		
			
	}
	


	

}
