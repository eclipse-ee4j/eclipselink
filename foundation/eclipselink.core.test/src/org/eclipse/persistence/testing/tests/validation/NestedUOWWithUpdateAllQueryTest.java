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
