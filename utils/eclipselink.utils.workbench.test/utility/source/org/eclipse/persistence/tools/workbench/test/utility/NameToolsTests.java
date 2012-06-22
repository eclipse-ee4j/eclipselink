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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;

public class NameToolsTests extends TestCase {

	public static Test suite() {
		return new TestSuite(NameToolsTests.class);
	}
	
	public NameToolsTests(String name) {
		super(name);
	}

	public void testStringAbsentIgnoreCase() {
		List colorCollection = this.buildColorCollection();
		String returned = NameTools.uniqueNameForIgnoreCase("Taupe", colorCollection);
		assertEquals("Taupe", returned);
	}
	
	public void testStringPresentCaseDiffers() {
		List colorCollection = this.buildColorCollection();
		String returned = NameTools.uniqueNameFor("green", colorCollection);
		assertEquals("green", returned);
	}
	
	public void testStringPresentIgnoreCase() {
		List colorCollection = this.buildColorCollection();
		String returned = NameTools.uniqueNameForIgnoreCase("green", colorCollection);
		assertEquals("green2", returned);
	}
	
	public void testStringPresentWithAppendices() {
		List colorCollection = this.buildColorCollection();
		colorCollection.add("Red1");
		colorCollection.add("red2");
		String returned = NameTools.uniqueNameForIgnoreCase("red", colorCollection);
		colorCollection.remove("Red1");
		colorCollection.remove("red2");
		assertEquals("red3", returned);
	}

	private List buildColorCollection() {
		List colorCollection = new ArrayList();
		colorCollection.add("Red");
		colorCollection.add("Orange");	
		colorCollection.add("Yellow");	
		colorCollection.add("Green");
		colorCollection.add("Blue");	
		colorCollection.add("Indigo");
		colorCollection.add("Violet");
		return colorCollection;
	}

	public void testUniqueNameForCollection1() {
		Collection strings = new ArrayList();
		strings.add("Oracle");
		strings.add("Oracle Corporation");
		strings.add("Oracle2");
		strings.add("oracle1");
		strings.add("Oracl");

		assertEquals("Oracle3", NameTools.uniqueNameFor("Oracle", strings));
		assertEquals("Test", NameTools.uniqueNameFor("Test", strings));

		assertEquals("Oracle3", NameTools.uniqueNameForIgnoreCase("Oracle", strings));
		assertEquals("oracle3", NameTools.uniqueNameForIgnoreCase("oracle", strings));
		assertEquals("Test", NameTools.uniqueNameForIgnoreCase("Test", strings));
	}

	public void testUniqueNameForCollection2() {
		Collection strings = new ArrayList();
		strings.add("Oracle");
		strings.add("oracle");
		strings.add("Oracle2");
		strings.add("Oracle1");

		assertEquals("Oracle3", NameTools.uniqueNameFor("Oracle", strings));
		assertEquals("Test", NameTools.uniqueNameFor("Test", strings));

		strings.add("Oracle Corporation");
		assertEquals("Oracle3", NameTools.uniqueNameForIgnoreCase("Oracle", strings));
		assertEquals("oracle3", NameTools.uniqueNameForIgnoreCase("oracle", strings));
		assertEquals("Test", NameTools.uniqueNameForIgnoreCase("Test", strings));
	}

	public void testUniqueNameForCollection3() {
		Collection strings = new ArrayList();
		strings.add("Oracle");
		strings.add("Oracle");
		strings.add("Oracle2");
		strings.add("Oracle1");

		assertEquals("Oracle3", NameTools.uniqueNameFor("Oracle", strings));
	}

	public void testUniqueNameForIterator1() {
		Collection strings = new ArrayList();
		strings.add("Oracle");
		strings.add("Oracle Corporation");
		strings.add("Oracle2");
		strings.add("oracle1");
		strings.add("Oracl");

		assertEquals("Oracle3", NameTools.uniqueNameFor("Oracle", strings.iterator()));
		assertEquals("Test",    NameTools.uniqueNameFor("Test",   strings.iterator()));

		assertEquals("Oracle3", NameTools.uniqueNameForIgnoreCase("Oracle", strings.iterator()));
		assertEquals("oracle3", NameTools.uniqueNameForIgnoreCase("oracle", strings.iterator()));
		assertEquals("Test", NameTools.uniqueNameForIgnoreCase("Test", strings.iterator()));
	}

	public void testUniqueNameForIterator2() {
		Collection strings = new ArrayList();
		strings.add("Oracle");
		strings.add("oracle");
		strings.add("Oracle2");
		strings.add("Oracle1");

		assertEquals("Oracle3", NameTools.uniqueNameFor("Oracle", strings.iterator()));
		assertEquals("Test",    NameTools.uniqueNameFor("Test",   strings.iterator()));

		strings.add("Oracle Corporation");
		assertEquals("Oracle3", NameTools.uniqueNameForIgnoreCase("Oracle", strings.iterator()));
		assertEquals("oracle3", NameTools.uniqueNameForIgnoreCase("oracle", strings.iterator()));
		assertEquals("Test", NameTools.uniqueNameForIgnoreCase("Test", strings.iterator()));
	}

	public void testUniqueNameForIterator3() {
		Collection strings = new ArrayList();
		strings.add("Oracle");
		strings.add("Oracle");
		strings.add("Oracle2");
		strings.add("Oracle1");

		assertEquals("Oracle3", NameTools.uniqueNameFor("Oracle", strings.iterator()));
	}

	public void testUniqueJavaNameForCollection() {
		Collection strings = new ArrayList();
		strings.add("Oracle");
		strings.add("Oracle");
		strings.add("Oracle2");
		strings.add("Oracle1");

		assertEquals("private2", NameTools.uniqueJavaNameFor("private", strings.iterator()));
		assertEquals("class2", NameTools.uniqueJavaNameFor("class", strings.iterator()));
	}

	public void testBuildQualifiedDatabaseObjectName() {
		assertEquals("catalog.schema.name", NameTools.buildQualifiedDatabaseObjectName("catalog", "schema", "name"));
		assertEquals("schema.name", NameTools.buildQualifiedDatabaseObjectName("schema", null, "name"));
		assertEquals("catalog.name", NameTools.buildQualifiedDatabaseObjectName(null, "catalog", "name"));
		assertEquals("name", NameTools.buildQualifiedDatabaseObjectName(null, null, "name"));
	}

	public void testJavaReservedWords() {
		assertTrue(CollectionTools.contains(NameTools.javaReservedWords(), "class"));
		assertFalse(CollectionTools.contains(NameTools.javaReservedWords(), "Class"));
		assertTrue(CollectionTools.contains(NameTools.javaReservedWords(), "private"));
	}

}
