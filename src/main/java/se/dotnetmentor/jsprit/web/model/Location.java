package se.dotnetmentor.jsprit.web.model;

import jsprit.core.util.Coordinate;

class Location {
	public String location_id;
	public double lon;
	public double lat;

	public Location() {}
	
	public Location(jsprit.core.problem.Location location) {
		location_id = location.getId();
		Coordinate coordinate = location.getCoordinate();
		lon = coordinate.getX();
		lat = coordinate.getY();
	}

	public jsprit.core.problem.Location build() {
		// TODO: what about ids? Maybe cache etc.
		return jsprit.core.problem.Location.newInstance(lon, lat);
	}
}