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
package org.eclipse.persistence.testing.tests.proxyindirection;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class DeleteProxyObjectTest extends AutoVerifyTestCase {

    Object address;

    public DeleteProxyObjectTest() {
        setDescription("Tests deleting proxy object with Proxy Indirection.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").like("%Rick%"));

        address = emp.getAddress();
        UnitOfWorkImpl uow = (UnitOfWorkImpl)getSession().acquireUnitOfWork();
        Employee aClone = (Employee)uow.registerObject(emp);
        aClone.setAddress(null);
        uow.deleteObject(address);
        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Rick"));
        if (emp.getAddress() != null) {
            throw new TestErrorException("Employee's address is not set to null.");
        }
        Address add  = (Address)getSession().readObject(address);
        if (add != null) {
            throw new TestErrorException("Address was not deleted.");
        }
    }
}
