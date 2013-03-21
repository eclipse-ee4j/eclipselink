/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractSalesRep;
import org.eclipse.persistence.testing.models.transparentindirection.SetOrder;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.SetOrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.SetSalesRep;

public class IndirectSetTestDatabaseRemote extends IndirectContainerTestDatabaseRemote {
    public IndirectSetTestDatabaseRemote(String name) {
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
}
