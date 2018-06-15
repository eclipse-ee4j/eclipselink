/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     John Vandale - initial API and implementation
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.models.inheritance.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.expressions.*;
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

    public void verify() {
        // verify ReadAllQuery
        for( int i = 0; i < people.size(); i++ ) {
          Person result = (Person) people.get(i);
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
