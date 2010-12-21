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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.ExtendedListValueModelWrapper;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


public class ExtendedListValueModelWrapperTests extends TestCase {
	private ListValueModel listHolder;
	private ListValueModel extendedListHolder;
	ListChangeEvent event;
	String eventType;

	private static final String ADD = "add";
	private static final String REMOVE = "remove";
	private static final String REPLACE = "replace";
	private static final String CHANGE = "change";

	public static Test suite() {
		return new TestSuite(ExtendedListValueModelWrapperTests.class);
	}
	
	public ExtendedListValueModelWrapperTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.listHolder = new SimpleListValueModel(this.buildList());
		this.extendedListHolder = this.buildExtendedListHolder(this.listHolder);
	}

	private List buildList() {
		List result = new ArrayList();
		result.add("A");
		result.add("B");
		result.add("C");
		result.add("D");
		return result;
	}

	private List buildExtendedList() {
		List extendedList = new ArrayList();
		extendedList.addAll(this.buildPrefix());
		extendedList.addAll(this.buildList());
		extendedList.addAll(this.buildSuffix());
		return extendedList;
	}

	private List buildPrefix() {
		List prefix = new ArrayList();
		prefix.add("x");
		prefix.add("y");
		prefix.add("z");
		return prefix;
	}

	private List buildSuffix() {
		List suffix = new ArrayList();
		suffix.add("i");
		suffix.add("j");
		return suffix;
	}

	private ListValueModel buildExtendedListHolder(ListValueModel lvm) {
		return new ExtendedListValueModelWrapper(this.buildPrefix(), lvm, this.buildSuffix());
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());
		assertEquals(this.buildExtendedList(), CollectionTools.list((Iterator) this.extendedListHolder.getValue()));
	}

	public void testSize() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());
		assertEquals(this.buildExtendedList().size(), CollectionTools.size((Iterator) this.extendedListHolder.getValue()));
		assertEquals(this.buildExtendedList().size(), this.extendedListHolder.size());
	}

	private boolean extendedListContains(Object item) {
		return CollectionTools.contains((Iterator) this.extendedListHolder.getValue(), item);
	}

	private boolean extendedListContainsAll(Collection items) {
		return CollectionTools.containsAll((Iterator) this.extendedListHolder.getValue(), items);
	}

	private boolean extendedListContainsAny(Collection items) {
		List extendedList = CollectionTools.list((ListIterator) this.extendedListHolder.getValue());
		for (Iterator stream = items.iterator(); stream.hasNext(); ) {
			if (extendedList.contains(stream.next())) {
				return true;
			}
		}
		return false;
	}

	private boolean listContains(Object item) {
		return CollectionTools.contains((Iterator) this.listHolder.getValue(), item);
	}

	private boolean listContainsAll(Collection items) {
		return CollectionTools.containsAll((Iterator) this.listHolder.getValue(), items);
	}

	private boolean listContainsAny(Collection items) {
		List extendedList = CollectionTools.list((ListIterator) this.listHolder.getValue());
		for (Iterator stream = items.iterator(); stream.hasNext(); ) {
			if (extendedList.contains(stream.next())) {
				return true;
			}
		}
		return false;
	}

	public void testAddItem1() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertFalse(this.extendedListContains("E"));
		this.listHolder.addItem(4, "E");
		assertTrue(this.extendedListContains("E"));
		assertTrue(this.listContains("E"));
	}

	public void testAddItem2() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertFalse(this.extendedListContains(null));
		this.listHolder.addItem(4, null);
		assertTrue(this.extendedListContains(null));
		assertTrue(this.listContains(null));
	}

	public void testAddItem3() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertFalse(this.extendedListContains("E"));
		this.extendedListHolder.addItem(7, "E");
		assertTrue(this.extendedListContains("E"));
		assertTrue(this.listContains("E"));
	}

	public void testAddItem4() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		boolean exCaught = false;
		try {
			this.extendedListHolder.addItem(0, "Z");
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("prefix") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
		assertFalse(this.extendedListContains("Z"));
		assertFalse(this.listContains("Z"));
	}

	public void testAddItem5() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		boolean exCaught = false;
		try {
			this.extendedListHolder.addItem(8, "Z");
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("suffix") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
		assertFalse(this.extendedListContains("Z"));
		assertFalse(this.listContains("Z"));
	}

	private List buildAddList() {
		List addList = new ArrayList();
		addList.add("E");
		addList.add("F");
		return addList;
	}

	public void testAddItems1() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertFalse(this.extendedListContainsAny(this.buildAddList()));
		this.listHolder.addItems(4, this.buildAddList());
		assertTrue(this.extendedListContainsAll(this.buildAddList()));
		assertTrue(this.listContainsAll(this.buildAddList()));
	}

	public void testAddItems2() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertFalse(this.extendedListContainsAny(this.buildAddList()));
		this.extendedListHolder.addItems(4, this.buildAddList());
		assertTrue(this.extendedListContainsAll(this.buildAddList()));
		assertTrue(this.listContainsAll(this.buildAddList()));
	}

	public void testAddItems3() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		boolean exCaught = false;
		try {
			this.extendedListHolder.addItems(0, this.buildAddList());
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("prefix") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
		assertFalse(this.extendedListContainsAny(this.buildAddList()));
		assertFalse(this.listContainsAny(this.buildAddList()));
	}

	public void testAddItems4() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		boolean exCaught = false;
		try {
			this.extendedListHolder.addItem(8, this.buildAddList());
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("suffix") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
		assertFalse(this.extendedListContainsAny(this.buildAddList()));
		assertFalse(this.listContainsAny(this.buildAddList()));
	}

	public void testRemoveItem1() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertTrue(this.extendedListContains("B"));
		this.listHolder.removeItem(this.buildList().indexOf("B"));
		assertFalse(this.extendedListContains("B"));
		assertFalse(this.listContains("B"));
	}

	public void testRemoveItem2() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertTrue(this.extendedListContains("B"));
		this.extendedListHolder.removeItem(this.buildPrefix().size() + this.buildList().indexOf("B"));
		assertFalse(this.extendedListContains("B"));
		assertFalse(this.listContains("B"));
	}

	public void testRemoveItem3() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		this.listHolder.addItem(0, null);
		assertTrue(this.extendedListContains(null));
		this.extendedListHolder.removeItem(this.buildPrefix().size());
		assertFalse(this.extendedListContains(null));
		assertFalse(this.listContains(null));
	}

	public void testRemoveItem4() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertTrue(this.extendedListContains("x"));
		boolean exCaught = false;
		try {
			this.extendedListHolder.removeItem(CollectionTools.indexOf((ListIterator) this.extendedListHolder.getValue(), "x"));
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("prefix") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
		assertTrue(this.extendedListContains("x"));
	}

	public void testRemoveItem5() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertTrue(this.extendedListContains("i"));
		boolean exCaught = false;
		try {
			this.extendedListHolder.removeItem(CollectionTools.indexOf((ListIterator) this.extendedListHolder.getValue(), "i"));
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("suffix") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
		assertTrue(this.extendedListContains("i"));
	}

	public void testRemoveItems1() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertTrue(this.extendedListContains("B"));
		assertTrue(this.extendedListContains("C"));
		this.listHolder.removeItems(this.buildList().indexOf("B"), 2);
		assertFalse(this.extendedListContains("B"));
		assertFalse(this.extendedListContains("C"));
		assertFalse(this.listContains("B"));
		assertFalse(this.listContains("C"));
	}

	public void testRemoveItems2() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertTrue(this.extendedListContains("B"));
		assertTrue(this.extendedListContains("C"));
		this.extendedListHolder.removeItems(this.buildPrefix().size() + this.buildList().indexOf("B"), 2);
		assertFalse(this.extendedListContains("B"));
		assertFalse(this.extendedListContains("C"));
		assertFalse(this.listContains("B"));
		assertFalse(this.listContains("C"));
	}

	public void testRemoveItems3() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertTrue(this.extendedListContains("x"));
		assertTrue(this.extendedListContains("y"));
		boolean exCaught = false;
		try {
			this.extendedListHolder.removeItems(CollectionTools.indexOf((ListIterator) this.extendedListHolder.getValue(), "x"), 2);
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("prefix") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
		assertTrue(this.extendedListContains("x"));
		assertTrue(this.extendedListContains("y"));
	}

	public void testRemoveItems4() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());

		assertTrue(this.extendedListContains("D"));
		assertTrue(this.extendedListContains("i"));
		boolean exCaught = false;
		try {
			this.extendedListHolder.removeItems(CollectionTools.indexOf((ListIterator) this.extendedListHolder.getValue(), "D"), 2);
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf("suffix") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
		assertTrue(this.extendedListContains("D"));
		assertTrue(this.extendedListContains("i"));
	}

	public void testListChangeGeneric() {
		this.extendedListHolder.addListChangeListener(this.buildListener());
		this.verifyListChange();
	}

	public void testListChangeNamed() {
		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());
		this.verifyListChange();
	}

	private void verifyListChange() {
		this.event = null;
		this.eventType = null;
		this.listHolder.addItem(4, "E");
		this.verifyEvent(ADD, 7, "E");

		this.event = null;
		this.eventType = null;
		this.listHolder.addItem(5, null);
		this.verifyEvent(ADD, 8, null);

		this.event = null;
		this.eventType = null;
		this.listHolder.removeItem(5);
		this.verifyEvent(REMOVE, 8, null);

		this.event = null;
		this.eventType = null;
		this.listHolder.removeItem(4);
		this.verifyEvent(REMOVE, 7, "E");

		this.event = null;
		this.eventType = null;
		this.listHolder.addItems(0, this.buildList());
		this.verifyEvent(ADD);
		assertEquals(this.buildList(), CollectionTools.list(this.event.items()));

		this.event = null;
		this.eventType = null;
		this.listHolder.replaceItem(0, "AA");
		this.verifyEvent(REPLACE);
		assertFalse(CollectionTools.contains(this.event.items(), "A"));
		assertTrue(CollectionTools.contains(this.event.items(), "AA"));
	}

	private ListChangeListener buildListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				ExtendedListValueModelWrapperTests.this.eventType = ADD;
				ExtendedListValueModelWrapperTests.this.event = e;
			}
			public void itemsRemoved(ListChangeEvent e) {
				ExtendedListValueModelWrapperTests.this.eventType = REMOVE;
				ExtendedListValueModelWrapperTests.this.event = e;
			}
			public void itemsReplaced(ListChangeEvent e) {
				ExtendedListValueModelWrapperTests.this.eventType = REPLACE;
				ExtendedListValueModelWrapperTests.this.event = e;
			}
			public void listChanged(ListChangeEvent e) {
				ExtendedListValueModelWrapperTests.this.eventType = CHANGE;
				ExtendedListValueModelWrapperTests.this.event = e;
			}
		};
	}

	private void verifyEvent(String type) {
		assertEquals(type, this.eventType);
		assertEquals(this.extendedListHolder, this.event.getSource());
		assertEquals(ValueModel.VALUE, this.event.getListName());
	}

	private void verifyEvent(String type, int index, Object item) {
		this.verifyEvent(type);
		assertEquals(index, this.event.getIndex());
		assertEquals(item, this.event.items().next());
	}

	public void testHasListeners() {
		/*
		 * adding listeners to the extended list will cause listeners
		 * to be added to the wrapped list;
		 * likewise, removing listeners from the extended list will
		 * cause listeners to be removed from the wrapped list
		 */
		assertFalse(((AbstractModel) this.listHolder).hasAnyListChangeListeners(ValueModel.VALUE));

		ListChangeListener listener = this.buildListener();

		this.extendedListHolder.addListChangeListener(ValueModel.VALUE, listener);
		assertTrue(((AbstractModel) this.listHolder).hasAnyListChangeListeners(ValueModel.VALUE));

		this.extendedListHolder.removeListChangeListener(ValueModel.VALUE, listener);
		assertFalse(((AbstractModel) this.listHolder).hasAnyListChangeListeners(ValueModel.VALUE));

		this.extendedListHolder.addListChangeListener(listener);
		assertTrue(((AbstractModel) this.listHolder).hasAnyListChangeListeners(ValueModel.VALUE));

		this.extendedListHolder.removeListChangeListener(listener);
		assertFalse(((AbstractModel) this.listHolder).hasAnyListChangeListeners(ValueModel.VALUE));
	}

}
