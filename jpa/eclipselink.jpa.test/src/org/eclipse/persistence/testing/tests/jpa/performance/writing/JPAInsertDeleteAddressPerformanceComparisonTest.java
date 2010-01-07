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
 package org.eclipse.persistence.testing.tests.jpa.performance.writing;

import javax.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of inserting and deleting Address.
 */
public class JPAInsertDeleteAddressPerformanceComparisonTest extends PerformanceRegressionTestCase {
    public JPAInsertDeleteAddressPerformanceComparisonTest() {
        setDescription("This test compares the performance of insert and delete Address.");
    }

    /**
     * Read an existing address for emulated database run.
     */
    public void setup() {
        EntityManager manager = createEntityManager();
        Address any = (Address)manager.createQuery("Select a from Address a").getResultList().get(0);
        // Create a query to avoid a cache hit to load emulated data.
        Query query = manager.createQuery("Select a from Address a where a.id = :id");
        query.setParameter("id", new Long(any.getId()));
        any = (Address)query.getSingleResult();
        manager.close();
        manager = createEntityManager();
        // Also call find, as may use different SQL.
        any = manager.find(Address.class, any.getId());
        manager.close();
    }
    
    /**
     * Insert and delete address.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Address address = new Address();
        address.setCity("Ottawa");
        address.setStreet("Hastings Perf");
        address.setProvince("ONT");
        manager.persist(address);
        manager.getTransaction().commit();
        manager.close();

        manager = createEntityManager();
        manager.getTransaction().begin();
        address = manager.getReference(Address.class, new Long(address.getId()));
        manager.remove(address);
        manager.getTransaction().commit();
        manager.close();
    }
}
