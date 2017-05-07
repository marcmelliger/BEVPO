package manager;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import com.google.maps.model.LatLng;

import geo.RouteLeg;
import geo.testpoints.ChargingStation;
import geo.testpoints.ExistingChargingStation;
import geo.testpoints.ServiceArea;
import model.Car;
import model.CarModel;
import model.CarTrip;
import model.ChargerType;
import model.Household;
import model.Person;
import model.dayphases.Activity;
import model.dayphases.ChargingActivity;
import model.dayphases.ChargingActivityStats;
import model.dayphases.Leg;
import runmodel.RunAnalysis;
import utils.ProbabilityItemCar;
import utils.ProbabilityItemCharger;



/**
 * @author marc
 *
 */
public class ManageDatafile {
	
	private static String LEG_DATA_PATH; 
	private static String CAR_MODEL_PATH; 
	private static String CAR_MODEL_SHARE_PATH; 
	private static String CHARGER_TYPE_PATH;
	private static String CHARGER_MATRIX_PATH;
	private static String CHARGING_STATIONS_PATH;


	// Datalists
	private static ArrayList<Map<String, String>> legDataList;
	private static ArrayList<Map<String, String>> chargingStationList;

	private static ArrayList<Map<String, String>> carModelList;
	private static ArrayList<Map<String, String>> carModelShareList;
	private static ArrayList<Map<String, String>> chargerTypeList;
	private static ArrayList<Map<String, String>> chargerMatrixList;

	private static String charset = null;

	
	// Should not be changed due to caching of legs file
	public static boolean parseLeisurePurpose = true;
	public static boolean assignReturnHomePurposes = true;



	public static boolean isParseLeisurePurpose() {
		return parseLeisurePurpose;
	}

	public static boolean isAssignDirectionPurposes() {
		return assignReturnHomePurposes;
	}

	public static void processLegDataFile(
			String LEG_DATA_FILENAME,
			String CHARGING_STATIONS_FILENAME) throws IOException{
		
		ManageDatafile.LEG_DATA_PATH         =  RunAnalysis.basePathQueries + LEG_DATA_FILENAME;				

		
		legDataList = readCSV(LEG_DATA_PATH);
		
		if(CHARGING_STATIONS_FILENAME!=null){
			ManageDatafile.CHARGING_STATIONS_PATH  =  RunAnalysis.basePathChargermap + CHARGING_STATIONS_FILENAME;				
			setCharset("macRoman");
			chargingStationList = readCSV(CHARGING_STATIONS_PATH);
			
		}

	}
	
	public static void processDataFiles( 
			String CAR_MODEL_FILENAME, 
			String CAR_MODEL_SHARE_FILENAME, 
			String CHARGER_TYPE_FILENAME,
			String CHARGER_MATRIX_FILENAME) throws IOException{
		
		String extension =  ".csv";
		
		ManageDatafile.CAR_MODEL_PATH        =  RunAnalysis.basePathCar + CAR_MODEL_FILENAME + extension;
		ManageDatafile.CAR_MODEL_SHARE_PATH  =  RunAnalysis.basePathCar + CAR_MODEL_SHARE_FILENAME + extension;
		ManageDatafile.CHARGER_TYPE_PATH     =  RunAnalysis.basePathChargers + CHARGER_TYPE_FILENAME + extension;
		ManageDatafile.CHARGER_MATRIX_PATH   =  RunAnalysis.basePathChargers + CHARGER_MATRIX_FILENAME + extension;
		
		
		carModelList = readCSV(CAR_MODEL_PATH);
		chargerTypeList = readCSV(CHARGER_TYPE_PATH);		
		carModelShareList = readCSV(CAR_MODEL_SHARE_PATH);
		chargerMatrixList = readCSV(CHARGER_MATRIX_PATH);
		
	}
	
	/**
	 * Generates objects that are not stored but stay constant between MC iterations
	 */
	public static void generateObjects(){	
		
		populateChargingTypeShare();
		generateChargerTypes();
		
		populateCarModelShare();
		generateCarModels();
	
	}

