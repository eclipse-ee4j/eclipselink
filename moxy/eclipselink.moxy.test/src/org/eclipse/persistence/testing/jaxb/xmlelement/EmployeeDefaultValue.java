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
//     rbarkhouse - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "employee")
public class EmployeeDefaultValue {

    public static final String DEFAULT_NAME = "DEFAULT_NAME";

    @XmlElement(defaultValue = DEFAULT_NAME)
    public String name;

    @XmlElement(defaultValue = "123", nillable = true)
    public List<Integer> ints;

    public String toString() {
        return "EMPLOYEE: " + this.name;
    }

    public boolean equals(Object object) {
        EmployeeDefaultValue emp = ((EmployeeDefaultValue) object);
        return emp.name.equals(this.name) && ints.equals(emp.ints);
    }

}
