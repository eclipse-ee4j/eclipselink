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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;

public class AddressClient extends Client2 {
    public AddressClient(Server server, Session session, String name) {
        super(server, session, name);
    }

    /**
     * This client thread runs in the background, and continuously switches
     * address of the employee back and forth. The code is a little weird.
     */
    public void run() {
        org.eclipse.persistence.testing.models.employee.domain.Address newAddress;
        Employee theEmployee;
        ExpressionBuilder eb = new ExpressionBuilder();
        for (int i = 0; i < 20; i++) {
            UnitOfWork uow = this.clientSession.acquireUnitOfWork();
            theEmployee = (Employee)uow.readObject(Employee.class, eb.get("lastName").equal("MacIvor"));
            newAddress = new org.eclipse.persistence.testing.models.employee.domain.Address();
            newAddress.setStreet("Street" + i);
            theEmployee.setAddress(newAddress);
            uow.commit();

        }
    }
}
