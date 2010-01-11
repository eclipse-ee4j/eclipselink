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
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class ObjectListSelectionModelTests extends TestCase {
	private DefaultListModel listModel;
	private ObjectListSelectionModel selectionModel;

	public static Test suite() {
		return new TestSuite(ObjectListSelectionModelTests.class);
	}
	
	public ObjectListSelectionModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.listModel = this.buildListModel();
		this.selectionModel = this.buildSelectionModel(this.listModel);
	}

	private DefaultListModel buildListModel() {
		DefaultListModel lm = new DefaultListModel();
		lm.addElement("foo");
		lm.addElement("bar");
		lm.addElement("baz");
		return lm;
	}

	private ObjectListSelectionModel buildSelectionModel(ListModel lm) {
		return new ObjectListSelectionModel(lm);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testListDataListener() {
		this.selectionModel.addListSelectionListener(this.buildListSelectionListener());
		this.selectionModel.setSelectionInterval(0, 0);
		assertEquals("foo", this.selectionModel.getSelectedValue());
		this.listModel.set(0, "jar");
		assertEquals("jar", this.selectionModel.getSelectedValue());
	}

	public void testGetSelectedValue() {
		this.selectionModel.setSelectionInterval(0, 0);
		assertEquals("foo", this.selectionModel.getSelectedValue());
	}

	public void testGetSelectedValues() {
		this.selectionModel.setSelectionInterval(0, 0);
		this.selectionModel.addSelectionInterval(2, 2);
		assertEquals(2, this.selectionModel.getSelectedValues().length);
		assertTrue(CollectionTools.contains(this.selectionModel.getSelectedValues(), "foo"));
		assertTrue(CollectionTools.contains(this.selectionModel.getSelectedValues(), "baz"));
	}

	public void testSetSelectedValue() {
		this.selectionModel.setSelectedValue("foo");
		assertEquals(0, this.selectionModel.getMinSelectionIndex());
		assertEquals(0, this.selectionModel.getMaxSelectionIndex());
	}

	public void testSetSelectedValues() {
		this.selectionModel.setSelectedValues(new Object[] {"foo", "baz"});
		assertEquals(0, this.selectionModel.getMinSelectionIndex());
		assertEquals(2, this.selectionModel.getMaxSelectionIndex());
	}

	public void testAddSelectedValue() {
		this.listModel.addElement("joo");
		this.listModel.addElement("jar");
		this.listModel.addElement("jaz");
		this.selectionModel.setSelectedValue("foo");
		this.selectionModel.addSelectedValue("jaz");
		assertEquals(0, this.selectionModel.getMinSelectionIndex());
		assertEquals(5, this.selectionModel.getMaxSelectionIndex());
		assertTrue(this.selectionModel.isSelectedIndex(0));
		assertFalse(this.selectionModel.isSelectedIndex(1));
		assertFalse(this.selectionModel.isSelectedIndex(2));
		assertFalse(this.selectionModel.isSelectedIndex(3));
		assertFalse(this.selectionModel.isSelectedIndex(4));
		assertTrue(this.selectionModel.isSelectedIndex(5));
	}

	public void testAddSelectedValues() {
		this.listModel.addElement("joo");
		this.listModel.addElement("jar");
		this.listModel.addElement("jaz");
		this.selectionModel.setSelectedValue("foo");
		this.selectionModel.addSelectedValues(new Object[] {"bar", "jar"});
		assertEquals(0, this.selectionModel.getMinSelectionIndex());
		assertEquals(4, this.selectionModel.getMaxSelectionIndex());
		assertTrue(this.selectionModel.isSelectedIndex(0));
		assertTrue(this.selectionModel.isSelectedIndex(1));
		assertFalse(this.selectionModel.isSelectedIndex(2));
		assertFalse(this.selectionModel.isSelectedIndex(3));
		assertTrue(this.selectionModel.isSelectedIndex(4));
		assertFalse(this.selectionModel.isSelectedIndex(5));
	}

	public void testRemoveSelectedValue() {
		this.listModel.addElement("joo");
		this.listModel.addElement("jar");
		this.listModel.addElement("jaz");
		this.selectionModel.setSelectedValues(new Object[] {"foo", "baz", "jar"});
		this.selectionModel.removeSelectedValue("jar");
		assertEquals(0, this.selectionModel.getMinSelectionIndex());
		assertEquals(2, this.selectionModel.getMaxSelectionIndex());
		assertTrue(this.selectionModel.isSelectedIndex(0));
		assertFalse(this.selectionModel.isSelectedIndex(1));
		assertTrue(this.selectionModel.isSelectedIndex(2));
		assertFalse(this.selectionModel.isSelectedIndex(3));
		assertFalse(this.selectionModel.isSelectedIndex(4));
		assertFalse(this.selectionModel.isSelectedIndex(5));
	}

	public void testRemoveSelectedValues() {
		this.listModel.addElement("joo");
		this.listModel.addElement("jar");
		this.listModel.addElement("jaz");
		this.selectionModel.setSelectedValues(new Object[] {"foo", "baz", "joo", "jar"});
		this.selectionModel.removeSelectedValues(new Object[] {"foo", "joo"});
		assertEquals(2, this.selectionModel.getMinSelectionIndex());
		assertEquals(4, this.selectionModel.getMaxSelectionIndex());
		assertFalse(this.selectionModel.isSelectedIndex(0));
		assertFalse(this.selectionModel.isSelectedIndex(1));
		assertTrue(this.selectionModel.isSelectedIndex(2));
		assertFalse(this.selectionModel.isSelectedIndex(3));
		assertTrue(this.selectionModel.isSelectedIndex(4));
		assertFalse(this.selectionModel.isSelectedIndex(5));
	}

	public void testGetAnchorSelectedValue() {
		this.selectionModel.setAnchorSelectionIndex(1);
		assertEquals("bar", this.selectionModel.getAnchorSelectedValue());
	}

	public void testGetLeadSelectedValue() {
		this.selectionModel.setSelectedValue("bar");
		assertEquals("bar", this.selectionModel.getLeadSelectedValue());
		this.selectionModel.setSelectedValues(new Object[] {"foo", "baz"});
		assertEquals("baz", this.selectionModel.getLeadSelectedValue());
	}

	public void testGetMinMaxSelectedValue() {
		this.listModel.addElement("joo");
		this.listModel.addElement("jar");
		this.listModel.addElement("jaz");
		this.selectionModel.setSelectedValue("foo");
		this.selectionModel.addSelectedValues(new Object[] {"bar", "jar"});
		assertEquals("foo", this.selectionModel.getMinSelectedValue());
		assertEquals("jar", this.selectionModel.getMaxSelectedValue());
	}

	public void testValueIsSelected() {
		this.listModel.addElement("joo");
		this.listModel.addElement("jar");
		this.listModel.addElement("jaz");
		this.selectionModel.setSelectedValue("foo");
		this.selectionModel.addSelectedValues(new Object[] {"bar", "jar"});
		assertTrue(this.selectionModel.valueIsSelected("foo"));
		assertTrue(this.selectionModel.valueIsSelected("bar"));
		assertTrue(this.selectionModel.valueIsSelected("jar"));
		assertFalse(this.selectionModel.valueIsSelected("baz"));
	}

	public void testHasListeners() throws Exception {
		ListSelectionListener listener = this.buildListSelectionListener();
		assertEquals(0, this.listModel.getListDataListeners().length);
		this.selectionModel.addListSelectionListener(listener);
		assertEquals(1, this.listModel.getListDataListeners().length);
		this.selectionModel.removeListSelectionListener(listener);
		assertEquals(0, this.listModel.getListDataListeners().length);
	}

	private ListSelectionListener buildListSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// do nothing for now...
			}
		};
	}

}
