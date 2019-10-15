/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.distributedservers;

import java.util.Vector;

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;


/**
 * Test changing private parts of an object.
 *
 */
public class UpdateChangeNothingTest extends ComplexUpdateTest {

    public UpdateChangeNothingTest() {
        super();
    }

    public UpdateChangeNothingTest(Employee originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        Employee employee = (Employee)this.workingCopy;
        // Direct to field
        employee.setFirstName(new String(employee.getFirstName().getBytes()));
        // Object type
        employee.setGender(new String(employee.getGender().getBytes()));
        // Transformation
        employee.setNormalHours(employee.getNormalHours().clone());
        // Aggregate
        employee.setPeriod(new EmploymentPeriod(employee.getPeriod().getStartDate(), employee.getPeriod().getEndDate()));
        // One to many private/public
        employee.setPhoneNumbers((Vector)employee.getPhoneNumbers().clone());
        employee.setProjects((Vector)employee.getProjects().clone());
        employee.setManagedEmployees((Vector)employee.getManagedEmployees().clone());
        // Direct collection
        employee.setResponsibilitiesList((Vector)employee.getResponsibilitiesList().clone());
        // One to one private/public
        employee.getAddress();
        employee.getManager();
    }
}
