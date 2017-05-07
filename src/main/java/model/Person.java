package model;

import java.io.Serializable;

public class Person implements Serializable {
	
	private static final long serialVersionUID = 1114884131648088010L;

	private int personId;
	
	private Household household;


	public Person(int personId, Household household) {
		this.personId = personId;
		this.household = household;
		//carTrips = new HashMap<>();

	}

	public int personId() {
		return personId;
	}
	
	public Household getHousehold() {
		return household;
	}


	public void setPersonId(int personId) {
		this.personId = personId;
	}

//	public CarTrip getCarTrip(Car car) {
//		return carTrips.get(car);
//	}
//	
//	public HashMap<Car, CarTrip> getCarTrips() {
//		return carTrips;
//	}

//	private void setTrip(CarTrip trip) {
//		this.trip = trip;
//	}
	
//	public void addNewCarTrip(int tripId, Car car){
//		CarTrip carTrip = new CarTrip(tripId, this);
//		carTrips.put(car, carTrip);
//		
//	}

	@Override
	public String toString() {
		return "Person " + personId + " in " + household;
	}
	
	

}
