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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test cascaded refreshing of an object.
 */
public class RefreshTest extends TestCase {
    protected Employee employeeObject;
    protected String city;
    protected String managerName;
    protected int collectionSize;
    protected java.sql.Time startTime;
    protected java.sql.Date endDate;
    protected int responsibilityListSize;

    public RefreshTest() {
        setDescription("This test verifies the refresh feature works properly");
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

        collectionSize = employeeObject.getPhoneNumbers().size();
        employeeObject.getPhoneNumbers().removeAllElements();

        responsibilityListSize = employeeObject.getResponsibilitiesList().size();
        employeeObject.getResponsibilitiesList().removeAllElements();

        getSession().refreshObject(employeeObject);
    }

    protected void verify() throws Exception {
        if (!(employeeObject.getAddress().getCity().equals(city))) {
            throw new TestErrorException("The refresh test failed.");
        }

        if (!(employeeObject.getPeriod().getEndDate().equals(endDate))) {
            throw new TestErrorException("The refresh test failed.");
        }

        if (!(employeeObject.getManager().getFirstName().equals("Karl"))) {
            throw new TestErrorException("The refresh test failed.");
        }

        if (employeeObject.getPhoneNumbers().size() != collectionSize) {
            throw new TestErrorException("The refresh test failed.");
        }

        if (employeeObject.getResponsibilitiesList().size() != responsibilityListSize) {
            throw new TestErrorException("The refresh test failed.");
        }
    }
}
