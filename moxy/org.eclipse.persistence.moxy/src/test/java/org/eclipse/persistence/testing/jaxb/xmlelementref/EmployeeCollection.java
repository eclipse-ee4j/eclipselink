/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
// mmacivor - June 05/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.JAXBElement;
import java.util.Iterator;

@XmlRootElement(name="employee-collection")
public class EmployeeCollection {

    @XmlElementRefs({@XmlElementRef(name="integer-root", namespace="myns"), @XmlElementRef(name="root"), @XmlElementRef(name="employee-collection")})
    public java.util.List refs;

    public boolean equals(Object emp) {
        if(this.refs.size() != ((EmployeeCollection)emp).refs.size()) {
            return false;
        }
        Iterator iter1 = this.refs.iterator();
        Iterator iter2 = ((EmployeeCollection)emp).refs.iterator();
        while(iter1.hasNext()) {
            Object next1 = iter1.next();
            Object next2 = iter2.next();
            if((next1 instanceof JAXBElement elem1) && (next2 instanceof JAXBElement elem2)) {
                if(!(elem1.getName().equals(elem2.getName()) && elem1.getValue().equals(elem2.getValue()) && elem1.getDeclaredType() == elem2.getDeclaredType())) {
                    return false;
                }
            } else if(!(next1.equals(next2))) {
                return false;
            }
        }

        return true;
    }
}
