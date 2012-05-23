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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

public class DeepRefreshTest extends TestCase {
    protected Employee employeeObject;
    protected String city;
    protected String managerName;
    protected int projectSize;
    protected int phoneSize;
    protected java.sql.Time startTime;
    protected java.sql.Date endDate;

    public DeepRefreshTest() {
        setDescription("This test verifies the deep refresh feature works properly");
    }

    public void reset() {
        // Because the name of the employee was changed, clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        employeeObject = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Charles"));
    }

    public void test() throws Exception {
        city = employeeObject.getAddress().getCity();
        employeeObject.getAddress().setCity("Chelmsford");

        startTime = employeeObject.getStartTime();
        employeeObject.setStartTime(null);

        endDate = employeeObject.getPeriod().getEndDate();
        employeeObject.getPeriod().setEndDate(null);

        managerName = employeeObject.getManager().getFirstName();
        employeeObject.getManager().setFirstName("Karl");

        phoneSize = employeeObject.getPhoneNumbers().size();
        employeeObject.getPhoneNumbers().removeAllElements();

        projectSize = employeeObject.getProjects().size();
        employeeObject.getProjects().removeAllElements();

        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(employeeObject);
        query.refreshIdentityMapResult();
        query.cascadeAllParts();

        getSession().executeQuery(query);
    }

    protected void verify() throws Exception {
        if (!(employeeObject.getAddress().getCity().equals(city))) {
            throw new TestErrorException("The refresh test failed, city not refreshed:" + city + ":" + employeeObject.getAddress().getCity());
        }

        if (!(employeeObject.getPeriod().getEndDate().equals(endDate))) {
            throw new TestErrorException("The refresh test failed, end-date not refreshed:" + endDate + ":" + employeeObject.getPeriod().getEndDate());
        }

	if (!(employeeObject.getStartTime().equals(startTime))) {
		throw new TestErrorException("The refresh test failed, start-time not refreshed:" + startTime + ":" + employeeObject.getStartTime());
	}
	
        if (employeeObject.getManager().getFirstName().equals("Karl")) {
            throw new TestErrorException("The refresh test failed, first-name not refreshed:Karl:" + employeeObject.getManager().getFirstName());
        }

        if (employeeObject.getPhoneNumbers().size() != phoneSize) {
            throw new TestErrorException("The refresh test failed, phones not refreshed:" + phoneSize + ":" + employeeObject.getPhoneNumbers());
        }

        if (employeeObject.getProjects().size() != projectSize) {
            throw new TestErrorException("The refresh test failed, projects not refreshed:" + projectSize + ":" + employeeObject.getProjects());
        }
    }
}
