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
package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Test to ensure deletes are not propogated when a delete occurs to an object who's
 * descriptor is set to DO_NOT_SEND_CHANGES
 */
public class InvalidCacheSyncTypeTest extends ConfigurableCacheSyncDistributedTest {
    protected Exception exception;
    protected int cacheSyncType;

    public InvalidCacheSyncTypeTest(int type) {
        super();
        setName("InvalidCacheSyncTypeTest(" + type + ")");
        cacheSyncType = type;
        cacheSyncConfigValues.put(Employee.class, new Integer(type));
    }

    public void setup() {
        try {
            super.setup();
        } catch (Exception e) {
            exception = e;
        }
    }

    public void verify() {
        if ((exception == null) && ((cacheSyncType < 0) || (cacheSyncType > 4))) {
            throw new TestErrorException("An exception is expected when an invalid cache sync type is set.");
        }
    }
}
