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
 * Tests the cascade optimistic locking on removing from a colleciton mapping
 * that requires multiple node notification/traverals.
 * 
 * @auther Guy Pelletier
 * @version 1.0 June 2/05
 */
public class CascadedMultiLevelCollectionOptimisticLockingTest extends CascadedOptimisticLockingTest {
    public CascadedMultiLevelCollectionOptimisticLockingTest() {}
    
    public void test () {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Bar bar = (Bar) uow.readObject(Bar.class);
        m_id = bar.getId();
        m_originalVersion = bar.getVersion();
        
        Bartender bartender = (Bartender) bar.getBartenders().firstElement();
        
        Award award = new Award();
        award.setDescription("A bogus award");
        award.setQualification(bartender.getQualification());
        bartender.getQualification().addAward(award);
        
        uow.commit();
    }
}
