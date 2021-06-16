/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// dmccann - September 14/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

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
