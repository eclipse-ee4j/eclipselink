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
package org.eclipse.persistence.tools.workbench.test.utility;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.Association;
import org.eclipse.persistence.tools.workbench.utility.SimpleAssociation;

public class SimpleAssociationTests extends TestCase {
	private SimpleAssociation assoc;

	public static Test suite() {
		return new TestSuite(SimpleAssociationTests.class);
	}
	
	public SimpleAssociationTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.assoc = new SimpleAssociation("foo", "bar");
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetKey() {
		assertEquals("foo", this.assoc.getKey());
	}

	public void testGetValue() {
		assertEquals("bar", this.assoc.getValue());
	}

	public void testSetValue() {
		assertEquals("bar", this.assoc.getValue());
		this.assoc.setValue("baz");
		assertEquals("baz", this.assoc.getValue());
	}

	public void testEquals() {
		assertEquals(this.assoc, this.copy(this.assoc));

		SimpleAssociation assoc2 = new SimpleAssociation("foo", "baz");
		assertFalse(this.assoc.equals(assoc2));

		assoc2 = new SimpleAssociation("fop", "bar");
		assertFalse(this.assoc.equals(assoc2));
	}

	public void testHashCode() {
		assertEquals(this.assoc.hashCode(), this.copy(this.assoc).hashCode());
	}

	public void testClone() {
		this.verifyClone(this.assoc, (Association) this.assoc.clone());
	}

	private void verifyClone(Association expected, Association actual) {
		assertEquals(expected, actual);
		assertNotSame(expected, actual);
		assertEquals(expected.getKey(), actual.getKey());
		assertSame(expected.getKey(), actual.getKey());
		assertEquals(expected.getValue(), actual.getValue());
		assertSame(expected.getValue(), actual.getValue());
	}

	public void testSerialization() throws Exception {
		Association assoc2 = (Association) TestTools.serialize(this.assoc);

		assertEquals(this.assoc, assoc2);
		assertNotSame(this.assoc, assoc2);
		assertEquals(this.assoc.getKey(), assoc2.getKey());
		assertNotSame(this.assoc.getKey(), assoc2.getKey());
		assertEquals(this.assoc.getValue(), assoc2.getValue());
		assertNotSame(this.assoc.getValue(), assoc2.getValue());
	}

	private SimpleAssociation copy(SimpleAssociation sa) {
		return new SimpleAssociation(sa.getKey(), sa.getValue());
	}

}
