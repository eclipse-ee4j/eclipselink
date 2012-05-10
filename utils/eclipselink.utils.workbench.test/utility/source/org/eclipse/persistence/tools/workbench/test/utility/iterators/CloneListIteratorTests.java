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
package org.eclipse.persistence.tools.workbench.test.utility.iterators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;


public class CloneListIteratorTests extends TestCase {
	List originalList;

	private boolean concurrentProblem;
	private List concurrentList;

	public static Test suite() {
		return new TestSuite(CloneListIteratorTests.class);
	}
	
	public CloneListIteratorTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.originalList = this.buildList();
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testHasNext() {
		int originalSize = this.originalList.size();
		int i = 0;
		for (ListIterator stream = this.buildCloneListIterator(); stream.hasNext(); ) {
			stream.next();
			// should allow concurrent modification
			this.originalList.add("foo");
			i++;
		}
		assertTrue(originalSize != this.originalList.size());
		assertEquals(originalSize, i);
	}
	
	public void testNext() {
		ListIterator nestedListIterator = this.buildNestedListIterator();
		for (ListIterator stream = this.buildCloneListIterator(); stream.hasNext(); ) {
			assertEquals("bogus element", nestedListIterator.next(), stream.next());
		}
	}
	
	public void testIndex() {
		ListIterator cloneListIterator = this.buildCloneListIterator();
		ListIterator nestedListIterator = this.buildNestedListIterator();
		for (int i = 0; i < 7; i++) {
			nestedListIterator.next();
			cloneListIterator.next();
			assertEquals("bogus index", nestedListIterator.nextIndex(), cloneListIterator.nextIndex());
			assertEquals("bogus index", nestedListIterator.previousIndex(), cloneListIterator.previousIndex());
		}

		for (int i = 0; i < 3; i++) {
			nestedListIterator.previous();
			cloneListIterator.previous();
			assertEquals("bogus index", nestedListIterator.nextIndex(), cloneListIterator.nextIndex());
			assertEquals("bogus index", nestedListIterator.previousIndex(), cloneListIterator.previousIndex());
		}

		while (nestedListIterator.hasNext()) {
			nestedListIterator.next();
			cloneListIterator.next();
			assertEquals("bogus index", nestedListIterator.nextIndex(), cloneListIterator.nextIndex());
			assertEquals("bogus index", nestedListIterator.previousIndex(), cloneListIterator.previousIndex());
		}
	}
	
	public void testHasPrevious() {
		int originalSize = this.originalList.size();
		int i = 0;
		ListIterator stream = this.buildCloneListIterator();
		while (stream.hasNext()) {
			stream.next();
			this.originalList.add("foo");
			i++;
		}
		assertTrue(originalSize != this.originalList.size());
		originalSize = this.originalList.size();
		while (stream.hasPrevious()) {
			stream.previous();
			// should allow concurrent modification
			this.originalList.add("bar");
			i--;
		}
		assertTrue(originalSize != this.originalList.size());
		assertEquals(0, i);
	}
	
	public void testPrevious() {
		ListIterator nestedListIterator = this.buildNestedListIterator();
		ListIterator stream = this.buildCloneListIterator();
		while (stream.hasNext()) {
			nestedListIterator.next();
			stream.next();
		}
		while (stream.hasPrevious()) {
			assertEquals("bogus element", nestedListIterator.previous(), stream.previous());
		}
	}
	
