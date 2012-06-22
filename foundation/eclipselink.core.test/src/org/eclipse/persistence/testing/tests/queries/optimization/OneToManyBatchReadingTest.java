/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
