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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.diff.ContainerDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ContainerDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;



public class MapDiffTests extends TestCase {
	private ContainerDifferentiator differentiator;
	private Map map1;
	private Map map2;

	public static Test suite() {
		return new TestSuite(MapDiffTests.class);
	}
	
	public MapDiffTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.differentiator = ContainerDifferentiator.forMaps();
		this.map1 = this.buildMap();
		this.map2 = this.buildMap();
	}

	private Map buildMap() {
		Map result = new HashMap();
		result.put("key0", "zero");
		result.put("key1", "one");
		result.put("key2", "two");
		result.put("key3", "three");
		result.put("key4", "four");
		return result;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSameObject() {
		this.map2 = this.map1;
		Diff diff = this.differentiator.diff(this.map1, this.map2);
		this.verifyDiffMatch(diff, this.map1, this.map2);
	}

	public void testDifferentObjects() {
		Diff diff = this.differentiator.diff(this.map1, this.map2);
		this.verifyDiffMatch(diff, this.map1, this.map2);
	}

	public void testChangedValue() {
		String key = "key0";
		String originalString = (String) this.map2.remove(key);
		String modifiedString = "xxx-" + originalString + "-xxx";
		this.map2.put(key, modifiedString);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.map1, this.map2);
		this.verifyDiffMatchMismatch(diff, this.map1, this.map2);

		// since the values are strings, the "key" of the value will be changed;
		// and the map differentiator will treat this change as a remove/add
		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);
		for (int i = 0; i < diffs.length; i++) {
			assertTrue(diffs[i].identical());
		}

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(1, removedElements.length);
		Map.Entry entry = (Map.Entry) removedElements[0];
		assertEquals(key, entry.getKey());
		assertEquals(originalString, entry.getValue());

		Object[] addedElements = diff.getAddedElements();
		assertEquals(1, addedElements.length);
		entry = (Map.Entry) addedElements[0];
		assertEquals(key, entry.getKey());
		assertEquals(modifiedString, entry.getValue());
	}

	public void testUnequalKey() {
		String originalKey = "key0";
		Object value = this.map2.remove(originalKey);
		String modifiedKey = "xxx-" + originalKey + "-xxx";
		this.map2.put(modifiedKey, value);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.map1, this.map2);
		this.verifyDiffMatchMismatch(diff, this.map1, this.map2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(1, removedElements.length);
		Map.Entry entry = (Map.Entry) removedElements[0];
		assertEquals(originalKey, entry.getKey());
		assertEquals(value, entry.getValue());

		Object[] addedElements = diff.getAddedElements();
		assertEquals(1, addedElements.length);
		entry = (Map.Entry) addedElements[0];
		assertEquals(modifiedKey, entry.getKey());
		assertEquals(value, entry.getValue());
	}

	public void testOneNull() {
		Object map3 = null;
		Diff diff = this.differentiator.diff(this.map1, map3);
		this.verifyDiffMatchMismatch(diff, this.map1, map3);
	}
	
	public void testNullValue() {
		String key = "key0";
		String originalString = (String) this.map2.remove(key);
		String modifiedString = null;
		
		this.map2.put(key, modifiedString);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.map1, this.map2);
		this.verifyDiffMatchMismatch(diff, this.map1, this.map2);

		// since the values are strings, the "key" of the value will be changed;
		// and the map differentiator will treat this change as a remove/add
		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);
		for (int i = 0; i < diffs.length; i++) {
			assertTrue(diffs[i].identical());
		}

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(1, removedElements.length);
		Map.Entry entry = (Map.Entry) removedElements[0];
		assertEquals(key, entry.getKey());
		assertEquals(originalString, entry.getValue());

		Object[] addedElements = diff.getAddedElements();
		assertEquals(1, addedElements.length);
		entry = (Map.Entry) addedElements[0];
		assertEquals(key, entry.getKey());
		assertEquals(modifiedString, entry.getValue());
	}

	public void testNullKey() {
		String originalKey = "key0";
		Object value = this.map2.remove(originalKey);
		String modifiedKey = null;
		this.map2.put(modifiedKey, value);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.map1, this.map2);
		this.verifyDiffMatchMismatch(diff, this.map1, this.map2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(1, removedElements.length);
		Map.Entry entry = (Map.Entry) removedElements[0];
		assertEquals(originalKey, entry.getKey());
		assertEquals(value, entry.getValue());

		Object[] addedElements = diff.getAddedElements();
		assertEquals(1, addedElements.length);
		entry = (Map.Entry) addedElements[0];
		assertEquals(modifiedKey, entry.getKey());
		assertEquals(value, entry.getValue());
	}

	public void testRemovedEntry() {
		String removedKey = "key0";
		Object removedValue = this.map2.remove(removedKey);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.map1, this.map2);
		this.verifyDiffMatchMismatch(diff, this.map1, this.map2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(1, removedElements.length);
		Map.Entry entry = (Map.Entry) removedElements[0];
		assertEquals(removedKey, entry.getKey());
		assertEquals(removedValue, entry.getValue());

		Object[] addedElements = diff.getAddedElements();
		assertEquals(0, addedElements.length);
	}

	public void testAddedElement() {
		Object addedKey = "key0";
		Object addedValue = this.map1.remove(addedKey);
		ContainerDiff diff = (ContainerDiff) this.differentiator.diff(this.map1, this.map2);
		this.verifyDiffMatchMismatch(diff, this.map1, this.map2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(4, diffs.length);

		Object[] removedElements = diff.getRemovedElements();
		assertEquals(0, removedElements.length);

		Object[] addedElements = diff.getAddedElements();
		assertEquals(1, addedElements.length);
		Map.Entry entry = (Map.Entry) addedElements[0];
		assertEquals(addedKey, entry.getKey());
		assertEquals(addedValue, entry.getValue());
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

}
