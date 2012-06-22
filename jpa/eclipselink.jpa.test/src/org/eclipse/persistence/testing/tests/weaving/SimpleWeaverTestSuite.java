/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;
import java.io.*;

import junit.framework.*;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.testing.models.weaving.SimpleObject;
import org.eclipse.persistence.testing.models.weaving.SimpleProject;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.weaving.*;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.weaving.*;

public class SimpleWeaverTestSuite extends TestCase {
	
	// fixtures
	public static SimpleClassLoader simpleClassLoader = null;
	public static byte[] originalBytes = null;
	public static Collection entities = null;
	
	static {
		setUpFixtures();
	}
	public static void setUpFixtures() {
		simpleClassLoader = new SimpleClassLoader();
		InputStream is = simpleClassLoader.getResourceAsStream(SimpleObject.class.getName().replace('.','/') + ".class");
		originalBytes = readStreamContentsIntoByteArray(is);
		entities = new ArrayList();
		entities.add(new MetadataAsmFactory(new MetadataLogger(null), SimpleWeaverTestSuite.class.getClassLoader()).getMetadataClass(SimpleObject.class.getName()));
	}
	public static void tearDownFixtures() {
		simpleClassLoader = null;
		originalBytes = null;
		entities = null;
	}

	public SimpleWeaverTestSuite(String testName) {
		super(testName);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Simple TopLinkWeaver Tests");
		suite.addTest(new SimpleWeaverTestSuite(
			"test TopLinkWeaver with null Session") {
			public void setUp() {
			}
			public void tearDown() {
			}
			public void runTest() {
				nullSessionTest();
			}
		});
		suite.addTest(new SimpleWeaverTestSuite(
			"test TopLinkWeaver with null Project") {
			public void setUp() {
			}
			public void tearDown() {
			}
			public void runTest() {
				Session session = new ServerSession();
				session.setLogLevel(SessionLog.OFF);
				nullProjectTest(session);
			}
		});
		suite.addTest(new SimpleWeaverTestSuite(
			"test TopLinkWeaver with null list of entities") {
			public void setUp() {
			}
			public void tearDown() {
			}
			public void runTest() {
				Session session = new ServerSession(new SimpleProject());
				session.setLogLevel(SessionLog.OFF);
				nullEntitiesTest(session);
			}
		});
		suite.addTest(new SimpleWeaverTestSuite(
			"test TopLinkWeaver with empty list of entities") {
			public void setUp() {
			}
			public void tearDown() {
			}
			public void runTest() {
				Session session = new ServerSession(new SimpleProject());
				session.setLogLevel(SessionLog.OFF);
				emptyEntitiesTest(session);
			}
		});
		suite.addTest(new SimpleWeaverTestSuite("build TopLinkWeaver") {
			public void setUp() {
			}
			public void tearDown() {
			}
			public void runTest() {
				Session session = new ServerSession(new SimpleProject());
				session.setLogLevel(SessionLog.OFF);
				buildWeaver(session, entities);
			}
		});
		return suite;
	}
	
	public void nullSessionTest() {
		boolean expectedFailure = false;
		try {
			TransformerFactory.createTransformerAndModifyProject(null, null, Thread.currentThread().getContextClassLoader(), true, false, true, true); 

		}
		catch (IllegalArgumentException iae) {
			expectedFailure = true;
		}
		assertTrue(
			"TopLinkWeaver did not throw proper IllegalArgumentException for null Session in constructor",
			expectedFailure);
	}
	
	public void nullProjectTest(Session session) {
		boolean expectedFailure = false;
		try {
			TransformerFactory.createTransformerAndModifyProject(session, null, Thread.currentThread().getContextClassLoader(), true, false, true, true); 
		}
		catch (IllegalArgumentException iae) {
			expectedFailure = true;
		}
		assertTrue(
			"TopLinkWeaver did not throw proper IllegalArgumentException for null Session's Project in constructor",
			expectedFailure);
	}
	
	public void nullEntitiesTest(Session session) {
		try {
			TransformerFactory.createTransformerAndModifyProject(session, null, Thread.currentThread().getContextClassLoader(), true, false, true, true); 
		}
		catch (Exception e) {
			fail(getName() + " failed: " + e.toString());
		}
	}
	
	public void emptyEntitiesTest(Session session) {
		try {
			TransformerFactory.createTransformerAndModifyProject(session, new ArrayList(), Thread.currentThread().getContextClassLoader(), true, false, true, true); 
		}
		catch (Exception e) {
			fail(getName() + " failed: " + e.toString());
		}
	}
	
	public PersistenceWeaver buildWeaver(Session session, Collection entities) {

		PersistenceWeaver tw = null;
		try {
			tw = TransformerFactory.createTransformerAndModifyProject(session, entities, Thread.currentThread().getContextClassLoader(), true, false, true, true); 
		}
		catch (Exception e) {
			fail(getName() + " failed: " + e.toString());
		}
		assertNotNull("could not build TopLinkWeaver", tw);
		return tw;
	}
	
	public void buildWeavedClass(byte[] weavedBytes) {

		Class weavedClass = null;
		try {
			weavedClass = simpleClassLoader.define_class(SimpleObject.class.getName(), weavedBytes, 0, weavedBytes.length);
		}
		catch (Exception e) {
			fail(getName() + " failed: " + e.toString());
		}
		assertNotNull("could not build weaved class", weavedClass);
		
		Class[] interfaces = null;
		try {
			interfaces = weavedClass.getInterfaces();
		}
		catch (Exception e) {
			fail(getName() + " failed: " + e.toString());
		}
		assertNotNull("Weaved class has no interfaces", interfaces);
		boolean containsSerializable = false;
		boolean containsTopLinkWeaved = false;
		boolean containsChangeTracker = false;
		for (int i = 0; i < interfaces.length; i++) {
			Class c = interfaces[i];
			if (c.equals(Serializable.class)) {
				containsSerializable = true;
			}
			if (c.equals(PersistenceWeavedLazy.class)) {
				containsTopLinkWeaved = true;
			}
		}
		// test that original interface is still there
		assertNotNull("Weaved class does not implement Serializable",
			containsSerializable);
		// test that 'marker' interface added
		assertNotNull("Weaved class does not implement TopLinkWeaved",
			containsTopLinkWeaved);
		// none of the mapping are isMutable, so it should be a ChangeTracker
		assertNotNull("Weaved class does not implement ChangeTracker",
			containsChangeTracker);
	}
	
	public static byte[] readStreamContentsIntoByteArray(InputStream is) {
		
		byte[] result = null;
		try {
			int readPosition = 0;
			result = new byte[is.available()];
			while (true) {
				int bytesRead = is.read(result, readPosition,
					result.length - readPosition);
				if (bytesRead == -1) {
					break;
				}
				readPosition += bytesRead;
				if (readPosition == result.length) {
					int peek = is.read();
					if (peek == -1) {
						break;
					}
					byte[] temp = new byte[result.length * 2];
					System.arraycopy(result, 0, temp, 0, readPosition);
					result = temp;
					result[readPosition++] = (byte) peek;
				}
			}
			if (readPosition < result.length) {
				byte[] temp = new byte[readPosition];
				System.arraycopy(result, 0, temp, 0, readPosition);
				result = temp;
			}
		}
		catch (Exception e) { /* ignore */ }
		finally {
			try {
				is.close();
			}
			catch (IOException e) { /* ignore */ }
		}
		return result;
	}

}
