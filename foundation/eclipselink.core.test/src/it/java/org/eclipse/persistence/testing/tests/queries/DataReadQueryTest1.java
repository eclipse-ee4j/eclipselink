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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;

/**
 * Test data read queries with various container policies.
 */
public class DataReadQueryTest1 extends AutoVerifyTestCase {
    Stack stack;

    public DataReadQueryTest1() {
        setDescription("This tests data read queries with various container policies.");
    }

    public DataReadQuery buildNewQuery() {
        return new DataReadQuery("select F_NAME, L_NAME from EMPLOYEE");
    }

    public void test() {
        this.testStackContainerPolicy();
    this.testCursoredStreamPolicy();
    }

    /**
     * assume the stack has already been populated by the previous test
     */
    public void testCursoredStreamPolicy() {

    ValueReadQuery sizeQuery = new ValueReadQuery("select count(*) from EMPLOYEE");

    DataReadQuery query = this.buildNewQuery();
    query.useCursoredStream(5, 5, sizeQuery);

    CursoredStream stream = (CursoredStream) getSession().executeQuery(query);
    // if we get here, we must not have generated a ClassCastException

    int count = 0;
    while (stream.hasMoreElements()) {
        count++;
        DatabaseRecord row = (DatabaseRecord) stream.nextElement();
        if (row.get("F_NAME") == null) {
            throw new TestErrorException("missing data");
        }
    }
    if (count != stack.size()) {
        throw new TestErrorException("stream does not match stack - "
                + "expected: " + stack.size() + " actual: " + count);
    }
    }

    public void testStackContainerPolicy() {
        DataReadQuery query = this.buildNewQuery();
        query.useCollectionClass(Stack.class);

        stack = (Stack)getSession().executeQuery(query);
        // if we get here, we must not have generated a ClassCastException
        org.eclipse.persistence.sessions.Record row = (org.eclipse.persistence.sessions.Record)stack.peek();
        if (row.get("F_NAME") == null) {
            throw new TestErrorException("missing data");
        }
    }
}
