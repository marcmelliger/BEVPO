package runmodel;

import java.io.IOException;
import java.util.logging.Level;

import geo.RouteLeg;

public class RunAnalysisSwiss extends RunAnalysis{
	
	public static void main(String[] args) throws IOException {

		CONFIG_SCENARIO = args[0];
		
		countryNameLong = "Switzerland";
		countryNameShort = "CH";
		countryToplevel = "ch";
		
		countriesAllowedInGoogleResult.add(countryNameLong);

		// Filenames are defaults and can be adjusted in the scenario config file
		LEG_DATA_FILENAME = "CH/a003_q09.csv";
		CHARGING_STATIONS_FILENAME = "CH_stations.csv";

		CAR_MODEL_FILENAME = "car_models_v2";
		CHARGER_TYPE_FILENAME = "chargers";
		
		householdParkingSpacesFactor = 1;
				
		init();
				
		HOUSEHOLD_ID 	= "householdID";
		PERSON_ID 		= "personID";
		LEG_ID 			= "legID";
		CAR_TYPE 		= "carType";
		HOMECAR_ID 		= "carID"; 
		DISTANCE 		= "distance";
		LEG_STARTTIME 	= "legStartTime";
		LEG_ENDTIME 	= "legEndTime";
		LEG_PURPOSE 	= "legPurpose";
		LEG_VEHICLETYPE	= "driverType";
		PARKING_SPACES	= "homeParking";
		LEISURE_PURPOSE	= "leisurePurpose";
		RETURNHOME_PURPOSE = "wayPurpose";
		LEG_STARTADDRESS = "legStartAddress";
		LEG_ENDADDRESS  = "legEndAddress";
		LEG_STARTADDRESS_ALT = "legStartAddressAlt";
		LEG_ENDADDRESS_ALT  = "legEndAddressAlt";
		
		CODE_RETURN_HOME  = 11;
		CODE_TRANSFER = 1;
		
		activityStrings.put(CODE_RETURN_HOME, "Home:Return");
		activityStrings.put(CODE_TRANSFER, 	"Transfer");	

		activityStrings.put(-99,"Pseudoleg");
		activityStrings.put(2, 	"Work");
		activityStrings.put(3, 	"Education");
		activityStrings.put(4, 	"Shopping");
		activityStrings.put(5, 	"Services");
		activityStrings.put(6, 	"Business");
		activityStrings.put(7, 	"Business:Drive");
		activityStrings.put(8, 	"Leisure:Misc");
		activityStrings.put(9, 	"Escort:Kids");
		activityStrings.put(10, "Escort:Misc");
		activityStrings.put(12, "Misc");
		activityStrings.put(13, "Borderpassing");
		activityStrings.put(20, "Home");
		
		
		activityStrings.put(801,"Leisure:Visits");
		activityStrings.put(802,"Leisure:Gastronomy");
		activityStrings.put(803,"Leisure:Sports"); // active
		activityStrings.put(804,"Leisure:Outdoor");
		activityStrings.put(805,"Leisure:Outdoor");
		activityStrings.put(806,"Leisure:Sports"); // passive
		activityStrings.put(807,"Leisure:Walk");
		activityStrings.put(808,"Leisure:Fitness");
		activityStrings.put(809,"Leisure:Cultural");
		activityStrings.put(810,"Leisure:nonpaid work");
		activityStrings.put(811,"Leisure:Club");
		activityStrings.put(812,"Leisure:Holidays");
		activityStrings.put(813,"Leisure:Religion");
		activityStrings.put(814,"Leisure:Misc");
		activityStrings.put(815,"Leisure:Outdoor"); // BBQ
		activityStrings.put(816,"Leisure:Shopping");
		activityStrings.put(817,"Leisure:Joyride");
		activityStrings.put(818,"Leisure:Misc");
		activityStrings.put(822,"Leisure:Multiple");
		
		
		carTypeStrings.put(-99, "UnknownCar");		// -99	F51300 not in (7,8)
		carTypeStrings.put(-98, "UnknownCar");		// -98	Keine Angabe
		carTypeStrings.put(-97, "UnknownCar");		// -97	weiss nicht
		carTypeStrings.put(1, 	"HomeCar");	 		//	1	Auto im Haushalt
		carTypeStrings.put(2, 	"BusinessCar");		//	2	Firmenauto, Dienstwagen
		carTypeStrings.put(3, 	"RentalCar");		//	3	Mietauto
		carTypeStrings.put(4, 	"CarsharingCar");	//	4	Carsharing-Auto, Autoteilen
		carTypeStrings.put(5, 	"UnknownCar");		//	5	Anderes Auto

		legCoPassenger.put(7, false);
		legCoPassenger.put(8, true);
		
		processData();
		
		// ----- Run -----
		
		monteCarloRun();
		
		// ----- Analysis ----
		
		RouteLeg.logger.log(Level.INFO, "Swiss Run finish");
		
			
	}
	


	

}
