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
//     Jan 9, 2009-1.1 Chris Delahunt
//       - Bug 244802: PostLoad callback getting invoked twice
package org.eclipse.persistence.testing.tests.jpa.advanced.testmodel;

import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.jpa.junit.EntityContainerTestBase;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeeListener;

/**
 * Tests the @PostLoad event from an EntityListener is fired only once when in a transaction.
 *
 * @author Chris Delahunt
 */
public class EntityListenerPostLoadRefreshTest extends EntityContainerTestBase {
    protected int m_beforeEvent, m_afterEvent;

    @Override
    public void test() {
        beginTransaction();

        Employee employee= new Employee();
        employee.setFirstName("Bob");
        employee.setLastName("Smith");
        m_beforeEvent = EmployeeListener.POST_LOAD_COUNT;
        getEntityManager().persist(employee);
        getEntityManager().flush();
        getEntityManager().refresh( employee );

        m_afterEvent = EmployeeListener.POST_LOAD_COUNT;
        this.rollbackTransaction();
    }

    @Override
    public void verify() {
        if ((m_afterEvent-m_beforeEvent) != 1) {
            throw new TestErrorException("The callback method was called "+(m_afterEvent - m_beforeEvent)+
                    " times.  It should have been called only once");
        }
    }
}
