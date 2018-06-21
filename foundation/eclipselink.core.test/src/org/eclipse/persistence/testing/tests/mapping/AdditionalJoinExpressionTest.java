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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.mapping.Employee;

public class AdditionalJoinExpressionTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Employee original;
    public Employee fromDatabase;

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        beginTransaction();
    }

    public void test() {
        this.original = (Employee)getSession().readObject(Employee.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee empClone = (Employee)uow.registerObject(this.original);
        empClone.getComputer().distibuted = "true";
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        this.fromDatabase = (Employee)getSession().readObject(this.original);
        if (this.fromDatabase.getComputer() != null) {
            throw new TestErrorException("The additional Join expression failed to limit the query");
        }
    }

    public void verify() {
    }
}
