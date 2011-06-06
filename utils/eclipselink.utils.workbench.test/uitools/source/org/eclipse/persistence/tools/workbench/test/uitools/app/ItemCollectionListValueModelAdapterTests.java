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
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemCollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.HashBag;


public class ItemCollectionListValueModelAdapterTests extends TestCase {
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
		return new TestSuite(ItemCollectionListValueModelAdapterTests.class);
	}
	
	public ItemCollectionListValueModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.foo = new Junk("foo");
		this.bar = new Junk("bar");
		this.baz = new Junk("baz");
		this.joo = new Junk("joo");
		this.jar = new Junk("jar");
		this.jaz = new Junk("jaz");

		this.tom = new Junk("tom");
		this.dick = new Junk("dick");
		this.harry = new Junk("harry");
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testCollectionSynchronization() {
		CollectionValueModel collectionHolder = this.buildCollectionHolder();
		ListValueModel listValueModel = new ItemCollectionListValueModelAdapter(collectionHolder, Junk.STUFF_COLLECTION);
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
	}

	public void testListSynchronization() {
		ListValueModel listHolder = this.buildListHolder();
		ListValueModel listValueModel = new ItemCollectionListValueModelAdapter(listHolder, Junk.STUFF_COLLECTION);
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
	}

	private void compare(ListValueModel listValueModel, List list) {
		assertEquals(listValueModel.size(), list.size());
		for (int i = 0; i < listValueModel.size(); i++) {
			assertEquals(listValueModel.getItem(i), list.get(i));
		}
	}


	public void testHasListeners() throws Exception {
		SimpleListValueModel listHolder = this.buildListHolder();
		assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		assertFalse(this.foo.hasAnyListChangeListeners(Junk.STUFF_COLLECTION));
		assertFalse(this.jaz.hasAnyListChangeListeners(Junk.STUFF_COLLECTION));

		ListValueModel listValueModel = new ItemCollectionListValueModelAdapter(listHolder, Junk.STUFF_COLLECTION);
		assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		assertFalse(this.foo.hasAnyCollectionChangeListeners(Junk.STUFF_COLLECTION));
		assertFalse(this.jaz.hasAnyCollectionChangeListeners(Junk.STUFF_COLLECTION));
		this.verifyHasNoListeners(listValueModel);

		SynchronizedList synchList = new SynchronizedList(listValueModel);
		assertTrue(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		assertTrue(this.foo.hasAnyCollectionChangeListeners(Junk.STUFF_COLLECTION));
		assertTrue(this.jaz.hasAnyCollectionChangeListeners(Junk.STUFF_COLLECTION));
		this.verifyHasListeners(listValueModel);

		listValueModel.removeListChangeListener(ValueModel.VALUE, synchList);
		assertFalse(listHolder.hasAnyListChangeListeners(ValueModel.VALUE));
		assertFalse(this.foo.hasAnyCollectionChangeListeners(Junk.STUFF_COLLECTION));
		assertFalse(this.jaz.hasAnyCollectionChangeListeners(Junk.STUFF_COLLECTION));
		this.verifyHasNoListeners(listValueModel);
	}

	public void testGetSize() throws Exception {
		SimpleListValueModel listHolder = this.buildListHolder();
		ListValueModel listValueModel = new ItemCollectionListValueModelAdapter(listHolder, Junk.STUFF_COLLECTION);
		SynchronizedList synchList = new SynchronizedList(listValueModel);
		this.verifyHasListeners(listValueModel);
		assertEquals(6, listValueModel.size());
		assertEquals(6, synchList.size());
	}

	public void testGetElementAt() throws Exception {
		SimpleListValueModel listHolder = this.buildListHolder();
		ListValueModel listValueModel = new SortedListValueModelAdapter(new ItemCollectionListValueModelAdapter(listHolder, Junk.STUFF_COLLECTION));
		SynchronizedList synchList = new SynchronizedList(listValueModel);
		this.verifyHasListeners(listValueModel);
		assertEquals(this.bar, listValueModel.getItem(0));
		assertEquals(this.bar, synchList.get(0));
		this.bar.removeStuff("bar");
		this.bar.addStuff("zzz");
		this.bar.addStuff("bar");
		assertEquals(this.bar, listValueModel.getItem(5));
		assertEquals(this.bar, synchList.get(5));
		this.bar.removeStuff("zzz");
	}

	private void verifyHasNoListeners(ListValueModel listValueModel) throws Exception {
		assertTrue(((AbstractModel) listValueModel).hasNoListChangeListeners(ValueModel.VALUE));
	}

	private void verifyHasListeners(ListValueModel listValueModel) throws Exception {
		assertTrue(((AbstractModel) listValueModel).hasAnyListChangeListeners(ValueModel.VALUE));
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
		c.add(this.foo);
		c.add(this.bar);
		c.add(this.baz);
		c.add(this.joo);
		c.add(this.jar);
		c.add(this.jaz);
	}


private class Junk extends AbstractModel implements Displayable {
	private Collection stuff;
		public static final String STUFF_COLLECTION = "stuff";
		

	public Junk(String stuffItem) {
		this.stuff = new ArrayList();
		this.stuff.add(stuffItem);
	}

	public void addStuff(String stuffItem) {
		this.addItemToCollection(stuffItem, this.stuff, STUFF_COLLECTION);
	}
	
	public void removeStuff(String stuffItem) {
		this.removeItemFromCollection(stuffItem, this.stuff, STUFF_COLLECTION);
	}

	public String displayString() {
		return toString();
	}

	public Icon icon() {
		return null;
	}
	
	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}

	public String toString() {
		return "Junk(" + this.stuff + ")";
	}

}

}
