package manager;

import java.util.Collections;
import java.util.HashSet;

import model.CarTrip;
import model.dayphases.Leg;
import model.filteredlegs.RemovedLeg;

public class ManageLegs {
	
	
	/**
	 * Filters out trips that overlap due to codrivers
	 */
	public static void filterOutOverlappingLegs(CarTrip carTrip) {

		// loop through legs
		HashSet<Leg> legsToRemove = new HashSet<>();

		int size = carTrip.getLegs().size();
		
		//if(carTrip.getCarTripID() == 133){

		// compare all legs ...
		for(int i = 0; i < size; i++){
			Leg referenceLeg = carTrip.getLegs().get(i) ;

			// ... to every other leg...
			for(int j = 0; j <  size; j++){
				Leg comparedLeg = carTrip.getLegs().get(j);

				// ... but to itself
				if(referenceLeg != comparedLeg){					

					if(comparedLeg.personIsCoPassenger() && !referenceLeg.personIsCoPassenger()){

						// In case of any overlap, remove the co-passenger
						if(overlap(comparedLeg, referenceLeg)){
							legsToRemove.add(comparedLeg);
							RemovedLeg.allRemovedLegs.add(new RemovedLeg(comparedLeg, carTrip, "overlap"));
						}
					}	
					else if(comparedLeg.personIsCoPassenger() == referenceLeg.personIsCoPassenger()){

						Leg longerLeg = referenceLeg;
						Leg shorterLeg = comparedLeg; 

						if(comparedLeg.getDistance() > referenceLeg.getDistance()){
							longerLeg = comparedLeg;
							shorterLeg = referenceLeg;
						}

						// remove shorter leg
						if(overlap(shorterLeg, longerLeg)){
							
							// Make sure that the legs don't are of equal size				
							if(!legsToRemove.contains(longerLeg)){
								legsToRemove.add(shorterLeg);
								RemovedLeg.allRemovedLegs.add(new RemovedLeg(shorterLeg, carTrip, "shortleg"));
							}
						}
					}
				}
			}
		}
		
		//}
		if(!legsToRemove.isEmpty())
			carTrip.getDayPhases().removeAll(legsToRemove);
		
	}


	private static boolean overlap(Leg comparedLeg, Leg referenceLeg){

		return (
				// Start of compared Leg inside reference Leg
				(comparedLeg.getStartTime()  >= referenceLeg.getStartTime() && comparedLeg.getStartTime() < referenceLeg.getEndTime()) ||
				// End of compared Leg inside reference Leg
				(comparedLeg.getEndTime() <= referenceLeg.getEndTime() && comparedLeg.getEndTime() > referenceLeg.getStartTime()) ||
				// Total overlap
				(comparedLeg.getStartTime() == referenceLeg.getStartTime() && comparedLeg.getEndTime() == referenceLeg.getEndTime())
				);
	}
						
					

	
	public static void sortDayphases(CarTrip carTrip){
		
		Collections.sort(carTrip.getDayPhases());
		
		
	}

}
