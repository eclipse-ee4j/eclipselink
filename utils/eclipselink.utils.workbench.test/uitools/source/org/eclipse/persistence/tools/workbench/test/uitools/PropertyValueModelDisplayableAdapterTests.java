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
package org.eclipse.persistence.tools.workbench.test.uitools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.PropertyValueModelDisplayableAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;


public class PropertyValueModelDisplayableAdapterTests 
	extends TestCase 
{
	private PropertyValueModel objectHolder;
	PropertyChangeEvent event;
	
	private Displayable displayableAdapter;
	PropertyChangeEvent displayStringEvent;
	PropertyChangeEvent iconEvent;
	
	public static Test suite() {
		return new TestSuite(PropertyValueModelDisplayableAdapterTests.class);
	}
	
	public PropertyValueModelDisplayableAdapterTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		this.objectHolder = new SimplePropertyValueModel("foo");
		this.displayableAdapter = this.buildDisplayableAdapter(this.objectHolder); 
	}
	
	private Displayable buildDisplayableAdapter(PropertyValueModel pvm) {
		return new PropertyValueModelDisplayableAdapter(pvm) {
			public String displayString(Object obj) {
				return (obj == null) ? "null" : ((String) obj).toUpperCase();
			}
			
			public Icon icon(Object obj) {
				return (obj == null) ? null : new ImageIcon(((String) obj).getBytes());
			}
		};
	}
	
	private PropertyChangeListener buildListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				PropertyValueModelDisplayableAdapterTests.this.event = e;
			}
		};
	}
	
	private PropertyChangeListener buildDisplayStringListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName() == Displayable.DISPLAY_STRING_PROPERTY) {
					PropertyValueModelDisplayableAdapterTests.this.displayStringEvent = e;
				}
			}
		};
	}
	
	private PropertyChangeListener buildIconListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName() == Displayable.ICON_PROPERTY) {
					PropertyValueModelDisplayableAdapterTests.this.iconEvent = e;
				}
			}
		};
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testDisplayString() {
		assertEquals("foo", this.objectHolder.getValue());
		assertEquals("FOO", this.displayableAdapter.displayString());
		
		this.objectHolder.setValue("bar");
		assertEquals("bar", this.objectHolder.getValue());
		assertEquals("BAR", this.displayableAdapter.displayString());
		
		this.objectHolder.setValue(null);
		assertNull(this.objectHolder.getValue());
		assertEquals("null", this.displayableAdapter.displayString());
		
		this.objectHolder.setValue("foo");
		assertEquals("foo", this.objectHolder.getValue());
		assertEquals("FOO", this.displayableAdapter.displayString());
	}
	
	public void testIcon() {
		assertEquals("foo", this.objectHolder.getValue());
		assertNotNull(this.displayableAdapter.icon());
	}
	
	public void testLazyListening() {
		PropertyChangeListener displayStringListener = this.buildDisplayStringListener();
		PropertyChangeListener iconListener = this.buildIconListener();
		
		assertTrue(((AbstractModel) this.objectHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
		
		this.displayableAdapter.addPropertyChangeListener(displayStringListener);
		assertTrue(((AbstractModel) this.objectHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.displayableAdapter.addPropertyChangeListener(iconListener);
		assertTrue(((AbstractModel) this.objectHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.displayableAdapter.removePropertyChangeListener(iconListener);
		assertTrue(((AbstractModel) this.objectHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.displayableAdapter.removePropertyChangeListener(displayStringListener);
		assertTrue(((AbstractModel) this.objectHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
		
		this.displayableAdapter.addPropertyChangeListener(Displayable.DISPLAY_STRING_PROPERTY, displayStringListener);
		assertTrue(((AbstractModel) this.objectHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.displayableAdapter.removePropertyChangeListener(Displayable.DISPLAY_STRING_PROPERTY, displayStringListener);
		assertTrue(((AbstractModel) this.objectHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
		
		this.displayableAdapter.addPropertyChangeListener(Displayable.ICON_PROPERTY, iconListener);
		assertTrue(((AbstractModel) this.objectHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.displayableAdapter.removePropertyChangeListener(Displayable.ICON_PROPERTY, iconListener);
		assertTrue(((AbstractModel) this.objectHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
	}
	
	public void testPropertyChange1() {
		this.objectHolder.addPropertyChangeListener(this.buildListener());
		this.displayableAdapter.addPropertyChangeListener(this.buildDisplayStringListener());
		this.displayableAdapter.addPropertyChangeListener(this.buildIconListener());
		this.verifyPropertyChanges();
	}
	
	public void testPropertyChange2() {
		this.objectHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildListener());
		this.displayableAdapter.addPropertyChangeListener(Displayable.DISPLAY_STRING_PROPERTY, this.buildDisplayStringListener());
		this.displayableAdapter.addPropertyChangeListener(Displayable.ICON_PROPERTY, this.buildIconListener());
		this.verifyPropertyChanges();
	}
	
	private void verifyPropertyChanges() {
		this.event = null;
		this.displayStringEvent = null;
		this.iconEvent = null;
		this.objectHolder.setValue("bar");
		this.verifyEvent(this.event, this.objectHolder, ValueModel.VALUE, "foo", "bar");
		this.verifyEvent(this.displayStringEvent, this.displayableAdapter, Displayable.DISPLAY_STRING_PROPERTY, "FOO", "BAR");
		assertNotNull(this.iconEvent);
		
		this.event = null;
		this.displayStringEvent = null;
		this.iconEvent = null;
		this.objectHolder.setValue("baz");
		this.verifyEvent(this.event, this.objectHolder, ValueModel.VALUE, "bar", "baz");
		this.verifyEvent(this.displayStringEvent, this.displayableAdapter, Displayable.DISPLAY_STRING_PROPERTY, "BAR", "BAZ");
		assertNotNull(this.iconEvent);
		
		this.event = null;
		this.displayStringEvent = null;
		this.iconEvent = null;
		this.objectHolder.setValue("Baz");
		this.verifyEvent(this.event, this.objectHolder, ValueModel.VALUE, "baz", "Baz");
		assertNull(this.displayStringEvent);
		assertNotNull(this.iconEvent);
		
		this.event = null;
		this.displayStringEvent = null;
		this.iconEvent = null;
		this.objectHolder.setValue(null);
		this.verifyEvent(this.event, this.objectHolder, ValueModel.VALUE, "Baz", null);
		this.verifyEvent(this.displayStringEvent, this.displayableAdapter, Displayable.DISPLAY_STRING_PROPERTY, "BAZ", "null");
		assertNotNull(this.iconEvent);
		
		this.event = null;
		this.displayStringEvent = null;
		this.iconEvent = null;
		this.objectHolder.setValue("foo");
		this.verifyEvent(this.event, this.objectHolder, ValueModel.VALUE, null, "foo");
		this.verifyEvent(this.displayStringEvent, this.displayableAdapter, Displayable.DISPLAY_STRING_PROPERTY, "null", "FOO");
		assertNotNull(this.iconEvent);
		
		this.event = null;
		this.displayStringEvent = null;
		this.iconEvent = null;
		this.objectHolder.setValue("foo");
		assertNull(this.event);
		assertNull(this.displayStringEvent);
		assertNull(this.iconEvent);	
	}
	
	private void verifyEvent(PropertyChangeEvent pce, Object source, String propertyName, Object oldValue, Object newValue) {
		assertEquals(source, pce.getSource());
		assertEquals(propertyName, pce.getPropertyName());
		assertEquals(oldValue, pce.getOldValue());
		assertEquals(newValue, pce.getNewValue());
	}
}
