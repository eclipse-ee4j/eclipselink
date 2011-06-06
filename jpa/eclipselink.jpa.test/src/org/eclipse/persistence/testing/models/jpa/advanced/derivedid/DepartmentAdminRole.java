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
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Table;
import javax.persistence.ManyToOne;

import org.eclipse.persistence.testing.models.jpa.advanced.compositepk.Department;


/**
 * @author cdelahun
 *
 */

@Entity
@Table(name="CMP3_DEPT_ADMIN")
@IdClass(DepartmentAdminRolePK.class)
public class DepartmentAdminRole {
    private Administrator admin;
    private Department department;
    private AdminPool pool;

    public DepartmentAdminRole () {
    }

    @Id
    public Administrator getAdmin(){
        return admin;
    }

    @Id
    //This is required by the spec, but if not specified, defaults field names to NAME, ROLE and LOCATION. 
    @JoinColumns({
        @JoinColumn(name="DEPT_NAME", referencedColumnName="NAME"),
        @JoinColumn(name="DEPT_ROLE", referencedColumnName="DROLE"),
        @JoinColumn(name="DEPT_LOCATION", referencedColumnName="LOCATION")
    })
    public Department getDepartment(){
        return department;
    }

    public void setAdmin(Administrator admin){
        this.admin = admin;
    }

    public void setDepartment(Department department){
        this.department = department;
    }
    
    @ManyToOne
    @JoinColumn(name="POOL_ID")
    public AdminPool getPool() {
        return pool;
    }

    public void setPool(AdminPool pool) {
        this.pool = pool;
    }
    
    public DepartmentAdminRolePK buildDepartmentAdminRolePK(){
        return new DepartmentAdminRolePK(department.getName(), department.getRole(), department.getLocation(), admin.getEmployee().getId());
    }
}
