/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     mmacivor - 2.1 Initial Implementation
 ******************************************************************************/  
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
