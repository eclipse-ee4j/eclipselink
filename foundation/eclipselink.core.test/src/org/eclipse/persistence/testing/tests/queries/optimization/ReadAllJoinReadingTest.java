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
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.ReadAllTest;

/**
 * This class tests the join reading feature.
 */
public class ReadAllJoinReadingTest extends ReadAllTest {
    public String attribute;

    public ReadAllJoinReadingTest(int size, String attribute) {
        super(LargeProject.class, size);
        setName("JoinReadingTest" + attribute);
        this.attribute = attribute;
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        super.setup();
    }

    protected void verify() throws Exception {
        super.verify();

        org.eclipse.persistence.internal.queries.ContainerPolicy aContainerPolicy = getQuery().getContainerPolicy();
        for (Object iter = aContainerPolicy.iteratorFor(objectsFromDatabase);
                 aContainerPolicy.hasNext(iter);) {
            LargeProject proj = (LargeProject)aContainerPolicy.next(iter, getAbstractSession());
            if (!proj.teamLeader.isInstantiated()) {
                throw new TestErrorException("teamLeader value holder not instantiated.");
            }
            if (!((Employee)proj.getTeamLeader()).address.isInstantiated()) {
                throw new TestErrorException("teamLeader address value holder not instantiated.");
            }
            if (proj.getTeamLeader().getAddress().getCity().length() == 0) {
                throw new org.eclipse.persistence.testing.framework.TestErrorException("Address wrong.");
            }
        }
    }
}
