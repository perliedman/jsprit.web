package se.dotnetmentor.jsprit.web;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import se.dotnetmentor.jsprit.web.model.Vrp;

import com.google.gson.Gson;

import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.Builder;

public class VrpJsonReader {
	private Builder builder;
	private URL osrmDistanceTableUrl;

	public VrpJsonReader(VehicleRoutingProblem.Builder builder, URL osrmDistanceTableUrl) {
		this.builder = builder;
		this.osrmDistanceTableUrl = osrmDistanceTableUrl;
	}
	
	public VehicleRoutingProblem read(Reader reader) throws IOException {
		Gson gson = new Gson();
		Vrp vrp = gson.fromJson(reader, Vrp.class);
		if (vrp != null) {
			return vrp.build(builder, osrmDistanceTableUrl);
		} else {
			throw new IOException("Unable to parse incoming JSON as VRP model");
		}
	}
}
