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
		private List<String> ids = new ArrayList<String>();
		private List<Coordinate> coordinates = new ArrayList<Coordinate>();
		private URL osrmDistanceTableUrl;
		
		public Builder(URL osrmDistanceTableUrl) {
			this.osrmDistanceTableUrl = osrmDistanceTableUrl;
		}
		
		public static Builder newInstance(URL osrmDistanceTableUrl) {
			return new Builder(osrmDistanceTableUrl);
		}
		
		public Builder addLocation(Location location) {
			ids.add(location.getId());
			coordinates.add(location.getCoordinate());
			return this;
		}

		public Builder addLocation(String id, Coordinate coordinate) {
			ids.add(id);
			coordinates.add(coordinate);
			return this;
		}
		
		public OsrmTransportCostsMatrix build() throws IOException {
			return new OsrmTransportCostsMatrix(this);
		}
	}

	private int[][] distanceTableMatrix;
	private Map<String, Integer> locationIndices = new HashMap<String, Integer>();
	
	private OsrmTransportCostsMatrix(Builder builder) throws IOException {
		distanceTableMatrix = getDistanceTableMatrix(builder.osrmDistanceTableUrl, builder.coordinates);
		for (int i = 0; i < builder.coordinates.size(); i++) {
			locationIndices.put(builder.ids.get(i), i);
		}
	}
	
	private int[][] getDistanceTableMatrix(URL osrmDistanceTableUrl,
			List<Coordinate> locations) throws IOException {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Coordinate coord : locations) {
			if (!first) {
				builder.append('&');
			} else {
				first = false;
			}
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
		double transportTime = getTransportTime(from, to, departureTime, driver, vehicle);
		if(vehicle == null) return transportTime;
		VehicleCostParams costParams = vehicle.getType().getVehicleCostParams();
		return costParams.perTimeUnit*transportTime;
	}

}
