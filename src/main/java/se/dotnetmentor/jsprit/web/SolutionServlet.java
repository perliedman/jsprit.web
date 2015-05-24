package se.dotnetmentor.jsprit.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import jsprit.core.problem.VehicleRoutingProblem.Builder;
import jsprit.core.problem.io.VrpXMLReader;
import jsprit.core.problem.io.VrpXMLWriter;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;

public class SolutionServlet extends HttpServlet {
	private static WorkRepository workRepository = new WorkRepository();
	
	private Log log = LogFactory.getLog(SolutionServlet.class);

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "accept, content-type");
		
		String sessionId = getJobId(request);
		
		if (sessionId == null) {
			endpointDoesNotExist(response);
		}
	};
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/xml;charset=utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		String sessionId = getJobId(request);
		
		if (sessionId != null) {
			File temp = File.createTempFile("jsprit", ".xml");
			try {
				long bytesCopied = Files.copy(request.getInputStream(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
				log.info("Wrote VRP XML to temp file: " + bytesCopied + " bytes.");
				
				Builder builder = VehicleRoutingProblem.Builder.newInstance();
				VrpXMLReader reader = new VrpXMLReader(builder);
				reader.read(temp.getAbsolutePath());
				workRepository.setProblem(sessionId, builder.build());
			} finally {
				temp.delete();
			}
		} else {
			endpointDoesNotExist(response);
		}		
	}

	private void endpointDoesNotExist(HttpServletResponse response) throws IOException,
			ServletException {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		XMLSerializer serializer = new XMLSerializer(response.getWriter(), new OutputFormat(Method.XML, OutputFormat.Defaults.Encoding, true));
		try {
			serializer.serialize(createError("NOT_FOUND", "Specified endpoint does not exist."));
		} catch (ParserConfigurationException e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/xml;charset=utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");

		String sessionId = getJobId(request);
		
		if (sessionId != null) {
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
			endpointDoesNotExist(response);
		}		
    }

	private String getJobId(HttpServletRequest request) {
		Pattern getPattern = Pattern.compile("/([0-9a-zA-Z\\-]+)");
		String pi = request.getPathInfo();
		Matcher matcher = getPattern.matcher(pi);
		if (matcher.matches()) {
			return matcher.group(1).toLowerCase();
		} else {
			return null;
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
