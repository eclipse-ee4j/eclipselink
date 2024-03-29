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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelements;

import jakarta.xml.bind.annotation.*;

import java.util.Arrays;

@XmlRootElement(name="employee-data")
public class EmployeeArray
{
    @XmlElement(name="id")
    public int id;

    @XmlElements({@XmlElement(name="integer", type=Integer.class), @XmlElement(name="address", type=Address.class), @XmlElement(name="string", type=String.class)})
    public Object[] choice;

    public String toString()
    {
        return "EMPLOYEE: " + id + "\n choice=" + choice;
    }

    public boolean equals(Object object) {
        EmployeeArray emp = ((EmployeeArray)object);
        return emp.id == this.id && Arrays.equals(choice, emp.choice);
    }
}
