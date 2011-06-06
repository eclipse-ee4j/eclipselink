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
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.Document;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


public class DocumentAdapterTests extends TestCase {
	private PropertyValueModel stringHolder;
	Document documentAdapter;
	boolean eventFired;

	public static Test suite() {
		return new TestSuite(DocumentAdapterTests.class);
	}
	
	public DocumentAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.stringHolder = new SimplePropertyValueModel("0123456789");
		this.documentAdapter = new DocumentAdapter(this.stringHolder);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testRemove() throws Exception {
		this.eventFired = false;
		this.documentAdapter.addDocumentListener(new TestDocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				DocumentAdapterTests.this.eventFired = true;
				assertEquals(EventType.REMOVE, e.getType());
				assertEquals(DocumentAdapterTests.this.documentAdapter, e.getDocument());
				// this will be the removal of "23456"
				assertEquals(2, e.getOffset());
				assertEquals(5, e.getLength());
			}
		});
		this.documentAdapter.remove(2, 5);
		assertTrue(this.eventFired);
		assertEquals("01789", this.stringHolder.getValue());
	}

	public void testInsert() throws Exception {
		this.eventFired = false;
		this.documentAdapter.addDocumentListener(new TestDocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				DocumentAdapterTests.this.eventFired = true;
				assertEquals(EventType.INSERT, e.getType());
				assertEquals(DocumentAdapterTests.this.documentAdapter, e.getDocument());
				// this will be the insert of "xxxxxx"
				assertEquals(2, e.getOffset());
				assertEquals(5, e.getLength());
			}
		});
		this.documentAdapter.insertString(2, "xxxxx", null);
		assertTrue(this.eventFired);
		assertEquals("01xxxxx23456789", this.stringHolder.getValue());
	}

	public void testSetValue() throws Exception {
		this.eventFired = false;
		this.documentAdapter.addDocumentListener(new TestDocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				DocumentAdapterTests.this.eventFired = true;
				assertEquals(EventType.INSERT, e.getType());
				assertEquals(DocumentAdapterTests.this.documentAdapter, e.getDocument());
				// this will be the insert of "foo"
				assertEquals(0, e.getOffset());
				assertEquals(3, e.getLength());
			}
			public void removeUpdate(DocumentEvent e) {
				assertEquals(EventType.REMOVE, e.getType());
				assertEquals(DocumentAdapterTests.this.documentAdapter, e.getDocument());
				// this will be the removal of "0123456789"
				assertEquals(0, e.getOffset());
				assertEquals(10, e.getLength());
			}
		});
		assertEquals("0123456789", this.documentAdapter.getText(0, this.documentAdapter.getLength()));
		this.stringHolder.setValue("foo");
		assertTrue(this.eventFired);
		assertEquals("foo", this.documentAdapter.getText(0, this.documentAdapter.getLength()));
	}

	public void testHasListeners() throws Exception {
		SimplePropertyValueModel localStringHolder = (SimplePropertyValueModel) this.stringHolder;
		assertFalse(localStringHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(this.documentAdapter);

		DocumentListener listener = new TestDocumentListener();
		this.documentAdapter.addDocumentListener(listener);
		assertTrue(localStringHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasListeners(this.documentAdapter);

		this.documentAdapter.removeDocumentListener(listener);
		assertFalse(localStringHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(this.documentAdapter);
	}

	private void verifyHasNoListeners(Object document) throws Exception {
		Object delegate = ClassTools.getFieldValue(document, "delegate");
		Object[] listeners = (Object[]) ClassTools.invokeMethod(delegate, "getDocumentListeners");
		assertEquals(0, listeners.length);
	}

	private void verifyHasListeners(Object document) throws Exception {
		Object delegate = ClassTools.getFieldValue(document, "delegate");
		Object[] listeners = (Object[]) ClassTools.invokeMethod(delegate, "getDocumentListeners");
		assertFalse(listeners.length == 0);
	}


private class TestDocumentListener implements DocumentListener {
	public void changedUpdate(DocumentEvent e) {
		fail("unexpected event");
	}
	public void insertUpdate(DocumentEvent e) {
		fail("unexpected event");
	}
	public void removeUpdate(DocumentEvent e) {
		fail("unexpected event");
	}
}

}
