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
 package org.eclipse.persistence.testing.tests.jpa.performance.writing;

import javax.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of updating Address.
 */
public class JPAUpdateAddressPerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected long addressId;
    protected String street;
    protected long count;

    public JPAUpdateAddressPerformanceComparisonTest() {
        setDescription("This test compares the performance of update Address.");
    }

    /**
     * Get an address id.
     */
    public void setup() {
        EntityManager manager = createEntityManager();
        Address address = (Address)manager.createQuery("Select a from Address a").getResultList().get(0);
        addressId = address.getId();
        street = address.getStreet();
        manager.close();
        count = 0;
    }

    /**
     * Update address.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Address address = manager.find(Address.class, new Long(this.addressId));
        count++;
        address.setStreet(street + count);
        manager.getTransaction().commit();
        manager.close();
    }
}
