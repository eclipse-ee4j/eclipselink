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
// dmccann - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmltype;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(factoryMethod="buildEmployee")
public class Employee {
    @XmlElement
    private String name;

    private Employee(String name) {
        super();
        this.name = name;
    }

    public static Employee buildEmployee() {
        return new Employee("John");
    }

    public boolean equals(Object o) {
        Employee e;
        try {
            e = (Employee) o;
        } catch (ClassCastException cce) {
            return false;
        }
        return this.name.equals(e.name);
    }
}
