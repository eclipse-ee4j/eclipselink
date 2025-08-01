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

import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.DirectReadQuery;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.util.Stack;

/**
 * Test direct read queries with various container policies.
 */
public class DirectReadQueryTest1 extends TestCase {
    Stack stack;

    public DirectReadQueryTest1() {
        setDescription("This tests direct read queries with various container policies.");
    }

    public DirectReadQuery buildNewQuery() {
        return new DirectReadQuery("select L_NAME from EMPLOYEE");
    }

    @Override
    public void test() {
        this.testStackContainerPolicy();
    this.testCursoredStreamPolicy();
    }

    /**
     * assume the stack has already been populated by the previous test
     */
    public void testCursoredStreamPolicy() {

    ValueReadQuery sizeQuery = new ValueReadQuery("select count(*) from EMPLOYEE");

    DirectReadQuery query = this.buildNewQuery();
    query.useCursoredStream(5, 5, sizeQuery);

    CursoredStream stream = (CursoredStream) getSession().executeQuery(query);
    // if we get here, we must not have generated a ClassCastException

    int count = 0;
    while (stream.hasMoreElements()) {
        count++;
        String lastName = (String) stream.nextElement();
        if (lastName.isEmpty()) {
            throw new TestErrorException("missing data");
        }
    }
    if (count != stack.size()) {
        throw new TestErrorException("stream does not match stack - "
                + "expected: " + stack.size() + " actual: " + count);
    }
    }

    public void testStackContainerPolicy() {
        DirectReadQuery query = this.buildNewQuery();
        query.useCollectionClass(Stack.class);

        stack = (Stack)getSession().executeQuery(query);
        // if we get here, we must not have generated a ClassCastException
        String lastName = (String)stack.peek();
        if (lastName.isEmpty()) {
            throw new TestErrorException("missing data");
        }
    }
}
