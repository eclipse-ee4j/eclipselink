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
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.insurance.Policy;
import org.eclipse.persistence.testing.models.insurance.PolicyHolder;

import java.util.Enumeration;
import java.util.Vector;

public class NestedBatchReadingTest extends TestCase {
    public Vector result;
    BatchFetchType batchType;

    public NestedBatchReadingTest(BatchFetchType batchType) {
        setDescription("Tests batch reading nesting across two 1-m mappings, polcies and claims");
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setBatchFetchType(batchType);
        query.setReferenceClass(PolicyHolder.class);
        query.addBatchReadAttribute("policies");
        query.addBatchReadAttribute(query.getExpressionBuilder().get("policies").get("claims"));
        result = (Vector)getSession().executeQuery(query);
    }

    @Override
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
