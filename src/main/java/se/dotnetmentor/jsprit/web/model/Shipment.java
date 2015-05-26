package se.dotnetmentor.jsprit.web.model;


class Shipment {
	public String id;
	public String name;
	public Job pickup;
	public Job delivery;
	public float[] size;
	public String[] required_skills;
}