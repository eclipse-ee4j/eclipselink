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
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.legacy.*;
import org.eclipse.persistence.testing.framework.*;

public class OneToManyBatchReadingTest extends TestCase {
    BatchFetchType batchType;
    public List result;

    public OneToManyBatchReadingTest(BatchFetchType batchType) {
        this.batchType = batchType;
        setName(getName() + batchType);
        setDescription("Tests batch reading using 1 to 1 mapping and composite primary key");
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
        q.addBatchReadAttribute("orders");
        UnitOfWork uow = getSession().acquireUnitOfWork();
        result = (List) uow.executeQuery(q);
    }

    public void verify() {
        Shipment shipment = (Shipment)result.get(0);
        strongAssert((shipment.orders.size() > 0), "Test failed. Batched objects were not read");
    }
}
