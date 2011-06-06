/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 *     08/04/2010-2.1.1 Guy Pelletier
 *       - 315782: JPA2 derived identity metadata processing validation doesn't account for autoboxing
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.DepartmentPK;

public class DepartmentAdminRolePK {
    public DepartmentPK department;

    public int admin;

    public DepartmentAdminRolePK(){
    }
    
    public DepartmentAdminRolePK(String depName, String depRole, String depLocation, int employeeId) {
        this.department = new DepartmentPK(depName, depRole, depLocation);
        this.admin = employeeId;
    }
    
    public int getAdmin(){
        return admin;
    }

    public DepartmentPK getDepartment(){
        return department;
    }

    public void setAdmin(int admin){
        this.admin = admin;
    }

    public void setDepartment(DepartmentPK department){
        this.department = department;
    }
    
    public boolean equals(Object object){
        if (!(object instanceof DepartmentAdminRolePK)){
            return false;
        }
        DepartmentAdminRolePK pk = (DepartmentAdminRolePK)object;
        return pk.getAdmin() == admin && pk.getDepartment().equals(department);
    }
    
    public int hashCode(){
        return department.hashCode() + admin;
    }
}
