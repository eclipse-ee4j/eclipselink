/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/24/2018-2.7.2 Radek Felcman
//       - 534812 - HIERARCHY_REQUEST_ERR marshalling a CDATA-containing XmlAnyElement to a Node result
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class EmployeeLaxCDATA {
    @XmlAttribute
    public String name;

    @XmlElement(name="home-address")
    public Address homeAddress;

    @XmlAnyElement(lax = true)
    public Element element;

    public boolean equals(Object obj) {
        if(!(obj instanceof EmployeeLaxCDATA)) {
            return false;
        }

        EmployeeLaxCDATA emp = (EmployeeLaxCDATA)obj;
        if(!(name.equals(emp.name))) {
            return false;
        }
        if(!(homeAddress.equals(emp.homeAddress))) {
            return false;
        }

        XMLComparer comparer = new XMLComparer();

        Object next1 = element;
        Object next2 =  emp.element;

        if((next1 instanceof Element) && (next2 instanceof Element)) {
            Element nextElem1 = (Element)next1;
            Element nextElem2 = (Element)next2;
            if(!(comparer.isNodeEqual(nextElem1, nextElem2))) {
                return false;
            }

        } else if(!(next1.equals(next2))) {
                return false;
        }
        return true;
    }

}
