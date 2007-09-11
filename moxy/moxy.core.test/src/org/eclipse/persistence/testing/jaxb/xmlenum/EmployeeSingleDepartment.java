/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlenum;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee")
public class EmployeeSingleDepartment {
    public String name;
    
    @XmlElement(name="department-number")
    public Department department;
    
    public boolean equals(Object o) {
        if(!(o instanceof EmployeeSingleDepartment) || o == null) {
            return false;
        } else {
            return ((EmployeeSingleDepartment)o).department == this.department;
        }
    }
    
    public String toString() {
        return "EMPLOYEE(" + department + ")";
    }    
}