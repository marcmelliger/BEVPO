package model;

import java.io.Serializable;
import java.util.HashMap;

public class Household implements Serializable{
	

	private static final long serialVersionUID = 4802693966602860004L;

	private int householdId;
	
	private int parkingSpaces;
	
	private HashMap<Integer,Person> persons;	
	private HashMap<String,Car> cars;
	private HashMap<Car, CarTrip> carTrips; // or multiple? -> currently only one
	

	public Household(int householdId) {
		this.householdId = householdId;
		persons = new HashMap<>(3);
		cars = new HashMap<>(10);
		carTrips = new HashMap<>();

	}
	
	public static HashMap<Integer, Household> getAllHouseholds() {
		
		HashMap<Integer, Household> allHouseholds = new HashMap<>();
		
		for(CarTrip carTrip : CarTrip.allCarTrips){
					
			allHouseholds.put(carTrip.getHousehold().getHouseholdId(), carTrip.getHousehold());
			 
		}
		return allHouseholds;
		
	}

	
	public int getHouseholdId() {
		return householdId;
	}

	public void setHouseholdId(int householdId) {
		this.householdId = householdId;
	}


	public int getParkingSpaces() {
		return parkingSpaces;
	}

	public void setParkingSpaces(int parkingSpaces) {
		this.parkingSpaces = parkingSpaces;
	}

	public HashMap<Integer, Person> getPersons() {
		return persons;
	}

	public void setPersons(HashMap<Integer, Person> persons) {
		this.persons = persons;
	}

	public void setCarTrips(HashMap<Car, CarTrip> carTrips) {
		this.carTrips = carTrips;
	}

	public HashMap<String,Car> getCars() {
		return cars;
	}

	public void addCar(Car car) {
		cars.put(car.getCarId(), car);
	}
	
	public Car getCar(String carID) {
		return cars.get(carID);
	}
	
	
	public CarTrip getCarTrip(Car car) {
		return getCarTrips().get(car);
	}
	
	public HashMap<Car, CarTrip> getCarTrips() {
		return carTrips;
	}
	
	public void addNewCarTrip(int tripId, Car car){
		CarTrip carTrip = new CarTrip(tripId, this);
		getCarTrips().put(car, carTrip);
		
	}
	
	

	@Override
	public String toString() {
		return "Household " + householdId + " with " + parkingSpaces + " parking spaces" ;
	}
	
	
	


}
