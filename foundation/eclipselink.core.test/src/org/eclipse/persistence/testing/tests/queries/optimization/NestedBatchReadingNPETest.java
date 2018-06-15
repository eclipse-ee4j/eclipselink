/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     vikram.jeet.bhatia@oracle.com
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.legacy.Shipment;

public class NestedBatchReadingNPETest extends TestCase {
    public Vector result;
    BatchFetchType batchType;

    public NestedBatchReadingNPETest(BatchFetchType batchType) {
        setDescription("Tests batch reading nesting across 1-m and 1-1 mappings, shipment, orders and employee");
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        if ((batchType == BatchFetchType.IN) && !getSession().getPlatform().isOracle()) {
            throwWarning("Nested arrays not supported on this database");
        }
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setBatchFetchType(batchType);
        query.setReferenceClass(Shipment.class);
        Expression expression = query.getExpressionBuilder();
        Expression expression2List = expression.anyOfAllowingNone("orders");
        query.addBatchReadAttribute(expression2List);
        query.addBatchReadAttribute(expression2List.getAllowingNull("employee").getAllowingNull("computer"));
        result = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        // Nothing to verify, will test to verify NPE during executeQuery() above.
    }
}
