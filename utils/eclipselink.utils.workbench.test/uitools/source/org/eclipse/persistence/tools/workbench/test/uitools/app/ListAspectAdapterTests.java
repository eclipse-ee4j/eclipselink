/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyListIterator;


public class ListAspectAdapterTests extends TestCase {
	private TestSubject subject1;
	private PropertyValueModel subjectHolder1;
	private ListAspectAdapter aa1;
	private ListChangeEvent event1;
	private ListChangeListener listener1;

	private TestSubject subject2;

	public static Test suite() {
		return new TestSuite(ListAspectAdapterTests.class);
	}
	
	public ListAspectAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.subject1 = new TestSubject();
		this.subject1.addNames(this.subject1Names());
		this.subject1.addDescriptions(this.subject1Descriptions());
		this.subjectHolder1 = new SimplePropertyValueModel(this.subject1);
		this.aa1 = this.buildAspectAdapter(this.subjectHolder1);
		this.listener1 = this.buildValueChangeListener1();
		this.aa1.addListChangeListener(ValueModel.VALUE, this.listener1);
		this.event1 = null;

		this.subject2 = new TestSubject();
		this.subject2.addNames(this.subject2Names());
		this.subject2.addDescriptions(this.subject2Descriptions());
	}

	private List subject1Names() {
		List result = new ArrayList();
		result.add("foo");
		result.add("bar");
		result.add("baz");
		result.add("bam");
		return result;
	}

	private List subject1Descriptions() {
		List result = new ArrayList();
		result.add("this.subject1 description1");
		result.add("this.subject1 description2");
		return result;
	}

	private List subject2Names() {
		List result = new ArrayList();
		result.add("joo");
		result.add("jar");
		result.add("jaz");
		result.add("jam");
		return result;
	}

	private List subject2Descriptions() {
		List result = new ArrayList();
		result.add("this.subject2 description1");
		result.add("this.subject2 description2");
		return result;
	}

	private ListAspectAdapter buildAspectAdapter(ValueModel subjectHolder) {
		return new ListAspectAdapter(subjectHolder, TestSubject.NAMES_LIST) {
			// this is not a typical aspect adapter - the value is determined by the aspect name
			protected ListIterator getValueFromSubject() {
				if (this.listName == TestSubject.NAMES_LIST) {
					return ((TestSubject) this.subject).names();
				} else if (this.listName == TestSubject.DESCRIPTIONS_LIST) {
					return ((TestSubject) this.subject).descriptions();
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.listName);
				}
			}
			public void addItem(int index, Object item) {
				if (this.listName == TestSubject.NAMES_LIST) {
					((TestSubject) this.subject).addName(index, (String) item);
				} else if (this.listName == TestSubject.DESCRIPTIONS_LIST) {
					((TestSubject) this.subject).addDescription(index, (String) item);
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.listName);
				}
			}
			public Object removeItem(int index) {
				if (this.listName == TestSubject.NAMES_LIST) {
					return ((TestSubject) this.subject).removeName(index);
				} else if (this.listName == TestSubject.DESCRIPTIONS_LIST) {
					return ((TestSubject) this.subject).removeDescription(index);
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.listName);
				}
			}
			public Object replaceItem(int index, Object item) {
				if (this.listName == TestSubject.NAMES_LIST) {
					return ((TestSubject) this.subject).setName(index, (String) item);
				} else if (this.listName == TestSubject.DESCRIPTIONS_LIST) {
					return ((TestSubject) this.subject).setDescription(index, (String) item);
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.listName);
				}
			}
		};
	}

	private ListChangeListener buildValueChangeListener1() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				ListAspectAdapterTests.this.value1Changed(e);
			}
			public void itemsRemoved(ListChangeEvent e) {
				ListAspectAdapterTests.this.value1Changed(e);
			}
			public void itemsReplaced(ListChangeEvent e) {
				ListAspectAdapterTests.this.value1Changed(e);
			}
			public void listChanged(ListChangeEvent e) {
				ListAspectAdapterTests.this.value1Changed(e);
			}
		};
	}

	void value1Changed(ListChangeEvent e) {
		this.event1 = e;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSubjectHolder() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
		assertNull(this.event1);

		this.subjectHolder1.setValue(this.subject2);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(-1, this.event1.getIndex());
		assertFalse(this.event1.items().hasNext());
		assertEquals(this.subject2Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
		
		this.event1 = null;
		this.subjectHolder1.setValue(null);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(-1, this.event1.getIndex());
		assertFalse(this.event1.items().hasNext());
		assertFalse(((Iterator) this.aa1.getValue()).hasNext());
		
		this.event1 = null;
		this.subjectHolder1.setValue(this.subject1);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(-1, this.event1.getIndex());
		assertFalse(this.event1.items().hasNext());
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
	}

	public void testAddItem() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
		assertNull(this.event1);

		this.subject1.addName("jam");
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(this.subject1Names().size(), this.event1.getIndex());
		assertEquals("jam", this.event1.items().next());
		List namesPlus = this.subject1Names();
		namesPlus.add("jam");
		assertEquals(namesPlus, CollectionTools.list((ListIterator) this.aa1.getValue()));

		this.event1 = null;
		this.aa1.addItem(2, "jaz");
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(2, this.event1.getIndex());
		assertEquals("jaz", this.event1.items().next());
		namesPlus.add(2, "jaz");
		assertEquals(namesPlus, CollectionTools.list((ListIterator) this.aa1.getValue()));
	}

	public void testDefaultAddItems() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
		assertNull(this.event1);

		List items = new ArrayList();
		items.add("joo");
		items.add("jar");
		items.add("jaz");
		items.add("jam");

		this.event1 = null;
		this.aa1.addItems(2, items);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(5, this.event1.getIndex());		// only the last "add" event will still be there
		assertEquals("jam", this.event1.items().next());
		List namesPlus = this.subject1Names();
		namesPlus.addAll(2, items);
		assertEquals(namesPlus, CollectionTools.list((ListIterator) this.aa1.getValue()));
	}

	public void testRemoveItem() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
		assertNull(this.event1);

		String removedName = this.subject1.removeName(0);	// should be "foo"
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(0, this.event1.getIndex());
		assertEquals(removedName, this.event1.items().next());
		List namesMinus = this.subject1Names();
		namesMinus.remove(0);
		assertEquals(namesMinus, CollectionTools.list((ListIterator) this.aa1.getValue()));

		this.event1 = null;
		Object removedItem = this.aa1.removeItem(0);	
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(0, this.event1.getIndex());
		assertEquals(removedItem, this.event1.items().next());
		namesMinus.remove(0);
		assertEquals(namesMinus, CollectionTools.list((ListIterator) this.aa1.getValue()));
	}

	public void testDefaultRemoveItems() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
		assertNull(this.event1);

		List items = new ArrayList();
		items.add("bar");
		items.add("baz");

		this.event1 = null;
		this.aa1.removeItems(1, 2);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(1, this.event1.getIndex());		// only the last "remove" event will still be there
		assertEquals("baz", this.event1.items().next());
		List namesPlus = this.subject1Names();
		namesPlus.remove(1);
		namesPlus.remove(1);
		assertEquals(namesPlus, CollectionTools.list((ListIterator) this.aa1.getValue()));
	}

	public void testReplaceItem() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
		assertNull(this.event1);

		String replacedName = this.subject1.setName(0, "jelly");	// should be "foo"
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(0, this.event1.getIndex());
		assertEquals("jelly", this.event1.items().next());
		assertEquals(replacedName, this.event1.replacedItems().next());
		List namesChanged = this.subject1Names();
		namesChanged.set(0, "jelly");
		assertEquals(namesChanged, CollectionTools.list((ListIterator) this.aa1.getValue()));

		this.event1 = null;
		replacedName = this.subject1.setName(1, "roll");	// should be "bar"
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(1, this.event1.getIndex());
		assertEquals("roll", this.event1.items().next());
		assertEquals(replacedName, this.event1.replacedItems().next());
		namesChanged = this.subject1Names();
		namesChanged.set(0, "jelly");
		namesChanged.set(1, "roll");
		assertEquals(namesChanged, CollectionTools.list((ListIterator) this.aa1.getValue()));
	}

	public void testDefaultReplaceItems() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
		assertNull(this.event1);

		List items = new ArrayList();
		items.add("jar");
		items.add("jaz");

		this.event1 = null;
		this.aa1.replaceItems(1, items);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(2, this.event1.getIndex());		// only the last "replace" event will still be there
		assertEquals("baz", this.event1.replacedItems().next());
		assertEquals("jaz", this.event1.items().next());
		List namesPlus = this.subject1Names();
		namesPlus.set(1, items.get(0));
		namesPlus.set(2, items.get(1));
		assertEquals(namesPlus, CollectionTools.list((ListIterator) this.aa1.getValue()));
	}

	public void testListChange() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
		assertNull(this.event1);

		this.subject1.addTwoNames("jam", "jaz");
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(-1, this.event1.getIndex());
		assertFalse(this.event1.items().hasNext());
		List namesPlus2 = this.subject1Names();
		namesPlus2.add(0, "jaz");
		namesPlus2.add(0, "jam");
		assertEquals(namesPlus2, CollectionTools.list((ListIterator) this.aa1.getValue()));
	}

	public void testGetValue() {
		assertEquals(this.subject1Names(), CollectionTools.list(this.subject1.names()));
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.aa1.getValue()));
	}

	public void testGetItem() {
		assertEquals(this.subject1Names().get(0), this.subject1.getName(0));
		assertEquals(this.subject1Names().get(0), this.aa1.getItem(0));
	}

	public void testSize() {
		assertEquals(this.subject1Names().size(), CollectionTools.size(this.subject1.names()));
		assertEquals(this.subject1Names().size(), CollectionTools.size((ListIterator) this.aa1.getValue()));
	}

	public void testHasListeners() {
		assertTrue(this.aa1.hasAnyListChangeListeners(ValueModel.VALUE));
		assertTrue(this.subject1.hasAnyListChangeListeners(TestSubject.NAMES_LIST));
		this.aa1.removeListChangeListener(ValueModel.VALUE, this.listener1);
		assertFalse(this.subject1.hasAnyListChangeListeners(TestSubject.NAMES_LIST));
		assertFalse(this.aa1.hasAnyListChangeListeners(ValueModel.VALUE));

		ListChangeListener listener2 = this.buildValueChangeListener1();
		this.aa1.addListChangeListener(listener2);
		assertTrue(this.aa1.hasAnyListChangeListeners(ValueModel.VALUE));
		assertTrue(this.subject1.hasAnyListChangeListeners(TestSubject.NAMES_LIST));
		this.aa1.removeListChangeListener(listener2);
		assertFalse(this.subject1.hasAnyListChangeListeners(TestSubject.NAMES_LIST));
		assertFalse(this.aa1.hasAnyListChangeListeners(ValueModel.VALUE));
	}

