package se.dotnetmentor.jsprit.web.model;

import java.util.Map;

import jsprit.core.problem.AbstractVehicle;
import jsprit.core.problem.Skills;
import jsprit.core.problem.vehicle.VehicleImpl;

class Vehicle {
	public String vehicle_id;
	public String type_id;
	public Location start_address;
	public Location end_address;
	public boolean return_to_depot;
	public Long earliest_start;
	public Long latest_end;
	public String[] skills;
	
	public Vehicle() {}
	
	public Vehicle(jsprit.core.problem.vehicle.Vehicle vehicle) {
		vehicle_id = vehicle.getId();
		type_id = vehicle.getType().getTypeId();
		start_address = new Location(vehicle.getStartLocation());
		if (vehicle.getEndLocation() != null) {
			end_address = new Location(vehicle.getEndLocation());
		}
		earliest_start = (long) vehicle.getEarliestDeparture();
		latest_end = (long) vehicle.getLatestArrival();
		Skills skills = vehicle.getSkills();
		this.skills = new String[skills.values().size()];
		int i = 0;
		for (String skill : skills.values()) {
			this.skills[i++] = skill;
		}
	}

	public AbstractVehicle build(Map<String, jsprit.core.problem.vehicle.VehicleType> types) {
		VehicleImpl.Builder builder = VehicleImpl.Builder.newInstance(vehicle_id)
				.setType(types.get(type_id))
				.setStartLocation(start_address.build());
		if (end_address != null && return_to_depot) {
			builder.setEndLocation(end_address.build());
		}
		if (earliest_start != null) {
			builder.setEarliestStart(earliest_start);
		}
		if (latest_end != null) {
			builder.setLatestArrival(latest_end);
		}

		if (skills != null) {
			for (String skill : skills) {
				builder.addSkill(skill);
			}
		}
		
		return builder.build();
	}
}