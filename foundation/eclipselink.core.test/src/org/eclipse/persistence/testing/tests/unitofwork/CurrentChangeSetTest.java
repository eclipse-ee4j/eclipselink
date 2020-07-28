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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


// Bug 5744009 - Make sure new objects are included in the current change set.
public class CurrentChangeSetTest extends AutoVerifyTestCase {
    int countBeforeCommit;
    int countAfterCommit;

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        }
    }

    public void setup() {
        getAbstractSession().beginTransaction();
        countBeforeCommit = 0;
        countAfterCommit = 0;
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee emp = (Employee)uow.registerObject(new Employee());
        countBeforeCommit = uow.getCurrentChanges().getAllChangeSets().size();
        uow.commit();
        countAfterCommit = uow.getCurrentChanges().getAllChangeSets().size();
    }

    public void verify() {
        if (countBeforeCommit == 0) {
            throw new TestErrorException("Invalid number of change sets before commit [" + countBeforeCommit +
                                         "]. ");
        }
    }
}
