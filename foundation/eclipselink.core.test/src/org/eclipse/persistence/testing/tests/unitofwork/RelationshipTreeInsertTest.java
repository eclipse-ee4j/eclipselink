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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.inheritance.Bus;
import org.eclipse.persistence.testing.models.inheritance.Company;
import org.eclipse.persistence.testing.models.inheritance.Person;


/**
 * @author         Rick Barkhouse
 * @version        19 August 1999
 */
public class RelationshipTreeInsertTest extends AutoVerifyTestCase {
    public RelationshipTreeInsertTest() {
        setName("RelationshipTreeInsertTest");
        setDescription("");
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
    }

    public void test() {
        Company company = new Company();
        company.setName("School Bus Services");

        Person busDriver = new Person();
        busDriver.setName("Otto");

        Bus bus = new Bus();
        bus.setDescription("School bus");

        bus.busDriver = busDriver;

        Vector vehicles = new Vector();
        vehicles.addElement(bus);
        company.getVehicles().setValue(vehicles);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(company);
        uow.commit();
    }

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
