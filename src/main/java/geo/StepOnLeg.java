package geo;

import java.io.Serializable;

import com.google.maps.model.LatLng;

public class StepOnLeg implements Serializable{

	private static final long serialVersionUID = -583501672092082779L;
	
	double lat;
	double lng;

	public StepOnLeg(LatLng latLng) {
		super();
		this.lat = latLng.lat;
		this.lng = latLng.lng;
	}

	public LatLng getLatLng() {
		return new LatLng(lat, lng);
	}
	
}