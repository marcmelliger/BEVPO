package utils;


public class ProbabilityItemCar implements ProbabilityItem {
    
	private int relativeProb;
	private int CarModelID;
	
    public ProbabilityItemCar(int CarModelID, Double relativeProbabilityPercent) {
		super();
		
		relativeProbabilityPercent *= 100;
		this.relativeProb = relativeProbabilityPercent.intValue();
		this.CarModelID = CarModelID;
	}

	public int getRelativeProb() {
		return relativeProb;
	}

    public int getCarModelID() {
		return CarModelID;
	}

    
}
