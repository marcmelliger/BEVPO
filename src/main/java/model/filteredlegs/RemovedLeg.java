package model.filteredlegs;

import java.io.Serializable;
import java.util.ArrayList;

import model.CarTrip;
import model.dayphases.Leg;

public class RemovedLeg implements Serializable{
	
	private static final long serialVersionUID = -3451995174131307705L;

	public static ArrayList<RemovedLeg> allRemovedLegs = new ArrayList<>();

	private Leg removedLeg;
	private CarTrip associatedTrip;
	private String reasonForRemoval;

	public RemovedLeg(Leg removedLeg, CarTrip associatedTrip, String reasonForRemoval) {
		
		this.removedLeg = removedLeg;
		this.associatedTrip = associatedTrip;
		this.reasonForRemoval = reasonForRemoval;
		
	}

	public Leg getRemovedLeg() {
		return removedLeg;
	}

	public CarTrip getAssociatedTrip() {
		return associatedTrip;
	}

	public String getReasonForRemoval() {
		return reasonForRemoval;
	}

	@Override
	public String toString() {
		return "RemovedLeg [removedLeg=" + removedLeg + ", associatedTrip=" + associatedTrip + ", reasonForRemoval="
				+ reasonForRemoval + "]";
	}


}
