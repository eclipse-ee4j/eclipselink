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
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 *  @author  smcritch
 */
public class BeginTransactionEarly_WriteChanges_TestCase extends AutoVerifyTestCase {
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        uow.beginEarlyTransaction();

        uow.writeChanges();
        uow.commit();

        if (((UnitOfWorkImpl)uow).isInTransaction()) {
            ((UnitOfWorkImpl)uow).rollbackTransaction();
            throw new TestErrorException("After beginning transaction early and writing changes, still in transaction afterwards.");
        }
    }
}
