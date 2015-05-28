package se.dotnetmentor.jsprit.web.model;

import java.util.ArrayList;
import java.util.List;

import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.activity.TourActivity;

public class Route {
	public Route() {}
	
	public Route(VehicleRoute r) {
		vehicle_id = r.getVehicle().getId();

		List<Activity> activityList = new ArrayList<Activity>();
		for (TourActivity ta : r.getActivities()) {
			activityList.add(new Activity(ta));
		}
		
		activities = activityList.toArray(new Activity[0]);
		
	}
	public String vehicle_id;
	public Activity[] activities;
}
