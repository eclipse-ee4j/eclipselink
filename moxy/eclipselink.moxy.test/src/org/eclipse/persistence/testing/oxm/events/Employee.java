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
package org.eclipse.persistence.testing.oxm.events;
/**
 *  @version $Header: Employee.java 17-may-2006.14:00:39 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class Employee {

    public Employee() {
        phoneNumbers = new java.util.ArrayList();
    }
    public Address address;
    public java.util.ArrayList phoneNumbers;

    public Object anyObject;

    public java.util.Collection anyCollection;

    public boolean equals(Object obj) {
        boolean equal = false;
        if(!(obj instanceof Employee)) {
            return false;
        }
        Employee objEmp = (Employee)obj;
        equal = (objEmp.address == address) || (objEmp.address != null && address != null && objEmp.address.equals(address));

        equal = equal && ((objEmp.phoneNumbers == phoneNumbers) || (objEmp.phoneNumbers != null && phoneNumbers != null && objEmp.phoneNumbers.equals(phoneNumbers)));
        equal = equal && ((objEmp.anyObject == anyObject) || (objEmp.anyObject != null && anyObject != null && objEmp.anyObject.equals(anyObject)));
        equal = equal && ((objEmp.anyCollection == anyCollection) || (objEmp.anyCollection != null && anyCollection != null && objEmp.anyCollection.equals(anyCollection)));

        return equal;
    }
}
