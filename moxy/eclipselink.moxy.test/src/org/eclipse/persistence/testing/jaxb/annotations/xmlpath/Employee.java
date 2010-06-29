/*******************************************************************************
* Copyright (c) 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Employee {
    @XmlAttribute
    @XmlPath("employee-info/@id")
    public Integer id;
    
    @XmlElement
    @XmlPath("personal-info/first-name/text()")
    public String firstName;
    
    @XmlPath("personal-info/last-name/text()")
    public String lastName;
    
    @XmlPath("contact-info/address-id/text()")
    @XmlIDREF
    public Address address;
    
    @XmlPath("contact-info/phones/phone-number")
    public List<PhoneNumber> phones;
    
    @XmlPath("responsibilities/responsibility/text()")
    public List<String> responsibilities;
    
    @XmlPath("extra-attributes")
    @XmlAnyAttribute
    public Map<QName, String> attributes;
    
    @XmlPath("any-object-root")
    public Object anyObject;
    
    @XmlPath("any-collection-root")
    public List<Object> anyCollection;
    
    public boolean equals(Object obj) {
        if(!(obj instanceof Employee)) {
            return false;
        }
        
        Employee emp = (Employee)obj;
        boolean equal = this.firstName.equals(emp.firstName);
        equal = equal && this.lastName.equals(emp.lastName);
        equal = equal && this.address.equals(emp.address);
        
        Iterator<PhoneNumber> phones1 = this.phones.iterator();
        Iterator<PhoneNumber> phones2 = emp.phones.iterator();
        while(phones1.hasNext()) {
            equal = (phones1.next().equals(phones2.next())) && equal;
        }
        
        for(QName nextKey:this.attributes.keySet()) {
            String value1 = this.attributes.get(nextKey);
            String value2 = emp.attributes.get(nextKey);
            equal = equal && value1.equals(value2);
        }
        return equal;
    }
}
