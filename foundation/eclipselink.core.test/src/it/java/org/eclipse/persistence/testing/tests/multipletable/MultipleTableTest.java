/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.multipletable;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Tests inserting and reading back an object that uses multiple table foreign
 * or primary key.
 *
 * BUG# 3970215
 *
 * @author Guy Pelletier
 * @version 1.0
 */
public class MultipleTableTest extends AutoVerifyTestCase {
    private Object m_testObject;
    private Exception m_exception;
    private DatabaseSession m_session;

    public MultipleTableTest(Object testObject) {
        m_testObject = testObject;
        setName("Multiple table test [" + m_testObject.getClass().getSimpleName() + "]");
    }

    @Override
    public void reset() {
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    @Override
    protected void setup() {
        m_session = (DatabaseSession) getSession();
        m_exception = null;
        beginTransaction();
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    protected void test() {
        try {
            UnitOfWork uow = m_session.acquireUnitOfWork();
            uow.registerObject(m_testObject);
            uow.commit();
        } catch (Exception e) {
            m_exception = e;
        }
    }

    @Override
    protected void verify() {
        if (m_exception != null) {
            throw new TestErrorException("An exception was throw when committing the test object [" + m_testObject + "]" + ": " + m_exception);
        }

        if (m_session.readObject(m_testObject.getClass()) == null) {
            throw new TestErrorException("The test object was not read back, hence not written to the database");
        }
    }
}
