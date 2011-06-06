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
package org.eclipse.persistence.testing.tests.distributedservers;

import java.util.Vector;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


/** 
 * Test changing private parts of an object.
 * 
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
        employee.setPeriod(new EmploymentPeriod(Helper.dateFromYearMonthDate(2001, 1, 1), Helper.dateFromYearMonthDate(2002, 2, 2)));
        // One to many private
        employee.setPhoneNumbers(new Vector());
        employee.addPhoneNumber(new PhoneNumber("home", "613", "2263374"));
        employee.addPhoneNumber(new PhoneNumber("office", "416", "8224599"));
        // Many to many
        employee.setProjects(new Vector());
        employee.addProject((Project)getUnitOfWork().readObject(SmallProject.class));
        employee.addProject((Project)getUnitOfWork().readObject(LargeProject.class));
        // Direct collection
        employee.setResponsibilitiesList(new Vector());
        employee.addResponsibility("make cafee");
        employee.addResponsibility("buy donuts");
        // One to one private/public
        employee.setAddress(new EmployeePopulator().addressExample12());
        // make sure that the employee is not his own manager
        Vector employees = getUnitOfWork().readAllObjects(Employee.class);
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
