/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.3.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="customer")
public class Customer {

    @XmlAttribute
    public int id;

    public String name;

    //@XmlAnyElement(lax=true)
    @XmlElementRefs({
        @XmlElementRef(name="p", type=JAXBElement.class),
        @XmlElementRef(name="e", type=JAXBElement.class)
    })
    @XmlMixed
    public List<Object> contacts = new ArrayList<Object>();

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Employee [" + id + "] " + name + "\n");

        for (Iterator iterator = contacts.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            try {
                JAXBElement e = (JAXBElement) o;
                sb.append("\t" + e.getValue().toString() + "\n");
            } catch (Exception e) {
                sb.append("\t" + o.toString() + "\n");
            }
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (!(obj instanceof Customer)) return false;

        Customer c = (Customer) obj;

        if (this.id != c.id) return false;

        if (!(this.name.equals(c.name))) return false;

        // Compare contacts
        ListIterator e1 = this.contacts.listIterator();
        ListIterator e2 = c.contacts.listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            Object o1 = e1.next();
            Object o2 = e2.next();
            if(o1 instanceof JAXBElement && o2 instanceof JAXBElement) {
                o1 = ((JAXBElement)o1).getValue();
                o2 = ((JAXBElement)o2).getValue();
            }
            if (!(o1 == null ? o2 == null : o1.equals(o2))) {
                return false;
            }
        }
        return !(e1.hasNext() || e2.hasNext());
    }

}
