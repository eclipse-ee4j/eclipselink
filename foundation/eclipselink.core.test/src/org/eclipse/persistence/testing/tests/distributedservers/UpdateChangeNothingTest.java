/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
