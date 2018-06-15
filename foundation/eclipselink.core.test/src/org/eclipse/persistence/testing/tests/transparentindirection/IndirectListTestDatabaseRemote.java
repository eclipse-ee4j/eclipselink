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
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractSalesRep;
import org.eclipse.persistence.testing.models.transparentindirection.Order;
import org.eclipse.persistence.testing.models.transparentindirection.OrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.SalesRep;

public class IndirectListTestDatabaseRemote extends IndirectContainerTestDatabaseRemote {
    public IndirectListTestDatabaseRemote(String name) {
        super(name);
    }

    protected AbstractOrder buildOrderShell() {
        return new Order();
    }

    protected AbstractOrder buildTestOrderShell(String customerName) {
        return new Order(customerName);
    }

    protected AbstractOrderLine newOrderLine(String item, int quanity) {
        return new OrderLine(item, quanity);
    }

    protected AbstractSalesRep newSalesRep(String name) {
        return new SalesRep(name);
    }
}
