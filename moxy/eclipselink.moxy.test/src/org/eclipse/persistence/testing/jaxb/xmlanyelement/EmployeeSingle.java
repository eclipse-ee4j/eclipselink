/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.Collection;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

@XmlRootElement(name="employee")
public class EmployeeSingle {
    @XmlAttribute
    public String name;

    @XmlElement(name="home-address")
    public Address homeAddress;

    @XmlAnyElement
    public Object element;

    public boolean equals(Object obj) {
        if(!(obj instanceof EmployeeSingle)) {
            return false;
        }

        EmployeeSingle emp = (EmployeeSingle)obj;
        if(!(name.equals(emp.name))) {
            return false;
        }
        if(!(homeAddress.equals(emp.homeAddress))) {
            return false;
        }

        XMLComparer comparer = new XMLComparer();

        Object next1 = element;
        Object next2 =  emp.element;

        if((next1 instanceof org.w3c.dom.Element) && (next2 instanceof Element)) {
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
