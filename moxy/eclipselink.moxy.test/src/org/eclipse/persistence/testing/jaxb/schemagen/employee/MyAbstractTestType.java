package org.eclipse.persistence.testing.jaxb.schemagen.employee;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="abstract-type")
public abstract class MyAbstractTestType {
    @XmlElement
    public int abstractTypeInt;
}
