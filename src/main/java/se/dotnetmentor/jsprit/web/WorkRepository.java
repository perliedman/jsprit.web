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
		Work work = getWork(id);
		return work != null ? work.getProblem() : null;
	}
	
	public Collection<VehicleRoutingProblemSolution> getSolutions(String id) throws IOException {
		Work work = getWork(id);
		return work != null ? work.getSolutions() : null;
	}
	
	private Work getWork(String id) throws IOException {
		synchronized (this) {
			Work work = workItems.get(id);		
			return work;
		}
	}

	public void setProblem(String sessionId, VehicleRoutingProblem problem) {
		synchronized (this) {
			Work work = new Work(problem);
			workItems.put(sessionId, work);
			executor.execute(work);
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
