/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.oracle;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.mapping.*;

/**
 * This class is the root class for most Hierarchical Query tests. The subclasses
 * override the getQuery and expectedResults methods. The test method executes the
 * query and the verify checks the results against the expected results.
 */
public abstract class HierarchicalQueryTest extends TestCase {
    static String ERROR_1 = "The number of results returned was not the number of results expected.";
    static String ERROR_2 = "The results did not match, missing:";

    public Vector results;

    public HierarchicalQueryTest() {
    }

    //Returns a vector of employee names, representing the expected results of the query
    public abstract Vector expectedResults();

    //Returns the query to execute
    public abstract ReadAllQuery getQuery();

    @Override
    public void setup() {
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test is intended for Oracle databases only");
        }
    }

    @Override
    public void reset() {
    }

    @Override
    public void test() {
        try {
            results = (Vector)getSession().executeQuery(getQuery());
        } catch (Exception ex) {
            ex.printStackTrace();
            if ((ex.getMessage().contains("missing BY keyword")) || (ex.getMessage().contains("cannot have join with CONNECT BY"))) {
                throw new TestWarningException("This test is indended for Oracle 9i and up");
            }
            ex.printStackTrace();
            throw new TestErrorException(ex.getMessage());
        }
    }

    @Override
    public void verify() {
        Vector expectedResults = expectedResults();
        if (expectedResults.size() != results.size()) {
            throw new TestErrorException(ERROR_1);
        }
        for (Object next : results) {
            if (!expectedResults.contains(next)) {
                throw new TestErrorException(ERROR_2 + next);
            }
        }
    }

    //Adds an employee and all his managed employees recursivly to the provided vector.
    //This creates a hierarchy rooted at Employee emp
    public void addEmployee(Vector toVector, Employee emp) {
        toVector.addElement(emp);
        Vector managed = emp.getManagedEmployees();
        if (managed != null && !(managed.isEmpty())) {
            for (int i = 0; i < managed.size(); i++) {
                Employee next = (Employee)managed.elementAt(i);
                addEmployee(toVector, next);
            }
        }
    }
}