	public static ArrayList<Map<String, String>> readCSV(final String FILENAME) throws FileNotFoundException {
		
		final String DELIMITER = ",";
		
		// Read csv
		File data = new File(FILENAME);
		
		Scanner input;
		if(charset == null){
			input =  new Scanner(data);
		} else {
			input =  new Scanner(data, charset);
		}
		
		input.useDelimiter(DELIMITER);
		
		// Title to Array
		String titleLine = input.nextLine();
		String[] titleArray = titleLine.split(DELIMITER);
		
		//checkRequiredTitles(titleArray);
						
		// ArrayList with all inputs
		ArrayList<Map<String, String>> dataList;
		dataList = new ArrayList<>();
		
		// Put all data into ArrayList<Map>
		while(input.hasNextLine()){
			
			String dataLine = input.nextLine();
			String[] dataArray = dataLine.split(DELIMITER);
			
			// key, value
			LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
			
			int i = 0;
			for(String item: dataArray){				
				dataMap.put(titleArray[i++], item);				
			}
			
			dataList.add(dataMap);
				
		}
				
		input.close();
		
		return dataList;
		
	}
	
	public static String getCharset() {
		return charset;
	}

	public static void setCharset(String charset) {
		ManageDatafile.charset = charset;
	}


	/**
	 * Generates Trips. Need to be processed only once.
	 * Need an option to determine, if serialised files should be loaded
	 */
	public static void generateTrips() {
				
		int currentHouseholdId = 0;
		int currentPersonId = 0;
		int currentTripId = 0;
		String currentCarId = "";
		String currentCarType = "";
		
		CarTrip.allCarTrips = new ArrayList<>();

		HashMap<Integer, Household> allHouseholds = new HashMap<>();

						
		// Go through all lines of the Dataset, i.e. Array
	
			for(Map<String, String> entry: legDataList){
				
				//boolean include = Boolean.parseBoolean(entry.get(RunAnalysis.HOUSEHOLD_ID));
				boolean include = true;

				// Check for Missing data and only add if leg data is there
				String distance = entry.get(RunAnalysis.DISTANCE);
				if(distance.equals(RunAnalysis.CODE_MISSING_DATA)){
					include = false;
				}
				
					
				
				if(include){
				
				// ----- Households -----
				currentHouseholdId = Integer.parseInt(entry.get(RunAnalysis.HOUSEHOLD_ID));
				Household household;
				
				if(allHouseholds.containsKey(currentHouseholdId)){
					// don't make a new object but get corresponding object from housholds
					household = allHouseholds.get(currentHouseholdId);
					
				} else {
					// new household object
					household = new Household(currentHouseholdId);
					allHouseholds.put(currentHouseholdId, household);
					
					if(RunAnalysis.PARKING_SPACES != null){
						int parkingSpaces = Integer.parseInt(entry.get(RunAnalysis.PARKING_SPACES));
						household.setParkingSpaces(parkingSpaces);
						
					}
					
				}
				
				// ----- Persons -----
				currentPersonId = Integer.parseInt(entry.get(RunAnalysis.PERSON_ID));			
				Person person;
				
				if(household.getPersons().containsKey(currentPersonId)){
					person = household.getPersons().get(currentPersonId);
					
				} else {
					// new Person object.
					person = new Person(currentPersonId, household);
					household.getPersons().put(currentPersonId, person);
				}
				
				
				// ---- Cars -----
				int carTypeNr = Integer.parseInt(entry.get(RunAnalysis.CAR_TYPE));
				currentCarType = RunAnalysis.carTypeStrings.get(carTypeNr);
				
				
				if (currentCarType == "HomeCar"){
					currentCarId = currentCarType + entry.get(RunAnalysis.HOMECAR_ID);
				} else {
					currentCarId = currentCarType;
				}
				
				Car car;
				
				if(currentCarType==null){
					System.err.println("Not supported carType used");
					currentCarType = "UnknownCar";
				}
				
				// Checks car and instantiate cars.
				switch (currentCarType){
					case "BusinessCar":
					case "RentalCar":
					case "CarsharingCar":
					case "UnknownCar":
					case "HomeCar":
					default:
	
						if(household.getCars().containsKey(currentCarId)){
							car = household.getCar(currentCarId);
							
						} else {
							
							// Populate for now with standard car and change later in iterations
							car = new Car(currentCarId, new CarModel());				
							household.addCar(car);
							Car.allCars.add(car);
							
						}
						
						car.setCarType(currentCarType);
						
						break;
				}
							
				// ----- Trips -----	
				CarTrip carTrip;
								
				// Current Household has same car as previously -> get existing CarTrip of household
				if(household.getCarTrips().containsKey(car)){
					carTrip = household.getCarTrip(car);
				
				// Household has a new car -> add a new CarTrip
				} else {
					household.addNewCarTrip(currentTripId++, car);
					carTrip = household.getCarTrip(car);
					CarTrip.allCarTrips.add(carTrip); 
				}
				
				// References to each other
				carTrip.setCar(car);
							
						
				// ----- Legs -----
				// since only one legs per line: no need to check if there is already one
				
				int purposeNr = Integer.parseInt(entry.get(RunAnalysis.LEG_PURPOSE));
				
				// Assign more detailed Leisure Purpose 
				if(entry.containsKey(RunAnalysis.LEISURE_PURPOSE) & isParseLeisurePurpose()){
					int leisurePurpose = Integer.parseInt(entry.get(RunAnalysis.LEISURE_PURPOSE));
					
					// In Swiss dataset no leisure purpose is coded as -99
					// Make sure to correctly code the Leisure activities
					if(leisurePurpose > 0){
						purposeNr = leisurePurpose + 800;
					}
				}
				
				
				String postfix = "";
				// Assign different Return Home purpose
				if(entry.containsKey(RunAnalysis.RETURNHOME_PURPOSE) & isAssignDirectionPurposes()){
					int returnhomePurpose = Integer.parseInt(entry.get(RunAnalysis.RETURNHOME_PURPOSE));
					
					if(purposeNr == RunAnalysis.CODE_RETURN_HOME){
						purposeNr = returnhomePurpose;
						postfix = "#returnhome";
					}
					if(RunAnalysis.CODE_TRANSFER != null){
						if((purposeNr == RunAnalysis.CODE_TRANSFER)) {
							purposeNr = returnhomePurpose;
							postfix = "#transfer";
						}		
					}
				}
				
				String purpose = RunAnalysis.activityStrings.get(purposeNr) + postfix;
				
				Leg leg = new Leg(
						entry.get(RunAnalysis.LEG_ID), 
						Integer.parseInt(entry.get(RunAnalysis.LEG_STARTTIME)),
						Integer.parseInt(entry.get(RunAnalysis.LEG_ENDTIME)),
						purpose,
						Double.parseDouble(distance)
						);
				
				carTrip.addLeg(leg);
				
				carTrip.addToTotalDistance(leg.getDistance());
	
				leg.setCarTrip(carTrip);
				leg.setCar(car);
				leg.setPerson(person);
				
				if(entry.containsKey(RunAnalysis.LEG_STARTADDRESS) && entry.containsKey(RunAnalysis.LEG_ENDADDRESS)){
					leg.setAddressStart(entry.get(RunAnalysis.LEG_STARTADDRESS));
					leg.setAddressEnd(entry.get(RunAnalysis.LEG_ENDADDRESS));
				}
				
				if(entry.containsKey(RunAnalysis.LEG_STARTADDRESS_ALT) && entry.containsKey(RunAnalysis.LEG_ENDADDRESS_ALT)){
					leg.setAddressStartAlt(entry.get(RunAnalysis.LEG_STARTADDRESS_ALT));
					leg.setAddressEndAlt(entry.get(RunAnalysis.LEG_ENDADDRESS_ALT));
				}
						
				// Check for co-passengers
				Boolean isCoPassenger = RunAnalysis.legCoPassenger.get(Integer.parseInt(entry.get(RunAnalysis.LEG_VEHICLETYPE)));
				
				if(isCoPassenger==null){
					System.err.println("Non recognized vehicle Type in data");
				} else {
					leg.setPersonAsCoPassenger(isCoPassenger);
				}
				
				
			}
		}
				
		for(CarTrip carTrip: CarTrip.allCarTrips){
			ManageLegs.filterOutOverlappingLegs(carTrip);
			ManageLegs.sortDayphases(carTrip);
			ManageActivities.addActivities(carTrip);
			if(!ChargingStation.allChargingStations.isEmpty())
				ManageActivities.addChargingStations(carTrip);
				
		}
		

		if(RouteLeg.googleErrors>0)
			System.err.println(RouteLeg.googleErrors + " GoogleMap query errors occured");

			
	}
		
