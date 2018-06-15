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
import org.eclipse.persistence.testing.models.aggregate.*;

public class AggregateBatchReadingTest extends TestCase {
    public Vector result;
    BatchFetchType batchType;

    public AggregateBatchReadingTest(BatchFetchType batchType) {
        setDescription("Tests batch reading nesting across an aggregate");
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.setBatchFetchType(batchType);
        // changed attribute name from "addressDescription" to "address" to reproduce bug 3566341,
        // which requires the same name for attribute mapped as an aggregate and an attribute
        // on the aggregate mapped 1 to 1.
        query.addBatchReadAttribute(query.getExpressionBuilder().get("address").get("address"));
        result = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        for (Enumeration employeesEnum = result.elements(); employeesEnum.hasMoreElements(); ) {
            ((Employee)employeesEnum.nextElement()).getAddressDescription().getAddress().getValue();
        }
    }
}
