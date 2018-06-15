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
// mmacivor - June 05/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.unmarshaller.autodetect;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.JAXBElement;

import java.util.Iterator;

@XmlRootElement(name="e")
public class EmployeeCollection {

    @XmlElementRefs({@XmlElementRef(name="integer-root"), @XmlElementRef(name="root"), @XmlElementRef(name="e")})
    public java.util.List refs;

    public boolean equals(Object emp) {
        if(this.refs == null && ((EmployeeCollection)emp).refs == null){
            return true;
        }
        if(this.refs.size() != ((EmployeeCollection)emp).refs.size()) {
            return false;
        }
        Iterator iter1 = this.refs.iterator();
        Iterator iter2 = ((EmployeeCollection)emp).refs.iterator();
        while(iter1.hasNext()) {
            Object next1 = iter1.next();
            Object next2 = iter2.next();
            if((next1 instanceof JAXBElement) && (next2 instanceof JAXBElement)) {
                JAXBElement elem1 = (JAXBElement)next1;
                JAXBElement elem2 = (JAXBElement)next2;
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
