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
package org.eclipse.persistence.tools.workbench.test.uitools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.SimpleDisplayable;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;

public class SimpleDisplayableTests extends TestCase {
	boolean isNotified = false;

	public static Test suite() {
		return new TestSuite(SimpleDisplayableTests.class);
	}
	
	public SimpleDisplayableTests(String name) {
		super(name);
	}

	public void testDisplayString() {
		Displayable d;
		d = new SimpleDisplayable("foo");
		assertEquals("foo", d.displayString());
		d = new SimpleDisplayable(true);
		assertEquals("true", d.displayString());
		d = new SimpleDisplayable('c');
		assertEquals("c", d.displayString());
		d = new SimpleDisplayable((byte) 55);
		assertEquals("55", d.displayString());
		d = new SimpleDisplayable((short) 55);
		assertEquals("55", d.displayString());
		d = new SimpleDisplayable(55);
		assertEquals("55", d.displayString());
		d = new SimpleDisplayable(55L);
		assertEquals("55", d.displayString());
		d = new SimpleDisplayable(55.5);
		assertEquals("55.5", d.displayString());
		d = new SimpleDisplayable(55.5D);
		assertEquals("55.5", d.displayString());
	}

	public void testIconKey() {
		Displayable d = new SimpleDisplayable("foo");
		assertNull(d.icon());
	}

	public void testChangeNotification() {
		SimpleDisplayable sd = new SimpleDisplayable("foo");
		PropertyChangeListener listener = this.buildListener();

		this.isNotified = false;
		sd.addPropertyChangeListener(Displayable.DISPLAY_STRING_PROPERTY, listener);
		sd.setObject("bar");
		assertTrue(this.isNotified);

		this.isNotified = false;
		sd.setObject("bar");
		assertFalse(this.isNotified);		// the display string did not change
		sd.removePropertyChangeListener(Displayable.DISPLAY_STRING_PROPERTY, listener);

		this.isNotified = false;
		sd.addPropertyChangeListener(Displayable.ICON_PROPERTY, listener);
		sd.setObject("baz");
		assertFalse(this.isNotified);		// the icon does not change
		sd.removePropertyChangeListener(Displayable.ICON_PROPERTY, listener);

		// now, test with a custom wrapper
		sd = this.buildCustomDisplayableWrapper("foo");
		this.isNotified = false;
		sd.addPropertyChangeListener(Displayable.ICON_PROPERTY, listener);
		sd.setObject("bazoo");
		assertTrue(this.isNotified);		// now the icon does change
		sd.removePropertyChangeListener(Displayable.ICON_PROPERTY, listener);

	}

	private PropertyChangeListener buildListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				SimpleDisplayableTests.this.isNotified = true;
			}
		};
	}

	private SimpleDisplayable buildCustomDisplayableWrapper(String string) {
		return new SimpleDisplayable(string) {
			public Icon icon() {
				return new EmptyIcon(((String) this.object).length());
			}
		};
	}

}
