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
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.expressions.*;

public class ReadAllOuterJoinExpressionTest extends ReadAllExpressionTest {

    public ReadAllOuterJoinExpressionTest(Class referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
    }

    public void checkOuterJoinSupport() {
        // Most have some level of support, even dbase.
    }

    public void reset() throws Exception {
        super.reset();
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        super.setup();
        // Make one of the employees have no address.
        beginTransaction();
        org.eclipse.persistence.sessions.UnitOfWork uow = getSession().acquireUnitOfWork();
        org.eclipse.persistence.testing.models.employee.domain.Employee employee = (org.eclipse.persistence.testing.models.employee.domain.Employee)uow.readObject(org.eclipse.persistence.testing.models.employee.domain.Employee.class, new ExpressionBuilder().get("firstName").like("Bob%"));
        employee.setAddress(null);
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        checkOuterJoinSupport();
    }
}
