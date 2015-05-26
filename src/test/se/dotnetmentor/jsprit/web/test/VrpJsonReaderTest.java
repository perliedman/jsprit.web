package se.dotnetmentor.jsprit.web.test;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import se.dotnetmentor.jsprit.web.VrpJsonReader;
import jsprit.core.problem.VehicleRoutingProblem;
import junit.framework.TestCase;

public class VrpJsonReaderTest extends TestCase {
	public void testRead() throws IOException {
		FileReader reader = new FileReader("src/test/resources/vrp-test1.json");
		VrpJsonReader vrpReader = new VrpJsonReader(VehicleRoutingProblem.Builder.newInstance(), new URL("http://router.project-osrm.org/table"));
		VehicleRoutingProblem problem = vrpReader.read(reader);
		
		assertEquals(2, problem.getVehicles().size());
		assertEquals(9, problem.getJobs().size());
	}
}
