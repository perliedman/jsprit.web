package se.dotnetmentor.jsprit.web.model;

import jsprit.core.problem.job.Job;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.route.activity.Start;
import jsprit.core.problem.solution.route.activity.TourActivity;
import jsprit.core.problem.solution.route.activity.TourActivity.JobActivity;

public class Activity {
	public Activity() {}
	
	public Activity(TourActivity ta) {
		if (ta instanceof JobActivity) {
			JobActivity ja = (JobActivity)ta;
			Job job = ja.getJob();
			if (job instanceof Service) {
				type = "service";
			} else {
				type = "shipment";
			}
			id = job.getId();
			arr_time = ja.getArrTime();
			end_time = ja.getEndTime();
		} else if (ta instanceof Start) {
			type = "start";
		} else {
			type = "end";
		}
	}
	
	public String type;
	public String id;
	public String location_id;
	public double arr_time;
	public double end_time;
}
