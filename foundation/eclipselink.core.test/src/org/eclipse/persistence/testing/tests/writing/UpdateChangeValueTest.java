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
package org.eclipse.persistence.testing.tests.writing;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * Test changing private parts of an object.
 */
public class UpdateChangeValueTest extends ComplexUpdateTest {
    public UpdateChangeValueTest() {
        super();
    }

    public UpdateChangeValueTest(Employee originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        Employee employee = (Employee)this.workingCopy;

        // Direct to field
        employee.setFirstName("Barney");
        // Object type
        employee.setFemale();
        // Transformation
        employee.setStartTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        // Aggregate
        employee.getPeriod().setStartDate(Helper.dateFromYearMonthDate(1901, 1, 1));
        if (!employee.getPhoneNumbers().isEmpty()) {
            PhoneNumber phone = (PhoneNumber)employee.getPhoneNumbers().firstElement();
            phone.setAreaCode("999");
        }
        employee.addPhoneNumber(new PhoneNumber("office", "416", "8224599"));
        // Many to many
        if (!employee.getProjects().isEmpty()) {
            Project lastProject = (Project)employee.getProjects().lastElement();
            employee.removeProject(lastProject);
        }
        employee.addProject((Project)getSession().readObject(LargeProject.class));

        // Direct collection
        if (!employee.getResponsibilitiesList().isEmpty()) {
            String lastResponsibility = (String)employee.getResponsibilitiesList().lastElement();
            employee.removeResponsibility(lastResponsibility);
        }
        employee.addResponsibility("buy lots of donuts");

        // One to one private
        employee.getAddress().setCity("Cornwall");
    }
}
