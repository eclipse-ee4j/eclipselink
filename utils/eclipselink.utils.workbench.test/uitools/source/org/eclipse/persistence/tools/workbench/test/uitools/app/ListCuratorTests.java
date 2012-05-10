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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.ListCurator;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyListIterator;


public final class ListCuratorTests 
	extends TestCase 
{
	private TestSubject subject1;
	private PropertyValueModel subjectHolder1;
	
	private ListCurator curator;
	private ListChangeListener listener1;
	private ListChangeEvent event1;
	
	private TestSubject subject2;

	public static Test suite() {
		return new TestSuite(ListCuratorTests.class);
	}
	
	public ListCuratorTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.subject1 = new TestSubject(this.subject1Names());
		this.subjectHolder1 = new SimplePropertyValueModel(this.subject1);
		this.curator = this.buildListCurator(this.subjectHolder1);
		this.listener1 = this.buildListChangeListener1();
		this.curator.addListChangeListener(ValueModel.VALUE, this.listener1);
		this.event1 = null;
		
		this.subject2 = new TestSubject(this.subject2Names());
	}
	
	private List subject1Names() {
		ArrayList list = new ArrayList();
		list.add("alpha");
		list.add("bravo");
		list.add("charlie");
		list.add("delta");
		return list;
	}
	
	private List subject2Names() {
		ArrayList list = new ArrayList();
		list.add("echo");
		list.add("foxtrot");
		list.add("glove");
		list.add("hotel");
		return list;
	}
	
	private ListCurator buildListCurator(ValueModel subjectHolder) {
		return new ListCurator(subjectHolder) {
			public Iterator getValueForRecord() {
				return ((TestSubject) this.subject).strings();
			}
		};
	}

	private ListChangeListener buildListChangeListener1() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				ListCuratorTests.this.value1Changed(e);
			}
			
			public void itemsRemoved(ListChangeEvent e) {
				ListCuratorTests.this.value1Changed(e);
			}
			
			public void itemsReplaced(ListChangeEvent e) {
				ListCuratorTests.this.value1Changed(e);
			}
			
			public void listChanged(ListChangeEvent e) {
				ListCuratorTests.this.value1Changed(e);
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
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.curator.getValue()));
		assertNull(this.event1);

		this.subjectHolder1.setValue(this.subject2);
		assertNotNull(this.event1);
		assertEquals(this.curator, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(-1, this.event1.getIndex());
		assertFalse(this.event1.items().hasNext());
		assertEquals(this.subject2Names(), CollectionTools.list((ListIterator) this.curator.getValue()));
		
		this.event1 = null;
		this.subjectHolder1.setValue(null);
		assertNotNull(this.event1);
		assertEquals(this.curator, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(-1, this.event1.getIndex());
		assertFalse(this.event1.items().hasNext());
		assertFalse(((Iterator) this.curator.getValue()).hasNext());
		
		this.event1 = null;
		this.subjectHolder1.setValue(this.subject1);
		assertNotNull(this.event1);
		assertEquals(this.curator, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(-1, this.event1.getIndex());
		assertFalse(this.event1.items().hasNext());
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.curator.getValue()));
	}

	public void testAddItem() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.curator.getValue()));
		assertNull(this.event1);

		this.subject1.addString("echo");
		assertNotNull(this.event1);
		assertEquals(this.curator, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(this.subject1Names().size(), this.event1.getIndex());
		assertEquals("echo", this.event1.items().next());
		List stringsPlus = this.subject1Names();
		stringsPlus.add("echo");
		assertEquals(stringsPlus, CollectionTools.list((ListIterator) this.curator.getValue()));

		this.event1 = null;
		this.subject1.addString(0, "zulu");
		assertNotNull(this.event1);
		assertEquals(this.curator, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(0, this.event1.getIndex());
		assertEquals("zulu", this.event1.items().next());
		stringsPlus.add(0, "zulu");
		assertEquals(stringsPlus, CollectionTools.list((ListIterator) this.curator.getValue()));
	}
	
	public void testRemoveItem() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.curator.getValue()));
		assertNull(this.event1);

		String removedString = this.subject1.removeString(0);	// should be "alpha"
		assertNotNull(this.event1);
		assertEquals(this.curator, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(0, this.event1.getIndex());
		assertEquals(removedString, this.event1.items().next());
		List stringsMinus = this.subject1Names();
		stringsMinus.remove(0);
		assertEquals(stringsMinus, CollectionTools.list((ListIterator) this.curator.getValue()));
		
		removedString = this.subject1.removeString(2);	// should be "delta"
		assertNotNull(this.event1);
		assertEquals(this.curator, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(2, this.event1.getIndex());
		assertEquals(removedString, this.event1.items().next());
		stringsMinus.remove(2);
		assertEquals(stringsMinus, CollectionTools.list((ListIterator) this.curator.getValue()));
	}
	
	public void testCompleteListChange() {
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.curator.getValue()));
		assertNull(this.event1);
		
		this.subject1.setStrings(this.subject2Names());
		assertNotNull(this.event1);
		assertEquals(this.curator, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		List newStrings = this.subject2Names();
		assertEquals(newStrings, CollectionTools.list((ListIterator) this.curator.getValue()));
	}
	
	public void testPartialListChange() {
		List startingList = CollectionTools.list((ListIterator) this.curator.getValue());
		assertEquals(this.subject1Names(), startingList);
		assertNull(this.event1);
		
		String identicalString = (String) startingList.get(1);  // should be "bravo"
		String nonidenticalString = (String) startingList.get(0); // should be "alpha"
		List newStrings = CollectionTools.list(new String[] {new String("bravo"), new String("alpha"), "echo", "delta", "foxtrot"});
		this.subject1.setStrings(newStrings);
		
		List finalList = CollectionTools.list((ListIterator) this.curator.getValue());
		assertNotNull(this.event1);
		assertEquals(this.curator, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getListName());
		assertEquals(newStrings, finalList);
		assertTrue(identicalString == finalList.get(0));
		assertTrue(nonidenticalString != finalList.get(1));
	}
	
	public void testGetValue() {
		assertEquals(this.subject1Names(), CollectionTools.list(this.subject1.strings()));
		assertEquals(this.subject1Names(), CollectionTools.list((ListIterator) this.curator.getValue()));
	}
	
	public void testGetItem() {
		assertEquals(this.subject1Names().get(0), this.subject1.getString(0));
		assertEquals(this.subject1Names().get(0), this.curator.getItem(0));
	}
	
	public void testSize() {
		assertEquals(this.subject1Names().size(), CollectionTools.size(this.subject1.strings()));
		assertEquals(this.subject1Names().size(), CollectionTools.size((ListIterator) this.curator.getValue()));
		assertEquals(this.subject1Names().size(), this.curator.size());
	}
	
	public void testHasListeners() {
		assertTrue(this.curator.hasAnyListChangeListeners(ValueModel.VALUE));
		assertTrue(this.subject1.hasAnyStateChangeListeners());
		this.curator.removeListChangeListener(ValueModel.VALUE, this.listener1);
		assertFalse(this.subject1.hasAnyStateChangeListeners());
		assertFalse(this.curator.hasAnyListChangeListeners(ValueModel.VALUE));

		ListChangeListener listener2 = this.buildListChangeListener1();
		this.curator.addListChangeListener(listener2);
		assertTrue(this.curator.hasAnyListChangeListeners(ValueModel.VALUE));
		assertTrue(this.subject1.hasAnyStateChangeListeners());
		this.curator.removeListChangeListener(listener2);
		assertFalse(this.subject1.hasAnyStateChangeListeners());
		assertFalse(this.curator.hasAnyListChangeListeners(ValueModel.VALUE));
	}
	
	
	// **************** Inner Class *******************************************
	
	private class TestSubject 
		extends AbstractModel
	{
		private List strings;
		
		public TestSubject() {
			this.strings = new ArrayList();
		}
		
		public TestSubject(List strings) {
			this();
			this.setStrings(strings);
		}
		
		public String getString(int index) {
			return (String) this.strings.get(index);
		}
		
		public ListIterator strings() {
			return new ReadOnlyListIterator(this.strings);
		}
		
		public void addString(int index, String string) {
			this.strings.add(index, string);
			this.fireStateChanged();
		}
		
		public void addString(String string) {
			this.addString(this.strings.size(), string);
		}
		
		public String removeString(int index) {
			String string = (String) this.strings.get(index);
			this.removeString(string);
			return string;
		}
		
		public void removeString(String string) {
			this.strings.remove(string);
			this.fireStateChanged();
		}
		
		public void setStrings(List strings) {
			this.strings = new ArrayList(strings);
			this.fireStateChanged();
		}
		
		public void setStrings(String[] strings) {
			this.strings = CollectionTools.list(strings);
			this.fireStateChanged();
		}
	}
}
