/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.xmlenum;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="employee")
public class EmployeeDepartmentArray {
    public String name;

    @XmlElement(name="department-number")
    public Department[] deps;

    public boolean equals(Object o) {
        if(!(o instanceof EmployeeDepartmentArray) || o == null) {
            return false;
        }
        if(((EmployeeDepartmentArray)o).deps.length != (this.deps.length)){
            return false;
        }
        for(int i=0;i<deps.length; i++){
            if(!deps[i].equals(((EmployeeDepartmentArray)o).deps[i])){
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return "EMPLOYEE(" + deps + ")";
    }
}
