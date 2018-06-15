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
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * Tests the cascade optimistic locking on a 1-1 privately owned mapping that
 * requires multiple node notification/traversals.
 *
 * @author Guy Pelletier
 * @version 1.0 June 2/05
 */
public class CascadedMultiLevel121OptimisticLockingTest extends CascadedOptimisticLockingTest {
    public CascadedMultiLevel121OptimisticLockingTest() {}

    public void test () {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Bar bar = (Bar) uow.readObject(Bar.class);
        m_id = bar.getId();
        m_originalVersion = bar.getVersion();

        ((Bartender) bar.getBartenders().firstElement()).getQualification().setYearsOfExperience(99);

        uow.commit();
    }
}
