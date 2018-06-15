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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.*;

/**
 * Test that an Aggregate with a 1:1 relationship and batch reading works correctly
 * without throwing an InvalidQueryKeyException.
 * For Bug 5478648
 * @author David Minsky
 */

public class AggregateWithOneToOneRelationshipTest extends AutoVerifyTestCase {

    public AggregateWithOneToOneRelationshipTest() {
        super();
        setDescription("Test that an Aggregate with a 1:1 relationship with batch reading enabled works.");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        // use the existing Employee example1 object tree
        Employee example1 = Employee.example1();

        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression expression = builder.get("firstName").equal(example1.getFirstName());
        query.setSelectionCriteria(expression);

        try {
            // must not throw an exception on read (w/batch reading on 1:1 off Aggregate)
            Vector employees = (Vector) getSession().executeQuery(query);
            Iterator iterator = employees.iterator();
            while (iterator.hasNext()) {
                Employee employee = (Employee) iterator.next();
                // traverse Aggregate, 1:1 relationships
                Address addressFromDB = (Address) employee.getAddressDescription().getAddress().getValue();
                assertNotNull(addressFromDB);
            }
        } catch (QueryException qe) {
            if (qe.getErrorCode() == QueryException.INVALID_QUERY_KEY_IN_EXPRESSION) {
                throw new TestErrorException("Aggregate with a 1:1 relationship and batch reading failed: " + qe.getMessage(), qe);
            } else {
                throw qe;
            }
        }
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

}
