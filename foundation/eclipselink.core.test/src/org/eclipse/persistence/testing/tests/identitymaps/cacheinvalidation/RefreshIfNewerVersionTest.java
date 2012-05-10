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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test to ensure refreshOnlyIfNewerVersion will refresh the expiry flag if the object
 * is not refreshed.
 */
public class RefreshIfNewerVersionTest extends CacheExpiryTest {
    protected boolean shouldRefreshCache = false;
    protected long originalReadTime = 0;
    protected long secondReadTime = 0;

    public RefreshIfNewerVersionTest() {
        setDescription("Ensure refresh if newer version updates the cache expiry flag when the versions are the same.");
    }

    public void setup() {
        super.setup();
        shouldRefreshCache = getSession().getDescriptor(Employee.class).shouldOnlyRefreshCacheIfNewerVersion();
        getSession().getDescriptor(Employee.class).setShouldOnlyRefreshCacheIfNewerVersion(true);
    }

    public void test() {
        Employee employee = (Employee)getSession().readObject(Employee.class);
        originalReadTime = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getCacheKeyForObject(employee).getReadTime();

        try {
            // sleep to ensure timestamps are different
            Thread.sleep(100);
        } catch (InterruptedException exc) {
        }
        ;
        ReadObjectQuery query = new ReadObjectQuery(employee);
        query.refreshIdentityMapResult();
        employee = (Employee)getSession().executeQuery(query);
        secondReadTime = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getCacheKeyForObject(employee).getReadTime();
    }

    public void verify() {
        // Check to see if the expiry is more than 10 seconds away.
        if (originalReadTime >= secondReadTime) {
            throw new TestErrorException("Refresh if newer version did not update the expiry time for an object that was a newer version.");
        }
    }

    public void reset() {
        super.reset();
        getSession().getDescriptor(Employee.class).setShouldOnlyRefreshCacheIfNewerVersion(shouldRefreshCache);
    }
}
