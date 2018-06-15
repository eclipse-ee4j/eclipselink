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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue;

public class Employee {
    private int id;
    private int numericNoNullValue;
    private String firstName;

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public boolean equals(Object object) {
        try {
            Employee employee = (Employee)object;
            if(this.getID() != employee.getID()) {
                return false;
            }
            if(this.getNumericNoNullValue() != employee.getNumericNoNullValue()) {
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
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append("Employee(id=");
        aBuffer.append(getID());
        aBuffer.append(", numericNoNullValue=");
        aBuffer.append(getNumericNoNullValue());
        aBuffer.append(", firstName=");
        aBuffer.append(getFirstName());
        aBuffer.append(")");
        return aBuffer.toString();

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getNumericNoNullValue() {
        return numericNoNullValue;
    }

    public void setNumericNoNullValue(int numericNoNullValue) {
        this.numericNoNullValue = numericNoNullValue;
    }
}
