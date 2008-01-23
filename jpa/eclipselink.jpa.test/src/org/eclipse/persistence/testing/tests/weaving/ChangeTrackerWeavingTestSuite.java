/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.weaving;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.weaving.PersistenceWeaver;
import org.eclipse.persistence.internal.weaving.TransformerFactory;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.models.weaving.Customer;
import org.eclipse.persistence.testing.models.weaving.Item;
import org.eclipse.persistence.testing.models.weaving.Order;

public class ChangeTrackerWeavingTestSuite  extends TestCase {

    static class CustomizeMetadataProcessor extends org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor{
        CustomizeMetadataProcessor(AbstractSession session, ClassLoader loader, Collection<Class> entities, boolean enableLazyForOneToOne){
            super(null, session, loader, enableLazyForOneToOne);
            m_loader = loader;
            m_project = new MetadataProject(null, session, enableLazyForOneToOne);
            m_session = session;
            Collection<String> entityNames = new HashSet<String>(entities.size());
            for (Class entity : entities) {
                m_project.addDescriptor(new MetadataDescriptor(entity));
                entityNames.add(entity.getName());
            }
            m_project.setWeavableClassNames(entityNames);
            m_logger = new MetadataLogger(session);
            
        }
    }
    
    // fixtures
    public static SimpleClassLoader setupClassLoader = null;
    public static SimpleClassLoader simpleClassLoader = null;
    public static byte[] originalOrderBytes = null;
    public static byte[] originalItemBytes = null;
    public static List<Class> entities = null;
    
    static {
        setUpFixtures();
    }
    public static void setUpFixtures() {
        setupClassLoader = new SimpleClassLoader();
        simpleClassLoader = new SimpleClassLoader();
        InputStream is = simpleClassLoader.getResourceAsStream(Order.class.getName().replace('.','/') + ".class");
        originalOrderBytes = SimpleWeaverTestSuite.readStreamContentsIntoByteArray(is);
        is = simpleClassLoader.getResourceAsStream(Item.class.getName().replace('.','/') + ".class");
        originalItemBytes = SimpleWeaverTestSuite.readStreamContentsIntoByteArray(is);
        entities = new ArrayList<Class>();
        entities.add(Customer.class);
        entities.add(Item.class);
        entities.add(Order.class);
    }
    public static void tearDownFixtures() {
        simpleClassLoader = null;
        originalOrderBytes = null;
        originalItemBytes = null;
        entities = null;
    }

    public ChangeTrackerWeavingTestSuite(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("ChangeTrackerWeavingTestSuite");
        suite.addTest(new RelationshipWeaverTestSuite("test weaving of change tracking") {
            public void setUp() {
            }
            public void tearDown() {
            }
            public void runTest() throws Exception {
                testChangeTrackingWeaving();
            }
        });
        return suite;
    }
    
    public static void testChangeTrackingWeaving() throws Exception {
        Session session = new Project(new DatabaseLogin()).createServerSession();
        session.setLogLevel(SessionLog.OFF);
        MetadataProcessor eap = new CustomizeMetadataProcessor((AbstractSession) session, setupClassLoader, entities, true);
        eap.processAnnotations();
        PersistenceWeaver tw = (PersistenceWeaver)TransformerFactory.createTransformerAndModifyProject(session, entities, Thread.currentThread().getContextClassLoader(), true, true, true, true);
        byte[] newOrderBytes = tw.transform(simpleClassLoader, Order.class.getName().replace('.','/'), null, null, originalOrderBytes);
        byte[] newItemBytes = tw.transform(simpleClassLoader, Item.class.getName().replace('.','/'), null, null, originalItemBytes);
        
        Class newOrderClass = simpleClassLoader.define_class(Order.class.getName(), newOrderBytes, 0, newOrderBytes.length);
        assertNotNull("could not build weaved Order class", newOrderClass);

        Class newItemClass = simpleClassLoader.define_class(Item.class.getName(), newItemBytes, 0, newItemBytes.length);
        assertNotNull("could not build weaved Item class", newItemClass);

        Object newOrder = null;
        Object newItem = null;

        try {
            // ensure ChangeTracker interface has been added
            newOrder = newOrderClass.newInstance();
            
            // ensure TopLinkWeavedChangeTracking interface has been added
            newOrder = newOrderClass.newInstance();
            
            // ensure ChangeTracker interface has been added
            newItem = newItemClass.newInstance();
            
            // ensure TopLinkWeavedChangeTracking interface has been added
            newItem = newItemClass.newInstance();

        }
        catch (Exception e) {
            fail("testChangeTrackingWeaving failed: " + e.toString());
        }

        // check that Order's '_persistence_listener' exists
        Field orderField = null;
        Field itemField = null;

        try {
            orderField = newOrderClass.getDeclaredField("_persistence_listener");
            itemField = newItemClass.getDeclaredField("_persistence_listener");
        } catch (Exception e) {
            fail("testChangeTrackingWeaving failed: " + e.toString());
        }
        assertNotNull("Weaved Order class does not have '_persistence_listener' field", orderField);
        assertNotNull("Weaved Item class does not have '_persistence_listener' field", itemField);
    }

}
