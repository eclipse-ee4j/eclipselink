/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * Tests the cascade optimistic locking on a 1-M privately owned mapping that
 * requires multiple node notification/traverals.
 * 
 * @auther Guy Pelletier
 * @version 1.0 June 2/05
 */
public class CascadedMultiLevel12MOptimisticLockingTest extends CascadedOptimisticLockingTest {
    public CascadedMultiLevel12MOptimisticLockingTest() {}

    public void test () {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Bar bar = (Bar) uow.readObject(Bar.class);
        m_id = bar.getId();
        m_originalVersion = bar.getVersion();
        
        Bartender bartender = (Bartender) bar.getBartenders().firstElement();
        Award award = (Award) bartender.getQualification().getAwards().firstElement();
        award.setDescription("A completely bogus award");
        
        uow.commit();
    }
}
