package geo.testpoints;

import java.io.Serializable;
import java.util.HashMap;

import com.google.maps.model.LatLng;

import model.ChargerType;

public abstract class ChargingStation implements TestPoint, Serializable {


	private static final long serialVersionUID = -3966179526984338760L;
	
	private String testPointID;
	
	private boolean active = false;

	
	private double lat;
	private double lng;

	private String address;
	private String name; 
	
	private ChargerType chargerType;
	
	public static HashMap<String, ChargingStation> allChargingStations = new HashMap<>();
	
	public ChargingStation(String testPointID, LatLng latLng) {
		
		this.testPointID = testPointID;
		this.lat = latLng.lat;
		this.lng = latLng.lng;
	}
	
	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public String getTestPointID() {
		return testPointID;
	}

	@Override
	public LatLng getLatLng() {
		return new LatLng(this.lat, this.lng);
	}

	@Override
	public String getAddress() {
		return address;
		
	}
	
	@Override
	public void setLatLng(LatLng latLng) {
		this.lat = latLng.lat;
		this.lng = latLng.lng;
	}
	
	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ChargerType getChargerType() {
		return chargerType;
	}

	public void setChargerType(ChargerType chargerType) {
		this.chargerType = chargerType;
	}


}