	/**
	 * Add activities and Cars to Trips. Differ each iteration due to Random
	 */
	
	public static void generateNewRandomObjects(){
		
		// Reset Result Lists for further Monte Carlo Iterations
		CarTrip.failedCarTrips = new ArrayList<>();
		CarTrip.successfullCarTrips  = new ArrayList<>();
		ChargingActivityStats.listOfChargingEvents = new LinkedList<>();

		// Reset random objects
		ManageCars.setRandom(null);

		
		for(CarTrip carTrip: CarTrip.allCarTrips){
			
			// Reset Charge and failedTrips and driven Distance
			carTrip.setFailedTrip(false);
			carTrip.setTotalDrivenDistance(0);
			

			carTrip.getCar().resetTimeAtChargingStation();
			carTrip.getCar().setCarModel(ManageCars.selectRandomCarModel());
			carTrip.getCar().setChargeStatus(0);
			carTrip.getCar().setDayPhaseChargeStatus(new LinkedHashMap<>());
			carTrip.getCar().setEmptyBattery(false);
	
						
			int i = 0; // For first activity
			boolean firstActivity = true;
			
			// Assign new chargers
			for(Activity activity: carTrip.getActivities()){
				
				if(!(activity instanceof ChargingActivity)){
					if(i++>0)
						firstActivity = false;		

					ManageActivities.randomlyAssignCharger(activity, firstActivity);
				}
			}
			
			// Reset failed Leg status
			for(Leg leg: carTrip.getLegs()){
				leg.setFailedLeg(false);
				leg.setDistanceDriven(0);
				
			}
		}
		
	}
	
	
	private static void populateCarModelShare(){
		
		for(Map<String, String> carModelShareMap : carModelShareList){
			
			if(carModelShareMap.containsKey("carModelID") && carModelShareMap.get("probability") != null)
				RunAnalysis.carModelsShare.add(new ProbabilityItemCar(
					Integer.parseInt(carModelShareMap.get("carModelID")), 
					Double.parseDouble(carModelShareMap.get("probability"))
				));	
			
		}		
	}
	
