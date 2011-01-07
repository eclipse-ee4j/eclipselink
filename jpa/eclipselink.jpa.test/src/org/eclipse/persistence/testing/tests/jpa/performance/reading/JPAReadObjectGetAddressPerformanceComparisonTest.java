/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import javax.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read object Address.
 */
public class JPAReadObjectGetAddressPerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected long addressId;

    public JPAReadObjectGetAddressPerformanceComparisonTest() {
        setDescription("This test compares the performance of read object Address.");
    }

    /**
     * Get an address id.
     */
    public void setup() {
        EntityManager manager = createEntityManager();
        addressId = ((Address)manager.createQuery("Select a from Address a").getResultList().get(0)).getId();
        manager.close();
    }

    /**
     * Read address.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Address address = manager.getReference(Address.class, new Long(this.addressId));
        manager.getTransaction().commit();
        manager.close();
    }
}
