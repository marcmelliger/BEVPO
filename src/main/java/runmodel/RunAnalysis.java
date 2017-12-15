package runmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import analysis.AnalyzeFailedLegs;
import analysis.Tests;
import geo.RouteLeg;
import geo.testpoints.ChargingStation;
import manager.ManageActivities;
import manager.ManageCars;
import manager.ManageDatafile;
import model.CarModel;
import model.CarTrip;
import model.dayphases.ChargingActivity;
import model.filteredlegs.RemovedLeg;
import model.filteredlegs.TrimmedLeg;
import utils.ProbabilityItem;


public abstract class RunAnalysis{ 
		
	// Adjust these paths to the specific directories
	public static String basePathQueries 	= "/Users/user/bevpo/data/queries/";
	public static String baseInputPath		= "/Users/user/bevpo/data/input/";
	public static String basePathChargermap	= "/Users/user/bevpo/data/input/chargermap/";
	public static String basePathCar 		= "/Users/user/bevpo/data/input/cars/";
	public static String basePathChargers 	= "/Users/user/bevpo/data/input/chargers/";
	public static String basePathOutput		= "/Users/user/bevpo/data/output/";
	public static String basePathSer 		= "/Users/user/bevpo/data/serialised/";

	// Adjust these number to the missing data codes in your data
	public static String CODE_MISSING_DATA = "-99";
	public static String CODE_MISSING_DATA2 = "-97";

	
	// No edit from here necessary
	
	public static int CODE_RETURN_HOME;
	public static Integer CODE_TRANSFER;
	
	public static String LEG_DATA_FILENAME;
	public static String CHARGING_STATIONS_FILENAME;
	public static String EXISTING_CHARGING_STATIONS_FILENAME;
	public static String CAR_MODEL_FILENAME;
	public static String CAR_MODEL_SHARE_FILENAME;
	public static String CONFIG_SCENARIO;
	public static String CHARGER_TYPE_FILENAME;
	public static String CHARGER_MATRIX_FILENAME;
	
	public static String GOOGLE_LOGGER_FILENAME;
	
	// data field names
	public static String HOUSEHOLD_ID;
	public static String PERSON_ID;
	public static String LEG_ID;
	public static String CAR_TYPE; 
	public static String HOMECAR_ID;  
	public static String DISTANCE;	
	public static String LEG_STARTTIME; 	
	public static String LEG_ENDTIME;
	public static String LEG_PURPOSE;
	public static String LEG_VEHICLETYPE;
	public static String PARKING_SPACES;
	public static String LEISURE_PURPOSE;
	public static String RETURNHOME_PURPOSE;
	public static String LEG_STARTADDRESS;
	public static String LEG_ENDADDRESS;
	public static String LEG_STARTADDRESS_ALT; // Alternative address for Routing
	public static String LEG_ENDADDRESS_ALT;
	
	/**
	 * Values of this Map should be the same for all datasets
	 */
	public static HashMap<Integer, String> activityStrings;
	public static HashMap<Integer, String> carTypeStrings;
	public static HashMap<Integer, Boolean> legCoPassenger;


	
	/**
	 * List that defines the used car and their share in the population
	 */
	public static ArrayList<ProbabilityItem> carModelsShare; 

	/**
	 * Map that assigns the probabilites of chargers in the population to activities
	 */
	public static HashMap<String, ArrayList<ProbabilityItem>> chargerTypesShare; 

	
	public static long randomSeed;
	public static boolean useRandomSeed;
	public static int nrOfMonteCarloIterations;
	private static int monteCarloIteration;
	public static double householdParkingSpacesFactor;



	public static HashMap<String, List<String>> monteCarloResultStats = new HashMap<>();
	
	public static String scenarioName;

	public static String countryNameLong;
	public static String countryNameShort;
	public static String countryToplevel;
	public static ArrayList<String> countriesAllowedInGoogleResult = new ArrayList<>();
	
	public static boolean serialiseTrips = false;
	
