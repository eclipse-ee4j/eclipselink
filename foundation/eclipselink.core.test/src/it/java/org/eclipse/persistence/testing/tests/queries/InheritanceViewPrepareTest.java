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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.inheritance.Vehicle;

import java.util.List;
import java.util.Vector;

/**
 * BUG#3158703
 * Inheritance queries were not preparing the view query in the query,
 * so regenerated the SQL each exceution.
 * Tests that query generated SQL contain parameter.
 */
public class InheritanceViewPrepareTest extends AutoVerifyTestCase {
    protected ReadAllQuery query;

    public InheritanceViewPrepareTest() {
        setDescription("Tests the inheritance view query is prepare correctly with binding.");
    }

    @Override
    public void setup() {
        if (getSession().getPlatform().isTimesTen()) {
            throw new TestWarningException("TimesTen does not support TO_NUMBER");
        }
        if (getSession().getPlatform().isDB2()) {
          throw new TestWarningException("The test does not support DB2 (Bug 4563813).");
        }

        query = new ReadAllQuery();
        query.setReferenceClass(Vehicle.class);
        query.bindAllParameters();
        ExpressionBuilder vehicle = new ExpressionBuilder();
        query.setSelectionCriteria(vehicle.get("id").equal(vehicle.getParameter("id").toNumber()));
        query.addArgument("id");
    }

    @Override
    public void test() {
        Vector arguments = new Vector(1);
        arguments.add(null);
        List result = (List)getSession().executeQuery(query, arguments);
        arguments = new Vector(1);
        arguments.add("0");
        result = (List)getSession().executeQuery(query, arguments);
    }

    @Override
    protected void verify() {
        if (!query.getSQLString().contains("?")) {
            throw new TestErrorException("SQL not prepared correctly: " + query.getSQLString());
        }
    }
}
