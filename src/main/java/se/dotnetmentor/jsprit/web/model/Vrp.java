package se.dotnetmentor.jsprit.web.model;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.dotnetmentor.jsprit.web.OsrmTransportCostsMatrix;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.Builder;
import jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import jsprit.core.problem.job.Job;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.util.Coordinate;

public class Vrp {
	public Vehicle[] vehicles;
	public se.dotnetmentor.jsprit.web.model.VehicleType[] vehicle_types;
	public Service[] services;
	public Shipment[] shipments;
	
	public Vrp() {}
	
	public Vrp(VehicleRoutingProblem problem) {
		Collection<jsprit.core.problem.vehicle.Vehicle> vehicles = problem.getVehicles();
		this.vehicles = new Vehicle[vehicles.size()];
		int i = 0;
		for (jsprit.core.problem.vehicle.Vehicle vehicle : vehicles) {
			this.vehicles[i++] = new Vehicle(vehicle);
		}

		Collection<jsprit.core.problem.vehicle.VehicleType> vehicleTypes = problem.getTypes();
		this.vehicle_types = new se.dotnetmentor.jsprit.web.model.VehicleType[vehicleTypes.size()];
		i = 0;
		for (jsprit.core.problem.vehicle.VehicleType vehicleType : vehicleTypes) {
			this.vehicle_types[i++] = new se.dotnetmentor.jsprit.web.model.VehicleType(vehicleType);
		}

		Map<String, Job> jobs = problem.getJobs();
		List<Service> services = new ArrayList<Service>();
		for (jsprit.core.problem.job.Job job : jobs.values()) {
			if (job instanceof jsprit.core.problem.job.Service) {
				services.add(new Service((jsprit.core.problem.job.Service)job));
			}
		}
		this.services = services.toArray(new Service[0]);
		// TODO: shipment
	}

	public VehicleRoutingProblem build(Builder builder, URL osrmDistanceTableUrl) throws IOException {
		Map<String, jsprit.core.problem.vehicle.VehicleType> vehicleTypes = 
				new HashMap<String, jsprit.core.problem.vehicle.VehicleType>();
				
		for (se.dotnetmentor.jsprit.web.model.VehicleType type : vehicle_types) {
			vehicleTypes.put(type.type_id, type.build());
		}
		
		for (Vehicle vehicle : vehicles) {
			builder.addVehicle(vehicle.build(vehicleTypes));
		}
		
		for (Service service : services) {
			builder.addJob(service.build());
		}
		
		// TODO: shipment

		OsrmTransportCostsMatrix.Builder costMatrixBuilder = 
				OsrmTransportCostsMatrix.Builder.newInstance(osrmDistanceTableUrl);
		Map<String, Coordinate> locationMap = builder.getLocationMap();
		for (String id : locationMap.keySet()) {
			costMatrixBuilder.addLocation(id, locationMap.get(id));
		}
		builder.setRoutingCost(costMatrixBuilder.build());
		builder.setFleetSize(FleetSize.FINITE);

		return builder.build();
	}
}