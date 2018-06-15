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
//     Vikram Bhatia
package org.eclipse.persistence.testing.tests.inheritance;

import java.util.Collection;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.inheritance.GrassHopper;
import org.eclipse.persistence.testing.models.inheritance.Insect;

/**
 * Test for executing a query with pagination on sub-entities using downcasting.
 * Bug 380929
 */
public class PaginationInheritanceTest extends TestCase {


    public PaginationInheritanceTest() {
        setDescription("Verifies that inheritance queries are well formed with pagination.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Insect.class);
        Expression expression = query.getExpressionBuilder();
        Expression filter = expression.as(GrassHopper.class).get("gh_maximumJump").equal(5);

        query.setFirstResult(0);
        query.setMaxRows(10);
        query.setSelectionCriteria(filter);

        try {
            Collection<Insect> result = (Collection<Insect>) getAbstractSession().executeQuery(query);
        } catch (DatabaseException ex) {
            throw new TestErrorException("Failed to fetch results with downcasting and pagination. " + ex.getMessage());
        }
    }
}
