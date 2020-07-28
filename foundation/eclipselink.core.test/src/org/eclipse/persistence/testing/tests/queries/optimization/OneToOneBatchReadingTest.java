/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Vector;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.legacy.*;

public class OneToOneBatchReadingTest extends TestCase {
    public Vector v;
    BatchFetchType batchType;

    public OneToOneBatchReadingTest(BatchFetchType batchType) {
        setDescription("Tests batch reading using 1 to 1 mapping and composite primary key");
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
        if ((batchType == BatchFetchType.IN) && !getSession().getPlatform().isOracle()) {
            throwWarning("Nested arrays not supported on this database");
        }
    }

    public void test() {
        ReadAllQuery q = new ReadAllQuery();
        q.setBatchFetchType(batchType);
        q.setReferenceClass(Shipment.class);
        q.addBatchReadAttribute("employee");
        q.setSelectionCriteria(q.getExpressionBuilder().get("employee").get("address").equal(q.getExpressionBuilder().getParameter("ADDRESS")));
        q.addArgument("ADDRESS");
        Vector r = new Vector();
        r.addElement("885 Meadowlands Dr.");
        UnitOfWork uow = getSession().acquireUnitOfWork();
        v = (java.util.Vector)uow.executeQuery(q, r);
    }

    public void verify() {
        Shipment s = (Shipment)v.firstElement();
        if (s.employee == null) {
            throw new TestWarningException("Test failed. Batched objects were not read");
        }
    }
}
