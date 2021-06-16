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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.xmlenum;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


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
