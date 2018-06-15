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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test to ensure read time is properly set in the cache for objects created in a session
 */
public class SessionCreateObjectReadTimeTest extends CacheExpiryTest {

    public SessionCreateObjectReadTimeTest() {
        setDescription("Test that the read time for objects created on a Session is correctly set.");
    }

    protected Employee employee = null;

    public void test() {
        employee = new Employee();
        employee.setFirstName("Charley");
        employee.setLastName("Dickens");
        getDatabaseSession().writeObject(employee);
    }

    public void verify() {
        if (getAbstractSession().getIdentityMapAccessorInstance().getCacheKeyForObject(employee).getReadTime() == 0) {
            throw new TestErrorException("Objects created on a Session do not have read time set.");
        }
    }
}
