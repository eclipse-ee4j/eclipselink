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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyIterator;


public class CollectionAspectAdapterTests extends TestCase {
	private TestSubject subject1;
	private PropertyValueModel subjectHolder1;
	private CollectionAspectAdapter aa1;
	private CollectionChangeEvent event1;
	private CollectionChangeListener listener1;
	private String event1Type;

	private static final String ADD = "add";
	private static final String REMOVE = "remove";
	private static final String CHANGE = "change";

	private TestSubject subject2;

	public static Test suite() {
		return new TestSuite(CollectionAspectAdapterTests.class);
	}
	
	public CollectionAspectAdapterTests(String name) {
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
		this.aa1.addCollectionChangeListener(ValueModel.VALUE, this.listener1);
		this.event1 = null;
		this.event1Type = null;

		this.subject2 = new TestSubject();
		this.subject2.addNames(this.subject2Names());
		this.subject2.addDescriptions(this.subject2Descriptions());
	}

	private Collection subject1Names() {
		Collection result = new HashBag();
		result.add("foo");
		result.add("bar");
		return result;
	}

	private Collection subject1Descriptions() {
		Collection result = new HashBag();
		result.add("this.subject1 description1");
		result.add("this.subject1 description2");
		return result;
	}

	private Collection subject2Names() {
		Collection result = new HashBag();
		result.add("baz");
		result.add("bam");
		return result;
	}

	private Collection subject2Descriptions() {
		Collection result = new HashBag();
		result.add("this.subject2 description1");
		result.add("this.subject2 description2");
		return result;
	}

	private CollectionAspectAdapter buildAspectAdapter(ValueModel subjectHolder) {
		return new CollectionAspectAdapter(subjectHolder, TestSubject.NAMES_COLLECTION) {
			// this is not a typical aspect adapter - the value is determined by the aspect name
			protected Iterator getValueFromSubject() {
				if (this.collectionName == TestSubject.NAMES_COLLECTION) {
					return ((TestSubject) this.subject).names();
				} else if (this.collectionName == TestSubject.DESCRIPTIONS_COLLECTION) {
					return ((TestSubject) this.subject).descriptions();
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.collectionName);
				}
			}
			public void addItem(Object item) {
				if (this.collectionName == TestSubject.NAMES_COLLECTION) {
					((TestSubject) this.subject).addName((String) item);
				} else if (this.collectionName == TestSubject.DESCRIPTIONS_COLLECTION) {
					((TestSubject) this.subject).addDescription((String) item);
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.collectionName);
				}
			}
			public void removeItem(Object item) {
				if (this.collectionName == TestSubject.NAMES_COLLECTION) {
					((TestSubject) this.subject).removeName((String) item);
				} else if (this.collectionName == TestSubject.DESCRIPTIONS_COLLECTION) {
					((TestSubject) this.subject).removeDescription((String) item);
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.collectionName);
				}
			}
		};
	}

	private CollectionChangeListener buildValueChangeListener1() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				CollectionAspectAdapterTests.this.value1Changed(e, ADD);
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				CollectionAspectAdapterTests.this.value1Changed(e, REMOVE);
			}
			public void collectionChanged(CollectionChangeEvent e) {
				CollectionAspectAdapterTests.this.value1Changed(e, CHANGE);
			}
		};
	}

	void value1Changed(CollectionChangeEvent e, String eventType) {
		this.event1 = e;
		this.event1Type = eventType;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSubjectHolder() {
		assertEquals(this.subject1Names(), CollectionTools.bag((Iterator) this.aa1.getValue()));
		assertNull(this.event1);

		this.subjectHolder1.setValue(this.subject2);
		assertNotNull(this.event1);
		assertEquals(this.event1Type, CHANGE);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getCollectionName());
		assertFalse(this.event1.items().hasNext());
		assertEquals(this.subject2Names(), CollectionTools.bag((Iterator) this.aa1.getValue()));
		
		this.event1 = null;
		this.event1Type = null;
		this.subjectHolder1.setValue(null);
		assertNotNull(this.event1);
		assertEquals(this.event1Type, CHANGE);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getCollectionName());
		assertFalse(this.event1.items().hasNext());
		assertFalse(((Iterator) this.aa1.getValue()).hasNext());
		
		this.event1 = null;
		this.event1Type = null;
		this.subjectHolder1.setValue(this.subject1);
		assertNotNull(this.event1);
		assertEquals(this.event1Type, CHANGE);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getCollectionName());
		assertFalse(this.event1.items().hasNext());
		assertEquals(this.subject1Names(), CollectionTools.bag((Iterator) this.aa1.getValue()));
	}

	public void testAddItem() {
		assertEquals(this.subject1Names(), CollectionTools.bag((Iterator) this.aa1.getValue()));
		assertNull(this.event1);

		this.subject1.addName("jam");
		assertNotNull(this.event1);
		assertEquals(this.event1Type, ADD);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getCollectionName());
		assertEquals("jam", this.event1.items().next());
		Collection namesPlus = this.subject1Names();
		namesPlus.add("jam");
		assertEquals(namesPlus, CollectionTools.bag((Iterator) this.aa1.getValue()));

		this.event1 = null;
		this.event1Type = null;
		this.aa1.addItems(Collections.singleton("jaz"));
		assertNotNull(this.event1);
		assertEquals(this.event1Type, ADD);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getCollectionName());
		assertEquals("jaz", this.event1.items().next());
		namesPlus.add("jaz");
		assertEquals(namesPlus, CollectionTools.bag((Iterator) this.aa1.getValue()));
	}

	public void testRemoveItem() {
		assertEquals(this.subject1Names(), CollectionTools.bag((Iterator) this.aa1.getValue()));
		assertNull(this.event1);

		this.subject1.removeName("foo");
		assertNotNull(this.event1);
		assertEquals(this.event1Type, REMOVE);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getCollectionName());
		assertEquals("foo", this.event1.items().next());
		Collection namesMinus = this.subject1Names();
		namesMinus.remove("foo");
		assertEquals(namesMinus, CollectionTools.bag((Iterator) this.aa1.getValue()));

		this.event1 = null;
		this.event1Type = null;
		this.aa1.removeItems(Collections.singleton("bar"));
		assertNotNull(this.event1);
		assertEquals(this.event1Type, REMOVE);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getCollectionName());
		assertEquals("bar", this.event1.items().next());
		namesMinus.remove("bar");
		assertEquals(namesMinus, CollectionTools.bag((Iterator) this.aa1.getValue()));
	}

	public void testCollectionChange() {
		assertEquals(this.subject1Names(), CollectionTools.bag((Iterator) this.aa1.getValue()));
		assertNull(this.event1);

		this.subject1.addTwoNames("jam", "jaz");
		assertNotNull(this.event1);
		assertEquals(this.event1Type, CHANGE);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getCollectionName());
		assertFalse(this.event1.items().hasNext());
		Collection namesPlus2 = this.subject1Names();
		namesPlus2.add("jam");
		namesPlus2.add("jaz");
		assertEquals(namesPlus2, CollectionTools.bag((Iterator) this.aa1.getValue()));
	}

	public void testGetValue() {
		assertEquals(this.subject1Names(), CollectionTools.bag(this.subject1.names()));
		assertEquals(this.subject1Names(), CollectionTools.bag((Iterator) this.aa1.getValue()));
	}

	public void testSize() {
		assertEquals(this.subject1Names().size(), CollectionTools.size(this.subject1.names()));
		assertEquals(this.subject1Names().size(), CollectionTools.size((Iterator) this.aa1.getValue()));
	}

	public void testHasListeners() {
		assertTrue(this.aa1.hasAnyCollectionChangeListeners(ValueModel.VALUE));
		assertTrue(this.subject1.hasAnyCollectionChangeListeners(TestSubject.NAMES_COLLECTION));
		this.aa1.removeCollectionChangeListener(ValueModel.VALUE, this.listener1);
		assertFalse(this.subject1.hasAnyCollectionChangeListeners(TestSubject.NAMES_COLLECTION));
		assertFalse(this.aa1.hasAnyCollectionChangeListeners(ValueModel.VALUE));

		CollectionChangeListener listener2 = this.buildValueChangeListener1();
		this.aa1.addCollectionChangeListener(listener2);
		assertTrue(this.aa1.hasAnyCollectionChangeListeners(ValueModel.VALUE));
		assertTrue(this.subject1.hasAnyCollectionChangeListeners(TestSubject.NAMES_COLLECTION));
		this.aa1.removeCollectionChangeListener(listener2);
		assertFalse(this.subject1.hasAnyCollectionChangeListeners(TestSubject.NAMES_COLLECTION));
		assertFalse(this.aa1.hasAnyCollectionChangeListeners(ValueModel.VALUE));
	}

