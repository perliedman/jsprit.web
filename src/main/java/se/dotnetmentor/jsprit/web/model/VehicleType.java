package se.dotnetmentor.jsprit.web.model;

import jsprit.core.problem.Capacity;
import jsprit.core.problem.vehicle.VehicleTypeImpl;

class VehicleType {
	public String type_id;
	public String profile;
	public int[] capacity;

	public VehicleType() {}
	
	public VehicleType(jsprit.core.problem.vehicle.VehicleType vehicleType) {
		type_id = vehicleType.getTypeId();
		Capacity dimensions = vehicleType.getCapacityDimensions();
		capacity = new int[dimensions.getNuOfDimensions()];
		for (int i = 0; i < capacity.length; i++) {
			capacity[i] = dimensions.get(i);
		}
	}

	public VehicleTypeImpl build() {
		VehicleTypeImpl.Builder vtBuilder = VehicleTypeImpl.Builder.newInstance(type_id);
		vtBuilder.setCostPerTime(1);
		vtBuilder.setCostPerDistance(0);
		for (int i = 0; i < capacity.length; i++) {
			vtBuilder.addCapacityDimension(i, capacity[i]);
		}
		
		return vtBuilder.build();
	}
}