	private static void populateChargingTypeShare(){
		
		for(Map<String, String> chargerTypeShareMap : chargerMatrixList){

			if(chargerTypeShareMap.containsKey("activityType")){
				String activityType = chargerTypeShareMap.get("activityType");
				
				// only process allowed activities
				//if(Arrays.asList(RunAnalysis.allowedActivityTypes).contains(activityType)){
					
					// Iterate through all entries
					for (Map.Entry<String, String> entry : chargerTypeShareMap.entrySet()){
						
						// 1. Iteration should capture the activityType and setUp List
						if(entry.getKey().equals("activityType")){
							RunAnalysis.chargerTypesShare.put(entry.getValue(), new ArrayList<>());
						
							// all other Iterations contain the chargers and their value
						} else {
							
							RunAnalysis.chargerTypesShare.get(activityType).add(new ProbabilityItemCharger(
								entry.getKey(),
								Double.parseDouble(entry.getValue())
							));
						}
						
					//}

				}
				
			}

			
		}
	}
	
	
	private static void generateCarModels() {
		
		CarModel.allCarModels = new HashMap<>();
		
		for(Map<String, String> dataMap : ManageDatafile.carModelList){
			
			if(dataMap.containsKey("carModelID")){
				CarModel carModel = new CarModel();
				
				// Assign car Properties
				carModel.setCarModelID(Integer.parseInt(dataMap.get("carModelID")));
				carModel.setModelName(dataMap.get("carModel"));
				carModel.setModelYear(Integer.parseInt(dataMap.get("modelYear")));
				carModel.setRange(Double.parseDouble(dataMap.get("range")));
				carModel.setBatteryCapacity(Double.parseDouble(dataMap.get("batteryCapacity")));
				carModel.setEnergyConsumption(Double.parseDouble(dataMap.get("energyConsumption")));
				carModel.setOutletTypes(dataMap.get("availablePlugs").split(";"));
				
				CarModel.allCarModels.put(carModel.getCarModelID(), carModel);
			}
		}		

	}
	
