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

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee")
public class EmployeeMultipleDepartmentChoice {
    public String name;
    
    @XmlElements ({
        @XmlElement(name="department-number", type=Department.class)
    })
    public List<Object> departments;
    
    public boolean equals(Object o) {
        if(!(o instanceof EmployeeMultipleDepartmentChoice) || o == null) {
            return false;
        } else {
            return ((EmployeeMultipleDepartmentChoice)o).departments.equals(this.departments);
        }
    }
    
    public String toString() {
        return "EMPLOYEE(" + departments + ")";
    }    
}
