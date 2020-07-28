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
//     mmacivor - 2.1 Initial Implementation
package org.eclipse.persistence.testing.jaxb.xmlenum;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee")
public class EmployeeMapDepartmentKey {
    public String name;


    public java.util.Map<Department, String> deps;

    public boolean equals(Object o) {
        if(!(o instanceof EmployeeMapDepartmentKey) || o == null) {
            return false;
        } else {
            return ((EmployeeMapDepartmentKey)o).deps.equals(this.deps);
        }
    }

    public String toString() {
        return "EMPLOYEE(" + deps + ")";
    }
}
