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

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;


public class ExtendedComboBoxModelTests 
	extends TestCase
{
	private DefaultComboBoxModel comboBoxModel;
	private ComboBoxModel extendedComboBoxModel;
	ListDataEvent event;
	String eventType;
	
	private static final String ADD = "add";
	private static final String REMOVE = "remove";
	private static final String CHANGE = "change";
	
	public static Test suite() {
		return new TestSuite(ExtendedComboBoxModelTests.class);
	}
	
	public ExtendedComboBoxModelTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.comboBoxModel = this.buildComboBoxModel();
		this.extendedComboBoxModel = this.buildExtendedComboBoxModel();
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
	
	private DefaultComboBoxModel buildComboBoxModel() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		for (Iterator stream = this.buildList().iterator(); stream.hasNext(); ) {
			model.addElement(stream.next());
		}
		
		model.setSelectedItem(this.buildList().get(0));
		
		return model;
	}

	private ComboBoxModel buildExtendedComboBoxModel() {
		return new ExtendedComboBoxModel(this.buildPrefix(), this.comboBoxModel, this.buildSuffix());
	}
	
	/** Return a list of the data in the comboBoxModel */
	private List buildComboBoxModelList() {
		List modelList = new ArrayList();
		
		for (int i = 0; i < this.comboBoxModel.getSize(); i ++) {
			modelList.add(this.comboBoxModel.getElementAt(i));
		}
		
		return modelList;
	}
	
	/** Return a list of the data in the extendedComboBoxModel */
	private List buildExtendedComboBoxModelList() {
		List modelList = new ArrayList();
		
		for (int i = 0; i < this.extendedComboBoxModel.getSize(); i ++) {
			modelList.add(this.extendedComboBoxModel.getElementAt(i));
		}
		
		return modelList;
	}
	
	private ListDataListener buildListener() {
		return new ListDataListener() {
			public void intervalAdded(ListDataEvent e) {
				ExtendedComboBoxModelTests.this.eventType = ADD;
				ExtendedComboBoxModelTests.this.event = e;
			}
			public void intervalRemoved(ListDataEvent e) {
				ExtendedComboBoxModelTests.this.eventType = REMOVE;
				ExtendedComboBoxModelTests.this.event = e;
			}
			public void contentsChanged(ListDataEvent e) {
				ExtendedComboBoxModelTests.this.eventType = CHANGE;
				ExtendedComboBoxModelTests.this.event = e;
			}
		};
	}
	
	private boolean comboBoxModelContains(Object item) {
		return this.buildComboBoxModelList().contains(item);
	}
	
	private boolean extendedComboBoxModelContains(Object item) {
		return this.buildExtendedComboBoxModelList().contains(item);
	}
	
	private void internalTestEquivalence(ComboBoxModel model, List list) {
		assertEquals(model.getSize(), list.size());
		
		for (int i = 0; i < model.getSize(); i ++) {
			assertEquals(model.getElementAt(i), list.get(i));
		}
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testEquivalence() {
		this.internalTestEquivalence(this.extendedComboBoxModel, this.buildExtendedList());
	}
	
	public void testAddItem_Part1() {
		assertFalse(this.extendedComboBoxModelContains("E"));
		this.comboBoxModel.addElement("E");
		assertTrue(this.comboBoxModelContains("E"));
		assertTrue(this.extendedComboBoxModelContains("E"));
		
		List extendedList = this.buildExtendedList();
		extendedList.add(7, "E");
		this.internalTestEquivalence(this.extendedComboBoxModel, extendedList);
	}
	
	public void testAddItem_Part2() {
		assertFalse(this.extendedComboBoxModelContains(null));
		this.comboBoxModel.insertElementAt(null, 0);
		assertTrue(this.comboBoxModelContains(null));
		assertTrue(this.extendedComboBoxModelContains(null));
		
		List extendedList = this.buildExtendedList();
		extendedList.add(3, null);
		this.internalTestEquivalence(this.extendedComboBoxModel, extendedList);
	}
	
	public void testAddItem_Part3() {
		assertFalse(this.extendedComboBoxModelContains("AtoB"));
		this.comboBoxModel.insertElementAt("AtoB", 1);
		assertTrue(this.comboBoxModelContains("AtoB"));
		assertTrue(this.extendedComboBoxModelContains("AtoB"));
		
		List extendedList = this.buildExtendedList();
		extendedList.add(4, "AtoB");
		this.internalTestEquivalence(this.extendedComboBoxModel, extendedList);
	}
	
	public void testRemoveItem_Part1() {
		assertTrue(this.extendedComboBoxModelContains("B"));
		this.comboBoxModel.removeElement("B");
		assertFalse(this.comboBoxModelContains("B"));
		assertFalse(this.extendedComboBoxModelContains("B"));
		
		List extendedList = this.buildExtendedList();
		extendedList.remove("B");
		this.internalTestEquivalence(this.extendedComboBoxModel, extendedList);
	}
	
	public void testRemoveItem_Part2() {
		this.comboBoxModel.insertElementAt(null, 0);
		assertTrue(this.extendedComboBoxModelContains(null));
		this.comboBoxModel.removeElementAt(0);
		assertFalse(this.comboBoxModelContains(null));
		assertFalse(this.extendedComboBoxModelContains(null));
		
		this.internalTestEquivalence(this.extendedComboBoxModel, this.buildExtendedList());
	}

	public void testListChangeGeneric() {
		this.extendedComboBoxModel.addListDataListener(this.buildListener());
	
		this.event = null;
		this.eventType = null;
		this.comboBoxModel.insertElementAt("E", 4);
		this.verifyEvent(ADD, 7);
		
		this.event = null;
		this.eventType = null;
		this.comboBoxModel.insertElementAt(null, 5);
		this.verifyEvent(ADD, 8);
		
		this.event = null;
		this.eventType = null;
		this.comboBoxModel.removeElementAt(5);
		this.verifyEvent(REMOVE, 8);
		
		this.event = null;
		this.eventType = null;
		this.comboBoxModel.removeElementAt(4);
		this.verifyEvent(REMOVE, 7);
	}
	
	public void testSetSelectedItem() {
		assertTrue(this.comboBoxModel.getSelectedItem().equals("A"));
		assertTrue(this.extendedComboBoxModel.getSelectedItem().equals("A"));
		
		this.comboBoxModel.setSelectedItem(this.comboBoxModel.getElementAt(1));
		assertTrue(this.comboBoxModel.getSelectedItem().equals("B"));
		assertTrue(this.extendedComboBoxModel.getSelectedItem().equals("B"));
		
		this.extendedComboBoxModel.setSelectedItem("Z");
		assertTrue(this.comboBoxModel.getSelectedItem().equals("Z"));
		assertTrue(this.extendedComboBoxModel.getSelectedItem().equals("Z"));
	}
	
	public void testSelectionChangeEvent() {
		this.extendedComboBoxModel.addListDataListener(this.buildListener());
		
		this.event = null;
		this.eventType = null;
		this.comboBoxModel.setSelectedItem(this.comboBoxModel.getElementAt(3));
		this.verifyEvent(CHANGE, -1);
		
		this.event = null;
		this.eventType = null;
		this.extendedComboBoxModel.setSelectedItem(this.extendedComboBoxModel.getElementAt(0));
		this.verifyEvent(CHANGE, -1);
	}
	
	private void verifyEvent(String type, int index0) {
		this.verifyEvent(type);
		assertEquals(index0, this.event.getIndex0());
		assertEquals(index0, this.event.getIndex1());
	}
	
	private void verifyEvent(String type) {
		assertEquals(type, this.eventType);
		assertEquals(this.extendedComboBoxModel, this.event.getSource());
	}
}
