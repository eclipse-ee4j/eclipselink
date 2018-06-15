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
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractSalesRep;
import org.eclipse.persistence.testing.models.transparentindirection.MappedOrder;
import org.eclipse.persistence.testing.models.transparentindirection.MappedOrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.MappedSalesRep;

/**
 * comment
 */
public class IndirectMapTestDatabaseRemote extends IndirectContainerTestDatabaseRemote {

    /**
     * IndirectListTestDatabaseRemote constructor comment.
     * @param name java.lang.String
     */
    public IndirectMapTestDatabaseRemote(String name) {
        super(name);
    }

    protected AbstractOrder buildOrderShell() {
        return new MappedOrder();
    }

    protected AbstractOrder buildTestOrderShell(String customerName) {
        return new MappedOrder(customerName);
    }

    protected AbstractSalesRep newSalesRep(String name) {
        return new MappedSalesRep(name);
    }

    protected AbstractOrderLine newOrderLine(String item, int quanity) {
        return new MappedOrderLine(item, quanity);
    }

    /**
     * register the order without cloning...
     */
    protected void registerNewOrderIn(AbstractOrder order, UnitOfWork uow) {
        uow.registerNewObject(order);
    }
}
