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
package org.eclipse.persistence.testing.oxm.converter.typesafeenum;

import java.util.ArrayList;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private MyTypeSafeEnumClass gender;
    private MyTypeSafeEnumClass shirtSize;
    private ArrayList hatSizes;

    public Employee() {
        super();
        hatSizes = new ArrayList();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String newFirstName) {
        firstName = newFirstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String newLastName) {
        lastName = newLastName;
    }

    public MyTypeSafeEnumClass getShirtSize() {
        return shirtSize;
    }

    public void setShirtSize(MyTypeSafeEnumClass newSize) {
        shirtSize = newSize;
    }

    public void addHatSize(MyTypeSafeEnumClass hatSize) {
        hatSizes.add(hatSize);
    }

    public ArrayList getHatSizes() {
        return hatSizes;
    }

    public void setHatSizes(ArrayList newSize) {
        hatSizes = newSize;
    }

    public boolean equals(Object object) {
        try {
            Employee employee = (Employee)object;

            if (!this.getFirstName().equals(employee.getFirstName())) {
                return false;
            }
            if (!this.getLastName().equals(employee.getLastName())) {
                return false;
            }
            if (!this.getShirtSize().equals(employee.getShirtSize())) {
                return false;
            }
            if (this.getHatSizes().size() != employee.getHatSizes().size()) {
                return false;
            }

            if (!this.getHatSizes().isEmpty()) {
                if (!this.getHatSizes().containsAll(employee.getHatSizes())) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return "Employee: " + " fname:" + getFirstName() + " lname:" + getLastName() + " shirt size:" + getShirtSize() + " hat size:" + getHatSizes();
    }
}
