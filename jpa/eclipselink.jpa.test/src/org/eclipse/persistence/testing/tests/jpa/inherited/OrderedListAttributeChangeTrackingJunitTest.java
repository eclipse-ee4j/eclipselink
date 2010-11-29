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
 *     08/15/2008-1.0.1 Chris Delahunt 
 *       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.inherited;

import junit.framework.*;
import junit.extensions.TestSetup;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;

public class OrderedListAttributeChangeTrackingJunitTest extends OrderedListJunitTest {

    public OrderedListAttributeChangeTrackingJunitTest() {
        super();
    }

    public OrderedListAttributeChangeTrackingJunitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("OrderedListAttributeChangeTrackingJunitTest");
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("testInitialize"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("test1"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("testInitialize"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("test2"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("testInitialize"));
        suite.addTest(new OrderedListAttributeChangeTrackingJunitTest("test3"));

        return new TestSetup(suite) {
            private ObjectChangePolicy origPolicy;

            protected void setUp() {
                DatabaseSession session = JUnitTestCase.getServerSession();
                new InheritedTableManager().replaceTables(session);
                session.logout();
                origPolicy = session.getDescriptor(BeerConsumer.class).getObjectChangePolicy();
                session.getDescriptor(BeerConsumer.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
                session.login();
            }

            protected void tearDown() {
                new OrderedListAttributeChangeTrackingJunitTest().clearCache();
                ServerSession session = JUnitTestCase.getServerSession();
                session.logout();
                session.getDescriptor(BeerConsumer.class).setObjectChangePolicy(origPolicy);
                session.login();
            }
        };
    }
}
