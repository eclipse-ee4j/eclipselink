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
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;


/**
 *  @author  smcritch
 */
public class WriteChanges_Commit_TestCase extends AutoVerifyTestCase {
    public WriteChanges_Commit_TestCase() {
        super();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.writeChanges();
        uow.commit();
    }
}
