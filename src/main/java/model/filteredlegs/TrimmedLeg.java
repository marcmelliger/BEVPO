package model.filteredlegs;

import java.io.Serializable;
import java.util.ArrayList;

import model.CarTrip;
import model.dayphases.Leg;

public class TrimmedLeg implements Serializable{
	
	private static final long serialVersionUID = 2700737892204922066L;

	public static ArrayList<TrimmedLeg> allTrimmedLegs  = new ArrayList<>();
	
	private Leg trimmedLeg;
	private CarTrip associatedTrip;
	private String reasonForTrimming;

	public TrimmedLeg(Leg trimmedLeg, CarTrip associatedTrip, String reasonForTrimming) {
		
		this.trimmedLeg = trimmedLeg;
		this.associatedTrip = associatedTrip;
		this.reasonForTrimming = reasonForTrimming;


	}

	public Leg getRemovedLeg() {
		return trimmedLeg;
	}

	public CarTrip getAssociatedTrip() {
		return associatedTrip;
	}

	public String getReasonForRemoval() {
		return reasonForTrimming;
	}

	@Override
	public String toString() {
		return "TrimmedLeg [trimmedLeg=" + trimmedLeg + ", associatedTrip=" + associatedTrip + ", reasonForTrimming="
				+ reasonForTrimming + "]";
	}


}
