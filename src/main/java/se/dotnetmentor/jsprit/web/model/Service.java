package se.dotnetmentor.jsprit.web.model;

import jsprit.core.problem.AbstractJob;
import jsprit.core.problem.Capacity;
import jsprit.core.problem.Skills;

class Service {
	public String id;
	public String name;
	public Location address;
	public double duration;
	public int[] size;
	public TimeWindow[] time_windows;
	public String[] required_skills;

	public Service() {}
	
	public Service(jsprit.core.problem.job.Service service) {
		id = service.getId();
		name = service.getName();
		address = new Location(service.getLocation());
		duration = service.getServiceDuration();
		
		Capacity dimensions = service.getSize();
		size = new int[dimensions.getNuOfDimensions()];
		for (int i = 0; i < size.length; i++) {
			size[i] = dimensions.get(i);
		}
		
		if (service.getTimeWindow() != null) {
			time_windows = new TimeWindow[] {new TimeWindow(service.getTimeWindow())};
		}

		Skills skills = service.getRequiredSkills();
		this.required_skills = new String[skills.values().size()];
		int i = 0;
		for (String skill : skills.values()) {
			this.required_skills[i++] = skill;
		}
	}
	
	public AbstractJob build() {
		jsprit.core.problem.job.Service.Builder builder = jsprit.core.problem.job.Service.Builder.newInstance(id);
		builder.setName(name);
		builder.setLocation(address.build());
		builder.setServiceTime(duration);
		
		for (int i = 0; i < size.length; i++) {
			builder.addSizeDimension(i, size[i]);
		}
					
		if (time_windows != null && time_windows.length > 0) {
			builder.setTimeWindow(time_windows[0].build());
		}
		
		if (required_skills != null) {
			for (String skill : required_skills) {
				builder.addRequiredSkill(skill);
			}
		}

		return builder.build();
	}
}