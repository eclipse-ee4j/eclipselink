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
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 * @author  smcritch
 */
public class WriteChanges_Release_TestCase extends AutoVerifyTestCase {
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.writeChanges();
        uow.release();

        if (((UnitOfWorkImpl)uow).isInTransaction()) {
            throw new TestErrorException("Even though release was called, since we already wrote changes and started the transaction, it must be rolled back.");
        }
    }
}
