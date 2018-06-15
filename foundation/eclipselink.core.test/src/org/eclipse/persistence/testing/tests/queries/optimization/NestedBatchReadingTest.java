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
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.insurance.*;

public class NestedBatchReadingTest extends TestCase {
    public Vector result;
    BatchFetchType batchType;

    public NestedBatchReadingTest(BatchFetchType batchType) {
        setDescription("Tests batch reading nesting across two 1-m mappings, polcies and claims");
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setBatchFetchType(batchType);
        query.setReferenceClass(PolicyHolder.class);
        query.addBatchReadAttribute("policies");
        query.addBatchReadAttribute(query.getExpressionBuilder().get("policies").get("claims"));
        result = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        boolean foundClaims = false;
        for (Enumeration holdersEnum = result.elements(); holdersEnum.hasMoreElements(); ) {
            for (Enumeration policiesEnum = ((PolicyHolder)holdersEnum.nextElement()).getPolicies().elements();
                 policiesEnum.hasMoreElements(); ) {
                if (!((Policy)policiesEnum.nextElement()).getClaims().isEmpty()) {
                    foundClaims = true;
                }
            }
        }

        if (!foundClaims) {
            throw new TestErrorException("claims were not bacthed correctly.");
        }
    }
}
