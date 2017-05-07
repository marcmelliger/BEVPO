package geo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.NotFoundException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;

import analysis.Tests;
import geo.Exceptions.WrongCountryException;
import geo.testpoints.TestPoint;
import model.dayphases.Leg;
import runmodel.RunAnalysis;
import utils.AddressTrans;


public class RouteLeg {

	public static String googleApiKey;

	private static GeoApiContext context;

	private static double testRadiusNormal; //km 
	private static double testRadiusReplan;
	private static Boolean replan;

	public static int queryLimit;


	public static HashMap<String, Double> scalingFactorMap = new HashMap<>();
	public static HashMap<String, Double> scalingFactorMapBelow = new HashMap<>();

	public static int googleErrors = 0;

	public static Logger logger = Logger.getLogger("MyLog");

	public static int queryNr = 0;


	private static SerObjects getAndSerialiseListOfAllSteps(File file, DirectionsRoute route,
			DirectionsLeg directionsLeg) {
		SerObjects serObjects;
		ArrayList<StepOnLeg> listOfAllSteps = new ArrayList<>();

		for(DirectionsStep step: directionsLeg.steps){

			List<LatLng> list = step.polyline.decodePath();
			for(LatLng point : list){		
				listOfAllSteps.add(new StepOnLeg(point));		
			}
		}



		try {

			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream out;
			out = new ObjectOutputStream(fileOut);

			// Put Objects into serObject
			serObjects = new SerObjects();
			serObjects.listOfAllSteps = listOfAllSteps;
			serObjects.distanceInMeters = directionsLeg.distance.inMeters;
			serObjects.boundsNortheastLat = route.bounds.northeast.lat;
			serObjects.boundsNortheastLng = route.bounds.northeast.lng;
			serObjects.boundsSouthwestLat = route.bounds.southwest.lat;
			serObjects.boundsSouthwestLng = route.bounds.southwest.lng;
			serObjects.start_lat = directionsLeg.startLocation.lat;
			serObjects.start_lng = directionsLeg.startLocation.lng;
			serObjects.end_lat = directionsLeg.endLocation.lat;
			serObjects.end_lng = directionsLeg.endLocation.lng;
			serObjects.start_address = directionsLeg.startAddress;
			serObjects.end_address = directionsLeg.endAddress;

			out.writeObject(serObjects);
			out.close();
			fileOut.close();


			if(file.exists()){
				logger.log(Level.FINE,"Serialized data is saved in " + file.getName());
			}

			return serObjects;


		} catch (IOException e) {

			logger.log(Level.SEVERE, "Error while saving",e);
			return null;
		}



	}

	private static File getListOfStepsFile(String startAddress, String endAddress) {
		File file = new File(RunAnalysis.basePathSer + 
				"ListOfSteps" + File.separator +
				RunAnalysis.getCountryNameShort() + File.separator +
				startAddress + "_" + endAddress  + ".ser");
		return file;
	}

