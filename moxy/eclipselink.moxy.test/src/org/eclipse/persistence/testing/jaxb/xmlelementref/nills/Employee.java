/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmlelementref.nills;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employee {

    @XmlElementRef(name = "address")
    public JAXBElement<Address> address;

    @XmlElementRef(name = "task")
    public List<JAXBElement<Task>> tasks;

    static class Task {
        @XmlAttribute
        Integer id;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Task)) {
                return false;
            }
            Task t = (Task) obj;
            return t.id.equals(this.id);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Employee)) {
            return false;
        }
        Employee e = (Employee) obj;
        if (!isEqual(e.address, this.address)) {
            return false;
        }
        if (e.tasks.size() == this.tasks.size()) {
            Iterator<JAXBElement<Task>> i = this.tasks.iterator();
            Iterator<JAXBElement<Task>> j = e.tasks.iterator();
            while (i.hasNext()) {
                if (!isEqual(i.next(), j.next())) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean isEqual(JAXBElement<?> e1, JAXBElement<?> e2) {
        return e1.getName().equals(e2.getName()) &&
                e1.getDeclaredType().equals(e2.getDeclaredType()) &&
                (e1.isNil() == e2.isNil()) &&
                e1.getValue().equals(e2.getValue());
    }

}
