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


public class UnorderedArrayDiffTests extends TestCase {
	private ContainerDifferentiator differentiator;
	private Object[] array1;
	private Object[] array2;

	public static Test suite() {
		return new TestSuite(UnorderedArrayDiffTests.class);
	}
	
	public UnorderedArrayDiffTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.differentiator = ContainerDifferentiator.forArrays();
		this.array1 = this.buildArray();
		this.array2 = this.buildArray();
	}

	private Object[] buildArray() {
		Object[] result = new Object[5];
		result[0] = "zero";
		result[1] = "one";
		result[2] = "two";
		result[3] = "three";
		result[4] = "four";
		return result;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSameObject() {
		this.array2 = this.array1;
		Diff diff = this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMatch(diff, this.array1, this.array2);
	}

	public void testDifferentObjects() {
		Diff diff = this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMatch(diff, this.array1, this.array2);
	}

	public void testUnequalObjects() {
		String originalString = "zero";
		String modifiedString = "xxx-zero-xxx";
		int index = CollectionTools.indexOf(this.array2, originalString);
		this.array2[index] = modifiedString;
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMatchMismatch(diff, this.array1, this.array2);

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
		Diff diff = this.differentiator.diff(this.array1, collection3);
		this.verifyDiffMatchMismatch(diff, this.array1, collection3);
	}

	public void testNullElements() {
		String originalString = "zero";
		String modifiedString = null;
		int index = CollectionTools.indexOf(this.array2, originalString);
		this.array2[index] = modifiedString;
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMatchMismatch(diff, this.array1, this.array2);

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
		this.array2 = CollectionTools.remove(this.array2, removedString);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMatchMismatch(diff, this.array1, this.array2);

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
		this.array1 = CollectionTools.remove(this.array1, addedString);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMatchMismatch(diff, this.array1, this.array2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(0, removedElements.length);

		Object[] addedElements = diff.getAddedElements();
		assertEquals(1, addedElements.length);
		assertEquals(addedElements[0], addedString);
	}

	public void testChangedElement() {
		this.differentiator = ContainerDifferentiator.forArrays(this.buildSimpleElementDifferentiator());
		this.array1 = this.buildArray2();
		this.array2 = this.buildArray2();
		SimpleElement changedElement = (SimpleElement) this.array2[0];
		changedElement.description = "xxx-" + changedElement.description + "-xxx";

		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMatchMismatch(diff, this.array1, this.array2);

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

	private Object[] buildArray2() {
		Object[] result = new Object[5];
		result[0] = new SimpleElement(0, "zero");
		result[1] = new SimpleElement(1, "one");
		result[2] = new SimpleElement(2, "two");
		result[3] = new SimpleElement(3, "three");
		result[4] = new SimpleElement(4, "four");
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
