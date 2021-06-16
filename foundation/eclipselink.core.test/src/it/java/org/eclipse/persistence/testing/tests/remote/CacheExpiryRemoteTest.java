/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.internal.identitymaps.*;

/**
 * Ensure expired objects are correctly dealt with on a remote session
 */
public class CacheExpiryRemoteTest extends TestCase {

    protected static org.eclipse.persistence.sessions.server.ServerSession serverSession;
    protected Employee employee = null;
    protected CacheInvalidationPolicy employeeCacheExpiryPolicy;
    protected String employeeName = null;

    public CacheExpiryRemoteTest() {
        setDescription("Ensure objects expire properly on a remote session.");
    }

    public void setup() {
        // get the server session from the RemoteModel - it is strange that getServerSession() returns a client session
        serverSession = ((ClientSession)org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession()).getParent();

        employeeCacheExpiryPolicy = serverSession.getDescriptor(Employee.class).getCacheInvalidationPolicy();

        serverSession.getDescriptor(Employee.class).setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(0));

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        getAbstractSession().beginTransaction();
    }

    public void test() {
        employee = (Employee)getSession().readObject(Employee.class);
        employeeName = employee.getFirstName();
        CacheKey key = serverSession.getIdentityMapAccessorInstance().getCacheKeyForObject(employee);
        ((Employee)(key.getObject())).setFirstName(employee.getFirstName() + "-mutated");
    }

    public void verify() {
        employee = (Employee)getSession().readObject(employee);
        if (employee.getFirstName().equals(employeeName + "-mutated")) {
            throw new TestErrorException("UnitOfWork update did not work correctly when expiry occurred " +
                                         "between registration and commit.");
        }
    }


    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        serverSession.getDescriptor(Employee.class).setCacheInvalidationPolicy(employeeCacheExpiryPolicy);
    }

}
