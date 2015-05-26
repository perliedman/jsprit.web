package se.dotnetmentor.jsprit.web;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import se.dotnetmentor.jsprit.web.model.Vrp;

import com.google.gson.Gson;

import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.Builder;

public class VrpJsonWriter {
	public VrpJsonWriter() {
	}
	
	public void write(VehicleRoutingProblem problem, Writer writer) throws IOException {
		Gson gson = new Gson();
		Vrp vrp = new Vrp(problem);
		gson.toJson(vrp, writer);
	}
}