// ********** inner class **********

private class TestSubject extends AbstractModel {
	private List names;
	public static final String NAMES_LIST = "names";
	private List descriptions;
	public static final String DESCRIPTIONS_LIST = "descriptions";

	public TestSubject() {
		this.names = new ArrayList();
		this.descriptions = new ArrayList();
	}
	public ListIterator names() {
		return new ReadOnlyListIterator(this.names);
	}
	public String getName(int index) {
		return (String) this.names.get(index);
	}
	public void addName(int index, String name) {
		this.names.add(index, name);
		this.fireItemAdded(NAMES_LIST, index, name);
	}
	public void addName(String name) {
		this.addName(this.names.size(), name);
	}
	public void addNames(ListIterator newNames) {
		while (newNames.hasNext()) {
			this.addName((String) newNames.next());
		}
	}
	public void addNames(List newNames) {
		this.addNames(newNames.listIterator());
	}
	public void addTwoNames(String name1, String name2) {
		this.names.add(0, name2);
		this.names.add(0, name1);
		this.fireListChanged(NAMES_LIST);
	}
	public String removeName(int index) {
		String removedName = (String) this.names.remove(index);
		this.fireItemRemoved(NAMES_LIST, index, removedName);
		return removedName;
	}
	public String setName(int index, String name) {
		String replacedName = (String) this.names.set(index, name);
		this.fireItemReplaced(NAMES_LIST, index, name, replacedName);
		return replacedName;
	}
	public ListIterator descriptions() {
		return new ReadOnlyListIterator(this.descriptions);
	}
	public String getDescription(int index) {
		return (String) this.descriptions.get(index);
	}
	public void addDescription(int index, String description) {
		this.descriptions.add(index, description);
		this.fireItemAdded(DESCRIPTIONS_LIST, index, description);
	}
	public void addDescription(String description) {
		this.addDescription(this.descriptions.size(), description);
	}
	public void addDescriptions(ListIterator newDescriptions) {
		while (newDescriptions.hasNext()) {
			this.addDescription((String) newDescriptions.next());
		}
	}
	public void addDescriptions(List newDescriptions) {
		this.addDescriptions(newDescriptions.listIterator());
	}
	public String removeDescription(int index) {
		String removedDescription = (String) this.descriptions.remove(index);
		this.fireItemRemoved(DESCRIPTIONS_LIST, index, removedDescription);
		return removedDescription;
	}
	public String setDescription(int index, String description) {
		String replacedDescription = (String) this.descriptions.set(index, description);
		this.fireItemReplaced(DESCRIPTIONS_LIST, index, description, replacedDescription);
		return replacedDescription;
	}
}

}
