/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.inheritance.Dog;
import org.eclipse.persistence.testing.models.inheritance.LabradorRetriever;

/**
 * Test to ensure the getCursorSize() method on CursoredStream returns the correct value for
 * Objects using inheritance and the same table.
 * @author Tom Ware
 */
public class CursoredStreamInheritanceTestCase extends TestCase {
    private int transportId = 0;

    public void reset() {
        // Remove the elements that were added for this demo
        DatabaseSession session = (DatabaseSession)getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        uow.deleteObject(LabradorRetriever.example5());
        uow.deleteObject(LabradorRetriever.example6());
        uow.commit();
    }

    public void setup() {
        // add some objects to a leaf node of the Dog branch of the inheritance tree
        DatabaseSession session = (DatabaseSession)getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(LabradorRetriever.example5());
        uow.registerNewObject(LabradorRetriever.example6());
        uow.commit();
    }

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
        ;
    }

    public void verify() {
    }
}
