package se.dotnetmentor.jsprit.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsprit.core.problem.Location;
import jsprit.core.problem.cost.AbstractForwardVehicleRoutingTransportCosts;
import jsprit.core.problem.driver.Driver;
import jsprit.core.problem.vehicle.Vehicle;
import jsprit.core.problem.vehicle.VehicleTypeImpl.VehicleCostParams;
import jsprit.core.util.Coordinate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class OsrmTransportCostsMatrix extends AbstractForwardVehicleRoutingTransportCosts {
	public static class Builder {
		private List<Location> locations = new ArrayList<Location>();
		private URL osrmDistanceTableUrl;
		
		public Builder(URL osrmDistanceTableUrl) {
			this.osrmDistanceTableUrl = osrmDistanceTableUrl;
		}
		
		public static Builder newInstance(URL osrmDistanceTableUrl) {
			return new Builder(osrmDistanceTableUrl);
		}
		
		public Builder addLocation(Location location) {
			locations.add(location);
			return this;
		}
	}

	private int[][] distanceTableMatrix;
	private Map<String, Integer> locationIndices = new HashMap<String, Integer>();
	
	public OsrmTransportCostsMatrix(Builder builder) throws IOException {
		distanceTableMatrix = getDistanceTableMatrix(builder.osrmDistanceTableUrl, builder.locations);
		for (int i = 0; i < builder.locations.size(); i++) {
			locationIndices.put(builder.locations.get(i).getId(), i);
		}
	}
	
	private int[][] getDistanceTableMatrix(URL osrmDistanceTableUrl,
			List<Location> locations) throws IOException {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Location l : locations) {
			if (!first) {
				builder.append('&');
			} else {
				first = false;
			}
			Coordinate coord = l.getCoordinate();
			builder
				.append("loc=")
				.append(coord.getY())
				.append(',')
				.append(coord.getX());
		}
		
		URL url = new URL(osrmDistanceTableUrl.toString() + "?" + builder.toString());
		
		InputStream stream = url.openStream();
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(new InputStreamReader(stream));
		JsonArray array = root.getAsJsonObject().get("distance_table").getAsJsonArray();
		
		int[][] distances = new int[array.size()][];
		for (int i = 0; i < array.size(); i++) {
			JsonArray innerArray = array.get(i).getAsJsonArray();
			distances[i] = new int[innerArray.size()];
			for (int j = 0; j < innerArray.size(); j++) {
				distances[i][j] = innerArray.get(j).getAsInt();
			}
		}
		
		return distances;
	}

	@Override
	public double getTransportTime(Location from, Location to,
			double departureTime, Driver driver, Vehicle vehicle) {
		return distanceTableMatrix[locationIndices.get(from.getId())][locationIndices.get(to.getId())];
	}

	@Override
	public double getTransportCost(Location from, Location to,
			double departureTime, Driver driver, Vehicle vehicle) {
		if(vehicle == null) return getTransportTime(from, to, departureTime, driver, vehicle);
		VehicleCostParams costParams = vehicle.getType().getVehicleCostParams();
		return costParams.perTimeUnit*getTransportTime(from, to, departureTime, driver, vehicle);
	}

}
