/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.relationships;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.*;

import org.eclipse.persistence.internal.jpa.base.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.base.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.IsolatedItem;

import org.eclipse.persistence.testing.models.jpa.relationships.*;

public class IsolatedCacheTestSuite extends JUnitTestCase {
    public IsolatedCacheTestSuite() {}
    
    public IsolatedCacheTestSuite(String name) {
        super(name);
    }
    
    public void setUp () {
        super.setUp();
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());
    }
    
    public static Test suite() {
        return new TestSuite(IsolatedCacheTestSuite.class) {
            protected void setUp(){}
            protected void tearDown(){}
        };
    }
     
    public void testCacheIsolationDBQueryHit() throws Exception {
        EntityManager em = createEntityManager();
        
        // Step 1 - get an isolated item in the cache.
        em.getTransaction().begin();
        
        IsolatedItem item = new IsolatedItem();
        item.setDescription("A phoney item");
        item.setName("Phoney name");
        em.persist(item);
    
        em.getTransaction().commit();
        
        // Step 2 - clear the entity manager and see if the item still exists
        // in the uow cache.
        em.getTransaction().begin();
        
        em.clear();
        RepeatableWriteUnitOfWork uow = (RepeatableWriteUnitOfWork) ((EntityManagerImpl) em.getDelegate()).getActivePersistenceContext(em.getTransaction());
        
        assertFalse("The isolated item was not cleared from the shared cache", uow.getIdentityMapAccessor().containsObjectInIdentityMap(item));
        assertFalse("The isolated item was not cleared from the uow cache", uow.getParent().getIdentityMapAccessor().containsObjectInIdentityMap(item));
        
        em.getTransaction().commit();
        em.close();
    }
}
