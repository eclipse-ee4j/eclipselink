package org.eclipse.persistence.testing.jaxb.xmltype.builtin;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType
public class EmploymentPeriod {
	
	@XmlElement
	public MyDate startDate;
	
	@XmlElement
	public MyDate endDate;
}