	public static boolean georouteTrips;
	
	
	/**
	 * If true, the distance of trips is adjusted to the Google Maps output.
	 */
	public static boolean replanTrips = false;
	public static ArrayList<Integer> tripsToReplan = new ArrayList<>();
	
	public static void init() throws IOException {
				
		// values should be the same in all datasets.
		activityStrings = new HashMap<>();
		carTypeStrings = new HashMap<>();
		legCoPassenger = new HashMap<>();

		carModelsShare = new ArrayList<>();
		chargerTypesShare = new HashMap<>();

		InputStream input = null;

		
		// ----- Api Key -----

		Properties apiKey = new Properties();
		input = new FileInputStream(baseInputPath + "google_api.properties");
		apiKey.load(input);
		RouteLeg.googleApiKey = apiKey.getProperty("apiKey");
		
		// ----- Config -----

		
		Properties prop = new Properties();
		

		input = new FileInputStream("config/" + countryNameShort + "/" + CONFIG_SCENARIO + ".properties");

		prop.load(input);

	
		// ----- Files to load -----
		
		LEG_DATA_FILENAME = prop.getProperty("LEG_DATA_FILENAME", LEG_DATA_FILENAME);
		CAR_MODEL_FILENAME = prop.getProperty("CAR_MODEL_FILENAME", CAR_MODEL_FILENAME);
		CAR_MODEL_SHARE_FILENAME = prop.getProperty("CAR_MODEL_SHARE_FILENAME", CAR_MODEL_SHARE_FILENAME);
		CHARGER_TYPE_FILENAME = prop.getProperty("CHARGER_TYPE_FILENAME", CHARGER_TYPE_FILENAME);
		CHARGER_MATRIX_FILENAME = prop.getProperty("CHARGER_MATRIX_FILENAME", CHARGER_MATRIX_FILENAME);
		CHARGING_STATIONS_FILENAME = prop.getProperty("CHARGING_STATIONS_FILENAME", CHARGING_STATIONS_FILENAME);
		// ----- Properties -----

		RunAnalysis.setScenarioName(prop.getProperty("scenarioName", CONFIG_SCENARIO));
		
		// # Scenario Variable
		ManageActivities.considerHouseholdParkingSpaces = Boolean.parseBoolean(prop.getProperty("considerHouseholdParkingSpaces", "false"));
		ManageActivities.serviceAreasEnabled	 = Boolean.parseBoolean(prop.getProperty("serviceAreasEnabled", "false"));
		RouteLeg.setTestRadiusNormal(Double.parseDouble(prop.getProperty("testRadius", "0.1")));
		RouteLeg.setTestRadiusReplan(Double.parseDouble(prop.getProperty("testRadiusReplan", "0.1")));
		ChargingActivity.enableChargingDecisionLevel = Integer.parseInt(prop.getProperty("enableChargingDecisionLevel", "0"));
		ChargingActivity.maxChargingTime = Integer.parseInt(prop.getProperty("maxChargingTime", "40"));
		ChargingActivity.maxTimesAtStation = Integer.parseInt(prop.getProperty("maxTimesAtStation", "4"));

		
		// # Run Variables
		nrOfMonteCarloIterations = Integer.parseInt((prop.getProperty("nrOfMonteCarloIterations", "75")));		

		Tests.debug = Boolean.parseBoolean(prop.getProperty("debug", "false"));
		useRandomSeed = Boolean.parseBoolean(prop.getProperty("useRandomSeed", "false"));
		randomSeed = Long.parseLong(prop.getProperty("randomSeed", "12345"));
		
		
		
		
		AnalyzeFailedLegs.printCSVOutput = Boolean.parseBoolean(prop.getProperty("printCSVOutput", "true"));
		AnalyzeFailedLegs.printPointsCSV = Boolean.parseBoolean(prop.getProperty("printPointsCSV", "false"));

		
		RouteLeg.queryLimit = Integer.parseInt(prop.getProperty("queryLimit", "100000"));
		ManageActivities.serialiseTestPointsOnLegs = Boolean.parseBoolean(prop.getProperty("serialiseTestPointsOnLegs", "true"));
		RunAnalysis.serialiseTrips = Boolean.parseBoolean(prop.getProperty("serialiseTrips", "true"));
		RunAnalysis.georouteTrips = Boolean.parseBoolean(prop.getProperty("georouteTrips", "true"));
		
		for (String id : prop.getProperty("tripsToReplan", "").split(","))	
			if(!id.equals(""))
				tripsToReplan.add(Integer.parseInt(id));
		
		if(!tripsToReplan.isEmpty())
			RunAnalysis.replanTrips = true;
		
		for (String id : prop.getProperty("tripsToPrintToConsole", "").split(","))
			if(!id.equals(""))
				Tests.tripsToPrintToConsole.add(Integer.parseInt(id.trim()));

		
		// # Method Assumption Variables
		CarModel.setCalculationBasedOnRange(Boolean.parseBoolean(prop.getProperty("calculationBasedOnRange", "false")));
		CarModel.setChargerLossesFactor(Double.parseDouble(prop.getProperty("chargerLossesFactor", "0.85")));		
		CarModel.probabilisticConsumption = Boolean.parseBoolean(prop.getProperty("probabilisticConsumption", "true"));
		
		ManageCars.setStartOfChargeFactor(Double.parseDouble(prop.getProperty("startOfChargeFactor", "0.5")));
		ManageCars.fullFirstCharge = Boolean.parseBoolean(prop.getProperty("fullFirstCharge", "false"));
		
		ManageActivities.setHouseholdParkingSpacesFactor(Double.parseDouble(prop.getProperty("householdParkingSpacesFactor", Double.toString(householdParkingSpacesFactor))));
		ManageActivities.scalingFactorThreshold = Double.parseDouble(prop.getProperty("scalingFactorThreshold", "1.5"));

		// # Start of charge
		
		ManageActivities.useShareOfChargingStationsAtBeginning(Boolean.parseBoolean(prop.getProperty("useShareOfChargingStationsAtBeginning", "false")));
		ManageActivities.setShareOfChargingStationsAtBeginning(Double.parseDouble(prop.getProperty("shareOfChargingStationsAtBeginning", "1.0")));
		ManageActivities.useShareOfChargingStations(Boolean.parseBoolean(prop.getProperty("useShareOfChargingStations", "false")));
		ManageActivities.setShareOfChargingStations(Double.parseDouble(prop.getProperty("shareOfChargingStations", "0.0")));

		
 

		



	}
	