	private static RoutedLeg getTestPointsOnLeg(SerObjects serObjects, Leg correspondingLeg, 
			ArrayList<TestPoint> testPoints) throws Exception{

		// distance error factor of leg: example: routing: 100km ,  actual data distance = 120km -> data distance = f * routing
		double scalingFactor = correspondingLeg.getDistance() * 1000 / serObjects.distanceInMeters;

		logger.log(Level.FINE,"Nr of steps " + serObjects.listOfAllSteps.size());		

		LinkedList<TestPoint> testPointsCrossedList = new LinkedList<>();
		StringJoiner waypointSJ = new StringJoiner("|");
		for(TestPoint testPoint : testPoints){
	
			boolean testPointCrossed = false;

			// Loop through all points of route
			for(StepOnLeg routeStep : serObjects.listOfAllSteps){

				LatLng routePoint = routeStep.getLatLng();
				double distanceToPoint = DistanceCalculator.distance(routePoint.lat, routePoint.lng, testPoint.getLatLng().lat, testPoint.getLatLng().lng, "K");
				if(distanceToPoint <= RouteLeg.getTestRadius()){
					testPointCrossed = true;
					break;
				}

			}

			if(testPointCrossed){
				// TODO: not necessary, size of testPointsCrossedList sufficient
				testPointsCrossedList.add(testPoint);
				if(testPointsCrossedList.size()<23)
					waypointSJ.add(testPoint.getLatLng().toString());	
			}
		}

		LinkedList<TestPointOnLeg> testPointsOnLeg = new LinkedList<TestPointOnLeg>();
		TestPointOnLeg lastLeg = null;

		if(!testPointsCrossedList.isEmpty()){
			
			String startAddressLeg = correspondingLeg.getAddressStart(5);
			if(correspondingLeg.getAddressStart(3) == null)
				startAddressLeg = correspondingLeg.getAddressStart(2);
			
			String endAddressLeg = correspondingLeg.getAddressEnd(5);
			if(correspondingLeg.getAddressEnd(3) == null)
				endAddressLeg = correspondingLeg.getAddressEnd(2);

			int count = 0;
			boolean foundRoute = false;

			// First try address field one, then try address field two
			while(!foundRoute){


				DirectionsApiRequest request = DirectionsApi.getDirections(context, startAddressLeg, endAddressLeg);
				queryLimit++;

				String waypoints = waypointSJ.toString();
				request.waypoints("optimize:true|" + waypoints);
				request.region(RunAnalysis.countryToplevel);


				try{
					DirectionsRoute directionsRoute = request.await().routes[0];
					foundRoute = true;

					if(directionsRoute.warnings.length>0){
						for(String warning: directionsRoute.warnings)
							logger.warning(warning);
					}

					// All TestPoints

					for(int indexTestPoint = 0; indexTestPoint < (directionsRoute.legs.length - 1); indexTestPoint++){

						TestPoint testPoint = testPointsCrossedList.get(directionsRoute.waypointOrder[indexTestPoint]);
						DirectionsLeg directionsLeg = directionsRoute.legs[indexTestPoint];

						Tests.debug("Index Nr: " + indexTestPoint + " for " + directionsLeg.startLocation + " to " + directionsLeg.endLocation);

						TestPointOnLeg testPointOnLeg = newTestPointOnLeg(testPoint.getTestPointID(), directionsLeg, 
								correspondingLeg, scalingFactor);

						// TODO: Also save to file:
						//testPoint.setAddress(directionsLeg.endAddress);

						// To handle the first legs that have only PLZ as addresses. 
						// TODO: latter: improve and unify this.
						String startAddress = directionsLeg.startAddress;
						if(indexTestPoint==0)
							startAddress = correspondingLeg.getAddressStart();

						getAndSerialiseListOfAllSteps(getListOfStepsFile(startAddress, directionsLeg.endAddress), directionsRoute, directionsLeg);

						testPointsOnLeg.add(testPointOnLeg);

					}

					// The last leg
					DirectionsLeg directionsLeg = directionsRoute.legs[directionsRoute.legs.length-1];
					lastLeg = newTestPointOnLeg("", directionsLeg, correspondingLeg, scalingFactor);
					getAndSerialiseListOfAllSteps(getListOfStepsFile(directionsLeg.startAddress, correspondingLeg.getAddressEnd()), directionsRoute, directionsLeg);

				} catch (NotFoundException e){
					Leg leg = correspondingLeg;
					++count;

					if(count == 1){
						logger.log(Level.WARNING, 
								"No route returned (waypooints) for carTrip: " + leg.getCarTrip().getCarTripID() + " with '" + startAddressLeg + " to " + endAddressLeg + "'. Retry!" );
						// Retry.
						startAddressLeg = leg.getAddressStart(4);
						if(startAddressLeg == null)
							startAddressLeg = leg.getAddressStart(2);
						
						endAddressLeg = leg.getAddressEnd(4);
						if(startAddressLeg == null)
							startAddressLeg = leg.getAddressStart(2);

						} else if(count == 2){
							logger.log(Level.WARNING, 
									"No route returned for carTrip: " + leg.getCarTrip().getCarTripID() + " with '" + startAddressLeg + " to " + endAddressLeg + "'. Retry with Temp!" );
							
							String startAddressTemp = AddressTrans.getTransAddress(leg.getAddressStart(1));
							if(startAddressTemp != "")
								startAddressLeg = startAddressTemp + ", " + RunAnalysis.countryNameLong;
							
							String endAddressTemp = AddressTrans.getTransAddress(leg.getAddressEnd(1));
							if(endAddressTemp != "")
								endAddressLeg = endAddressTemp  + ", " + RunAnalysis.countryNameLong;

					} else {
						logger.log(Level.SEVERE, 
								"No route returned (waypooints) with alternative address for carTrip: " + correspondingLeg.getCarTrip().getCarTripID() + " with '" + startAddressLeg + " to " + endAddressLeg + "'" );
						RouteLeg.googleErrors ++;
						throw e; 
					}	
				}
			}

		}

		logger.log(Level.FINE,"");
		logger.log(Level.FINE,"Nr. of test points crossed:" + testPointsCrossedList.size());

		RoutedLeg routedLeg = new RoutedLeg(correspondingLeg.getDayPhaseID(), testPointsOnLeg, lastLeg);

		return routedLeg;
	}

