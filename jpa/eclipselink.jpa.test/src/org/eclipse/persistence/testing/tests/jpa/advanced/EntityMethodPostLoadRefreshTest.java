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
//     Jan 9, 2009-1.1 Chris Delahunt
//       - Bug 244802: PostLoad callback getting invoked twice
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.testing.models.jpa.advanced.Project;

/**
 * Tests the @PostLoad event from an EntityListener is fired only once when in a transaction.
 *
 * @author Chris Delahunt
 */
public class EntityMethodPostLoadRefreshTest extends EntityListenerPostLoadRefreshTest{

    public void test() throws Exception {
        beginTransaction();
        m_beforeEvent = 0;  // New object, count starts at 0.

        Project project= new Project();
        project.setName("new project");
        getEntityManager().persist(project);
        getEntityManager().flush();
        getEntityManager().refresh( project );

        m_afterEvent = project.post_load_count;
        this.rollbackTransaction();
    }

}
