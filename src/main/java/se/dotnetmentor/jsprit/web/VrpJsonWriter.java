package se.dotnetmentor.jsprit.web;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import se.dotnetmentor.jsprit.web.model.Container;
import se.dotnetmentor.jsprit.web.model.Solution;
import se.dotnetmentor.jsprit.web.model.Vrp;

import com.google.gson.Gson;

public class VrpJsonWriter {
	public VrpJsonWriter() {
	}
	
	public void write(VehicleRoutingProblem problem, VehicleRoutingProblemSolution[] solutions, Writer writer) throws IOException {
		Gson gson = new Gson();
		Vrp vrp = new Vrp(problem);
		List<Solution> sols = new ArrayList<Solution>();
		for (VehicleRoutingProblemSolution s : solutions) {
			sols.add(new Solution(s));
		}
		
		gson.toJson(new Container(vrp, sols.toArray(new Solution[0])), writer);
	}
}
