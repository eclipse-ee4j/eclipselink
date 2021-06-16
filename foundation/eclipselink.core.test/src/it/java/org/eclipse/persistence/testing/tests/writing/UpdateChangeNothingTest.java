/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.writing;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.tests.writing.ComplexUpdateTest;

/**
 * Test changing private parts of an object.
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
        employee.setPeriod(new org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod(employee.getPeriod().getStartDate(), employee.getPeriod().getEndDate()));
        // Direct collection
        employee.setResponsibilitiesList((Vector)employee.getResponsibilitiesList().clone());
        // One to many private/public
        employee.setPhoneNumbers((Vector)employee.getPhoneNumbers().clone());
        employee.setProjects((Vector)employee.getProjects().clone());
        employee.setManagedEmployees((Vector)employee.getManagedEmployees().clone());
        // One to one private/public
        employee.getAddress();
        employee.getManager();
    }
}