	/**
	 * Process data and handel serialisation of constant data
	 * @throws IOException
	 */
	public static void processData() throws IOException{

		long startTime = System.nanoTime();

		
		String serFilename = basePathSer + "trips_" + countryNameShort + "_" + RouteLeg.getTestRadiusNormal() + ((replanTrips)? "_" + RouteLeg.getTestRadiusReplan(): "" ) + ".ser";
		File file = new File(serFilename);
		

		if(serialiseTrips && file.exists()){

			try {
				FileInputStream fileIn = new FileInputStream(file);
				ObjectInputStream in = new ObjectInputStream(fileIn);

				SerObjects serObjects = (SerObjects)in.readObject();
				serObjects.assignObjects();

				in.close();
				fileIn.close();
				
				Tests.debug("Loaded serialized data from " + serFilename);

			} catch (IOException i) {
				i.printStackTrace();
				return;
			} catch(ClassNotFoundException c) {
				Tests.debug("CarTrip.allCarTrips class not found");
				c.printStackTrace();
				return;
			}

		} else {

			
			// ----- Loggerinit -----

			ManageDatafile.processLegDataFile(LEG_DATA_FILENAME, CHARGING_STATIONS_FILENAME);
			
			FileHandler fh;  

		    try {  

		        // This block configure the logger with handler and formatter  
		        fh = new FileHandler(basePathOutput + "/logs/google_log" + "_" + scenarioName + ".log");  
		        fh.setLevel(Level.INFO);
		        RouteLeg.logger.addHandler(fh);
		        
		        SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter);  

		        // the following statement is used to log any messages  
		        RouteLeg.logger.info("Google-log start for " + scenarioName);  

		    } catch (SecurityException e) {  
		        e.printStackTrace();  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    } 
			
			
			ManageDatafile.generateChargingStationTestPoints();
			ManageDatafile.generateTrips();
			

			// Write to serFile
			if(serialiseTrips){
				try {
					FileOutputStream fileOut = new FileOutputStream(file);
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					
					SerObjects serObjects = new SerObjects();
					out.writeObject(serObjects);
					out.close();
					fileOut.close();
					
					Tests.debug("Serialized data is saved in " + serFilename);
				
				} catch(IOException i) {
					i.printStackTrace();
				}
			}

		}
		

