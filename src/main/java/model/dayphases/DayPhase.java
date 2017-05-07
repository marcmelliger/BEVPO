/**
 * 
 */
package model.dayphases;

import model.Car;
import model.Person;

/**
 * @author marc
 * Interface for a phase of the day
 */
public interface DayPhase {

	public String getDayPhaseID();
	
	
	/**
	 * Get startTime in minutes
	 * @return
	 */
	public abstract int getStartTime();
	
	/**
	 * Set startTime in minutes
	 * @param startTime
	 */
	public abstract void setStartTime(int startTime);
	
	public abstract int getEndTime();
	public abstract void setEndTime(int startTime);
	
	public String getActivityType();
	public void setActivityType(String activityType);
	
	public Car getCar();
	public void setCar(Car car);
	
	public Person getPerson();
	public void setPerson(Person person);

	public String toString(boolean b);

	
}
