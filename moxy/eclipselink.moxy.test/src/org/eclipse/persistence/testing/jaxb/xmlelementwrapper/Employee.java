/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmlelementwrapper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employee
{
    @XmlElement(namespace = "http://www.somenamespace.org/")
    public int id;
    @XmlElement(namespace = "http://www.somenamespace.org/")
    public String name;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee) obj;

        if (emp.id != id) {
            return false;
        }

        if (name == null && emp.name == null) {
            return true;
        }

        if (name != null && name.equals(emp.name)) {
            return true;
        }

        return false;

    }

    @Override
    public String toString() {
        return "Employee :  id = " + id + ", name = " + name;
    }
}

