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

import org.eclipse.persistence.testing.models.jpa.advanced.Project;

/**
 * Tests the @PreUpdate events from an Entity.
 *
 * @author Guy Pelletier
 */
public class EntityMethodPreUpdateTest extends CallbackEventTest {
    public void test() throws Exception {
        m_beforeEvent = 0;  // Loading a new object to update, count starts at 0.

        Project project = updateProject();

        m_afterEvent = project.pre_update_count;
    }
}
