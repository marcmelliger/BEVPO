package geo;

import java.io.Serializable;
import java.util.LinkedList;

public class RoutedLeg implements Serializable{
	

	private static final long serialVersionUID = 3955460169033208764L;

	private LinkedList<TestPointOnLeg> testPointsOnLeg;
	
	private TestPointOnLeg lastLeg;
	
	private String legId;

	public RoutedLeg(String legId, LinkedList<TestPointOnLeg> testPointsOnLeg, TestPointOnLeg lastLeg) {
		this.legId = legId;
		this.testPointsOnLeg = testPointsOnLeg;
		this.lastLeg = lastLeg;
	}

	public LinkedList<TestPointOnLeg> getTestPointsOnLeg() {
		return testPointsOnLeg;
	}

	public TestPointOnLeg getLastLeg() {
		return lastLeg;
	}

	public String getLegId() {
		return legId;
	}

}
