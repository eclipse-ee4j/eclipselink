/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.testing.models.jpa.advanced.EmployeeListener;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;

/**
 * Tests the @PostLoad events from an EntityListener.
 *
 * @author Guy Pelletier
 */
public class EntityListenerPostLoadTest extends CallbackEventTest {
    public void test() throws Exception {
        m_beforeEvent = EmployeeListener.POST_LOAD_COUNT;

        getEntityManager().find(Employee.class, m_employee.getId());

        m_afterEvent = EmployeeListener.POST_LOAD_COUNT;
    }
}
