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
//     John Vandale - initial API and implementation
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.inheritance.Engineer;
import org.eclipse.persistence.testing.models.inheritance.Person;
import org.eclipse.persistence.testing.models.inheritance.SalesRep;

import java.util.Vector;

/**
 * Bug 327900 - Tests that queries with conform results in unit of work set don't
 * return subclasses in the UoW if the descriptor indicates not to read subclasses.
 */

public class ConformResultsSubclassesTest extends org.eclipse.persistence.testing.framework.TestCase {

    UnitOfWork uow;
    Vector people;
    Person result;

    public ConformResultsSubclassesTest() {
        setDescription("Test that ReadAllQuery and ReadObjectQuery don't return subclasses when conforming and don't read subclasses indicated.");
    }

    @Override
    public void setup() {
        uow = getSession().acquireUnitOfWork();
        Engineer engineer = new Engineer();
        engineer.setName("e");
        SalesRep salesrep = new SalesRep();
        salesrep.setName("s");
        Person person = new Person();
        person.setName("p");
        uow.registerNewObject(engineer);
        uow.registerNewObject(salesrep);
        uow.registerNewObject(person);
    }

    @Override
    public void test() {
        // test ReadAllQuery
        ReadAllQuery raq = new ReadAllQuery(Person.class);
        raq.conformResultsInUnitOfWork();
        people = (Vector) uow.executeQuery(raq);

        // test ReadObjectQuery
        ReadObjectQuery roq = new ReadObjectQuery(Person.class);
        roq.conformResultsInUnitOfWork();
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("name").equal("s");
        roq.setSelectionCriteria(exp);
        Person result = (Person) uow.executeQuery(roq);

        uow.release();

    }

    @Override
    public void verify() {
        // verify ReadAllQuery
        for (Object person : people) {
            Person result = (Person) person;
            if (result.name.equals("e") || result.name.equals("s")) {
                throwError("ReadAllQuery with conform in unit of work returned subclasses despite descriptor indication not to.");
            }
        }

        // verify ReadObjectQuery
        if (result != null) {
            throwError("ReadObjectQuery with conform in unit of work returned subclasses despite descriptor indication not to.");
        }
    }
}
