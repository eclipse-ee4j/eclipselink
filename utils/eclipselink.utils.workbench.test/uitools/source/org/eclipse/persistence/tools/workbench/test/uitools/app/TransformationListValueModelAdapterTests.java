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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


public class TransformationListValueModelAdapterTests extends TestCase {
	private ListValueModel listHolder;
	private ListValueModel transformedListHolder;
	ListChangeEvent event;
	String eventType;

	private static final String ADD = "add";
	private static final String REMOVE = "remove";
	private static final String REPLACE = "replace";
	private static final String CHANGE = "change";
	
	public static Test suite() {
		TestSuite suite = new TestSuite(TransformationListValueModelAdapterTests.class);
		suite.addTestSuite(TransformerTests.class);
		return suite;
	}
	
	public TransformationListValueModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.listHolder = new SimpleListValueModel(this.buildList());
		this.transformedListHolder = this.buildTransformedListHolder(this.listHolder);
	}

	private List buildList() {
		List result = new ArrayList();
		result.add("foo");
		result.add("bar");
		result.add("baz");
		return result;
	}

	private List buildTransformedList() {
		return this.transform(this.buildList());
	}

	private List transform(List list) {
		List result = new ArrayList(list.size());
		for (Iterator stream = list.iterator(); stream.hasNext(); ) {
			String string = (String) stream.next();
			if (string == null) {
				result.add(null);
			} else {
				result.add(string.toUpperCase());
			}
		}
		return result;
	}

	private List buildAddList() {
		List result = new ArrayList();
		result.add("joo");
		result.add("jar");
		result.add("jaz");
		return result;
	}

	private List buildTransformedAddList() {
		return this.transform(this.buildAddList());
	}

	private List buildRemoveList() {
		List result = new ArrayList();
		result.add("foo");
		result.add("bar");
		return result;
	}

	private List buildTransformedRemoveList() {
		return this.transform(this.buildRemoveList());
	}

	ListValueModel buildTransformedListHolder(ListValueModel lvm) {
		return new TransformationListValueModelAdapter(lvm) {
			protected Object transformItem(Object item) {
				return (item == null) ? null : ((String) item).toUpperCase();
			}
		};
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		this.transformedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());
		assertEquals(this.buildTransformedList(), CollectionTools.list((Iterator) this.transformedListHolder.getValue()));
	}

	public void testStaleValue() {
		ListChangeListener listener = this.buildListener();
		this.transformedListHolder.addListChangeListener(ValueModel.VALUE, listener);
		assertEquals(this.buildTransformedList(), CollectionTools.list((Iterator) this.transformedListHolder.getValue()));

		this.transformedListHolder.removeListChangeListener(ValueModel.VALUE, listener);
		assertEquals(Collections.EMPTY_LIST, CollectionTools.list((Iterator) this.transformedListHolder.getValue()));
	}

	public void testSize() {
		this.transformedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());
		assertEquals(this.buildTransformedList().size(), CollectionTools.size((Iterator) this.transformedListHolder.getValue()));
	}

	private boolean transformedListContains(Object item) {
		return CollectionTools.contains((Iterator) this.transformedListHolder.getValue(), item);
	}

	private boolean transformedListContainsAll(Collection items) {
		return CollectionTools.containsAll((Iterator) this.transformedListHolder.getValue(), items);
	}

	private boolean transformedListContainsAny(Collection items) {
		List transformedList = CollectionTools.list((ListIterator) this.transformedListHolder.getValue());
		for (Iterator stream = items.iterator(); stream.hasNext(); ) {
			if (transformedList.contains(stream.next())) {
				return true;
			}
		}
		return false;
	}

	public void testAddItem() {
		this.transformedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertFalse(this.transformedListContains("JOO"));
		this.listHolder.addItem(2, "joo");
		assertTrue(this.transformedListContains("JOO"));

		assertFalse(this.transformedListContains(null));
		this.listHolder.addItem(0, null);
		assertTrue(this.transformedListContains(null));
	}

	public void testAddItems() {
		this.transformedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertFalse(this.transformedListContainsAny(this.buildTransformedAddList()));
		this.listHolder.addItems(2, this.buildAddList());
		assertTrue(this.transformedListContainsAll(this.buildTransformedAddList()));
	}

	public void testRemoveItem() {
		this.transformedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertTrue(this.transformedListContains("BAR"));
		this.listHolder.removeItem(this.buildList().indexOf("bar"));
		assertFalse(this.transformedListContains("BAR"));

		this.listHolder.addItem(1, null);
		assertTrue(this.transformedListContains(null));
		this.listHolder.removeItem(1);
		assertFalse(this.transformedListContains(null));
	}

	public void testRemoveItems() {
		this.transformedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertTrue(this.transformedListContainsAll(this.buildTransformedRemoveList()));
		this.listHolder.removeItems(0, 2);
		assertFalse(this.transformedListContainsAny(this.buildTransformedRemoveList()));
	}

	public void testListChangeGeneric() {
		this.transformedListHolder.addListChangeListener(this.buildListener());
		this.verifyListChange();
	}

	public void testListChangeNamed() {
		this.transformedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());
		this.verifyListChange();
	}

	private void verifyListChange() {
		this.event = null;
		this.eventType = null;
		this.listHolder.addItem(1, "joo");
		this.verifyEvent(ADD, 1, "JOO");

		this.event = null;
		this.eventType = null;
		this.listHolder.addItem(1, null);
		this.verifyEvent(ADD, 1, null);

		this.event = null;
		this.eventType = null;
		this.listHolder.removeItem(1);
		this.verifyEvent(REMOVE, 1, null);

		this.event = null;
		this.eventType = null;
		this.listHolder.removeItem(1);
		this.verifyEvent(REMOVE, 1, "JOO");

		this.event = null;
		this.eventType = null;
		this.listHolder.addItems(0, this.buildList());
		this.verifyEvent(ADD);
		assertEquals(this.buildTransformedList(), CollectionTools.list(this.event.items()));

		this.event = null;
		this.eventType = null;
		this.listHolder.replaceItem(0, "joo");
		this.verifyEvent(REPLACE);
		assertFalse(CollectionTools.contains(this.event.items(), "FOO"));
		assertTrue(CollectionTools.contains(this.event.items(), "JOO"));
	}

	private ListChangeListener buildListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				TransformationListValueModelAdapterTests.this.eventType = ADD;
				TransformationListValueModelAdapterTests.this.event = e;
			}
			public void itemsRemoved(ListChangeEvent e) {
				TransformationListValueModelAdapterTests.this.eventType = REMOVE;
				TransformationListValueModelAdapterTests.this.event = e;
			}
			public void itemsReplaced(ListChangeEvent e) {
				TransformationListValueModelAdapterTests.this.eventType = REPLACE;
				TransformationListValueModelAdapterTests.this.event = e;
			}
			public void listChanged(ListChangeEvent e) {
				TransformationListValueModelAdapterTests.this.eventType = CHANGE;
				TransformationListValueModelAdapterTests.this.event = e;
			}
		};
	}

	private void verifyEvent(String type) {
		assertEquals(type, this.eventType);
		assertEquals(this.transformedListHolder, this.event.getSource());
		assertEquals(ValueModel.VALUE, this.event.getListName());
	}

	private void verifyEvent(String type, int index, Object item) {
		this.verifyEvent(type);
		assertEquals(index, this.event.getIndex());
		assertEquals(item, this.event.items().next());
	}

	public void testHasListeners() {
		/*
		 * adding listeners to the transformed list will cause listeners
		 * to be added to the wrapped list;
		 * likewise, removing listeners from the transformed list will
		 * cause listeners to be removed from the wrapped list
		 */
		assertFalse(((AbstractModel) this.listHolder).hasAnyListChangeListeners(ValueModel.VALUE));

		ListChangeListener listener = this.buildListener();

		this.transformedListHolder.addListChangeListener(ValueModel.VALUE, listener);
		assertTrue(((AbstractModel) this.listHolder).hasAnyListChangeListeners(ValueModel.VALUE));

		this.transformedListHolder.removeListChangeListener(ValueModel.VALUE, listener);
		assertFalse(((AbstractModel) this.listHolder).hasAnyListChangeListeners(ValueModel.VALUE));

		this.transformedListHolder.addListChangeListener(listener);
		assertTrue(((AbstractModel) this.listHolder).hasAnyListChangeListeners(ValueModel.VALUE));

		this.transformedListHolder.removeListChangeListener(listener);
		assertFalse(((AbstractModel) this.listHolder).hasAnyListChangeListeners(ValueModel.VALUE));
	}


/**
 * execute the same set of tests again, but by passing a Transformer to the adapter
 * (as opposed to overriding #transformItem(Object))
 */
public static class TransformerTests extends TransformationListValueModelAdapterTests {
	public static Test suite() {
		TestSuite suite = new TestSuite(TransformationListValueModelAdapterTests.class);
		suite.addTestSuite(TransformerTests.class);
		return suite;
	}
	public TransformerTests(String name) {
		super(name);
	}
	ListValueModel buildTransformedListHolder(ListValueModel lvm) {
		return new TransformationListValueModelAdapter(lvm, this.buildTransformer());
	}
	private Transformer buildTransformer() {
		return new Transformer() {
			public Object transform(Object o) {
				return (o == null) ? null : ((String) o).toUpperCase();
			}
		};
	}
}
	
}
