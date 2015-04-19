package se.dotnetmentor.jsprit.web;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.SchrimpfFactory;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleImpl.Builder;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;

public class WorkRepository {
	private Map<String, Work> workItems = new HashMap<String, WorkRepository.Work>();
	private Executor executor = Executors.newFixedThreadPool(4);
	
	public VehicleRoutingProblem getProblem(String id) throws IOException {		
		return getWork(id).getProblem();
	}
	
	public Collection<VehicleRoutingProblemSolution> getSolutions(String id) throws IOException {
		return getWork(id).getSolutions();
	}
	
	private Work getWork(String id) throws IOException {
		Work work = workItems.get(id);
		if (work == null) {
			work = createWork();
			workItems.put(id, work);
		}
		
		return work;
	}
	
	private Work createWork() throws IOException {
		synchronized (workItems) {
			/*
			 * get a vehicle type-builder and build a type with the typeId "vehicleType" and one capacity dimension, i.e. weight, and capacity dimension value of 2
			 */
			final int WEIGHT_INDEX = 0;
			VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(WEIGHT_INDEX, 2);
			VehicleType vehicleType = vehicleTypeBuilder.build();
			
			/*
			 * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
			 */
			Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
			Location startLocation = Location.newInstance(11.947364, 57.699676);
			vehicleBuilder.setStartLocation(startLocation);
			vehicleBuilder.setType(vehicleType);
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
			VehicleRoutingTransportCosts transportCosts = new OsrmTransportCostsMatrix(builder);
			
			VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
			vrpBuilder.setRoutingCost(transportCosts);
			vrpBuilder.addVehicle(vehicle);
			vrpBuilder.addJob(service1).addJob(service2).addJob(service3).addJob(service4);

			VehicleRoutingProblem problem = vrpBuilder.build();
			
			Work work = new Work(problem);
			executor.execute(work);
			
			return work;
		}
	}
	
	private static class Work implements Runnable {
		private VehicleRoutingProblem problem;
		private Collection<VehicleRoutingProblemSolution> solutions = new ArrayList<VehicleRoutingProblemSolution>();
		
		public Work(VehicleRoutingProblem problem) {
			this.problem = problem;
		}
		
		public VehicleRoutingProblem getProblem() {
			return problem;
		}
		
		public Collection<VehicleRoutingProblemSolution> getSolutions() {
			return solutions;
		}
		
		public synchronized void setSolutions(Collection<VehicleRoutingProblemSolution> solutions) {
			this.solutions = solutions; 
		}

		public void run() {
			VehicleRoutingAlgorithm algorithm = new SchrimpfFactory().createAlgorithm(problem);
			setSolutions(algorithm.searchSolutions());
		}
	}
}
