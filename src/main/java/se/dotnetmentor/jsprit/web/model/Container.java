package se.dotnetmentor.jsprit.web.model;

import se.dotnetmentor.jsprit.web.model.Solution;
import se.dotnetmentor.jsprit.web.model.Vrp;

public class Container {
	private Vrp problem;
	private Solution[] solutions;

	public Container(Vrp problem, Solution[] solutions) {
		this.problem = problem;
		this.solutions = solutions;
	}
}
