/*
 * Copyright (c) 2018, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     05/24/2018-2.7.2 Radek Felcman
//       - 534812 - HIERARCHY_REQUEST_ERR marshalling a CDATA-containing XmlAnyElement to a Node result
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Element;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class EmployeeLaxCDATA {
    @XmlAttribute
    public String name;

    @XmlElement(name="home-address")
    public Address homeAddress;

    @XmlAnyElement(lax = true)
    public Element element;

    public boolean equals(Object obj) {
        if(!(obj instanceof EmployeeLaxCDATA emp)) {
            return false;
        }

        if(!(name.equals(emp.name))) {
            return false;
        }
        if(!(homeAddress.equals(emp.homeAddress))) {
            return false;
        }

        XMLComparer comparer = new XMLComparer();

        Element next1 = element;
        Element next2 =  emp.element;

        if((next1 instanceof Element) && (next2 instanceof Element)) {
            Element nextElem1 = next1;
            Element nextElem2 = next2;
            if(!(comparer.isNodeEqual(nextElem1, nextElem2))) {
                return false;
            }

        } else if(!(next1.equals(next2))) {
                return false;
        }
        return true;
    }

}
