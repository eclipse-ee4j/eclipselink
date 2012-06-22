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
package org.eclipse.persistence.testing.tests.feature;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test default null values.
 */
public class NoDefaultNullValueTest extends AutoVerifyTestCase {
    Map saveDefaultNullValues;
    Employee employee;

    public NoDefaultNullValueTest() {
        setDescription("Verify that the appropriate values are put in the object when nulls are encountered on the database");
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getLogin().getPlatform().getConversionManager().setDefaultNullValues(saveDefaultNullValues);
    }

    protected void setup() {
        // save current null values for later restoration
        saveDefaultNullValues = getSession().getLogin().getPlatform().getConversionManager().getDefaultNullValues();
        getSession().getLogin().getPlatform().getConversionManager().setDefaultNullValues(new Hashtable());
        beginTransaction();

        employee = new Employee();
        // force first name to be NULL
        employee.setFirstName(null);
        employee.setLastName("Flintstone");
        employee.setSalary(22);
        employee.setGender("Male");

        getDatabaseSession().writeObject(employee);
        // force the salary to be NULL
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update SALARY set SALARY = null where EMP_ID = " + employee.getId()));
    }

    public void test() {
        // force a read from the database
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        try {
            employee = (Employee)getSession().readObject(employee);
        } catch (NullPointerException exception) {
            throw new TestErrorException("Null Pointer exception was thrown when null was returned from the database for a primitive attribute");
        }
    }

    protected void verify() {
    }
}
