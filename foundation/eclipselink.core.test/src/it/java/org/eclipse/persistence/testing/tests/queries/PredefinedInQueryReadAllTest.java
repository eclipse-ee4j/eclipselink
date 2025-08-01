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

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

/**
 * Test predefined queries.
 *
 */
public class PredefinedInQueryReadAllTest extends ReadAllTest {
    public PredefinedInQueryReadAllTest(Class<?> referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
        setName("PredefinedInQueryReadAllTest");
    }

    @Override
    protected void setup() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        org.eclipse.persistence.expressions.ExpressionBuilder builder = new org.eclipse.persistence.expressions.ExpressionBuilder();
        query.setSelectionCriteria(builder.get("salary").in(builder.getParameter("salaries")));
        query.addArgument("salaries");
        setQuery(query);

        getSession().removeQuery("getAllEmployeesIn");
        getSession().addQuery("getAllEmployeesIn", query);

    }

    @Override
    protected void test() {
        Vector sals = new Vector();
        sals.add(100);
        sals.add(56232);
        sals.add(10000);
        Vector args = new Vector();
        args.add(sals);

        this.objectsFromDatabase = getSession().executeQuery("getAllEmployeesIn", args);
        // Test execution twice to ensure query is cloned correctly
        this.objectsFromDatabase = getSession().executeQuery("getAllEmployeesIn", args);

        // Also execute the query using a collection type other than vector.
        Collection collection = new HashSet();
        collection.add(100);
        collection.add(56232);
        collection.add(10000);
        args = new Vector();
        args.add(collection);

        this.objectsFromDatabase = getSession().executeQuery("getAllEmployeesIn", args);
        // Test execution twice to ensure query is cloned correctly
        this.objectsFromDatabase = getSession().executeQuery("getAllEmployeesIn", args);
    }
}
