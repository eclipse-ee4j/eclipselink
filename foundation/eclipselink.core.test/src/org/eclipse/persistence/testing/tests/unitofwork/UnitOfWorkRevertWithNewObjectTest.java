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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class UnitOfWorkRevertWithNewObjectTest extends AutoVerifyTestCase {
    public UnitOfWorkRevertWithNewObjectTest() {
    }

    protected void test() {
        //First test to see if it works without registering any objects.
        //The motivation for this is a bug that was found.
        UnitOfWork unitOfWork = getSession().acquireUnitOfWork();
        unitOfWork.registerObject(new Employee());
        try {
            unitOfWork.revertAndResume();
        } catch (NullPointerException ex) {
            throw new TestErrorException("Null Pointer Exception thrown durring revert, bug 4544221");
        }
    }
}
