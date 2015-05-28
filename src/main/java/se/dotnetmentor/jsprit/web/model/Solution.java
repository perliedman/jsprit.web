package se.dotnetmentor.jsprit.web.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jsprit.core.problem.job.Job;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;

public class Solution {
	public Solution(VehicleRoutingProblemSolution s) {
		// TODO: distance
		time = s.getCost();
		
		Collection<Job> unassignedJobs = s.getUnassignedJobs();
		List<String> servicesList = new ArrayList<String>();
		List<String> shipmentsList = new ArrayList<String>();
		for (Job j : unassignedJobs) {
			if (j instanceof Service) {
				servicesList.add(j.getId());
			} else {
				shipmentsList.add(j.getId());
			}
		}
		unassigned = new Unassigned();
		unassigned.services = servicesList.toArray(new String[0]);
		unassigned.shipments = shipmentsList.toArray(new String[0]);
		no_unassigned = unassignedJobs.size();

		
		List<Route> routeList = new ArrayList<Route>();
		for (VehicleRoute r : s.getRoutes()) {
			routeList.add(new Route(r));
		}
		
		routes = routeList.toArray(new Route[0]);
	}
	
	public double distance;
	public double time;
	public int no_unassigned;
	public Route[] routes;
	public Unassigned unassigned;
}
