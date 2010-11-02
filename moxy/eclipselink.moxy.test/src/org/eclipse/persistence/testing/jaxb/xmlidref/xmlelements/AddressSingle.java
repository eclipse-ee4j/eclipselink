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
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@XmlRootElement(name="address")
public class AddressSingle {
    @XmlID
    @XmlAttribute(name="aid")
    public String id;
    
    @XmlElement(name="street")
    public String street;
    
    @XmlElement(name="city")
    public String city;
    
    @XmlElement(name="country")
    public String country;
    
    @XmlElement(name="zip")
    public String zip;
    
    @XmlInverseReference(mappedBy = "addressOrPhone")
    public List<EmployeeSingle> emp;
    
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AddressSingle)) {
            return false;
        }
        AddressSingle tgtAddress = (AddressSingle) obj;
        return (tgtAddress.city.equals(city) &&
                tgtAddress.country.equals(country) &&
                tgtAddress.id.equals(id) &&
                tgtAddress.street.equals(street) &&
                tgtAddress.zip.equals(zip) && (tgtAddress.emp == emp || (emp != null && tgtAddress.emp != null)));
    }
}

