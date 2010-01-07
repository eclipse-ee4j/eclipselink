/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlroot.complex.inheritance;

import org.eclipse.persistence.testing.oxm.xmlroot.Person;

public class Employee extends Person {
    int empId;

    public Employee() {
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getEmpId() {
        return empId;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Employee)) {
            return false;
        }

        //return this.name.equals(((Person)obj).getName());
        if (this.name.equals(((Employee)obj).getName())) {
            if (this.getEmpId() == ((Employee)obj).getEmpId()) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "Employee(name=" + this.getName() + "  id:" + this.getEmpId() + ")";
    }
}
