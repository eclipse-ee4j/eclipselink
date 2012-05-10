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
package org.eclipse.persistence.tools.workbench.test.utility.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.ReflectiveChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;

public class ReflectiveListChangeListenerTests extends TestCase {
	
	public static Test suite() {
		return new TestSuite(ReflectiveListChangeListenerTests.class);
	}
	
	public ReflectiveListChangeListenerTests(String name) {
		super(name);
	}

	private ListChangeListener buildZeroArgumentListener(Object target) {
		return ReflectiveChangeListener.buildListChangeListener(target, "itemAddedZeroArgument", "itemRemovedZeroArgument", "itemReplacedZeroArgument", "listChangedZeroArgument");
	}

	private ListChangeListener buildSingleArgumentListener(Object target) {
		return ReflectiveChangeListener.buildListChangeListener(target, "itemAddedSingleArgument", "itemRemovedSingleArgument", "itemReplacedSingleArgument", "listChangedSingleArgument");
	}

	public void testItemAddedZeroArgument() {
		TestModel testModel = new TestModel();
		String string = "foo";
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		testModel.addListChangeListener(this.buildZeroArgumentListener(target));
		testModel.addString(string);
		assertTrue(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemAddedZeroArgumentNamedList() {
		TestModel testModel = new TestModel();
		String string = "foo";
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		testModel.addListChangeListener(TestModel.STRINGS_LIST, this.buildZeroArgumentListener(target));
		testModel.addString(string);
		assertTrue(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemAddedSingleArgument() {
		TestModel testModel = new TestModel();
		String string = "foo";
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		testModel.addListChangeListener(this.buildSingleArgumentListener(target));
		testModel.addString(string);
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertTrue(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemAddedSingleArgumentNamedList() {
		TestModel testModel = new TestModel();
		String string = "foo";
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		testModel.addListChangeListener(TestModel.STRINGS_LIST, this.buildSingleArgumentListener(target));
		testModel.addString(string);
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertTrue(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemRemovedZeroArgument() {
		TestModel testModel = new TestModel();
		String string = "foo";
		testModel.addString(string);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		testModel.addListChangeListener(this.buildZeroArgumentListener(target));
		testModel.removeString(string);
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertTrue(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemRemovedZeroArgumentNamedList() {
		TestModel testModel = new TestModel();
		String string = "foo";
		testModel.addString(string);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		testModel.addListChangeListener(TestModel.STRINGS_LIST, this.buildZeroArgumentListener(target));
		testModel.removeString(string);
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertTrue(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemRemovedSingleArgument() {
		TestModel testModel = new TestModel();
		String string = "foo";
		testModel.addString(string);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		testModel.addListChangeListener(this.buildSingleArgumentListener(target));
		testModel.removeString(string);
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertTrue(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemRemovedSingleArgumentNamedList() {
		TestModel testModel = new TestModel();
		String string = "foo";
		testModel.addString(string);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		testModel.addListChangeListener(TestModel.STRINGS_LIST, this.buildSingleArgumentListener(target));
		testModel.removeString(string);
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertTrue(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemReplacedZeroArgument() {
		TestModel testModel = new TestModel();
		String oldString = "foo";
		String newString = "bar";
		testModel.addString(oldString);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, newString, 0, oldString);
		testModel.addListChangeListener(this.buildZeroArgumentListener(target));
		testModel.replaceString(oldString, newString);
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertTrue(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemReplacedZeroArgumentNamedList() {
		TestModel testModel = new TestModel();
		String oldString = "foo";
		String newString = "bar";
		testModel.addString(oldString);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, newString, 0, oldString);
		testModel.addListChangeListener(TestModel.STRINGS_LIST, this.buildZeroArgumentListener(target));
		testModel.replaceString(oldString, newString);
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertTrue(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemReplacedSingleArgument() {
		TestModel testModel = new TestModel();
		String oldString = "foo";
		String newString = "bar";
		testModel.addString(oldString);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, newString, 0, oldString);
		testModel.addListChangeListener(this.buildSingleArgumentListener(target));
		testModel.replaceString(oldString, newString);
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertTrue(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testItemReplacedSingleArgumentNamedList() {
		TestModel testModel = new TestModel();
		String oldString = "foo";
		String newString = "bar";
		testModel.addString(oldString);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, newString, 0, oldString);
		testModel.addListChangeListener(TestModel.STRINGS_LIST, this.buildSingleArgumentListener(target));
		testModel.replaceString(oldString, newString);
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertTrue(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testListChangedZeroArgument() {
		TestModel testModel = new TestModel();
		String string = "foo";
		testModel.addString(string);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, null, -1);
		testModel.addListChangeListener(this.buildZeroArgumentListener(target));
		testModel.replaceAllStrings(new String[] {"bar", "baz"});
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertTrue(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testListChangedZeroArgumentNamedList() {
		TestModel testModel = new TestModel();
		String string = "foo";
		testModel.addString(string);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, null, -1);
		testModel.addListChangeListener(TestModel.STRINGS_LIST, this.buildZeroArgumentListener(target));
		testModel.replaceAllStrings(new String[] {"bar", "baz"});
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertTrue(target.listChangedZeroArgumentFlag);
		assertFalse(target.listChangedSingleArgumentFlag);
	}

	public void testListChangedSingleArgument() {
		TestModel testModel = new TestModel();
		String string = "foo";
		testModel.addString(string);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, null, -1);
		testModel.addListChangeListener(this.buildSingleArgumentListener(target));
		testModel.replaceAllStrings(new String[] {"bar", "baz"});
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertTrue(target.listChangedSingleArgumentFlag);
	}

	public void testListChangedSingleArgumentNamedList() {
		TestModel testModel = new TestModel();
		String string = "foo";
		testModel.addString(string);
		Target target = new Target(testModel, TestModel.STRINGS_LIST, null, -1);
		testModel.addListChangeListener(TestModel.STRINGS_LIST, this.buildSingleArgumentListener(target));
		testModel.replaceAllStrings(new String[] {"bar", "baz"});
		assertFalse(target.itemAddedZeroArgumentFlag);
		assertFalse(target.itemAddedSingleArgumentFlag);
		assertFalse(target.itemRemovedZeroArgumentFlag);
		assertFalse(target.itemRemovedSingleArgumentFlag);
		assertFalse(target.itemReplacedZeroArgumentFlag);
		assertFalse(target.itemReplacedSingleArgumentFlag);
		assertFalse(target.listChangedZeroArgumentFlag);
		assertTrue(target.listChangedSingleArgumentFlag);
	}

	public void testBogusDoubleArgument1() {
		TestModel testModel = new TestModel();
		String string = "foo";
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		boolean exCaught = false;
		try {
			ListChangeListener listener = ReflectiveChangeListener.buildListChangeListener(target, "listChangedDoubleArgument");
			fail("bogus listener: " + listener);
		} catch (RuntimeException ex) {
			if (ex.getCause().getClass() == NoSuchMethodException.class) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	public void testBogusDoubleArgument2() throws Exception {
		TestModel testModel = new TestModel();
		String string = "foo";
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		Method method = ClassTools.method(target, "listChangedDoubleArgument", new Class[] {ListChangeEvent.class, Object.class});
		boolean exCaught = false;
		try {
			ListChangeListener listener = ReflectiveChangeListener.buildListChangeListener(target, method);
			fail("bogus listener: " + listener);
		} catch (RuntimeException ex) {
			if (ex.getMessage().equals(method.toString())) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	public void testListenerMismatch() {
		TestModel testModel = new TestModel();
		String string = "foo";
		Target target = new Target(testModel, TestModel.STRINGS_LIST, string, 0);
		// build a LIST change listener and hack it so we
		// can add it as a COLLECTION change listener
		Object listener = ReflectiveChangeListener.buildListChangeListener(target, "itemAddedSingleArgument");
		testModel.addCollectionChangeListener((CollectionChangeListener) listener);

		boolean exCaught = false;
		try {
			testModel.changeCollection();
			fail("listener mismatch: " + listener);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}


	private class TestModel extends AbstractModel {
		private List strings = new ArrayList();
			public static final String STRINGS_LIST = "strings";
		TestModel() {
			super();
		}
		ListIterator strings() {
			return new CloneListIterator(this.strings);
		}
		void addString(String string) {
			this.addItemToList(string, this.strings, STRINGS_LIST);
		}
		void removeString(String string) {
			this.removeItemFromList(this.strings.indexOf(string), this.strings, STRINGS_LIST);
		}
		void replaceString(String oldString, String newString) {
			this.setItemInList(this.strings.indexOf(oldString), newString, this.strings, STRINGS_LIST);
		}
		void replaceAllStrings(String[] newStrings) {
			this.strings.clear();
			CollectionTools.addAll(this.strings, newStrings);
			this.fireListChanged(STRINGS_LIST);
		}
		void changeCollection() {
			this.fireCollectionChanged("bogus collection");
		}
	}

	private class Target {
		TestModel testModel;
		String listName;
		String string;
		int index;
		String replacedString;
		boolean itemAddedZeroArgumentFlag = false;
		boolean itemAddedSingleArgumentFlag = false;
		boolean itemRemovedZeroArgumentFlag = false;
		boolean itemRemovedSingleArgumentFlag = false;
		boolean itemReplacedZeroArgumentFlag = false;
		boolean itemReplacedSingleArgumentFlag = false;
		boolean listChangedZeroArgumentFlag = false;
		boolean listChangedSingleArgumentFlag = false;
		Target(TestModel testModel, String listName, String string, int index) {
			super();
			this.testModel = testModel;
			this.listName = listName;
			this.string = string;
			this.index = index;
		}
		Target(TestModel testModel, String listName, String string, int index, String replacedString) {
			this(testModel, listName, string, index);
			this.replacedString = replacedString;
		}
		void itemAddedZeroArgument() {
			this.itemAddedZeroArgumentFlag = true;
		}
		void itemAddedSingleArgument(ListChangeEvent e) {
			this.itemAddedSingleArgumentFlag = true;
			assertSame(this.testModel, e.getSource());
			assertEquals(this.listName, e.getListName());
			assertEquals(this.string, e.items().next());
			assertEquals(this.index, e.getIndex());
		}
		void itemRemovedZeroArgument() {
			this.itemRemovedZeroArgumentFlag = true;
		}
		void itemRemovedSingleArgument(ListChangeEvent e) {
			this.itemRemovedSingleArgumentFlag = true;
			assertSame(this.testModel, e.getSource());
			assertEquals(this.listName, e.getListName());
			assertEquals(this.string, e.items().next());
			assertEquals(this.index, e.getIndex());
		}
		void itemReplacedZeroArgument() {
			this.itemReplacedZeroArgumentFlag = true;
		}
		void itemReplacedSingleArgument(ListChangeEvent e) {
			this.itemReplacedSingleArgumentFlag = true;
			assertSame(this.testModel, e.getSource());
			assertEquals(this.listName, e.getListName());
			assertEquals(this.string, e.items().next());
			assertEquals(this.replacedString, e.replacedItems().next());
			assertEquals(this.index, e.getIndex());
		}
		void listChangedZeroArgument() {
			this.listChangedZeroArgumentFlag = true;
		}
		void listChangedSingleArgument(ListChangeEvent e) {
			this.listChangedSingleArgumentFlag = true;
			assertSame(this.testModel, e.getSource());
			assertEquals(this.listName, e.getListName());
			assertFalse(e.items().hasNext());
			assertEquals(this.index, e.getIndex());
		}
		void listChangedDoubleArgument(ListChangeEvent e, Object o) {
			fail("bogus event: " + e);
		}
	}

}
