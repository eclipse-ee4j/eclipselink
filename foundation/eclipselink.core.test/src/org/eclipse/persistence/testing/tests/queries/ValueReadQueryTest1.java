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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;

/**
 * Test value read queries with various container policies.
 */
public class ValueReadQueryTest1 extends TestCase {
    int stackValue;

    public ValueReadQueryTest1() {
        setDescription("This tests value read queries with various container policies.");
    }

    public ValueReadQuery buildNewQuery() {
        return new ValueReadQuery("select count(*) from EMPLOYEE");
    }

    public void test() {
        this.testStackContainerPolicy();
    }

    public void testStackContainerPolicy() {
        ValueReadQuery query = this.buildNewQuery();
        query.useCollectionClass(Stack.class);

        stackValue = ((Number)getSession().executeQuery(query)).intValue();
        // if we get here, we must not have generated a ClassCastException
        if (stackValue == 0) {
            throw new TestErrorException("missing data");
        }
    }
}
