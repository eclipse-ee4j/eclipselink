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

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test default null values.
 */
public class NullValueTest extends AutoVerifyTestCase {
    Map saveDefaultNullValues;
    Map saveDefaultDefaultNullValues;
    Employee employee;

    public NullValueTest() {
        setDescription("Verify that the appropriate values are put in the object when nulls are encountered on the database");
    }

    protected void setup() {
        // save current null values for later restoration
        saveDefaultDefaultNullValues = ConversionManager.getDefaultManager().getDefaultNullValues();
        saveDefaultNullValues = getSession().getLogin().getPlatform().getConversionManager().getDefaultNullValues();
        getSession().getLogin().getPlatform().getConversionManager().setDefaultNullValues(new Hashtable());
        getSession().getLogin().setDefaultNullValue(String.class, "null");
        getSession().getLogin().setDefaultNullValue(int.class, new Integer(-1));
        // Reinit mappings.
        for (DatabaseMapping mapping : getSession().getDescriptor(Address.class).getMappings()) {
            if (mapping.isDirectToFieldMapping()) {
                mapping.preInitialize(getAbstractSession());
            }
        }
        getAbstractSession().beginTransaction();

        employee = new Employee();
        employee.setFirstName("Fred");
        employee.setLastName("Flintstone");
        employee.setSalary(22);
        employee.setGender("Male");
        Address address = new Address();
        address.setCity(null);
        employee.setAddress(address);

        getAbstractSession().writeObject(employee);
        // force the salary to be NULL
        getSession().executeNonSelectingCall(new SQLCall("update SALARY set SALARY = null where EMP_ID = " + employee.getId()));
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getLogin().getPlatform().getConversionManager().setDefaultNullValues(saveDefaultNullValues);
        ConversionManager.getDefaultManager().setDefaultNullValues(saveDefaultDefaultNullValues);
    }

    public void test() {
        // force a read from the database
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        employee = (Employee)getSession().readObject(employee);

    }

    protected void verify() {
        if (!employee.getAddress().getCity().equals("null")) {
            throw new TestErrorException("Null value not converted correctly for string.");
        }

        if (employee.getSalary() != -1) {
            throw new TestErrorException("Null value not converted correctly for int.");
        }
    }
}