		// Differs between scenarios
		ManageDatafile.processDataFiles(CAR_MODEL_FILENAME,CAR_MODEL_SHARE_FILENAME, CHARGER_TYPE_FILENAME, CHARGER_MATRIX_FILENAME);
		ManageDatafile.generateObjects();
		ManageActivities.processChargingActivities();
		
		long endTime = System.nanoTime();
		Tests.debug("Time for processing data:");
		Tests.elapsedTime(startTime, endTime);

	}
	
	
	public static void singleRun() throws FileNotFoundException {
				
		ManageDatafile.generateNewRandomObjects();
		ManageCars.runCarTrips();		
		Tests.analysis();
		AnalyzeFailedLegs.analysis();
		
	}
	

	
	public static void monteCarloRun() throws FileNotFoundException{
		
		long startTime = System.nanoTime();

		for(monteCarloIteration = 1; monteCarloIteration <= nrOfMonteCarloIterations; monteCarloIteration++){
						
			Tests.print("Monte Carlo Iteration Nr. " + monteCarloIteration);
			Tests.print(Tests.horizontalLine());
			
			singleRun();


		
		}
		
		Tests.monteCarloAnalysis();
		
		long endTime = System.nanoTime();
		Tests.debug("Time for MC runs:");
		Tests.elapsedTime(startTime, endTime);
	}


	public static int getMonteCarloIteration() {
		return monteCarloIteration;
	}

	public static List<String> getMonteCarloResult(String key) {
		return monteCarloResultStats.get(key);
	}

	public static void addMonteCarloResult(String key, String value) {
				
		if(!RunAnalysis.monteCarloResultStats.containsKey(key)){
			RunAnalysis.monteCarloResultStats.put(key, new ArrayList<>());
		}
		
		RunAnalysis.monteCarloResultStats.get(key).add(value);
	}

	public static String getScenarioName() {
		return scenarioName;
	}

	public static void setScenarioName(String scenarioName) {
		RunAnalysis.scenarioName = scenarioName;
	}

	public static String getCountryNameShort() {
		return countryNameShort;
	}

	public static void setCountryNameShort(String countryNameShort) {
		RunAnalysis.countryNameShort = countryNameShort;
	}



}

class SerObjects implements Serializable{
	
	private static final long serialVersionUID = 2856652311273525217L;
	
	public ArrayList<CarTrip> allCarTrips = new ArrayList<>();
	public ArrayList<RemovedLeg> allRemovedLegs = new ArrayList<>();
	public ArrayList<TrimmedLeg> allTrimmedLegs  = new ArrayList<>();
	public HashMap<String, ChargingStation> allChargingStations = new HashMap<>();
	
	

	public SerObjects() {
		
		allCarTrips = CarTrip.allCarTrips;
		allRemovedLegs = RemovedLeg.allRemovedLegs;
		allTrimmedLegs = TrimmedLeg.allTrimmedLegs;
		allChargingStations = ChargingStation.allChargingStations;
	}
	
	public void assignObjects(){
		
		CarTrip.allCarTrips = allCarTrips;
		RemovedLeg.allRemovedLegs = allRemovedLegs;
		TrimmedLeg.allTrimmedLegs = allTrimmedLegs;
		ChargingStation.allChargingStations = allChargingStations;
		
	}

}

