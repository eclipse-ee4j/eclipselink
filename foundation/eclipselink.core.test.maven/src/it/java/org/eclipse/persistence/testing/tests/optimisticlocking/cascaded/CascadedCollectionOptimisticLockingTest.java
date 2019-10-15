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
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * Tests the cascade optimistic locking on removing from a collection mapping.
 *
 * @author Guy Pelletier
 * @version 1.0 June 2/05
 */
public class CascadedCollectionOptimisticLockingTest extends CascadedOptimisticLockingTest {
    public CascadedCollectionOptimisticLockingTest() {}

    public void test () {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Bar bar = (Bar) uow.readObject(Bar.class);
        m_id = bar.getId();
        m_originalVersion = bar.getVersion();
        bar.removeBartender((Bartender) bar.getBartenders().firstElement());
        uow.commit();
    }
}
