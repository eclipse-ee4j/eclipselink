/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.binder.nullpolicy;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlMarshalNullRepresentation;
import org.eclipse.persistence.oxm.annotations.XmlNullPolicy;

@XmlRootElement(name="employee")
public class EmployeeC {
    private String name;
    
    private String age;
    
    private String address;
    
    @XmlElement(name="name")
    @XmlNullPolicy(nullRepresentationForXml = XmlMarshalNullRepresentation.EMPTY_NODE)
    public String getName() {
        return name;
    }
    
    public void setName(String theName) {
        this.name = theName;
    }
    
    public String getAge() {
        return age;
    }
    
    public void setAge(String theAge) {
        this.age = theAge;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String addr) {
        this.address = addr;
    }
}