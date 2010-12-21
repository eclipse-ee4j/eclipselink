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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.utility.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.diff.ContainerDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ContainerDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveFieldDiff;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


public class CollectionDiffTests extends TestCase {
	private ContainerDifferentiator differentiator;
	private Collection collection1;
	private Collection collection2;

	public static Test suite() {
		return new TestSuite(CollectionDiffTests.class);
	}
	
	public CollectionDiffTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.differentiator = ContainerDifferentiator.forCollections();
		this.collection1 = this.buildCollection();
		this.collection2 = this.buildCollection();
	}

	private Collection buildCollection() {
		Collection result = new ArrayList();
		result.add("zero");
		result.add("one");
		result.add("two");
		result.add("three");
		result.add("four");
		return result;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSameObject() {
		this.collection2 = this.collection1;
		Diff diff = this.differentiator.diff(this.collection1, this.collection2);
		this.verifyDiffMatch(diff, this.collection1, this.collection2);
	}

	public void testDifferentObjects() {
		Diff diff = this.differentiator.diff(this.collection1, this.collection2);
		this.verifyDiffMatch(diff, this.collection1, this.collection2);
	}

	public void testUnequalObjects() {
		String originalString = "zero";
		String modifiedString = "xxx-zero-xxx";
		this.collection2.remove(originalString);
		this.collection2.add(modifiedString);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.collection1, this.collection2);
		this.verifyDiffMatchMismatch(diff, this.collection1, this.collection2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(1, removedElements.length);
		assertTrue(CollectionTools.contains(removedElements, originalString));

		Object[] addedElements = diff.getAddedElements();
		assertEquals(1, addedElements.length);
		assertTrue(CollectionTools.contains(addedElements, modifiedString));
	}

	public void testOneNull() {
		Object collection3 = null;
		Diff diff = this.differentiator.diff(this.collection1, collection3);
		this.verifyDiffMatchMismatch(diff, this.collection1, collection3);
	}
	
	public void testNullElements() {
		String originalString = "zero";
		String modifiedString = null;
		this.collection2.remove(originalString);
		this.collection2.add(modifiedString);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.collection1, this.collection2);
		this.verifyDiffMatchMismatch(diff, this.collection1, this.collection2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(1, removedElements.length);
		assertTrue(CollectionTools.contains(removedElements, originalString));

		Object[] addedElements = diff.getAddedElements();
		assertEquals(1, addedElements.length);
		assertTrue(CollectionTools.contains(addedElements, modifiedString));
	}

	public void testRemovedElement() {
		String removedString = "zero";
		this.collection2.remove(removedString);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.collection1, this.collection2);
		this.verifyDiffMatchMismatch(diff, this.collection1, this.collection2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(1, removedElements.length);
		assertEquals(removedElements[0], removedString);

		Object[] addedElements = diff.getAddedElements();
		assertEquals(0, addedElements.length);
	}

	public void testAddedElement() {
		String addedString = "zero";
		this.collection1.remove(addedString);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.collection1, this.collection2);
		this.verifyDiffMatchMismatch(diff, this.collection1, this.collection2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(0, removedElements.length);

		Object[] addedElements = diff.getAddedElements();
		assertEquals(1, addedElements.length);
		assertEquals(addedElements[0], addedString);
	}

	public void testChangedElement() {
		this.differentiator = ContainerDifferentiator.forCollections(this.buildSimpleElementDifferentiator());
		this.collection1 = this.buildCollection2();
		this.collection2 = this.buildCollection2();
		SimpleElement changedElement = (SimpleElement) this.collection2.iterator().next();
		changedElement.description = "xxx-" + changedElement.description + "-xxx";

		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.collection1, this.collection2);
		this.verifyDiffMatchMismatch(diff, this.collection1, this.collection2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(5, diffs.length);
		Collection changedDiffs = new ArrayList();
		for (int i = 0; i < diffs.length; i++) {
			if (diffs[i].different()) {
				changedDiffs.add(diffs[i]);
			}
		}
		assertEquals(1, changedDiffs.size());

		List leafReflectiveFieldMismatches = DiffTestTools.differentLeafReflectiveFieldDiffList(diff);
		assertEquals(1, leafReflectiveFieldMismatches.size());

		ReflectiveFieldDiff leafDiff = (ReflectiveFieldDiff) leafReflectiveFieldMismatches.get(0);
		assertEquals("description", leafDiff.getField().getName());
		assertEquals("zero", leafDiff.getObject1());
		assertEquals("xxx-zero-xxx", leafDiff.getObject2());


		Object[] removedElements = diff.getRemovedElements();
		assertEquals(0, removedElements.length);

		Object[] addedElements = diff.getAddedElements();
		assertEquals(0, addedElements.length);
	}

	private Collection buildCollection2() {
		Collection result = new ArrayList();
		result.add(new SimpleElement(0, "zero"));
		result.add(new SimpleElement(1, "one"));
		result.add(new SimpleElement(2, "two"));
		result.add(new SimpleElement(3, "three"));
		result.add(new SimpleElement(4, "four"));
		return result;
	}

	private void verifyDiffMatchMismatch(Diff diff, Object object1, Object object2) {
		assertEquals(object1, diff.getObject1());
		assertEquals(object2, diff.getObject2());
		assertFalse(diff.identical());
		assertTrue(diff.different());
		assertTrue(diff.getDescription().length() > 0);
	}

	private void verifyDiffMatch(Diff diff, Object object1, Object object2) {
		assertEquals(object1, diff.getObject1());
		assertEquals(object2, diff.getObject2());
		assertTrue(diff.identical());
		assertFalse(diff.different());
		assertEquals(0, diff.getDescription().length());
	}

	private Differentiator buildSimpleElementDifferentiator() {
		ReflectiveDifferentiator rd = new ReflectiveDifferentiator(SimpleElement.class);
		rd.addKeyFieldNamed("key");
		return rd;
	}

	// ******************** member classes ********************

	private class SimpleElement {
		int key;
		String description;
		SimpleElement(int key, String description) {
			super();
			this.key = key;
			this.description = description;
		}
		public String toString() {
			return StringTools.buildToStringFor(this, Integer.toString(this.key) + " - " + this.description);
		}
	}

}
