package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

import model.dayphases.Activity;
import model.dayphases.DayPhaseImpl;
import model.dayphases.Leg;

/**
 * @author marc
 *
 */
public class CarTrip implements Serializable{
	
	private static final long serialVersionUID = -3526116373489271298L;
	
	public static ArrayList<CarTrip> allCarTrips = new ArrayList<>();
	public static ArrayList<CarTrip> failedCarTrips; // init during each iteration
	public static ArrayList<CarTrip> successfullCarTrips; // init during each iteration
	
	private int carTripID;
	
	
	/**
	 * A CarTrip has one Trip each.  
	 */
	private Car car;
	
	private Household household;
	
	// TODO consider sorting
	private LinkedList<DayPhaseImpl> dayPhases;

	
	private boolean failedTrip;
	private double totalDrivenDistance;
	private double totalDistance = 0;

	private boolean ignoreCarTrip = false;
	

	public CarTrip(int tripId, Household household) {
		this.carTripID = tripId;
		this.setHousehold(household);
		
		this.failedTrip = false;
		dayPhases = new LinkedList<>();


	}
	

	// ----- Getters & Setters
	public int getCarTripID() {
		return carTripID;
	}

	public void setCarTripID(int tripId) {
		this.carTripID = tripId;
	}

//	public Person getPerson() {
//		return person;
//	}
//
//	public void setPerson(Person person) {
//		this.person = person;
//	}

	
	public Household getHousehold() {
		return household;
	}


	public void setHousehold(Household household) {
		this.household = household;
	}

	// ----- Methods


	public void addLeg(Leg leg){		
		addDayPhase(leg);
	}
	
	public LinkedList<Leg> getLegs() {
		
	    LinkedList<Leg> filteredList = dayPhases
	            .stream()
	            .filter(p -> p instanceof Leg)
	            .map(p -> (Leg) p)
	            .collect(Collectors.toCollection(LinkedList::new));
		
		return filteredList;
	}
	
	
	public void addActivity(Activity activity){
		addDayPhase(activity);
	}
	
	public LinkedList<Activity> getActivities() {
		
	    LinkedList<Activity> filteredList = dayPhases
	            .stream()
	            .filter(p -> p instanceof Activity)
	            .map(p -> (Activity) p)
	            .collect(Collectors.toCollection(LinkedList::new));
		
		return filteredList;
	}
	
	
	/**
	 * 
	 * Adds the dayPhase at the end of the current list.
	 * @param dayPhase
	 */
	public void addDayPhase(DayPhaseImpl dayPhase) {
		dayPhases.add(dayPhase);
		
	}
	
	/**
	 * Adds the dayPhaseToInsert and shifts the dayPhaseInFront to the right
	 * 
	 * @param dayPhaseToInsert
	 * @param dayPhaseBefore
	 */
	public void addDayPhaseBefore(DayPhaseImpl dayPhaseToInsert, DayPhaseImpl dayPhaseInFront) {
		
		if(dayPhases.contains(dayPhaseInFront)){
			dayPhases.add(dayPhases.indexOf(dayPhaseInFront), dayPhaseToInsert);
		} 
	}
	
	//public void replaceDayPhase(DayPhaseImpl dayPhaseToReplace, DayPhaseImpl dayPhaseReplacement){
	//	dayPhases. (index, c)
	//}
	
	public void replaceDayPhase(DayPhaseImpl dayPhaseToReplace, LinkedList<DayPhaseImpl> dayPhaseReplacementList){
		
		if(dayPhases.contains(dayPhaseToReplace)){
			dayPhases.addAll(dayPhases.indexOf(dayPhaseToReplace), dayPhaseReplacementList);
			dayPhases.remove(dayPhaseToReplace);
		}
	}

	
	public LinkedList<DayPhaseImpl> getDayPhases(){
		return dayPhases;
	}


	public Car getCar() {
		return car;
	}


	public void setCar(Car car) {
		this.car = car;
	}


	public boolean isFailedTrip() {
		return failedTrip;
	}


	public void setFailedTrip(boolean failedTrip) {
		this.failedTrip = failedTrip;
	}


	public double getTotalDrivenDistance() {
		return totalDrivenDistance;
	}

	public void setTotalDrivenDistance(double totalDrivenDistance) {
		this.totalDrivenDistance = totalDrivenDistance;
	}


	public void addToTotalDrivenDistance(double totalDrivenDistance) {
		this.totalDrivenDistance += totalDrivenDistance;
	}
	
	public void addToTotalDistance(double distance) {
		this.totalDistance += distance;
	}
	
	public double getTotalDistance(){
		return totalDistance;
	}

	public boolean isIgnoreCarTrip() {
		return ignoreCarTrip;
	}


	public void setIgnoreCarTrip(boolean ignoreCarTrip) {
		this.ignoreCarTrip = ignoreCarTrip;
	}


	@Override
	public String toString() {
		return ((isIgnoreCarTrip())? "Ignored ":"") + "CarTrip " + carTripID;
	}





	
	
	

}
