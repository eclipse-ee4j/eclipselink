/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.relationships;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.*;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.IsolatedItem;

import org.eclipse.persistence.testing.models.jpa.relationships.*;

public class IsolatedCacheTestSuite extends JUnitTestCase {
    public IsolatedCacheTestSuite() {}
    
    public IsolatedCacheTestSuite(String name) {
        super(name);
    }
    
    public void testSetup () {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("IsolatedCacheTestSuite");
        
        suite.addTest(new IsolatedCacheTestSuite("testSetup")); 
        suite.addTest(new IsolatedCacheTestSuite("testCacheIsolationDBQueryHit"));
        
        return suite;
    }
     
    public void testCacheIsolationDBQueryHit() throws Exception {
        EntityManager em = createEntityManager();
        
        // Step 1 - get an isolated item in the cache.
        beginTransaction(em);
        try {
            IsolatedItem item = new IsolatedItem();
            item.setDescription("A phoney item");
            item.setName("Phoney name");
            em.persist(item);
        
            commitTransaction(em);
            
            // Step 2 - clear the entity manager and see if the item still exists
            // in the uow cache.
            beginTransaction(em);
            
            em.clear();
            RepeatableWriteUnitOfWork uow = ((EntityManagerImpl) em.getDelegate()).getActivePersistenceContext(em.getTransaction());
            
            assertFalse("The isolated item was not cleared from the shared cache", uow.getIdentityMapAccessor().containsObjectInIdentityMap(item));
            assertFalse("The isolated item was not cleared from the uow cache", uow.getParent().getIdentityMapAccessor().containsObjectInIdentityMap(item));
            
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
