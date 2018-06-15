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
package org.eclipse.persistence.testing.tests.distributedservers;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Test changing private parts of an object.
 *
 */
public class VerifyDeletedObjectsTest extends ComplexUpdateTest {
    public Vector numbers;

    public VerifyDeletedObjectsTest() {
        super();
    }

    protected void changeObject() {
        Employee employee = (Employee)this.workingCopy;
        employee.setFirstName("Bob");

        employee.setPhoneNumbers(new Vector());
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("home", "613", "2263374"));
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("office", "416", "8224599"));
    }

    public void setup() {
        this.originalObject = getSession().readObject(Employee.class);
        super.setup();
        this.numbers = (Vector)((Employee)this.distributedCopy).getPhoneNumbers().clone();
    }

    public void verify() {
        Employee distEmp = (Employee)getObjectFromDistributedSession(this.query);
        Enumeration enumtr = this.numbers.elements();
        while (enumtr.hasMoreElements()) {
            if (distEmp.getPhoneNumbers().contains(enumtr.nextElement())) {
                throw new org.eclipse.persistence.testing.framework.TestErrorException("Failed to delete private owned objects from distributed cache");
            }
        }
    }
}
