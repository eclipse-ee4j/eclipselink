/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.multipletable;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * Tests inserting and reading back an object that uses multiple table foreign
 * or primary key.
 *
 * BUG# 3970215
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date May 28/07
 */
public class MultipleTableTest extends AutoVerifyTestCase {
    private Object m_testObject;
    private Exception m_exception;
    private DatabaseSession m_session;

    public MultipleTableTest(Object testObject) {
        m_testObject = testObject;
        setName("Multiple table test [" + Helper.getShortClassName(m_testObject) + "]");
    }

    public void reset() {
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }
    
    protected void setup() {
        m_session = (DatabaseSession) getSession();
        m_exception = null;
        beginTransaction();
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void test() {
        try {
            UnitOfWork uow = m_session.acquireUnitOfWork();
            uow.registerObject(m_testObject);
            uow.commit();
        } catch (Exception e) {
            m_exception = e;
        }
    }
    
    protected void verify() {
        if (m_exception != null) {
            throw new TestErrorException("An exception was throw when committing the test object [" + m_testObject + "]" + ": " + m_exception);
        }
        
        if (m_session.readObject(m_testObject.getClass()) == null) {
            throw new TestErrorException("The test object was not read back, hence not written to the database");
        }
    }
}
