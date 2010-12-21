/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.Order;

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
}
