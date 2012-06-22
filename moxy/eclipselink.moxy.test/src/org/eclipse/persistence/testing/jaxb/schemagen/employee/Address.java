/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.schemagen.employee;
import javax.xml.bind.annotation.*;

@XmlType(name="address-type")
@XmlAccessorType(XmlAccessType.NONE)
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class Address {
    
    
    private String street;
    
    
    private String country;
    
    @XmlAttribute(name = "postal-code")
    public String postalCode;
    

    @XmlElement(name = "street-name")
    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getXCity() {
        return null;
    }
    
    @XmlSchemaType(name = "string")
    public void setXCity(String city) {
    }
    
    public String getCountry() {
        return country;
    }
    
    @XmlElement(name = "country-name")
    public void setCountry(String country) {
        this.country = country;
    }
    
    @XmlElement(type=Integer.class)
    public Object myObject;

    @XmlElement(defaultValue="1")
    public Integer myInteger;
}
