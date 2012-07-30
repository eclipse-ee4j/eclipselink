/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Vikram Bhatia
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlenum;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee")
public class EmployeeSingleDepartmentChoice {
    public String name;
    
    @XmlElements ({
        @XmlElement(name="department-number", type=Department.class)
    })
    public Department department;
    
    public boolean equals(Object o) {
        if(!(o instanceof EmployeeSingleDepartmentChoice) || o == null) {
            return false;
        } else {
            return ((EmployeeSingleDepartmentChoice)o).department == this.department;
        }
    }
    
    public String toString() {
        return "EMPLOYEE(" + department + ")";
    }    
}
