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
 *     02/02/2009-2.0 Chris delahunt 
 *       - 241765: JPA 2.0 Derived identities 
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced.derivedid;

import org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk.Department;

/**
 * @author cdelahun
 *
 */

public class DepartmentAdminRole {
    Administrator admin;
    Department department;
    
    public DepartmentAdminRole () {
    }

    public Administrator getAdmin(){
        return admin;
    }

    public Department getDepartment(){
        return department;
    }
    
    public void setAdmin(Administrator admin){
        this.admin = admin;
    }
    
    public void setDepartment(Department department){
        this.department = department;
    }

}
