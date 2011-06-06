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
package org.eclipse.persistence.tools.workbench.test.uitools.app.adapters;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.uitools.PreferencesTestCase;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;


public class PreferencePropertyValueModelTests extends PreferencesTestCase {
	private PropertyValueModel nodeHolder;
	PreferencePropertyValueModel preferenceAdapter;
	PropertyChangeEvent event;
	PropertyChangeListener listener;
	boolean listenerRemoved = false;
	PreferenceChangeEvent preferenceEvent;
	private static final String KEY_NAME = "foo";
	private static final String STRING_VALUE = "original string value";

	public static Test suite() {
		return new TestSuite(PreferencePropertyValueModelTests.class);
	}
	
	public PreferencePropertyValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.testNode.put(KEY_NAME, STRING_VALUE);

		this.nodeHolder = new SimplePropertyValueModel(this.testNode);
		this.preferenceAdapter = new PreferencePropertyValueModel(this.nodeHolder, KEY_NAME);
		this.listener = this.buildValueChangeListener();
		this.preferenceAdapter.addPropertyChangeListener(ValueModel.VALUE, this.listener);
		this.event = null;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private PropertyChangeListener buildValueChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (PreferencePropertyValueModelTests.this.event != null) {
					throw new IllegalStateException("unexpected this.event: " + e);
				}
				PreferencePropertyValueModelTests.this.event = e;
			}
		};
	}

	public void testSubjectHolder() throws Exception {
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
		assertNull(this.event);

		String ANOTHER_STRING_VALUE = "some other value";
		Preferences anotherNode = this.classNode.node("another test node");
		anotherNode.put(KEY_NAME, ANOTHER_STRING_VALUE);

		this.nodeHolder.setValue(anotherNode);
		this.verifyEvent(STRING_VALUE, ANOTHER_STRING_VALUE);
		assertEquals(ANOTHER_STRING_VALUE, this.preferenceAdapter.getValue());
		
		this.event = null;
		this.nodeHolder.setValue(null);
		this.verifyEvent(ANOTHER_STRING_VALUE, null);
		assertNull(this.preferenceAdapter.getValue());
		
		this.event = null;
		this.nodeHolder.setValue(this.testNode);
		this.verifyEvent(null, STRING_VALUE);
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
	}

	public void testPreferenceChange() throws Exception {
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
		assertNull(this.event);

		this.testNode.put(KEY_NAME, STRING_VALUE + STRING_VALUE);
		this.waitForEventQueueToClear();
		this.verifyEvent(STRING_VALUE, STRING_VALUE + STRING_VALUE);
		assertEquals(STRING_VALUE + STRING_VALUE, this.preferenceAdapter.getValue());
		
		this.event = null;
		this.testNode.remove(KEY_NAME);
		this.waitForEventQueueToClear();
		this.verifyEvent(STRING_VALUE + STRING_VALUE, null);
		assertNull(this.preferenceAdapter.getValue());
		
		this.event = null;
		this.testNode.put(KEY_NAME, STRING_VALUE);
		this.waitForEventQueueToClear();
		this.verifyEvent(null, STRING_VALUE);
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
	}

	public void testGetValue() throws Exception {
		assertEquals(STRING_VALUE, this.testNode.get(KEY_NAME, "<missing preference>"));
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
	}

	public void testSetValue() throws Exception {
		String ANOTHER_STRING_VALUE = "some other value";
		this.preferenceAdapter.setValue(ANOTHER_STRING_VALUE);
		assertEquals(ANOTHER_STRING_VALUE, this.preferenceAdapter.getValue());
		assertEquals(ANOTHER_STRING_VALUE, this.testNode.get(KEY_NAME, "<missing preference>"));
	}

	public void testHasListeners() throws Exception {
		assertTrue(this.preferenceAdapter.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(this.nodeHasAnyPrefListeners(this.testNode));
		this.preferenceAdapter.removePropertyChangeListener(ValueModel.VALUE, this.listener);
		assertFalse(this.nodeHasAnyPrefListeners(this.testNode));
		assertFalse(this.preferenceAdapter.hasAnyPropertyChangeListeners(ValueModel.VALUE));

		PropertyChangeListener listener2 = this.buildValueChangeListener();
		this.preferenceAdapter.addPropertyChangeListener(listener2);
		assertTrue(this.preferenceAdapter.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(this.nodeHasAnyPrefListeners(this.testNode));
		this.preferenceAdapter.removePropertyChangeListener(listener2);
		assertFalse(this.nodeHasAnyPrefListeners(this.testNode));
		assertFalse(this.preferenceAdapter.hasAnyPropertyChangeListeners(ValueModel.VALUE));
	}

	public void testRemoveAndReAddPreference() throws Exception {
		assertEquals(STRING_VALUE, this.testNode.get(KEY_NAME, null));
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
		assertNull(this.event);

		// remove the preference entirely...
		this.testNode.remove(KEY_NAME);
		this.waitForEventQueueToClear();
		assertNull(this.testNode.get(KEY_NAME, null));
		this.verifyEvent(STRING_VALUE, null);
		assertNull(this.preferenceAdapter.getValue());

		// ...then re-add it with the same key
		this.event = null;
		this.testNode.put(KEY_NAME, STRING_VALUE);
		this.waitForEventQueueToClear();
		assertEquals(STRING_VALUE, this.testNode.get(KEY_NAME, null));
		this.verifyEvent(null, STRING_VALUE);
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
	}

	public void testDefaultValue() throws Exception {
		// rebuild the adapter with a default value
		String DEFAULT_VALUE = "default value";
		this.preferenceAdapter.removePropertyChangeListener(ValueModel.VALUE, this.listener);
		this.preferenceAdapter = new PreferencePropertyValueModel(this.nodeHolder, KEY_NAME, DEFAULT_VALUE);
		this.preferenceAdapter.addPropertyChangeListener(ValueModel.VALUE, this.listener);

		assertEquals(STRING_VALUE, this.testNode.get(KEY_NAME, null));
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
		assertNull(this.event);

		// remove the preference entirely...
		this.testNode.remove(KEY_NAME);
		this.waitForEventQueueToClear();
		assertNull(this.testNode.get(KEY_NAME, null));
		this.verifyEvent(STRING_VALUE, DEFAULT_VALUE);
		assertEquals(DEFAULT_VALUE, this.preferenceAdapter.getValue());

		// ...then re-add it with the same key
		this.event = null;
		this.testNode.put(KEY_NAME, STRING_VALUE);
		this.waitForEventQueueToClear();
		assertEquals(STRING_VALUE, this.testNode.get(KEY_NAME, null));
		this.verifyEvent(DEFAULT_VALUE, STRING_VALUE);
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
	}

	public void testUnsynchronizedValue() throws Exception {
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
		assertNull(this.event);

		// remove the this.listener so the adapter no longer listens to the preference
		this.preferenceAdapter.removePropertyChangeListener(ValueModel.VALUE, this.listener);

		this.testNode.put(KEY_NAME, STRING_VALUE + STRING_VALUE);
		this.waitForEventQueueToClear();
		// no this.event should have been fired...
		assertNull(this.event);
		// ...and the adapter's value should be null
		assertNull(this.preferenceAdapter.getValue());
		
		this.testNode.remove(KEY_NAME);
		this.waitForEventQueueToClear();
		assertNull(this.event);
		assertNull(this.preferenceAdapter.getValue());
		
		this.testNode.put(KEY_NAME, STRING_VALUE);
		this.waitForEventQueueToClear();
		assertNull(this.event);
		assertNull(this.preferenceAdapter.getValue());

		// add the this.listener so the adapter synchs
		this.preferenceAdapter.addPropertyChangeListener(ValueModel.VALUE, this.listener);
		assertEquals(STRING_VALUE, this.preferenceAdapter.getValue());
	}

	public void testIntegerPreference() throws Exception {
		// stop listening to the node and convert it to an integer
		this.preferenceAdapter.removePropertyChangeListener(ValueModel.VALUE, this.listener);
		this.testNode.putInt(KEY_NAME, 123);
		this.preferenceAdapter.setConverter(BidiStringConverter.INTEGER_CONVERTER);
		this.preferenceAdapter.addPropertyChangeListener(ValueModel.VALUE, this.listener);
		assertEquals(new Integer(123), this.preferenceAdapter.getValue());
		assertNull(this.event);

		this.testNode.putInt(KEY_NAME, 246);
		this.waitForEventQueueToClear();
		this.verifyEvent(new Integer(123), new Integer(246));
		assertEquals(new Integer(246), this.preferenceAdapter.getValue());
		
		this.event = null;
		this.testNode.remove(KEY_NAME);
		this.waitForEventQueueToClear();
		this.verifyEvent(new Integer(246), null);
		assertNull(this.preferenceAdapter.getValue());
		
		this.event = null;
		this.testNode.putInt(KEY_NAME, 123);
		this.waitForEventQueueToClear();
		this.verifyEvent(null, new Integer(123));
		assertEquals(new Integer(123), this.preferenceAdapter.getValue());
	}

	/**
	 * test a situation where
	 * - we are listening to the node when it gets removed from the preferences "repository"
	 * - we get notification that it has been removed
	 * - we try to remove our this.listener
	 * - the node will throw an IllegalStateException - the adapter should handle it OK...
	 */
	public void testRemoveNode() throws Exception {
		assertTrue(this.preferenceAdapter.hasAnyPropertyChangeListeners(ValueModel.VALUE));

		Preferences parent = this.testNode.parent();
		parent.addNodeChangeListener(this.buildParentNodeChangeListener());
		this.testNode.removeNode();
		this.testNode.flush();		// this seems to be required for the this.event to trigger...
		this.waitForEventQueueToClear();

		assertTrue(this.listenerRemoved);
		assertTrue(this.preferenceAdapter.hasNoPropertyChangeListeners(ValueModel.VALUE));
	}

	private NodeChangeListener buildParentNodeChangeListener() {
		return new NodeChangeListener() {
			public void childAdded(NodeChangeEvent e) {
				throw new IllegalStateException("unexpected this.event: " + e);
			}
			public void childRemoved(NodeChangeEvent e) {
				if (e.getChild() == PreferencePropertyValueModelTests.this.testNode) {
					PreferencePropertyValueModelTests.this.preferenceAdapter.removePropertyChangeListener(ValueModel.VALUE, PreferencePropertyValueModelTests.this.listener);
					// this line of code will not execute if the line above triggers an exception
					PreferencePropertyValueModelTests.this.listenerRemoved = true;
				}
			}
		};
	}

	public void testSetSameValue() {
		assertNull(this.event);
		assertNull(this.preferenceEvent);
		this.testNode.addPreferenceChangeListener(this.buildPreferenceChangeListener());

		String ANOTHER_STRING_VALUE = "some other value";
		this.preferenceAdapter.setValue(ANOTHER_STRING_VALUE);

		this.verifyEvent(STRING_VALUE, ANOTHER_STRING_VALUE);
		this.waitForEventQueueToClear();
		this.verifyPreferenceEvent(ANOTHER_STRING_VALUE);

		// now set to *same* value - nothing should happen...
		this.event = null;
		this.preferenceEvent = null;
		this.preferenceAdapter.setValue(ANOTHER_STRING_VALUE);

		assertNull(this.event);
		assertNull(this.preferenceEvent);
	}

	public void testSetSameValueForcePassThrough() throws Exception {
		assertNull(this.event);
		assertNull(this.preferenceEvent);

		this.preferenceAdapter.removePropertyChangeListener(ValueModel.VALUE, this.listener);
		this.preferenceAdapter = new AlwaysUpdatePreferencePropertyValueModel(this.nodeHolder, KEY_NAME);
		this.preferenceAdapter.addPropertyChangeListener(ValueModel.VALUE, this.listener);

		this.testNode.addPreferenceChangeListener(this.buildPreferenceChangeListener());

		String ANOTHER_STRING_VALUE = "some other value";
		this.preferenceAdapter.setValue(ANOTHER_STRING_VALUE);

		this.verifyEvent(STRING_VALUE, ANOTHER_STRING_VALUE);
		this.waitForEventQueueToClear();
		this.verifyPreferenceEvent(ANOTHER_STRING_VALUE);

		// now set to *same* value - only one this.event should fire
		this.event = null;
		this.preferenceEvent = null;
		this.preferenceAdapter.setValue(ANOTHER_STRING_VALUE);

		assertNull(this.event);
		this.waitForEventQueueToClear();
		this.verifyPreferenceEvent(ANOTHER_STRING_VALUE);
		assertNull(this.event);
	}

	private PreferenceChangeListener buildPreferenceChangeListener() {
		return new PreferenceChangeListener() {
			public void preferenceChange(PreferenceChangeEvent evt) {
				PreferencePropertyValueModelTests.this.preferenceEvent = evt;
			}
		};
	}

	private void verifyEvent(Object oldValue, Object newValue) {
		assertNotNull(this.event);
		assertEquals(this.preferenceAdapter, this.event.getSource());
		assertEquals(ValueModel.VALUE, this.event.getPropertyName());
		assertEquals(oldValue, this.event.getOldValue());
		assertEquals(newValue, this.event.getNewValue());
	}

	private void verifyPreferenceEvent(Object newValue) {
		assertNotNull(this.preferenceEvent);
		assertEquals(this.testNode, this.preferenceEvent.getSource());
		assertEquals(KEY_NAME, this.preferenceEvent.getKey());
		assertEquals(newValue, this.preferenceEvent.getNewValue());
		assertEquals(newValue, this.testNode.get(KEY_NAME, "<missing preference>"));
	}

	private boolean nodeHasAnyPrefListeners(Preferences node) throws Exception {
		PreferenceChangeListener[] prefListeners = (PreferenceChangeListener[]) ClassTools.getFieldValue(node, "prefListeners");
		return prefListeners.length > 0;
	}


/**
 * Use this adapter to test out always passing through the new value
 * to the preference.
 */
private class AlwaysUpdatePreferencePropertyValueModel extends PreferencePropertyValueModel {

	AlwaysUpdatePreferencePropertyValueModel(ValueModel preferencesHolder, String key) {
		super(preferencesHolder, key);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel#shouldSetPreference(java.lang.Object, java.lang.Object)
	 */
	protected boolean shouldSetPreference(Object oldValue, Object newValue) {
		return true;
	}

}

}