	public void testNoSuchElementException() {
		boolean exCaught = false;
		ListIterator stream = this.buildCloneListIterator();
		String string = null;
		while (stream.hasNext()) {
			string = (String) stream.next();
		}
		try {
			string = (String) stream.next();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown: " + string, exCaught);

		exCaught = false;
		while (stream.hasPrevious()) {
			string = (String) stream.previous();
		}
		try {
			string = (String) stream.previous();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown: " + string, exCaught);
	}
	
	public void testModifyDefault() {
		boolean exCaught = false;
		for (ListIterator stream = this.buildCloneListIterator(); stream.hasNext(); ) {
			if (stream.next().equals("three")) {
				try {
					stream.remove();
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);

		exCaught = false;
		for (ListIterator stream = this.buildCloneListIterator(); stream.hasNext(); ) {
			if (stream.next().equals("three")) {
				try {
					stream.add("three and a half");
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);

		exCaught = false;
		for (ListIterator stream = this.buildCloneListIterator(); stream.hasNext(); ) {
			if (stream.next().equals("three")) {
				try {
					stream.set("another three");
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	public void testModifyMutatorNext() {
		this.verifyModifyNext(new CloneListIterator(this.originalList, this.buildMutator()));
	}
	
	public void testModifyMutatorPrevious() {
		this.verifyModifyPrevious(new CloneListIterator(this.originalList, this.buildMutator()));
	}
	
	private CloneListIterator.Mutator buildMutator() {
		return new CloneListIterator.Mutator() {
			public void add(int index, Object o) {
				CloneListIteratorTests.this.originalList.add(index, o);
			}
			public void remove(int index) {
				CloneListIteratorTests.this.originalList.remove(index);
			}
			public void set(int index, Object o) {
				CloneListIteratorTests.this.originalList.set(index, o);
			}
		};
	}
	
	public void testModifySubclassNext() {
		this.verifyModifyNext(this.buildSubclass());
	}
	
	public void testModifySubclassPrevious() {
		this.verifyModifyPrevious(this.buildSubclass());
	}
	
	private ListIterator buildSubclass() {
		return new CloneListIterator(this.originalList) {
			protected void add(int currentIndex, Object o) {
				CloneListIteratorTests.this.originalList.add(currentIndex, o);
			}
			protected void remove(int currentIndex) {
				CloneListIteratorTests.this.originalList.remove(currentIndex);
			}
			protected void set(int currentIndex, Object o) {
				CloneListIteratorTests.this.originalList.set(currentIndex, o);
			}
		};
	}
	
	private void verifyModifyNext(ListIterator iterator) {
		Object removed = "three";
		Object addedAfter = "five";
		Object added = "five and a half";
		Object replaced = "seven";
		Object replacement = "another seven";
		assertTrue(this.originalList.contains(removed));
		assertTrue(this.originalList.contains(addedAfter));
		assertTrue(this.originalList.contains(replaced));
		// try to remove before calling #next()
		boolean exCaught = false;
		try {
			iterator.remove();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("IllegalStateException not thrown", exCaught);
		while (iterator.hasNext()) {
			Object next = iterator.next();
			if (next.equals(addedAfter)) {
				iterator.add(added);
			}
			if (next.equals(removed)) {
				iterator.remove();
				// try to remove twice
				exCaught = false;
				try {
					iterator.remove();
				} catch (IllegalStateException ex) {
					exCaught = true;
				}
				assertTrue("IllegalStateException not thrown", exCaught);
			}
			if (next.equals(replaced)) {
				iterator.set(replacement);
			}
		}
		assertTrue(this.originalList.contains(added));
		assertFalse(this.originalList.contains(removed));
		assertFalse(this.originalList.contains(replaced));
		assertTrue(this.originalList.contains(replacement));
	}
	
	private void verifyModifyPrevious(ListIterator iterator) {
		Object removed = "three";
		Object addedBefore = "five";
		Object added = "four and a half";
		Object replaced = "seven";
		Object replacement = "another seven";
		assertTrue(this.originalList.contains(removed));
		assertTrue(this.originalList.contains(addedBefore));
		assertTrue(this.originalList.contains(replaced));
		while (iterator.hasNext()) {
			iterator.next();
		}
		while (iterator.hasPrevious()) {
			Object previous = iterator.previous();
			if (previous.equals(addedBefore)) {
				iterator.add(added);
			}
			if (previous.equals(removed)) {
				iterator.remove();
				// try to remove twice
				boolean exCaught = false;
				try {
					iterator.remove();
				} catch (IllegalStateException ex) {
					exCaught = true;
				}
				assertTrue("IllegalStateException not thrown", exCaught);
			}
			if (previous.equals(replaced)) {
				iterator.set(replacement);
			}
		}
		assertTrue(this.originalList.contains(added));
		assertFalse(this.originalList.contains(removed));
		assertFalse(this.originalList.contains(replaced));
		assertTrue(this.originalList.contains(replacement));
	}
	
	private ListIterator buildCloneListIterator() {
		return this.buildCloneListIterator(this.originalList);
	}
	
	private ListIterator buildCloneListIterator(List list) {
		return new CloneListIterator(list);
	}
	
	private ListIterator buildNestedListIterator() {
		return this.originalList.listIterator();
	}
	
	private List buildList() {
		List list = this.buildEmptyList();
		this.populateList(list);
		return list;
	}

	private void populateList(List list) {
		list.add("zero");
		list.add("one");
		list.add("two");
		list.add("three");
		list.add("four");
		list.add("five");
		list.add("six");
		list.add("seven");
		list.add("eight");
		list.add("nine");
	}

	protected List buildEmptyList() {
		return new ArrayList();
	}
	
	/**
	 * Test concurrent access: First build a clone iterator in a
	 * separate thread that hangs momentarily during its construction;
	 * then modify the shared collection in this thread. This would cause
	 * a ConcurrentModificationException in the other thread
	 * if the clone iterator were not synchronized on the original
	 * collection.
	 */
	public void testConcurrentAccess() throws Exception {
		CloneIteratorTests.SlowCollection slow = new CloneIteratorTests.SlowCollection();
		this.populateList(slow);
		// using the unsynchronized list will cause the test to fail
//		this.originalList = slow;
		this.originalList = Collections.synchronizedList(slow);

		this.concurrentProblem = false;
		this.concurrentList = new ArrayList();
		Thread thread = new Thread(this.buildRunnable());
		thread.start();
		while ( ! slow.hasStartedClone()) {
			// wait for the other thread to start the clone...
			Thread.yield();
		}
		// ...then sneak in an extra element
		this.originalList.add("seventeen");
		while (thread.isAlive()) {
			// wait for the other thread to finish
			Thread.yield();
		}
		assertFalse(this.concurrentProblem);
		List expected = new ArrayList();
		this.populateList(expected);
		assertEquals(expected, this.concurrentList);
	}

	private Runnable buildRunnable() {
		return new Runnable() {
			public void run() {
				CloneListIteratorTests.this.loopWithCloneListIterator();
			}
		};
	}

	/**
	 * use a clone iterator to loop over the "slow" collection
	 * and copy its contents to the concurrent collection
	 */
	void loopWithCloneListIterator() {
		try {
			for (ListIterator stream = this.buildCloneListIterator(); stream.hasNext(); ) {
				this.concurrentList.add(stream.next());
			}
		} catch (Throwable t) {
			this.concurrentProblem = true;
		}
	}

}
