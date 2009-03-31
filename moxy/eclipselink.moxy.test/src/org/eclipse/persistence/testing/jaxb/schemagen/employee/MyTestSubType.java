package org.eclipse.persistence.testing.jaxb.schemagen.employee;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="sub-type")
public class MyTestSubType extends MyAbstractTestType {
    @XmlElement
    public int subTypeInt;
}
