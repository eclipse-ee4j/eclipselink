/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     08/15/2008-1.0.1 Chris Delahunt
//       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking
package org.eclipse.persistence.testing.tests.jpa.inherited;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;

public class OrderedListAttributeChangeTrackingJunitTest extends OrderedListJunitTest {

    private ObjectChangePolicy origPolicy;

    public OrderedListAttributeChangeTrackingJunitTest() {
        super();
    }

    public OrderedListAttributeChangeTrackingJunitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("OrderedListAttributeChangeTrackingJunitTest");
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("testSetup"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("testInitialize"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("test1"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("testInitialize"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("test2"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("testInitialize"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("test3"));

        return suite;
    }

    @Override
    public void setUp() {
        super.setUp();
        if ("testSetup".equals(getName())) {
            return;
        }
        DatabaseSession session = getPersistenceUnitServerSession();
        session.logout();
        origPolicy = session.getDescriptor(BeerConsumer.class).getObjectChangePolicy();
        session.getDescriptor(BeerConsumer.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
        session.login();
    }

    @Override
    public void tearDown() {
        clearCache();
        ServerSession session = getPersistenceUnitServerSession();
        session.logout();
        session.getDescriptor(BeerConsumer.class).setObjectChangePolicy(origPolicy);
        session.login();
        super.tearDown();
    }
}