	/**
	 * @param startOfLeg
	 * @param endOfLeg
	 * @param testPoints
	 * @throws Exception
	 * 
	 * @return Ordered Map including distance from startOfLeg and testPoint reference (for latter use with power)
	 */
	public static LinkedList<RoutedLeg> getTestPointsOnLegs(LinkedList<Leg> listOfLegs, ArrayList<TestPoint> testPoints) throws Exception{

		if(context == null){
			context = new GeoApiContext().setApiKey(googleApiKey);
		}

		LinkedList<RoutedLeg> testPointsOnLegs = new LinkedList<>(); 

		for(Leg leg: listOfLegs){

			SerObjects serObjects = null;

			File file = getListOfStepsFile(leg.getAddressStart(), leg.getAddressEnd());

			if(file.exists()){

				FileInputStream fileIn;
				try {
					fileIn = new FileInputStream(file);

					ObjectInputStream in = new ObjectInputStream(fileIn);

					serObjects = (SerObjects)in.readObject();
					Tests.debug("Loaded serialized data from " + file.getName());

					in.close();
					fileIn.close();

				} catch (IOException | ClassNotFoundException e) {
					logger.log(Level.SEVERE, "Serialisation Exception", e);
					e.printStackTrace();
				}


			} else {

				String startAddress = leg.getAddressStart(5);
				if(leg.getAddressStart(3) == null)
					startAddress = leg.getAddressStart(2);
				
				String endAddress = leg.getAddressEnd(5);
				if(leg.getAddressEnd(3) == null)
					endAddress = leg.getAddressEnd(2);

				int count = 0;
				boolean foundRoute = false;

				while(!foundRoute){

					DirectionsApiRequest request = DirectionsApi.getDirections(context, startAddress, endAddress);
					request.region(RunAnalysis.countryToplevel);

					try {

						DirectionsResult directionsResult = request.await();
						RouteLeg.queryNr++;

						DirectionsRoute route = directionsResult.routes[0]; // Only get one route from Google
						foundRoute = true;
						DirectionsLeg directionsLeg = route.legs[0]; // Only get one leg from Google (no waypoints)

						logger.log(Level.FINE,"----------");
						logger.log(Level.FINE,"Process Leg nr " + leg.getDayPhaseID());
						logger.log(Level.FINE,"Querried: " + directionsLeg.startAddress + " to " + directionsLeg.endAddress);
						logger.log(Level.FINE,"Distance: " + directionsLeg.distance.humanReadable);

						// Check if country machtes
						Boolean countryMatch = false;
						for(String countryName : RunAnalysis.countriesAllowedInGoogleResult){
							String toMatch = ".*" + countryName + "\\b.*";
							if(directionsLeg.startAddress.matches(toMatch) && directionsLeg.endAddress.matches(toMatch))	
								countryMatch = true;	
						}
						if(!countryMatch)				
							throw new WrongCountryException();

						serObjects = getAndSerialiseListOfAllSteps(file, route, directionsLeg);

					 
					} catch (WrongCountryException e){
						logger.log(Level.SEVERE,"Country is outside " + RunAnalysis.countryNameLong);
						
						throw e;
						
					} catch (NotFoundException e){
						++count;

						if(count == 1){
							logger.log(Level.WARNING, 
									"No route returned for carTrip: " + leg.getCarTrip().getCarTripID() + " with '" + startAddress + " to " + endAddress + "'. Retry!" );
							startAddress = leg.getAddressStart(4);
							if(startAddress == null)
								startAddress = leg.getAddressStart(2);
							
							endAddress = leg.getAddressEnd(4);
							if(endAddress == null)
								endAddress = leg.getAddressStart(2);
							
							
						} else if(count == 2){
							logger.log(Level.WARNING, 
									"No route returned for carTrip: " + leg.getCarTrip().getCarTripID() + " with '" + startAddress + " to " + endAddress + "'. Retry with Temp!" );
							
							String startAddressTemp = AddressTrans.getTransAddress(leg.getAddressStart(1));
							if(startAddressTemp != "")
								startAddress = startAddressTemp + ", " + RunAnalysis.countryNameLong;
							
							String endAddressTemp = AddressTrans.getTransAddress(leg.getAddressEnd(1));
							if(endAddressTemp != "")
								endAddress = endAddressTemp  + ", " + RunAnalysis.countryNameLong;

						} else {
							logger.log(Level.SEVERE, 
									"No route returned with alternative address for carTrip: " + leg.getCarTrip().getCarTripID() + " with '" + startAddress + " to " + endAddress + "'" );
							RouteLeg.googleErrors ++;
							throw e; 
						}

					} finally {
						// Unhandeled exception?
					}
				}

			}

			// ----- Get Testpoints on Leg

			// replan trips false, not in List -> !(false && !false) = true  true
			// replan trips false, in List  -> !(false && !true) = false  true   
			// replan Trips true, not in List -> !(true && !false) = false  false
			// replan trips true, in List -> !(true && !true) = true   true  
			

			RoutedLeg routedLeg = getTestPointsOnLeg(serObjects, leg, testPoints);
			testPointsOnLegs.add(routedLeg);

		}

		return testPointsOnLegs;

	}

