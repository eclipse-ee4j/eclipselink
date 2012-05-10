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
 * dmccann - September 14/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlJoinNode;
import org.eclipse.persistence.oxm.annotations.XmlJoinNodes;

public class Employee {
    @XmlAttribute
    public int id;
    
    @XmlJoinNodes({
        @XmlJoinNode(xmlPath="work-address/@id", referencedXmlPath="@id"),
        @XmlJoinNode(xmlPath="work-address/city/text()", referencedXmlPath="city/text()")
    })
    public Address workAddress;
    
    public Employee() {}
    
    public Employee(int id, Address workAddress) {
        this.id = id;
        this.workAddress = workAddress;
    }
    
    public boolean equals(Object obj) {
        Employee emp;
        try {
            emp = (Employee) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        if (id != emp.id) {
            return false;
        }
        return workAddress.equals(emp.workAddress);
    }
}
