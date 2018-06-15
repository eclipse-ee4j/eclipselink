/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

public class Employee {

    private Integer id;
    private String firstName;
    private String lastName;
    private boolean isSetFirstName = false;

    public Employee() {
        super();
    }

    // override default equals
    public boolean equals(Object object) {
        try {
            Employee employee = (Employee) object;
            if(this.getId() != employee.getId()) {
                if(this.getId() == null) {
                    return false;
                }
                if(!this.getId().equals(employee.getId())) {
                    return false;
                }
              }
            if(this.isSetFirstName() != employee.isSetFirstName()) {
                return false;
            }
            if(this.getFirstName() != employee.getFirstName()) {
              if(this.getFirstName() == null) {
                  return false;
              }
              if(!this.getFirstName().equals(employee.getFirstName())) {
                  return false;
              }
            }
            if(this.getLastName() != employee.getLastName()) {
              if(this.getLastName() == null) {
                  return false;
              }
              if(!this.getLastName().equals(employee.getLastName())) {
                  return false;
              }
            }
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public Integer getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isSetFirstName() {
        return isSetFirstName;
    }

    public void setFirstName(String firstName) {
        // no unset for now
        isSetFirstName = true;
        this.firstName = firstName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append("Employee(id=");
        aBuffer.append(getId());
        aBuffer.append(", firstName=");
        aBuffer.append(getFirstName());
        aBuffer.append(", isSetFirstName=");
        aBuffer.append(isSetFirstName());
        aBuffer.append(", lastName=");
        aBuffer.append(getLastName());
        aBuffer.append(")");
        return aBuffer.toString();
    }
}
