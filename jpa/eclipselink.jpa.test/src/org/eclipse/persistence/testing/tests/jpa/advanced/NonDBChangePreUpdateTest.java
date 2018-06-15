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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.testing.models.jpa.advanced.EmployeeListener;

/**
 * Tests the @PreUpdate events from an EntityListener. It tests the
 * pre update event on objects that do not have a database changes.
 *
 * @author Guy Pelletier
 */
public class NonDBChangePreUpdateTest extends CallbackEventTest {
    public void test() throws Exception {
        m_beforeEvent = EmployeeListener.PRE_UPDATE_COUNT;

        updateEmployeePhoneNumber();

        m_afterEvent = EmployeeListener.PRE_UPDATE_COUNT;
    }
}
