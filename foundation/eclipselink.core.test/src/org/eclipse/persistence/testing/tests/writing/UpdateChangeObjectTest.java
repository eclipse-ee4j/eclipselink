/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.writing;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * Test changing private parts of an object.
 */
public class UpdateChangeObjectTest extends ComplexUpdateTest {
    public UpdateChangeObjectTest() {
        super();
    }

    public UpdateChangeObjectTest(Employee originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        Employee employee = (Employee)this.workingCopy;

        // Transformation
        employee.setNormalHours(new java.sql.Time[2]);
        employee.setStartTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        employee.setEndTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        // Aggregate
        employee.setPeriod(new org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod(Helper.dateFromYearMonthDate(1901, 1, 1), Helper.dateFromYearMonthDate(1902, 2, 2)));
        // Many to many
        employee.setProjects(new Vector());
        employee.addProject((Project)getSession().readObject(SmallProject.class));
        employee.addProject((Project)getSession().readObject(LargeProject.class));
        // Direct collection
        employee.setResponsibilitiesList(new Vector());
        employee.addResponsibility("make cafee");
        employee.addResponsibility("buy donuts");

        employee.setResponsibilitiesList(new Vector());
        employee.addResponsibility("make more cafee");
        employee.addResponsibility("buy more donuts");

        // One to many private
        employee.setPhoneNumbers(new Vector());
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("home", "613", "2263374"));
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("office", "416", "8224599"));
        // One to one private/public
        employee.setAddress(new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator().addressExample12());
        // make sure that the employee is not his own manager
        Vector employees = getSession().readAllObjects(Employee.class);
        Employee manager = null;
        for(int i=0; i<employees.size(); i++) {
            manager = (Employee)employees.elementAt(i);
            if(!manager.getId().equals(employee.getId())) {
                break;
            }
        }
        employee.setManager(manager);
    }
}
