package model;

import java.io.Serializable;
import java.util.HashMap;

public class ChargerType implements Serializable {
	
	private static final long serialVersionUID = -4025603807429951365L;

	public static HashMap<String, ChargerType> allChargerTypes;

	private String chargerType;
	private double chargerPower;
	
	public static double defaultPower = 3.3;

	public ChargerType() {
	
		chargerPower = defaultPower;
		chargerType = "default";
	}



	public String getChargerType() {
		return chargerType;
	}

	public void setChargerType(String chargerType) {
		this.chargerType = chargerType;
	}

	public double getChargerPower() {
		return chargerPower;
	}

	public void setChargerPower(double chargerPower) {
		this.chargerPower = chargerPower;
	}

}
