package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.none;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="employee-type")
public class Employee {
    @XmlElement
    public String firstName;
    public String lastName;
    private int id;

    @XmlElement
    public int getId() {
        return id;
    }
    
    public boolean getIsSet() {
        return true;
    }
}
