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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

public class DontMaintainCacheTest extends TestCase {
    protected Employee employeeFromDatabase;
    protected String firstName;

    public DontMaintainCacheTest() {
        super();
    }

    public void reset() {
        // Because the name of the employee was changed, clear the cache.
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        employeeFromDatabase = (Employee)getSession().readObject(Employee.class);
    }

    public void test() {
        firstName = employeeFromDatabase.getFirstName();
        employeeFromDatabase.setFirstName("Yvon");

    }

    protected void verify() {
        Employee employee;
        ReadObjectQuery query = new ReadObjectQuery();

        query.setSelectionObject(employeeFromDatabase);
        query.dontMaintainCache();

        employee = (Employee)getSession().executeQuery(query);

        if (!employee.getFirstName().equals(firstName)) {
            throw new TestErrorException("The dontMaintainCache test failed");
        }
    }
}