	public static double getTestRadius() {

		if(isReplan()){
			return testRadiusReplan;
		} else {
			return testRadiusNormal;
		}

	}

	public static double getTestRadiusNormal() {
		return testRadiusNormal;
	}


	public static double getTestRadiusReplan() {
		return testRadiusReplan;
	}


	public static Boolean isReplan() {
		return replan;
	}

	private static TestPointOnLeg newTestPointOnLeg(String testPointID, DirectionsLeg directionsLeg, Leg correspondingLeg, double scalingFactor) {

		double legDuration = directionsLeg.duration.inSeconds / 60;
		double legDistance = directionsLeg.distance.inMeters / 1000;

		TestPointOnLeg testPointOnLeg = new TestPointOnLeg(testPointID, correspondingLeg.getDayPhaseID());

		testPointOnLeg.setDistanceToPoint(legDistance);
		testPointOnLeg.setTimeToPoint(legDuration);
		testPointOnLeg.setScalingFactor(scalingFactor);
		testPointOnLeg.setAddress(directionsLeg.endAddress);

		return testPointOnLeg;
	}

	public static void setReplan(Boolean replan) {
		RouteLeg.replan = replan;
	}



	public static void setTestRadiusNormal(double testRadius) {
		RouteLeg.testRadiusNormal = testRadius;
	}

	public static void setTestRadiusReplan(double testRadiusReplan) {
		RouteLeg.testRadiusReplan = testRadiusReplan;
	}



}


