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
public class EmployeeDepartmentList {
    public String name;

    @XmlElement(name="department-number")
    public java.util.ArrayList<Department> deps;
    
    public boolean equals(Object o) {
        if(!(o instanceof EmployeeDepartmentList) || o == null) {
            return false;
        } else {
            return ((EmployeeDepartmentList)o).deps.equals(this.deps);
        }
    }
    
    public String toString() {
        return "EMPLOYEE(" + deps + ")";
    }        
}