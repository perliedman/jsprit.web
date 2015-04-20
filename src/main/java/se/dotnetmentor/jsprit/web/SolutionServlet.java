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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xml.internal.serializer.Method;

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
			XMLSerializer serializer = new XMLSerializer(response.getWriter(), new OutputFormat(Method.XML, OutputFormat.Defaults.Encoding, true));
			try {
				serializer.serialize(createError("NOT_FOUND", "Specified endpoint does not exist."));
			} catch (ParserConfigurationException e) {
				throw new ServletException(e);
			}
		}		
    }

	private Element createError(String codeStr, String messageStr) throws ParserConfigurationException {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = documentBuilder.newDocument();
		Element error = doc.createElement("error");
		Element code = doc.createElement("code");
		code.setTextContent(codeStr);
		Element message = doc.createElement("message");		
		message.setTextContent(messageStr);
		error.appendChild(code);
		error.appendChild(message);
		doc.appendChild(error);
		return doc.getDocumentElement();
	}
}
