package org.eclipse.persistence.testing.jaxb.schemagen.inheritance;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class TransientCar {
    
	private String model;
    private String year;
    private String make;
    
    public int duplicate;
    
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public String getYear() {
		return year;
	}
	
	public void setYear(String year) {
		this.year = year;
	}
	
	public String getMake() {
		return make;
	}
	
	public void setMake(String make) {
		this.make = make;
	}

}