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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.Icon;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.HashBag;


public class ItemPropertyListValueModelAdapterTests extends TestCase {
	private Junk foo;
	private Junk bar;
	private Junk baz;
	private Junk joo;
	private Junk jar;
	private Junk jaz;

	private Junk tom;
	private Junk dick;
	private Junk harry;

	public static Test suite() {
		return new TestSuite(ItemPropertyListValueModelAdapterTests.class);
	}
	
	public ItemPropertyListValueModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.foo = new Junk("this.foo");
		this.bar = new Junk("this.bar");
		this.baz = new Junk("this.baz");
		this.joo = new Junk("this.joo");
		this.jar = new Junk("this.jar");
		this.jaz = new Junk("this.jaz");

		this.tom = new Junk("this.tom");
		this.dick = new Junk("this.dick");
		this.harry = new Junk("this.harry");
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testCollectionSynchronization() {
		SimpleCollectionValueModel collectionHolder = this.buildCollectionHolder();
		ListValueModel listValueModel = new ItemPropertyListValueModelAdapter(collectionHolder, Displayable.DISPLAY_STRING_PROPERTY, Displayable.ICON_PROPERTY);
		SynchronizedList synchList = new SynchronizedList(listValueModel);
		assertEquals(6, synchList.size());
		this.compare(listValueModel, synchList);

		collectionHolder.addItem(this.tom);
		collectionHolder.addItem(this.dick);
		collectionHolder.addItem(this.harry);
		assertEquals(9, synchList.size());
		this.compare(listValueModel, synchList);

		collectionHolder.removeItem(this.foo);
		collectionHolder.removeItem(this.jar);
		collectionHolder.removeItem(this.harry);
		assertEquals(6, synchList.size());
		this.compare(listValueModel, synchList);

		collectionHolder.setValue(this.buildCollection());
		assertEquals(6, synchList.size());
		this.compare(listValueModel, synchList);
	}

	public void testListSynchronization() {
		SimpleListValueModel listHolder = this.buildListHolder();
		ListValueModel listValueModel = new ItemPropertyListValueModelAdapter(listHolder, Displayable.DISPLAY_STRING_PROPERTY, Displayable.ICON_PROPERTY);
		SynchronizedList synchList = new SynchronizedList(listValueModel);
		assertEquals(6, synchList.size());
		this.compare(listValueModel, synchList);

		listHolder.addItem(6, this.tom);
		listHolder.addItem(7, this.dick);
		listHolder.addItem(8, this.harry);
		assertEquals(9, synchList.size());
		this.compare(listValueModel, synchList);

		listHolder.removeItem(8);
		listHolder.removeItem(0);
		listHolder.removeItem(4);
		assertEquals(6, synchList.size());
		this.compare(listValueModel, synchList);

		// test concurrent modification exception
		listHolder.setValue(this.buildList());
		assertEquals(6, synchList.size());
		this.compare(listValueModel, synchList);
	}

	private void compare(ListValueModel listValueModel, List list) {
		assertEquals(listValueModel.size(), list.size());
		for (int i = 0; i < listValueModel.size(); i++) {
			assertEquals(listValueModel.getItem(i), list.get(i));
		}
	}

	public void testCollectionSort() {
		this.verifyCollectionSort(null);
	}

	public void testListSort() {
		this.verifyListSort(null);
	}

	public void testCustomCollectionSort() {
		this.verifyCollectionSort(this.buildCustomComparator());
	}

	public void testCustomListSort() {
		this.verifyListSort(this.buildCustomComparator());
	}

