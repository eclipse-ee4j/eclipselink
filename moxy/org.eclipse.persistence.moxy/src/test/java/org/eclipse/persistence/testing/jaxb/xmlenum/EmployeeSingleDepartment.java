/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlenum;

import java.math.RoundingMode;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "employee")
public class EmployeeSingleDepartment {

    public String name;

    @XmlElement(name = "department-number")
    public Department department;

    public RoundingMode roundingMode;

    public boolean equals(Object o) {
        if (!(o instanceof EmployeeSingleDepartment) || o == null) {
            return false;
        } else {
            EmployeeSingleDepartment e = ((EmployeeSingleDepartment) o);
            return (e.department == this.department) && (e.roundingMode == this.roundingMode);
        }
    }

    public String toString() {
        return "EMPLOYEE(" + department + ", " + roundingMode + ")";
    }

}
