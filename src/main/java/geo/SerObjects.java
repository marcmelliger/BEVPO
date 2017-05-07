package geo;

import java.io.Serializable;
import java.util.ArrayList;

public class SerObjects implements Serializable{
	

	
	private static final long serialVersionUID = 4653556330736726560L;
	
	public ArrayList<StepOnLeg> listOfAllSteps;
	
	public double distanceInMeters;
	public double distanceInHumanReadable;
	
	public double boundsSouthwestLat;
	public double boundsSouthwestLng;
	public double boundsNortheastLat;
	public double boundsNortheastLng;
	
	public double start_lat;
	public double start_lng;
	public double end_lat;
	public double end_lng;
	
	public String start_address;
	public String end_address;
	
	public SerObjects() {
		
	}
	
}