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

/**
 * This test compares the performance of read object Address.
 */
public class JPAReadObjectAddressExpressionPerformanceComparisonTest extends JPAReadPerformanceComparisonTest {
    protected long addressId;

    public JPAReadObjectAddressExpressionPerformanceComparisonTest(boolean isReadOnly) {
        super(isReadOnly);
        setName("JPAReadObjectAddressExpressionPerformanceComparisonTest-readonly:" + isReadOnly);
        setDescription("This test compares the performance of read object Address with an expression.");
    }

    /**
     * Get an address id.
     */
    public void setup() {
        addressId = ((Address)getSession().readObject(Address.class)).getId();
    }

    /**
     * Read address.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        Query query = manager.createQuery("Select a from Address a where a.city = :city");
        query.setParameter("city", "Ottawa");
        Address address = (Address)uniqueResult(query, manager);
        manager.close();
    }
}