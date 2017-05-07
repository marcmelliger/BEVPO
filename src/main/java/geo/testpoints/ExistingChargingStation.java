package geo.testpoints;

import com.google.maps.model.LatLng;

import model.ChargerType;

public class ExistingChargingStation extends ChargingStation {

	private static final long serialVersionUID = -1040314790394258471L;

	public ExistingChargingStation(String testPointID, LatLng latLng) {
		super(testPointID, latLng);
		
		ChargerType chargerType = new ChargerType();
		chargerType.setChargerPower(50);
		chargerType.setChargerType("fast-charger");
		
		this.setChargerType(chargerType);
		
	}




}
