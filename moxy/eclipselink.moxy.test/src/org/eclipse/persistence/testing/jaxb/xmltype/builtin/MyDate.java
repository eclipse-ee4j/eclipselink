package org.eclipse.persistence.testing.jaxb.xmltype.builtin;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(namespace="http://www.w3.org/2001/XMLSchema", name="date")
public class MyDate {
	
	@XmlValue
	public String value;
}
