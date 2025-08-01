/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.inheritance.Bus;
import org.eclipse.persistence.testing.models.inheritance.Company;
import org.eclipse.persistence.testing.models.inheritance.Person;

import java.util.Vector;


/**
 * @author         Rick Barkhouse
 * @version        19 August 1999
 */
public class RelationshipTreeInsertTest extends AutoVerifyTestCase {
    public RelationshipTreeInsertTest() {
        setName("RelationshipTreeInsertTest");
        setDescription("");
    }

    @Override
    public void setup() {
        getAbstractSession().beginTransaction();
    }

    @Override
    public void reset() {
        getAbstractSession().rollbackTransaction();
    }

    @Override
    public void test() {
        Company company = new Company();
        company.setName("School Bus Services");

        Person busDriver = new Person();
        busDriver.setName("Otto");

        Bus bus = new Bus();
        bus.setDescription("School bus");

        bus.busDriver = busDriver;

        Vector vehicles = new Vector();
        vehicles.add(bus);
        company.getVehicles().setValue(vehicles);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(company);
        uow.commit();
    }

    @Override
    public void verify() {
        Expression exp1;
        ExpressionBuilder builder1;
        ReadObjectQuery query1;

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        builder1 = new ExpressionBuilder();
        exp1 = builder1.get("name").equal("Otto");
        query1 = new ReadObjectQuery(org.eclipse.persistence.testing.models.inheritance.Person.class, exp1);

        Person personFromDB = (Person)getSession().executeQuery(query1);

        if (personFromDB == null) {
            throw new TestErrorException("UnitOfWork did not write the Company's Bus' busDriver.");
        }
    }
}
