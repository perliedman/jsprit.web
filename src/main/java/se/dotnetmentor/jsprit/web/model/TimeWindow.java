package se.dotnetmentor.jsprit.web.model;

class TimeWindow {
	public double earliest;
	public double latest;

	public TimeWindow() {}
	
	public TimeWindow(
			jsprit.core.problem.solution.route.activity.TimeWindow timeWindow) {
		earliest = timeWindow.getStart();
		latest = timeWindow.getEnd();
	}
	
	public jsprit.core.problem.solution.route.activity.TimeWindow build() {
		return jsprit.core.problem.solution.route.activity.TimeWindow.newInstance(earliest, latest);
	}		
}