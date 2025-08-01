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
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.inheritance.Dog;
import org.eclipse.persistence.testing.models.inheritance.LabradorRetriever;

/**
 * Test to ensure the getCursorSize() method on CursoredStream returns the correct value for
 * Objects using inheritance and the same table.
 * @author Tom Ware
 */
public class CursoredStreamInheritanceTestCase extends TestCase {
    private int transportId = 0;

    @Override
    public void reset() {
        // Remove the elements that were added for this demo
        DatabaseSession session = (DatabaseSession)getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        uow.deleteObject(LabradorRetriever.example5());
        uow.deleteObject(LabradorRetriever.example6());
        uow.commit();
    }

    @Override
    public void setup() {
        // add some objects to a leaf node of the Dog branch of the inheritance tree
        DatabaseSession session = (DatabaseSession)getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(LabradorRetriever.example5());
        uow.registerNewObject(LabradorRetriever.example6());
        uow.commit();
    }

    @Override
    public void test() {
        DatabaseSession session = (DatabaseSession)getSession();
        ReportQuery query = new ReportQuery();
        ExpressionBuilder builder = new ExpressionBuilder();
        query.addAttribute("id", builder.get("id"));
        query.setReferenceClass(Dog.class);
        query.useCursoredStream();
        CursoredStream stream = (CursoredStream)session.executeQuery(query);
        int size = stream.size();
        if (size != 5) {
            throw new TestErrorException("The CursoredStream should be of size 5 and it is of size " + size + ".");
        }
    }

    @Override
    public void verify() {
    }
}
