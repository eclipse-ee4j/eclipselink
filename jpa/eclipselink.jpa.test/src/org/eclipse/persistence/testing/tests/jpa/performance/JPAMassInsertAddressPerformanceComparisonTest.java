/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance;

import javax.persistence.*;
import org.eclipse.persistence.testing.models.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of inserting many Address.
 */
public class JPAMassInsertAddressPerformanceComparisonTest extends PerformanceRegressionTestCase {
    public JPAMassInsertAddressPerformanceComparisonTest() {
        setDescription("This test compares the performance of insert many Address.");
    }

    /**
     * Delete all addresses.
     */
    public void reset() {
        getSession().executeNonSelectingSQL("Delete from ADDRESS where STREET = 'Hastings Perf'");
        //getSession().getIdentityMapAccessor().initializeIdentityMaps();
        //HibernatePerformanceComparisonModel.getSessionFactory().evict(Address.class);
    }

    /**
     * Insert address .
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        for (int index = 0; index < 50; index++) {
            Address address = new Address();
            address.setCity("Ottawa" + index);
            address.setStreet("Hastings Perf");
            address.setProvince("ONT");
            manager.persist(address);
        }
        manager.getTransaction().commit();
        manager.close();

        getSession().executeNonSelectingSQL("Delete from ADDRESS where STREET = 'Hastings Perf'");
        //getSession().getIdentityMapAccessor().initializeIdentityMaps();
        //HibernatePerformanceComparisonModel.getSessionFactory().evict(Address.class);
    }
}