// ********** inner class **********

private class TestSubject extends AbstractModel {
	private Collection names;
	public static final String NAMES_COLLECTION = "names";
	private Collection descriptions;
	public static final String DESCRIPTIONS_COLLECTION = "descriptions";

	public TestSubject() {
		this.names = new HashBag();
		this.descriptions = new HashBag();
	}
	public Iterator names() {
		return new ReadOnlyIterator(this.names);
	}
	public void addName(String name) {
		if (this.names.add(name)) {
			this.fireItemAdded(NAMES_COLLECTION, name);
		}
	}
	public void addNames(Iterator newNames) {
		while (newNames.hasNext()) {
			this.addName((String) newNames.next());
		}
	}
	public void addNames(Collection newNames) {
		this.addNames(newNames.iterator());
	}
	public void addTwoNames(String name1, String name2) {
		if (this.names.add(name1) | this.names.add(name2)) {
			this.fireCollectionChanged(NAMES_COLLECTION);
		}
	}
	public void removeName(String name) {
		if (this.names.remove(name)) {
			this.fireItemRemoved(NAMES_COLLECTION, name);
		}
	}
	public Iterator descriptions() {
		return new ReadOnlyIterator(this.descriptions);
	}
	public void addDescription(String description) {
		if (this.descriptions.add(description)) {
			this.fireItemAdded(DESCRIPTIONS_COLLECTION, description);
		}
	}
	public void addDescriptions(Iterator newDescriptions) {
		while (newDescriptions.hasNext()) {
			this.addDescription((String) newDescriptions.next());
		}
	}
	public void addDescriptions(Collection newDescriptions) {
		this.addDescriptions(newDescriptions.iterator());
	}
	public void removeDescription(String description) {
		if (this.descriptions.remove(description)) {
			this.fireItemRemoved(DESCRIPTIONS_COLLECTION, description);
		}
	}
}

}