	private static void generateChargerTypes(){
		
		ChargerType.allChargerTypes = new HashMap<>();
		
		for(Map<String, String> dataMap : ManageDatafile.chargerTypeList){
			
			ChargerType chargerType = new ChargerType();
			
			chargerType.setChargerType(dataMap.get("chargerType"));
			chargerType.setChargerPower(Double.parseDouble(dataMap.get("chargerPower")));
			
			ChargerType.allChargerTypes.put(chargerType.getChargerType(), chargerType);
		}
	}
	
	public static ArrayList<Map<String, String>> getLegDataList() {
		return legDataList;
	}
	

	public static ArrayList<Map<String, String>> getChargingStationList() {
		return chargingStationList;
	}
	
	public static void generateChargingStationTestPoints(){
		//parseServiceAreas();
		//parseExistingChargingStations(EXISTING_CHARGING_STATIONS_FILENAME);

		for(Map<String, String> dataMap : ManageDatafile.chargingStationList){

			String ID = dataMap.get("ID");
			double lat = Double.parseDouble(dataMap.get("Lat"));
			double lng = Double.parseDouble(dataMap.get("Lng"));
			int power = Integer.parseInt(dataMap.get("Power"));
			String name = dataMap.get("Name");
			String type = dataMap.get("Type");
			int ignore = Integer.parseInt(dataMap.get("Double"));

			if(ignore!=1){

				ChargingStation station = null;

				if(type.equals("existing_station")){

					ChargerType chargerType = new ChargerType();
					chargerType.setChargerPower(power);
					chargerType.setChargerType("Existing Station");

					station = new ExistingChargingStation(ID, new LatLng(lat, lng));
					station.setChargerType(chargerType);

				} else if(type.equals("service_station")){
					station = new ServiceArea(ID, new LatLng(lat, lng));
					if(power!= 0)
						station.getChargerType().setChargerPower(power);
				}

				station.setName(name);
				ChargingStation.allChargingStations.put(station.getTestPointID(), station);
			}
		}


	}

//	private static void parseServiceAreas() {
//
//		for(Map<String, String> dataMap : ManageDatafile.chargingStationList){
//			
//			ServiceArea serviceArea = new ServiceArea(dataMap.get("ID"),
//					new LatLng(
//							Double.parseDouble(dataMap.get("Lat")), 
//							Double.parseDouble(dataMap.get("Lng"))
//							));
//			
//					
//			ChargingStation.allChargingStations.put(serviceArea.getTestPointID(), serviceArea);
//		}
//				
//	}


//	private static void parseExistingChargingStations(String EXISTING_CHARGING_STATIONS_FILENAME) {
//	
//		String filename = RunAnalysis.basePathChargermap + EXISTING_CHARGING_STATIONS_FILENAME; 
//				
//		File file = new File(filename);
//	
//		try {
//			String jsonString = Files.toString(file, Charsets.UTF_8);
//	
//			JSONArray allPoi = new JSONArray(jsonString);
//	
//			Tests.debug("Existing charging stations: " + allPoi.length());
//	
//			for(int i = 0; i < allPoi.length(); i++){
//				JSONObject poi = allPoi.getJSONObject(i);
//	
//				String ID =  Integer.toString(poi.getInt("ID"));
//				double lat = poi.getJSONObject("AddressInfo").getDouble("Latitude");
//				double lng = poi.getJSONObject("AddressInfo").getDouble("Longitude");
//				String title = poi.getJSONObject("AddressInfo").getString("Title");
//				
//				ExistingChargingStation station = new ExistingChargingStation(ID,new LatLng(lat, lng));
//				station.setName(title);
//	
//				for(int j = 0; j < poi.getJSONArray("Connections").length(); j++){
//	
//					try{
//						int level = poi.getJSONArray("Connections").getJSONObject(j).getInt("LevelID");	
//						int power = poi.getJSONArray("Connections").getJSONObject(j).getInt("PowerKW");
//						
//						if(level == 3){
//							ChargerType chargerType = new ChargerType();
//							chargerType.setChargerPower(power);
//							chargerType.setChargerType("existing level 3");
//							station.setChargerType(chargerType);
//							
//						}
//						
//					} catch(JSONException e){
//					}
//				}
//				if(station.getChargerType() != null){
//					ChargingStation.allChargingStations.put(station.getTestPointID(), station);
//				}
//				
//	
//			}
//
//	
//		} catch (IOException | JSONException e1) {
//			e1.printStackTrace();
//		}
//	
//	}




	

	



}


