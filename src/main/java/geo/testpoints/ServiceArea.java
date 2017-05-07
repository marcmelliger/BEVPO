package geo.testpoints;

import com.google.maps.model.LatLng;

import model.ChargerType;

public class ServiceArea extends ChargingStation {

	private static final long serialVersionUID = -1040314790394258471L;

	public ServiceArea(String testPointID, LatLng latLng) {
		super(testPointID,latLng);
		
		ChargerType chargerType = new ChargerType();
		chargerType.setChargerPower(50);
		chargerType.setChargerType("fastCharger 50 kW");
		
		this.setChargerType(chargerType);
		
	}


}
