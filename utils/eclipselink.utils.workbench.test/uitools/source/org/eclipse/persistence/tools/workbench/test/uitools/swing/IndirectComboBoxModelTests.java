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
import java.util.List;
import java.util.ListIterator;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeListIterator;


public class IndirectComboBoxModelTests 
	extends TestCase
{
	private List basicList;
	private List supplementalList;
	private boolean listIsExtended;
	private SimplePropertyValueModel selectedItemHolder;
	private SimplePropertyValueModel listSubjectHolder;
	ListDataEvent event;
	
	public static Test suite() {
		return new TestSuite(IndirectComboBoxModelTests.class);
	}
	
	public IndirectComboBoxModelTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.basicList = this.buildBasicList();
		this.supplementalList = this.buildSupplementalList();
		this.selectedItemHolder = new SimplePropertyValueModel(this.basicList.get(0));
		this.listSubjectHolder = new SimplePropertyValueModel(this);
	}
	
	private List buildBasicList() {
		List list = new ArrayList();
		list.add("One fish");
		list.add("Two fish");
		list.add("Red fish");
		list.add("Blue fish");
		return list;
	}
	
	private List buildSupplementalList() {
		List list = new ArrayList();
		list.add("Thing one");
		list.add("Thing two");
		return list;
	}
	
	ListIterator listValue() {
		ListIterator listValue = this.basicList.listIterator();
		
		if (this.listIsExtended) {
			ListIterator supplementalIterator = this.supplementalList.listIterator();
			listValue = new CompositeListIterator(listValue, supplementalIterator);
		}
		
		return listValue;
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testListeners() throws Exception {
		assertFalse(this.selectedItemHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		
		ComboBoxModel comboBoxModel = this.buildComboBoxModel();
		assertFalse(this.selectedItemHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(comboBoxModel);
		
		ListDataListener listener = this.buildListDataListener();
		comboBoxModel.addListDataListener(listener);
		assertTrue(this.selectedItemHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasListeners(comboBoxModel);
		
		comboBoxModel.removeListDataListener(listener);
		assertFalse(this.selectedItemHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(comboBoxModel);
	}
	
	public void testGetSelectedItem() {
		ComboBoxModel comboBoxModel = this.buildComboBoxModel();
		assertTrue(this.selectedItemHolder.getValue() == this.basicList.get(0));
		assertTrue(comboBoxModel.getSelectedItem() == this.basicList.get(0));
		
		// make sure setting the list subject doesn't affect the selected item
		this.listSubjectHolder.setValue(null);
		assertTrue(comboBoxModel.getSelectedItem() == this.basicList.get(0));
		this.listSubjectHolder.setValue(this);
		
		comboBoxModel.addListDataListener(this.buildListDataListener());
		this.selectedItemHolder.setValue(this.basicList.get(1));
		assertTrue(this.selectedItemHolder.getValue() == this.basicList.get(1));
		assertTrue(comboBoxModel.getSelectedItem() == this.basicList.get(1));
		assertTrue(this.event != null);
		assertTrue(this.event.getType() == ListDataEvent.CONTENTS_CHANGED);
		assertTrue(this.event.getIndex0() == -1);
		assertTrue(this.event.getIndex1() == -1);
	}
	
	public void testGetSize() {
		ComboBoxModel comboBoxModel = this.buildComboBoxModel();
		assertTrue(this.listIsExtended == false);
		assertTrue(comboBoxModel.getSize() == this.basicList.size());
		
		this.listIsExtended = true;
		assertTrue(comboBoxModel.getSize() == this.basicList.size() + this.supplementalList.size());
	
		this.listIsExtended = false;
		assertTrue(comboBoxModel.getSize() == this.basicList.size());
		
		this.listSubjectHolder.setValue(null);
		assertTrue(comboBoxModel.getSize() == 0);
		
		this.listSubjectHolder.setValue(this);
		assertTrue(comboBoxModel.getSize() == this.basicList.size());
	}
	
	public void testGetElements() {
		ComboBoxModel comboBoxModel = this.buildComboBoxModel();
		this.verifyEquivalent(comboBoxModel, this.basicList);
		
		this.listIsExtended = true;
		List extendedList = new ArrayList(this.basicList);
		extendedList.addAll(this.supplementalList);
		this.verifyEquivalent(comboBoxModel, extendedList);
		
		this.listIsExtended = false;
		this.verifyEquivalent(comboBoxModel, this.basicList);
		
		this.listSubjectHolder.setValue(null);
		this.verifyEquivalent(comboBoxModel, new ArrayList());
		
		this.listSubjectHolder.setValue(this);
		this.verifyEquivalent(comboBoxModel, this.basicList);
	}
	
	private ComboBoxModel buildComboBoxModel() {
		return new IndirectComboBoxModel(this.selectedItemHolder, this.listSubjectHolder) {
			protected ListIterator listValueFromSubject(Object subject) {
				return ((IndirectComboBoxModelTests) subject).listValue();
			}
		};
	}
	
	private ListDataListener buildListDataListener() {
		return new ListDataListener() {
			public void intervalAdded(ListDataEvent e) {
				IndirectComboBoxModelTests.this.event = e;
			}
			
			public void intervalRemoved(ListDataEvent e) {
				IndirectComboBoxModelTests.this.event = e;
			}
			
			public void contentsChanged(ListDataEvent e) {
				IndirectComboBoxModelTests.this.event = e;
			}
		};
	}

	private void verifyHasNoListeners(ComboBoxModel comboBoxModel) throws Exception {
		boolean hasNoListeners = ((Boolean) ClassTools.invokeMethod(comboBoxModel, "hasNoListDataListeners")).booleanValue();
		assertTrue(hasNoListeners);
	}

	private void verifyHasListeners(ComboBoxModel comboBoxModel) throws Exception {
		boolean hasListeners = ((Boolean) ClassTools.invokeMethod(comboBoxModel, "hasListDataListeners")).booleanValue();
		assertTrue(hasListeners);
	}
	
	private void verifyEquivalent(ComboBoxModel comboBoxModel, List list) {
		assertTrue(comboBoxModel.getSize() == list.size());
		
		for (int i = 0; i < list.size(); i ++) {
			assertEquals(comboBoxModel.getElementAt(i), list.get(i));
		}
	}
}
