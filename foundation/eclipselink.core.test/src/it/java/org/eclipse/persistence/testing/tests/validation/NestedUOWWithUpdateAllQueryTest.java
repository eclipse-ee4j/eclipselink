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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;


/**
 * Validation test for UpdateAllQuery's and nested UOW .
 *
 * @author Guy Pelletier
 * @version 1.0 May 17/04
 */
public class NestedUOWWithUpdateAllQueryTest extends ExceptionTest {
    public NestedUOWWithUpdateAllQueryTest() {
        super();
        setDescription("This test tests that you cannot execute an update all query within a nested UOW.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        super.reset();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
        expectedException = ValidationException.nestedUOWNotSupportedForModifyAllQuery();
    }

    public void test() {
        UnitOfWork uow1 = getSession().acquireUnitOfWork();
        UnitOfWork uow2 = uow1.acquireUnitOfWork();
        ExpressionBuilder eb = new ExpressionBuilder();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate(eb.get("lastName"), "dummyLastName");

        try {
            uow2.executeQuery(updateQuery);
        } catch (org.eclipse.persistence.exceptions.EclipseLinkException e) {
            caughtException = e;
        }
    }
}
