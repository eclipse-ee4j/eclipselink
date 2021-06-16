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
//     Jan 8, 2009-1.1 Chris Delahunt
//       - Bug 244802: PostLoad callback getting invoked twice
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.testing.models.jpa.advanced.Project;

/**
 * Tests the @PostLoad event from an EntityMethod is fired only once when in a transaction.
 *
 * @author Chris Delahunt
 */
public class EntityMethodPostLoadTransactionTest extends CallbackEventTest {
    public void test() throws Exception {
        beginTransaction();
        m_beforeEvent = 0;  // New object, count starts at 0.

        Project project = getEntityManager().find(Project.class, m_project.getId());

        m_afterEvent = project.post_load_count;
        this.rollbackTransaction();
    }
}
