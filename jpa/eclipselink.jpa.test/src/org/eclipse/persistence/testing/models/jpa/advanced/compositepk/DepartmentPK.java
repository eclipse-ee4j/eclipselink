/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

public class DepartmentPK {
    public String name;
    public String role;
    public String location;

    public DepartmentPK(){
    }
    
    public DepartmentPK(String name, String role, String location) {
        this.name = name;
        this.role = role;
        this.location = location;
    }

    public boolean equals(Object other) {
        if (other instanceof DepartmentPK) {
            final DepartmentPK otherDepartmentPK = (DepartmentPK) other;
            return (otherDepartmentPK.name.equals(name) && otherDepartmentPK.role.equals(role) && otherDepartmentPK.location.equals(location));
        }
        
        return false;
    }
    
    public int hashCode(){
        return name.hashCode() + role.hashCode() + location.hashCode();
    }
}
