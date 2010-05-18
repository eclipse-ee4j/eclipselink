package org.eclipse.persistence.testing.jaxb.annotations.xmlpath;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

public class Address {

    @XmlAttribute
    @XmlID
    public String id;
    public String street;
    public String city;
    
    public boolean equals(Object obj) {
        if(!(obj instanceof Address)) {
            return false;
        }
        Address addr = (Address)obj;
        return addr.id.equals(id) && addr.street.equals(street) && addr.city.equals(city);
    }
}
