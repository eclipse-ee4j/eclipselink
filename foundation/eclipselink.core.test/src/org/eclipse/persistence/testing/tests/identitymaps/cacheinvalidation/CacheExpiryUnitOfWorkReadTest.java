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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * Test to ensure read times are correctly transferred from the session to the
 * UnitOfWork
 */
public class CacheExpiryUnitOfWorkReadTest extends CacheExpiryTest {

    protected Employee sessionEmployee = null;
    protected UnitOfWork uow = null;

    public CacheExpiryUnitOfWorkReadTest() {
        setDescription("Test to ensure read time is correct in the UnitOfWork cache.");
    }

    public void setup() {
        super.setup();
        uow = getSession().acquireUnitOfWork();
    }

    public void test() {
        sessionEmployee = (Employee)getSession().readObject(Employee.class);
        Employee uowEmployee = (Employee)uow.readObject(sessionEmployee);
        uow.release();
    }

    public void verify() {
        if (((UnitOfWorkImpl)uow).getIdentityMapAccessorInstance().getCacheKeyForObject(sessionEmployee).getReadTime() != 
            ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getCacheKeyForObject(sessionEmployee).getReadTime()) {
            throw new TestErrorException("The read time in the UnitOfWork cache is different from the read " + 
                                         "time in the session cache.");
        }
    }

    public void reset() {
        uow.release();
        super.reset();
    }
}
