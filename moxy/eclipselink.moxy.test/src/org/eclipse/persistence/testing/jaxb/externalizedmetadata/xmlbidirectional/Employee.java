/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 03/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlbidirectional;

import java.util.Iterator;

/*
 * The following annotations will be done via XML metadata:
 * 
 * @javax.xml.bind.annotation.XmlRootElement
 * 
 */
public class Employee {
    public String name;
 
    /*
     * The following annotations will be done via XML metadata:
     * 
     * @javax.xml.bind.annotation.XmlIDREF
     * @javax.xml.bind.annotation.XmlAttribute(name="address-id")
     * @org.eclipse.persistence.oxm.annotations.XmlBidirectional(targetAttribute="emp")
     * 
     */
    public Address address;
    
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee) obj;
        if (!(this.name.equals(emp.name))) {
            return false;
        }
        if (this.address == null) {
            return emp.address == null;
        }
        if (emp.address == null) {
            return false;
        }
        return address.equals(emp.address);
    }
}