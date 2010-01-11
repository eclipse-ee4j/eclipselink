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
package org.eclipse.persistence.tools.workbench.test.uitools.swing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedListModel;


public class ExtendedListModelTests
	extends TestCase
{
	private DefaultListModel listModel;
	private ListModel extendedListModel;
	ListDataEvent event;
	String eventType;
	
	private static final String ADD = "add";
	private static final String REMOVE = "remove";
	private static final String CHANGE = "change";
	
	public static Test suite() {
		return new TestSuite(ExtendedListModelTests.class);
	}
	
	public ExtendedListModelTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.listModel = this.buildListModel();
		this.extendedListModel = this.buildExtendedListModel();
	}
	
	private List buildList() {
		List list = new ArrayList();
		list.add("A");
		list.add("B");
		list.add("C");
		list.add("D");
		return list;
	}
	
	private List buildPrefix() {
		List prefix = new ArrayList();
		prefix.add("1");
		prefix.add("2");
		prefix.add("3");
		return prefix;
	}
	
	private List buildSuffix() {
		List suffix = new ArrayList();
		suffix.add("X");
		suffix.add("Y");
		suffix.add("Z");
		return suffix;
	}
	
	private List buildExtendedList() {
		List list = new ArrayList();
		list.addAll(this.buildPrefix());
		list.addAll(this.buildList());
		list.addAll(this.buildSuffix());
		return list;
	}
	
	private DefaultListModel buildListModel() {
		DefaultListModel lm = new DefaultListModel();
		
		for (Iterator stream = this.buildList().iterator(); stream.hasNext(); ) {
			lm.addElement(stream.next());
		}
		
		return lm;
	}

	private ListModel buildExtendedListModel() {
		return new ExtendedListModel(this.buildPrefix(), this.listModel, this.buildSuffix());
	}
	
	/** Return a list of the data in the listModel */
	private List buildListModelList() {
		List modelList = new ArrayList();
		
		for (int i = 0; i < this.listModel.getSize(); i ++) {
			modelList.add(this.listModel.getElementAt(i));
		}
		
		return modelList;
	}
	
	/** Return a list of the data in the extendedListModel */
	private List buildExtendedListModelList() {
		List modelList = new ArrayList();
		
		for (int i = 0; i < this.extendedListModel.getSize(); i ++) {
			modelList.add(this.extendedListModel.getElementAt(i));
		}
		
		return modelList;
	}
	
	private boolean listModelContains(Object item) {
		return this.buildListModelList().contains(item);
	}
	
	private boolean extendedListModelContains(Object item) {
		return this.buildExtendedListModelList().contains(item);
	}
	
	private void internalTestEquivalence(ListModel lm, List list) {
		assertEquals(lm.getSize(), list.size());
		
		for (int i = 0; i < lm.getSize(); i ++) {
			assertEquals(lm.getElementAt(i), list.get(i));
		}
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testEquivalence() {
		this.internalTestEquivalence(this.extendedListModel, this.buildExtendedList());
	}
	
	public void testAddItem_Part1() {
		assertFalse(this.extendedListModelContains("E"));
		this.listModel.addElement("E");
		assertTrue(this.listModelContains("E"));
		assertTrue(this.extendedListModelContains("E"));
		
		List extendedList = this.buildExtendedList();
		extendedList.add(7, "E");
		this.internalTestEquivalence(this.extendedListModel, extendedList);
	}
	
	public void testAddItem_Part2() {
		assertFalse(this.extendedListModelContains(null));
		this.listModel.add(0, null);
		assertTrue(this.listModelContains(null));
		assertTrue(this.extendedListModelContains(null));
		
		List extendedList = this.buildExtendedList();
		extendedList.add(3, null);
		this.internalTestEquivalence(this.extendedListModel, extendedList);
	}
	
	public void testAddItem_Part3() {
		assertFalse(this.extendedListModelContains("AtoB"));
		this.listModel.add(1, "AtoB");
		assertTrue(this.listModelContains("AtoB"));
		assertTrue(this.extendedListModelContains("AtoB"));
		
		List extendedList = this.buildExtendedList();
		extendedList.add(4, "AtoB");
		this.internalTestEquivalence(this.extendedListModel, extendedList);
	}
	
	public void testRemoveItem_Part1() {
		assertTrue(this.extendedListModelContains("B"));
		this.listModel.removeElement("B");
		assertFalse(this.listModelContains("B"));
		assertFalse(this.extendedListModelContains("B"));
		
		List extendedList = this.buildExtendedList();
		extendedList.remove("B");
		this.internalTestEquivalence(this.extendedListModel, extendedList);
	}
	
	public void testRemoveItem_Part2() {
		this.listModel.add(0, null);
		assertTrue(this.extendedListModelContains(null));
		this.listModel.remove(0);
		assertFalse(this.listModelContains(null));
		assertFalse(this.extendedListModelContains(null));
		
		this.internalTestEquivalence(this.extendedListModel, this.buildExtendedList());
	}

	private ListDataListener buildListener() {
		return new ListDataListener() {
			public void intervalAdded(ListDataEvent e) {
				ExtendedListModelTests.this.eventType = ADD;
				ExtendedListModelTests.this.event = e;
			}
			public void intervalRemoved(ListDataEvent e) {
				ExtendedListModelTests.this.eventType = REMOVE;
				ExtendedListModelTests.this.event = e;
			}
			public void contentsChanged(ListDataEvent e) {
				ExtendedListModelTests.this.eventType = CHANGE;
				ExtendedListModelTests.this.event = e;
			}
		};
	}
	
	public void testListChangeGeneric() {
		this.extendedListModel.addListDataListener(this.buildListener());
		this.verifyListChange();
	}

	private void verifyListChange() {
		this.event = null;
		this.eventType = null;
		this.listModel.add(4, "E");
		this.verifyEvent(ADD, 7);
		
		this.event = null;
		this.eventType = null;
		this.listModel.add(5, null);
		this.verifyEvent(ADD, 8);
		
		this.event = null;
		this.eventType = null;
		this.listModel.remove(5);
		this.verifyEvent(REMOVE, 8);
		
		this.event = null;
		this.eventType = null;
		this.listModel.remove(4);
		this.verifyEvent(REMOVE, 7);
	}
	
	private void verifyEvent(String type, int index0) {
		this.verifyEvent(type);
		assertEquals(index0, this.event.getIndex0());
		assertEquals(index0, this.event.getIndex1());
	}
	
	private void verifyEvent(String type) {
		assertEquals(type, this.eventType);
		assertEquals(this.extendedListModel, this.event.getSource());
	}
}
