package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.unset.prop;

import javax.xml.bind.annotation.XmlTransient;

public class PhoneNumber {
    private String type;
    private String number;
    
    @XmlTransient
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    
    public boolean equals(Object obj) {
        PhoneNumber num = (PhoneNumber)obj;
        return type.equals(num.getType()) && number.equals(num.getNumber());
    }
}