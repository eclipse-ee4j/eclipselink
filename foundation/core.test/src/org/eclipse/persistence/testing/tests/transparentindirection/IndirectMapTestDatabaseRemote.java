/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.MappedOrder;

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

    /**
     * register the order without cloning...
     */
    protected void registerNewOrderIn(AbstractOrder order, UnitOfWork uow) {
        uow.registerNewObject(order);
    }
}