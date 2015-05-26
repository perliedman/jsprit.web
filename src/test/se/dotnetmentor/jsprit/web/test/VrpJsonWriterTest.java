package se.dotnetmentor.jsprit.web.test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import se.dotnetmentor.jsprit.web.OsrmTransportCostsMatrix;
import se.dotnetmentor.jsprit.web.VrpJsonWriter;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.problem.vehicle.VehicleImpl.Builder;
import junit.framework.TestCase;

public class VrpJsonWriterTest extends TestCase {
	public void testWrite() throws IOException {
		/*
		 * get a vehicle type-builder and build a type with the typeId "vehicleType" and one capacity dimension, i.e. weight, and capacity dimension value of 2
		 */
		final int WEIGHT_INDEX = 0;
		VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("Car")
				.addCapacityDimension(WEIGHT_INDEX, 2)
				.setCostPerTime(1);
		VehicleType vehicleType = vehicleTypeBuilder.build();
		
		/*
		 * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
		 */
		Location startLocation = Location.newInstance(11.947364, 57.699676);
		Builder vehicleBuilder = VehicleImpl.Builder.newInstance("ABC123")
				.setStartLocation(startLocation)
				.setType(vehicleType);
		VehicleImpl vehicle = vehicleBuilder.build();
		
		/*
		 * build services at the required locations, each with a capacity-demand of 1.
		 */
		Service service1 = Service.Builder.newInstance("1").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(11.941967, 57.731812)).build();
		Service service2 = Service.Builder.newInstance("2").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(11.944757, 57.697016)).build();
		
		Service service3 = Service.Builder.newInstance("3").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(11.992629, 57.70456)).build();
		Service service4 = Service.Builder.newInstance("4").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(12.028248, 57.656589)).build();
		
		OsrmTransportCostsMatrix.Builder builder = OsrmTransportCostsMatrix.Builder.newInstance(new URL("http://router.project-osrm.org/table"));
		builder
			.addLocation(startLocation)
			.addLocation(service1.getLocation())
			.addLocation(service2.getLocation())
			.addLocation(service3.getLocation())
			.addLocation(service4.getLocation());
//		VehicleRoutingTransportCosts transportCosts = new OsrmTransportCostsMatrix(builder);
		
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
//		vrpBuilder.setRoutingCost(transportCosts);
		vrpBuilder.addVehicle(vehicle);
		vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4);

		VehicleRoutingProblem problem = vrpBuilder.build();

		VrpJsonWriter vrpWriter = new VrpJsonWriter();
		StringWriter writer = new StringWriter();
		vrpWriter.write(problem, writer);
		System.out.println(writer.toString());
	}
}
