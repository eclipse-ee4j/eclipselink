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
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractSalesRep;
import org.eclipse.persistence.testing.models.transparentindirection.SetOrder;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectSetProject;
import org.eclipse.persistence.testing.models.transparentindirection.SetOrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.SetSalesRep;

/**
 * Test the IndirectList with assorted DatabaseSessions and UnitsOfWork.
 * this should only be used in jdk1.2+
 * @author: Big Country
 */
public class IndirectSetTestDatabase extends IndirectContainerTestDatabase {

    /**
     * Constructor
     * @param name java.lang.String
     */
    public IndirectSetTestDatabase(String name) {
        super(name);
    }

    protected AbstractOrder buildOrderShell() {
        return new SetOrder();
    }

    protected AbstractOrder buildTestOrderShell(String customerName) {
        return new SetOrder(customerName);
    }

    protected AbstractOrderLine newOrderLine(String item, int quanity) {
        return new SetOrderLine(item, quanity);
    }

    protected AbstractSalesRep newSalesRep(String name) {
        return new SetSalesRep(name);
    }

    /**
     * build the TopLink project
     */
    public Project setUpProjectFromCode() {
        return new IndirectSetProject();
    }
}
