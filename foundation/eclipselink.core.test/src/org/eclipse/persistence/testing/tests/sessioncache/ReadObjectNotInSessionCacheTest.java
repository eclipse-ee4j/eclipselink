/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.sessioncache;

import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.*;
import 	org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

public class ReadObjectNotInSessionCacheTest extends TestCase {
    private int oldLevel;

    public ReadObjectNotInSessionCacheTest() {
        setDescription("The test ensures we don't merge an object into the session cache when we dont need to.");
    }

    protected void setup() {
        checkNoWaitSupported();

        // Flush the cache 
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getAbstractSession().beginTransaction();
        this.oldLevel = getSession().getDescriptor(Employee.class).getUnitOfWorkCacheIsolationLevel();
        getSession().getDescriptor(Employee.class).setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setLockMode(ObjectLevelReadQuery.LOCK_NOWAIT);
        Employee emp = (Employee)uow.executeQuery(query);

        emp.setSalary(emp.getSalary() + 1);
        uow.commit();
    }

    protected void verify() {
        IdentityMap im = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(Employee.class);
        if ((im != null) && (im.getSize() > 0)) {
            throw new TestErrorException("Employee read should not have been put into session cache.");
        }
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getDescriptor(Employee.class).setUnitOfWorkCacheIsolationLevel(this.oldLevel);
    }
}
