package org.eclipse.persistence.testing.jaxb.schemagen.anonymoustype;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlRootElement(name = "process")
public class Process {
    @XmlElement(required = true)
    protected String input;
}
