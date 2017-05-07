package geo.testpoints;

import com.google.maps.model.LatLng;

public interface TestPoint {
	
	public String getTestPointID();
	
	public LatLng getLatLng();
	public String getAddress();
	void setLatLng(LatLng latLng);
	void setAddress(String address);
	

}
