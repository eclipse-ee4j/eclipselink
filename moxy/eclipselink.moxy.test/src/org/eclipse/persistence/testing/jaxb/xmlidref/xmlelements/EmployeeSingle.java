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
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class EmployeeSingle {
    @XmlID
    @XmlAttribute(name="id")
    public String id;
    
    @XmlElement(name="name")
    public String name;
    
    @XmlIDREF
    @XmlElements({@XmlElement(name="address-id", type=AddressSingle.class), @XmlElement(name="phone-id", type=PhoneSingle.class)})
    public Object addressOrPhone;
    
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof EmployeeSingle)) {
            return false;
        }
        EmployeeSingle emp = (EmployeeSingle) obj;
        
        return addressOrPhone.equals(emp.addressOrPhone); 
   }
}

