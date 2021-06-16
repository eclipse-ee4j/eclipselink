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

// Contributors:
//     Radek Felcman - 2.7.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "employee")
public class EmployeeSameElementAttributeName {
    @XmlType
    public static class EmployeeName {
        @XmlElement(name = "first")
        public String firstName;

        @XmlElement(name = "last")
        public String lastName;

        public boolean equals(Object object) {
            EmployeeName employeeName = ((EmployeeName) object);

            if ((firstName == null && employeeName.firstName != null) || (firstName != null && !firstName.equals(employeeName.firstName))) {
                return false;
            }

            if ((lastName == null && employeeName.lastName != null) || (lastName != null && !lastName.equals(employeeName.lastName))) {
                return false;
            }
            return true;
        }
    }

    @XmlAttribute
    public String name;

    @XmlElement(name = "name")
    public List<EmployeeName> names = new ArrayList<>();

    public boolean equals(Object object) {
        EmployeeSameElementAttributeName employee = ((EmployeeSameElementAttributeName) object);

        if ((name == null && employee.name != null) || (name != null && !name.equals(employee.name))) {
            return false;
        }

        if ((names == null && employee.names != null) || (names != null && !names.equals(employee.names))) {
            return false;
        }
        return true;
    }

}
