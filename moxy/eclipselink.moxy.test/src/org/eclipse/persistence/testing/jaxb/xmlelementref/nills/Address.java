package org.eclipse.persistence.testing.jaxb.xmlelementref.nills;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.xmlelementref.nills.Employee.Task;

public class Address {
	
	@XmlAttribute
	String city;
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Address)) {
			return false;
		}
		Address a = (Address) obj;
		return a.city.equals(this.city);
	}

}
