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
package org.eclipse.persistence.testing.tests.proxyindirection;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.expressions.ExpressionBuilder;

/**
 * UnitOfWorkCommitAndResume checks that a UnitOfWorkCommitAndResume
 * works while using Proxy Indirection
 */
public class UnitOfWorkCommitAndResume extends UnitOfWorkUpdateTest {
    public UnitOfWorkCommitAndResume() {
        setDescription("Checks that a commitAndResume() works while using Proxy Indirection.");
    }

    /*
     * Note that the following methods are inherited from UnitOfWorkUpdateTest:
     *
     * public void reset() {}
     * public void setup() {}
     * public void verify() {}
     *
     */
    public void test() {
        Employee emp = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").like("%Angie%"));

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee eClone = (Employee)uow.registerObject(emp);

        eClone.getAddress().setStreet("706-171 Lees Ave.");
        eClone.setLastName("Barkhouse");

        uow.commitAndResume();
    }
}// end test case
