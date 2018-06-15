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
package org.eclipse.persistence.testing.tests.queries.options;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;

public class RefreshCascadeAllNoIndirectionTest extends AutoVerifyTestCase {
    public RefreshCascadeAllNoIndirectionTest() {
        setDescription("This test verifies that infinite recursion will not occur when refreshing an object with cascade all and no indirection");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        try {
            ReadObjectQuery query = new ReadObjectQuery(org.eclipse.persistence.testing.models.insurance.PolicyHolder.class);
            query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal("Bob"));
            query.refreshIdentityMapResult();
            query.setCascadePolicy(ReadObjectQuery.CascadeAllParts);
            getSession().executeQuery(query);
        } catch (StackOverflowError ex) {
            throw new TestErrorException("Refresh-Cascade All with no indirection caused infinite recursion");
        }
    }
}
