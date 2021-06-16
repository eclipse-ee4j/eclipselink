/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.jaxb.xmlelementwrapper;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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

