/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.weaving;

// J2SE imports
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import javax.persistence.spi.PersistenceUnitInfo;

// JUnit imports
import junit.framework.*;

// TopLink imports
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.models.weaving.Customer;
import org.eclipse.persistence.testing.models.weaving.Item;
import org.eclipse.persistence.testing.models.weaving.Order;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.indirection.WeavedAttributeValueHolderInterface;
import org.eclipse.persistence.internal.weaving.*;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;;

public class RelationshipWeaverTestSuite extends TestCase {
    
    class CustomizeMetadataProcessor extends org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor{
        CustomizeMetadataProcessor(AbstractSession session, ClassLoader loader, Collection<Class> entities, boolean enableLazyForOneToOne){
            super(null, session, loader, enableLazyForOneToOne);
            m_loader = loader;
            m_project = new MetadataProject(null, session, enableLazyForOneToOne);
            m_session = session;
            Collection<String> entityNames = new HashSet<String>(entities.size());
            for (Class entity : entities) {
                m_project.addClassAccessor(new ClassAccessor(entity, m_project));
                entityNames.add(entity.getName());
            }
        }
    }
	
	// fixtures
    public static SimpleClassLoader setupClassLoader = null;
	public static SimpleClassLoader simpleClassLoader = null;
	public static byte[] originalCustomerBytes = null;
	public static byte[] originalItemBytes = null;
	public static byte[] originalOrderBytes = null;
	public static List<Class> entities = null;
	
	static {
		setUpFixtures();
	}
	public static void setUpFixtures() {
        setupClassLoader = new SimpleClassLoader();
		simpleClassLoader = new SimpleClassLoader();
		InputStream is = simpleClassLoader.getResourceAsStream(
			Customer.class.getName().replace('.','/') + ".class");
		originalCustomerBytes =
			SimpleWeaverTestSuite.readStreamContentsIntoByteArray(is);
		is = simpleClassLoader.getResourceAsStream(
			Item.class.getName().replace('.','/') + ".class");
		originalItemBytes =
			SimpleWeaverTestSuite.readStreamContentsIntoByteArray(is);
		is = simpleClassLoader.getResourceAsStream(
			Order.class.getName().replace('.','/') + ".class");
		originalOrderBytes =
				SimpleWeaverTestSuite.readStreamContentsIntoByteArray(is);
		entities = new ArrayList<Class>();
		entities.add(Customer.class);
		entities.add(Item.class);
		entities.add(Order.class);
	}
	public static void tearDownFixtures() {
		simpleClassLoader = null;
		originalCustomerBytes = null;
		originalItemBytes = null;
		originalOrderBytes = null;
		entities = null;
	}

	public RelationshipWeaverTestSuite(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("RelationshipWeaverTestSuite");
		suite.addTest(new RelationshipWeaverTestSuite(
			"test Relationships model with weaving") {
			public void setUp() {
			}
			public void tearDown() {
			}
			public void runTest() throws Exception {
				testRelationshipsModel();
			}
		});
		return suite;
	}
	
	public void testRelationshipsModel() throws Exception {
		Class newOrderClass = null;
        Session session = new Project(new DatabaseLogin()).createServerSession();
        session.setLogLevel(SessionLog.OFF);
        MetadataProcessor eap = new CustomizeMetadataProcessor((AbstractSession) session, setupClassLoader, entities, true);

        eap.processAnnotations();
        PersistenceWeaver tw = (PersistenceWeaver)
            TransformerFactory.createTransformerAndModifyProject(session, entities, Thread.currentThread().getContextClassLoader(), true, false, true, true);
        byte[] newOrderBytes = tw.transform(simpleClassLoader, Order.class.getName().replace('.','/'), null, null, originalOrderBytes);
        newOrderClass = simpleClassLoader.define_class(Order.class.getName(), newOrderBytes, 0, newOrderBytes.length);
		assertNotNull("could not build weaved Order class", newOrderClass);

		Object newOrder = null;
		try {
            // ensure TopLinkWeavedLazy interface has been added
			newOrder = (PersistenceWeavedLazy)newOrderClass.newInstance();
		}
		catch (Exception e) {
			fail(getName() + " failed: " + e.toString());
		}
	
		// check that Order's 'item' field has a corresponding ValueHOlder
		Field f = null;
		try {
			f = newOrderClass.getDeclaredField("_persistence_item_vh");
		} catch (Exception e) {
			fail(getName() + " failed: " + e.toString());
		}
		assertNotNull("Weaved Order class does not have '_toplink_item_vh' field", f);
		assertSame("Weaved Order class' '_toplink_item_vh' field is not a ValueHolder", WeavedAttributeValueHolderInterface.class, f.getType());
		// check that Order's 'customer' field is a ValueHolder
		f = null;
		try {
			f = newOrderClass.getDeclaredField("_persistence_customer_vh");
		} catch (Exception e) {
			fail(getName() + " failed: " + e.toString());
		}
		assertNotNull("Weaved Order class does not have '_toplink_customer_vh' field", f);
		assertSame("Weaved Order class' '_toplink_customer_vh' field is not a ValueHolder", WeavedAttributeValueHolderInterface.class, f.getType());				
	}

}
