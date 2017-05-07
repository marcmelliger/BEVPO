package utils;


public class ProbabilityItemCharger implements ProbabilityItem {
    
	private int relativeProb;
	private String chargerType;
	
    public ProbabilityItemCharger(String chargerType, Double relativeProbabilityPercent) {
		super();
		
		relativeProbabilityPercent *= 100;
		this.relativeProb = relativeProbabilityPercent.intValue();
		this.chargerType = chargerType;
	}

	public int getRelativeProb() {
		return relativeProb;
	}

    public String getChargerType() {
		return chargerType;
	}

	@Override
	public String toString() {
		return "[relativeProb=" + relativeProb + ", chargerType=" + chargerType + "]";
	}

    
}
