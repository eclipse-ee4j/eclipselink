/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="employee")
public class Employee {

    public String name;

    @XmlAttribute
    @XmlJavaTypeAdapter(ListToStringAdapter.class)
    public List<String> responsibilities;

    public boolean equals(Object obj) {
        if(!(obj instanceof Employee)) {
            return false;
        }

        Employee emp = (Employee)obj;
        return emp.name.equals(this.name)&& emp.responsibilities.equals(this.responsibilities);
    }
}
