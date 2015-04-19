package se.dotnetmentor.jsprit.web;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.io.VrpXMLWriter;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;

public class SolutionServlet extends HttpServlet {
	private static WorkRepository workRepository = new WorkRepository();
	
	private Log log = LogFactory.getLog(SolutionServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/xml;charset=utf-8");

		String pi = request.getPathInfo();
		
		if (pi.equals("/solution")) {
			String sessionId = request.getSession().getId();
			VehicleRoutingProblem problem = workRepository.getProblem(sessionId);
			Collection<VehicleRoutingProblemSolution> solutions = workRepository.getSolutions(sessionId);
			
			response.setStatus(HttpServletResponse.SC_OK);
			File temp = File.createTempFile("jsprit", ".xml");
			try {
				new VrpXMLWriter(problem, solutions).write(temp.getAbsolutePath());
				ServletOutputStream responseStream = response.getOutputStream();
				try {
					long bytesCopied = Files.copy(temp.toPath(), responseStream);
					log.info("Wrote VRP XML as response: " + bytesCopied + " bytes.");
				} finally {
					responseStream.close();
				}
			} finally {
				if (!temp.delete()) {
					log.warn("Failed to delete temp file " + temp.getAbsolutePath());
				} else {
					log.info("Deleted temp file " + temp.getAbsolutePath());
				}
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}		
    }
}