	private Comparator buildCustomComparator() {
		// sort with reverse order
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) o2).compareTo(o1);
			}
		};
	}

	private void verifyCollectionSort(Comparator comparator) {
		SimpleCollectionValueModel collectionHolder = this.buildCollectionHolder();
		ListValueModel listValueModel = new ItemPropertyListValueModelAdapter(new SortedListValueModelAdapter(collectionHolder, comparator), Displayable.DISPLAY_STRING_PROPERTY, Displayable.ICON_PROPERTY);
		SynchronizedList synchList = new SynchronizedList(listValueModel);
		assertEquals(6, synchList.size());
		this.compareSort(listValueModel, synchList, comparator);

		collectionHolder.addItem(this.tom);
		collectionHolder.addItem(this.dick);
		collectionHolder.addItem(this.harry);
		assertEquals(9, synchList.size());
		this.compareSort(listValueModel, synchList, comparator);

		collectionHolder.removeItem(this.foo);
		collectionHolder.removeItem(this.jar);
		collectionHolder.removeItem(this.harry);
		assertEquals(6, synchList.size());
		this.compareSort(listValueModel, synchList, comparator);

		collectionHolder.setValue(this.buildCollection());
		assertEquals(6, synchList.size());
		this.compareSort(listValueModel, synchList, comparator);
	}

	private void verifyListSort(Comparator comparator) {
		SimpleListValueModel listHolder = this.buildListHolder();
		ListValueModel listValueModel = new ItemPropertyListValueModelAdapter(new SortedListValueModelAdapter(listHolder, comparator), Displayable.DISPLAY_STRING_PROPERTY, Displayable.ICON_PROPERTY);
		SynchronizedList synchList = new SynchronizedList(listValueModel);
		assertEquals(6, synchList.size());
		this.compareSort(listValueModel, synchList, comparator);

		listHolder.addItem(0, this.tom);
		listHolder.addItem(0, this.dick);
		listHolder.addItem(0, this.harry);
		assertEquals(9, synchList.size());
		this.compareSort(listValueModel, synchList, comparator);

		listHolder.removeItem(8);
		listHolder.removeItem(4);
		listHolder.removeItem(0);
		listHolder.removeItem(5);
		assertEquals(5, synchList.size());
		this.compareSort(listValueModel, synchList, comparator);

		listHolder.setValue(this.buildList());
		assertEquals(6, synchList.size());
		this.compareSort(listValueModel, synchList, comparator);
	}

	private void compareSort(ListValueModel listValueModel, List list, Comparator comparator) {
		SortedSet ss = new TreeSet(comparator);
		for (int i = 0; i < listValueModel.size(); i++) {
			ss.add(listValueModel.getItem(i));
		}
		assertEquals(ss.size(), list.size());
		for (Iterator stream1 = ss.iterator(), stream2 = list.iterator(); stream1.hasNext(); ) {
			assertEquals(stream1.next(), stream2.next());
		}
	}

	public void testHasListeners() throws Exception {
		SimpleListValueModel listHolder = this.buildListHolder();
		assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		assertFalse(this.foo.hasAnyPropertyChangeListeners(Displayable.DISPLAY_STRING_PROPERTY));
		assertFalse(this.foo.hasAnyPropertyChangeListeners(Displayable.ICON_PROPERTY));
		assertFalse(this.jaz.hasAnyPropertyChangeListeners(Displayable.DISPLAY_STRING_PROPERTY));
		assertFalse(this.jaz.hasAnyPropertyChangeListeners(Displayable.ICON_PROPERTY));

		ListValueModel listValueModel = new ItemPropertyListValueModelAdapter(new SortedListValueModelAdapter(listHolder), Displayable.DISPLAY_STRING_PROPERTY, Displayable.ICON_PROPERTY);
		assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		assertFalse(this.foo.hasAnyPropertyChangeListeners(Displayable.DISPLAY_STRING_PROPERTY));
		assertFalse(this.foo.hasAnyPropertyChangeListeners(Displayable.ICON_PROPERTY));
		assertFalse(this.jaz.hasAnyPropertyChangeListeners(Displayable.DISPLAY_STRING_PROPERTY));
		assertFalse(this.jaz.hasAnyPropertyChangeListeners(Displayable.ICON_PROPERTY));
		this.verifyHasNoListeners(listValueModel);

		SynchronizedList synchList = new SynchronizedList(listValueModel);
		assertTrue(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		assertTrue(this.foo.hasAnyPropertyChangeListeners(Displayable.DISPLAY_STRING_PROPERTY));
		assertTrue(this.foo.hasAnyPropertyChangeListeners(Displayable.ICON_PROPERTY));
		assertTrue(this.jaz.hasAnyPropertyChangeListeners(Displayable.DISPLAY_STRING_PROPERTY));
		assertTrue(this.jaz.hasAnyPropertyChangeListeners(Displayable.ICON_PROPERTY));
		this.verifyHasListeners(listValueModel);

		listValueModel.removeListChangeListener(ValueModel.VALUE, synchList);
		assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		assertFalse(this.foo.hasAnyPropertyChangeListeners(Displayable.DISPLAY_STRING_PROPERTY));
		assertFalse(this.foo.hasAnyPropertyChangeListeners(Displayable.ICON_PROPERTY));
		assertFalse(this.jaz.hasAnyPropertyChangeListeners(Displayable.DISPLAY_STRING_PROPERTY));
		assertFalse(this.jaz.hasAnyPropertyChangeListeners(Displayable.ICON_PROPERTY));
		this.verifyHasNoListeners(listValueModel);
	}

	public void testGetSize() throws Exception {
		SimpleListValueModel listHolder = this.buildListHolder();
		ListValueModel listValueModel = new ItemPropertyListValueModelAdapter(new SortedListValueModelAdapter(listHolder), Displayable.DISPLAY_STRING_PROPERTY, Displayable.ICON_PROPERTY);
		SynchronizedList synchList = new SynchronizedList(listValueModel);
		this.verifyHasListeners(listValueModel);
		assertEquals(6, listValueModel.size());
		assertEquals(6, synchList.size());
	}

	public void testGetElementAt() throws Exception {
		SimpleListValueModel listHolder = this.buildListHolder();
		ListValueModel listValueModel = new SortedListValueModelAdapter(new ItemPropertyListValueModelAdapter(listHolder, Displayable.DISPLAY_STRING_PROPERTY, Displayable.ICON_PROPERTY));
		SynchronizedList synchList = new SynchronizedList(listValueModel);
		this.verifyHasListeners(listValueModel);
		assertEquals(this.bar, listValueModel.getItem(0));
		assertEquals(this.bar, synchList.get(0));
		this.bar.setName("zzz");
		assertEquals(this.bar, listValueModel.getItem(5));
		assertEquals(this.bar, synchList.get(5));
		this.bar.setName("this.bar");
	}

	private void verifyHasNoListeners(ListValueModel listValueModel) throws Exception {
		assertTrue(((AbstractModel) listValueModel).hasNoListChangeListeners(ValueModel.VALUE));
	}

	private void verifyHasListeners(ListValueModel listValueModel) throws Exception {
		assertTrue(((AbstractModel) listValueModel).hasAnyListChangeListeners(ValueModel.VALUE));
	}

	private SimpleCollectionValueModel buildCollectionHolder() {
		return new SimpleCollectionValueModel(this.buildCollection());
	}

	private Collection buildCollection() {
		Bag bag = new HashBag();
		this.populateCollection(bag);
		return bag;
	}

	private SimpleListValueModel buildListHolder() {
		return new SimpleListValueModel(this.buildList());
	}

	private List buildList() {
		List list = new ArrayList();
		this.populateCollection(list);
		return list;
	}

	private void populateCollection(Collection c) {
		c.add(this.foo);
		c.add(this.bar);
		c.add(this.baz);
		c.add(this.joo);
		c.add(this.jar);
		c.add(this.jaz);
	}

private class Junk extends AbstractModel implements Displayable {
	private String name;
	public Junk(String name) {
		this.name = name;
	}
	public String displayString() {
		return this.name;
	}
	public Icon icon() {
		return null;
	}
	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}
	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(DISPLAY_STRING_PROPERTY, old, name);
	}
	public String toString() {
		return "Junk(" + this.name + ")";
	}
}

}
