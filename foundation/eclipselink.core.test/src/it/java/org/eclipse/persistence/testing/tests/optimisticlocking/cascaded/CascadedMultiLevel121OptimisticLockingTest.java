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
