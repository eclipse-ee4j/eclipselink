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
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import javax.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read object Address.
 */
public class JPAReadObjectAddressNamedQueryPerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected String street;

    public JPAReadObjectAddressNamedQueryPerformanceComparisonTest() {
        setDescription("This test compares the performance of read object Address using a named query.");
    }

    /**
     * Get an address street.
     */
    public void setup() {
        EntityManager manager = createEntityManager();
        street = ((Address)manager.createQuery("Select a from Address a").getResultList().get(0)).getStreet();
        manager.close();
    }

    /**
     * Read address.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Query query = manager.createNamedQuery("findAddressByStreet");
        query.setParameter("street", street);
        Address address = (Address)query.getSingleResult();
        address.hashCode();
        manager.getTransaction().commit();
        manager.close();
    }
}
