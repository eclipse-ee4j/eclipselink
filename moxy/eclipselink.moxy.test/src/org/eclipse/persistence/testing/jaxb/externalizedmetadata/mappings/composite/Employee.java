/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - March 19/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite;

public class Employee {
    public Address homeAddress;
    public Address workAddress;
    public Address alternateAddress;
    public Phone phone1;
    public Phone phone2;
    public Foo foo;
    public Phone privatePhone;
    public Department department;
    public Department aDept;

    @javax.xml.bind.annotation.XmlTransient
    public boolean isADeptSet;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public Department getDepartment() {
        wasGetCalled = true;
        return department;
    }

    public void setDepartment(Department department) {
        wasSetCalled = true;
        this.department = department;
    }

    Department getADept() { 
        return aDept; 
    }
    
    void setADept(Department dept) {
        isADeptSet = true;
        aDept = dept; 
    }
    
    public boolean isSetADept() {
        return isADeptSet;
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Employee empObj;
        try {
            empObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }
        
        if (homeAddress == null && empObj.homeAddress != null) {
            return false;
        }
        if (workAddress == null && empObj.workAddress != null) {
            return false;
        }
        if (alternateAddress == null && empObj.alternateAddress != null) {
            return false;
        }
        if (phone1 == null && empObj.phone1 != null) {
            return false;
        }
        if (phone2 == null && empObj.phone2 != null) {
            return false;
        }
        if (department == null && empObj.department != null) {
            return false;
        }
        if (privatePhone == null && empObj.privatePhone != null) {
            return false;
        }

        return (homeAddress.equals(empObj.homeAddress) &&
                workAddress.equals(empObj.workAddress) &&
                alternateAddress.equals(empObj.alternateAddress) &&
                ((foo == null && empObj.foo == null) || foo != null && empObj != null && foo.equals(empObj.foo)) &&
                department.equals(empObj.department) &&
                phone1.equals(empObj.phone1) &&
                phone2.equals(empObj.phone2));
    }
}