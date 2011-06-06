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
package org.eclipse.persistence.testing.tests.proxyindirection;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class InsertTest extends AutoVerifyTestCase {
    public InsertTest() {
        setDescription("Tests InsertObject, including private ownership, using Proxy Indirection.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        Employee emp1 = new EmployeeImpl();
        emp1.setFirstName("Kevin");
        emp1.setLastName("Moore");
        emp1.setGender("Male");
        emp1.setAge(30);

        Address add = new AddressImpl();
        add.setStreet("600 Chromakey Dr.");
        add.setCity("Roswell");
        add.setState("NM");
        add.setCountry("USA");
        add.setPostalCode("21872");

        Cubicle cube1 = new CubicleImpl();
        cube1.setLength(7.7f);
        cube1.setWidth(12.2f);
        cube1.setHeight(5.0f);

        emp1.setAddress(add);
        cube1.setEmployee(emp1);

       ((DatabaseSession) getSession()).insertObject(add);
        getAbstractSession().insertObject(cube1);
        // Employee is private owned so should be inserted along with Cubicle.
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Employee kevin = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Kevin"));

        if (kevin == null) {
            throw new TestErrorException("No employee with first name \"Kevin\" was returned, private ownership may not have worked.");
        }

        // Test the indirection
        if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(AddressImpl.class).getSize() != 0) {
            throw new TestErrorException("ProxyIndirection did not work - Address was read in along with Employee.");
        }
        kevin.getAddress().getCity();
        if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(EmployeeImpl.class).getSize() == 0) {
            throw new TestErrorException("ProxyIndirection did not work - Address was not read in when triggered from Employee.");
        }
    }
}
