package model.dayphases;

import java.io.Serializable;

import model.Car;
import model.CarTrip;
import model.Person;
import utils.dataAnalysisUtils;

public abstract class DayPhaseImpl implements DayPhase, Serializable, Comparable<DayPhase> {

	private static final long serialVersionUID = -5139608608783926947L;

	private String dayPhaseID;
	private int startTime; //minutes
	private int endTime;
	private String activityType;
	
	private CarTrip carTrip = null;
	private Car car = null;
	private Person person = null;
	

	
	public DayPhaseImpl(String dayPhaseID, int startTime, int endTime, String activityType) {
		
		this.setDayPhaseID(dayPhaseID);
		this.setActivityType(activityType);
		this.setStartTime(startTime);
		this.setEndTime(endTime);
		
	}

	@Override
	public String getDayPhaseID() {
		return dayPhaseID;
	}

	public void setDayPhaseID(String dayPhaseID) {
		this.dayPhaseID = dayPhaseID;
	}

	@Override

	public int getStartTime() {
		return startTime;
	}


	@Override
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	@Override
	public int getEndTime() {
		return endTime;
	}

	@Override
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	
	@Override
	public String getActivityType() {
		return activityType;
	}
	
	@Override
	public void setActivityType(String activityType) {
		//if(Arrays.asList(RunAnalysis.allowedActivityTypes).contains(activityType)){
			this.activityType = activityType;
		//} else {
		//	System.err.println("The activityType " + activityType + " is not an allowed one.");
		//}
		
	}
	

	public CarTrip getCarTrip() {
		return carTrip;
	}

	public void setCarTrip(CarTrip carTrip) {
		this.carTrip = carTrip;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public int getTime(){
		return (endTime - startTime);
	}
	
	public String parseFormatedTime(int time){
		int hours = time / 60;
		int minutes = time % 60;
	
		return dataAnalysisUtils.formatString("%02d:%02d", hours, minutes);
	}

	@Override
	public String toString() {		
		// substring removes model. 
		return this.getClass().getName().substring(16) + " " + getDayPhaseID() + dataAnalysisUtils.printHashCode(this) ;
	}
	
	public String toString(boolean moreInfo) {
		

		return toString() + " of " + this.getPerson() + ":\n" 
		+ " - time: " + parseFormatedTime(this.getStartTime()) + " to " + parseFormatedTime(this.getEndTime()) + ", activityType = " + activityType + "\n" 
		+ " - car: " + this.getCar() + "]\n"
		+ " - charge at end: " + dataAnalysisUtils.formatString("%.5f", this.getCar().getDayPhaseChargeStatus().get(this)) + " kWh\n";
		
		 
	}
	
	public int compareTo(DayPhase comparePhase) {

		int compareStartTime = comparePhase.getStartTime();

		//ascending order
		return this.getStartTime() - compareStartTime;

	}
	
	


}
