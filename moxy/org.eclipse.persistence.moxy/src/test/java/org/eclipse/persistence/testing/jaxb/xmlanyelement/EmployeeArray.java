/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import jakarta.xml.bind.annotation.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.eclipse.persistence.platform.xml.XMLComparer;

@XmlRootElement(name="employee")
@XmlType(name="employee")
public class EmployeeArray {
    @XmlAttribute
    public String name;

    @XmlElement(name="home-address")
    public Address homeAddress;

    @XmlAnyElement
    public Object[] elements;

    public boolean equals(Object obj) {
        if(!(obj instanceof EmployeeArray)) {
            return false;
        }

        EmployeeArray emp = (EmployeeArray)obj;
        if(!(name.equals(emp.name))) {
            return false;
        }
        if(!(homeAddress.equals(emp.homeAddress))) {
            return false;
        }
        if(!(elements.length == emp.elements.length)) {
            return false;
        }

        XMLComparer comparer = new XMLComparer();

        for(int i=0; i< elements.length; i++){
            Object next1 = elements[i];
            Object next2 = emp.elements[i];

            if((next1 instanceof org.w3c.dom.Element) && (next2 instanceof Element)) {
                Element nextElem1 = (Element)next1;
                Element nextElem2 = (Element)next2;
                if(!(comparer.isNodeEqual(nextElem1, nextElem2))) {
                    return false;
                }
            } else {
                if(!(next1.equals(next2))) {
                    return false;
                }
            }
        }
        return true;
    }

}
