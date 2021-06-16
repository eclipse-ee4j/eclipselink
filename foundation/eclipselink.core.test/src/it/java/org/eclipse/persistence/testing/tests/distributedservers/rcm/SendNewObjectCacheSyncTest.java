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
package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Test to ensure the SEND_NEW_OBJECTS_WITH_CHANGES setting works
 */
public class SendNewObjectCacheSyncTest extends ConfigurableCacheSyncDistributedTest {

    protected Employee employee = null;
    protected boolean shouldSendObject = false;

    /**
     * Constructor
     * @param shouldSendObject boolean value indicating whether to test that the object is
     * sent with the SEND_NEW_OBJECTS_WITH_CHANGES or to test that it is not sent with the
     * SEND_OBJECT_CHANGES setting.
     */
    public SendNewObjectCacheSyncTest(boolean shouldSendObject) {
        super();
        this.shouldSendObject = shouldSendObject;
        if (shouldSendObject) {
            setName("SendNewObjectCacheSyncTest - SEND_NEW_OBJECTS_WITH_CHANGES");
            cacheSyncConfigValues.put(Employee.class, new Integer(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES));
        } else {
            setName("SendNewObjectCacheSyncTest - SEND_OBJECT_CHANGES");
            cacheSyncConfigValues.put(Employee.class, new Integer(ClassDescriptor.SEND_OBJECT_CHANGES));
        }
    }

    /**
     * Create a new employee and commit.
     */
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        employee = new Employee();
        employee.setFirstName("Dave");
        employee.setLastName("Kujan");
        uow.registerObject(employee);
        uow.commit();
    }

    public void verify() {
        if (shouldSendObject && getObjectFromDistributedCache(employee) == null) {
            throw new TestErrorException("New employee was not added to distributed cache with " + " SEND_NEW_OBJECTS_WITH_CHANGES descriptor CacheSynchronizationTypeSetting.");
        }
        if (!shouldSendObject && getObjectFromDistributedCache(employee) != null) {
            throw new TestErrorException("New employee was added to distributed cache with " + "SEND_OBJECT_CHANGES descriptor CacheSynchronizationTypeSetting. It should not be added " + "since it is not related to any objects being sent.");

        }
    }
}
