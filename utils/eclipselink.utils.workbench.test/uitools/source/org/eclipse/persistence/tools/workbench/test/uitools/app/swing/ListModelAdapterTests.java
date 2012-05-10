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
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.ListModel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.uitools.app.SynchronizedList;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;

public class ListModelAdapterTests extends TestCase {

	public static Test suite() {
		return new TestSuite(ListModelAdapterTests.class);
	}
	
	public ListModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		// nothing yet...
	}

	protected void tearDown() throws Exception {
		// nothing yet...
		super.tearDown();
	}

	public void testCollectionSynchronization() {
		CollectionValueModel collectionHolder = this.buildCollectionHolder();
		ListModel listModel = new ListModelAdapter(collectionHolder);
		SynchronizedList synchList = new SynchronizedList(listModel);
		assertEquals(6, synchList.size());
		this.compare(listModel, synchList);

		collectionHolder.addItem("tom");
		collectionHolder.addItem("dick");
		collectionHolder.addItem("harry");
		collectionHolder.addItem(null);
		assertEquals(10, synchList.size());
		this.compare(listModel, synchList);

		collectionHolder.removeItem("foo");
		collectionHolder.removeItem("jar");
		collectionHolder.removeItem("harry");
		collectionHolder.removeItem(null);
		assertEquals(6, synchList.size());
		this.compare(listModel, synchList);
	}

	public void testListSynchronization() {
		ListValueModel listHolder = this.buildListHolder();
		ListModel listModel = new ListModelAdapter(listHolder);
		SynchronizedList synchList = new SynchronizedList(listModel);
		assertEquals(6, synchList.size());
		this.compare(listModel, synchList);

		listHolder.addItem(6, "tom");
		listHolder.addItem(7, "dick");
		listHolder.addItem(8, "harry");
		listHolder.addItem(9, null);
		assertEquals(10, synchList.size());
		this.compare(listModel, synchList);

		listHolder.removeItem(9);
		listHolder.removeItem(8);
		listHolder.removeItem(4);
		listHolder.removeItem(0);
		assertEquals(6, synchList.size());
		this.compare(listModel, synchList);
	}

	public void testSetModel() {
		SimpleListValueModel listHolder1 = this.buildListHolder();
		ListModelAdapter listModel = new ListModelAdapter(listHolder1);
		SynchronizedList synchList = new SynchronizedList(listModel);
		assertTrue(listHolder1.hasAnyListChangeListeners(ValueModel.VALUE));
		assertEquals(6, synchList.size());
		this.compare(listModel, synchList);

		SimpleListValueModel listHolder2 = this.buildListHolder2();
		listModel.setModel(listHolder2);
		assertEquals(3, synchList.size());
		this.compare(listModel, synchList);
		assertTrue(listHolder1.hasNoListChangeListeners(ValueModel.VALUE));
		assertTrue(listHolder2.hasAnyListChangeListeners(ValueModel.VALUE));

		listModel.setModel(new SimpleListValueModel());
		assertEquals(0, synchList.size());
		this.compare(listModel, synchList);
		assertTrue(listHolder1.hasNoListChangeListeners(ValueModel.VALUE));
		assertTrue(listHolder2.hasNoListChangeListeners(ValueModel.VALUE));
	}

	private void compare(ListModel listModel, List list) {
		assertEquals(listModel.getSize(), list.size());
		for (int i = 0; i < listModel.getSize(); i++) {
			assertEquals(listModel.getElementAt(i), list.get(i));
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
		CollectionValueModel collectionHolder = this.buildCollectionHolder();
		ListModel listModel = new ListModelAdapter(new SortedListValueModelAdapter(collectionHolder, comparator));
		SynchronizedList synchList = new SynchronizedList(listModel);
		assertEquals(6, synchList.size());
		this.compareSort(listModel, synchList, comparator);

		collectionHolder.addItem("tom");
		collectionHolder.addItem("dick");
		collectionHolder.addItem("harry");
		assertEquals(9, synchList.size());
		this.compareSort(listModel, synchList, comparator);

		collectionHolder.removeItem("foo");
		collectionHolder.removeItem("jar");
		collectionHolder.removeItem("harry");
		assertEquals(6, synchList.size());
		this.compareSort(listModel, synchList, comparator);
	}

	private void verifyListSort(Comparator comparator) {
		ListValueModel listHolder = this.buildListHolder();
		ListModel listModel = new ListModelAdapter(new SortedListValueModelAdapter(listHolder, comparator));
		SynchronizedList synchList = new SynchronizedList(listModel);
		assertEquals(6, synchList.size());
		this.compareSort(listModel, synchList, comparator);

		listHolder.addItem(0, "tom");
		listHolder.addItem(0, "dick");
		listHolder.addItem(0, "harry");
		assertEquals(9, synchList.size());
		this.compareSort(listModel, synchList, comparator);

		listHolder.removeItem(8);
		listHolder.removeItem(4);
		listHolder.removeItem(0);
		listHolder.removeItem(5);
		assertEquals(5, synchList.size());
		this.compareSort(listModel, synchList, comparator);
	}

	private void compareSort(ListModel listModel, List list, Comparator comparator) {
		SortedSet ss = new TreeSet(comparator);
		for (int i = 0; i < listModel.getSize(); i++) {
			ss.add(listModel.getElementAt(i));
		}
		assertEquals(ss.size(), list.size());
		for (Iterator stream1 = ss.iterator(), stream2 = list.iterator(); stream1.hasNext(); ) {
			assertEquals(stream1.next(), stream2.next());
		}
	}

	public void testHasListeners() throws Exception {
		SimpleListValueModel listHolder = this.buildListHolder();
		assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));

		ListModel listModel = new ListModelAdapter(listHolder);
		assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(listModel);

		SynchronizedList synchList = new SynchronizedList(listModel);
		assertTrue(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		this.verifyHasListeners(listModel);

		listModel.removeListDataListener(synchList);
		assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(listModel);
	}

	public void testGetSize() throws Exception {
		SimpleListValueModel listHolder = this.buildListHolder();
		ListModel listModel = new ListModelAdapter(listHolder);
		this.verifyHasNoListeners(listModel);
		assertEquals(6, listModel.getSize());

		SynchronizedList synchList = new SynchronizedList(listModel);
		this.verifyHasListeners(listModel);
		assertEquals(6, listModel.getSize());

		listModel.removeListDataListener(synchList);
		this.verifyHasNoListeners(listModel);
		assertEquals(6, listModel.getSize());
	}

	public void testGetElementAt() throws Exception {
		SimpleListValueModel listHolder = this.buildListHolder();
		ListModel listModel = new ListModelAdapter(new SortedListValueModelAdapter(listHolder));
		SynchronizedList synchList = new SynchronizedList(listModel);
		this.verifyHasListeners(listModel);
		assertEquals("bar", listModel.getElementAt(0));
		assertEquals("bar", synchList.get(0));
	}

	private void verifyHasNoListeners(ListModel listModel) throws Exception {
		boolean hasNoListeners = ((Boolean) ClassTools.invokeMethod(listModel, "hasNoListDataListeners")).booleanValue();
		assertTrue(hasNoListeners);
	}

	private void verifyHasListeners(ListModel listModel) throws Exception {
		boolean hasListeners = ((Boolean) ClassTools.invokeMethod(listModel, "hasListDataListeners")).booleanValue();
		assertTrue(hasListeners);
	}

	private CollectionValueModel buildCollectionHolder() {
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
		c.add("foo");
		c.add("bar");
		c.add("baz");
		c.add("joo");
		c.add("jar");
		c.add("jaz");
	}

	private SimpleListValueModel buildListHolder2() {
		return new SimpleListValueModel(this.buildList2());
	}

	private List buildList2() {
		List list = new ArrayList();
		this.populateCollection2(list);
		return list;
	}

	private void populateCollection2(Collection c) {
		c.add("tom");
		c.add("dick");
		c.add("harry");
	}

}
