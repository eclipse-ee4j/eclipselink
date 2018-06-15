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
//     James Sutherland - initial impl
 package org.eclipse.persistence.testing.tests.jpa.performance.concurrent;

import java.util.List;

import javax.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of updating Address.
 */
public class JPAUpdateAddressConcurrencyComparisonTest extends ConcurrentPerformanceComparisonTest {
    protected List<Address> addresses;
    protected String street;
    protected int index;
    protected long count;
    protected int errors;

    public JPAUpdateAddressConcurrencyComparisonTest() {
        setDescription("This test compares the concurrency of update Address.");
    }

    public synchronized int incrementIndex() {
        this.index++;
        if (this.index >= this.addresses.size()) {
            this.index = 0;
        }
        return this.index;
    }

    /**
     * Get list of addresses.
     */
    public void setup() {
        super.setup();
        EntityManager manager = createEntityManager();
        this.addresses = manager.createQuery("Select a from Address a").getResultList();
        street = this.addresses.get(0).getStreet();
        manager.close();
        this.index = 0;
        this.count = 0;
    }

    /**
     * Update address.
     */
    public void runTask() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Address address = manager.find(Address.class, this.addresses.get(incrementIndex()).getId());
        count++;
        address.setStreet(street + count);
        try {
            manager.getTransaction().commit();
        } catch (Exception exception) {
            this.errors++;
            System.out.println("" + this.errors + ":" + exception);
        }
        manager.close();
    }
}
