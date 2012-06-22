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
package org.eclipse.persistence.tools.workbench.test.uitools.app.adapters;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.uitools.PreferencesTestCase;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencesCollectionValueModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;


public class PreferencesCollectionValueModelTests extends PreferencesTestCase {
	private Map expectedValues;
	private PropertyValueModel nodeHolder;
	PreferencesCollectionValueModel preferencesAdapter;
	CollectionChangeEvent event;
	CollectionChangeListener listener;
	private PropertyChangeListener itemListener;
	boolean listenerRemoved = false;
	private static final String KEY_NAME_1 = "foo 1";
	private static final String KEY_NAME_2 = "foo 2";
	private static final String KEY_NAME_3 = "foo 3";
	private static final String STRING_VALUE_1 = "original string value 1";
	private static final String STRING_VALUE_2 = "original string value 2";
	private static final String STRING_VALUE_3 = "original string value 3";

	public static Test suite() {
		return new TestSuite(PreferencesCollectionValueModelTests.class);
	}
	
	public PreferencesCollectionValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.expectedValues = new HashMap();
		this.testNode.put(KEY_NAME_1, STRING_VALUE_1);	this.expectedValues.put(KEY_NAME_1, STRING_VALUE_1);
		this.testNode.put(KEY_NAME_2, STRING_VALUE_2);	this.expectedValues.put(KEY_NAME_2, STRING_VALUE_2);
		this.testNode.put(KEY_NAME_3, STRING_VALUE_3);	this.expectedValues.put(KEY_NAME_3, STRING_VALUE_3);

		this.nodeHolder = new SimplePropertyValueModel(this.testNode);
		this.preferencesAdapter = new PreferencesCollectionValueModel(this.nodeHolder);
		this.listener = this.buildCollectionChangeListener();
		this.itemListener = this.buildItemListener();
		this.preferencesAdapter.addCollectionChangeListener(ValueModel.VALUE, this.listener);
		this.event = null;
	}

	private CollectionChangeListener buildCollectionChangeListener() {
		return new CollectionChangeListener() {
			public void collectionChanged(CollectionChangeEvent e) {
				this.logEvent(e);
			}
			public void itemsAdded(CollectionChangeEvent e) {
				this.logEvent(e);
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				this.logEvent(e);
			}
			private void logEvent(CollectionChangeEvent e) {
				if (PreferencesCollectionValueModelTests.this.event != null) {
					throw new IllegalStateException("unexpected this.event: " + e);
				}
				PreferencesCollectionValueModelTests.this.event = e;
			}
		};
	}

	private PropertyChangeListener buildItemListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				throw new IllegalStateException("unexpected this.event: " + e);
			}
		};
	}

	public void testSubjectHolder() throws Exception {
		this.verifyAdapter(this.preferencesAdapter);
		assertNull(this.event);

		String ANOTHER_KEY_NAME_1 = "another key 1";
		String ANOTHER_KEY_NAME_2 = "another key 2";
		String ANOTHER_KEY_NAME_3 = "another key 3";
		String ANOTHER_STRING_VALUE_1 = "another string value 1";
		String ANOTHER_STRING_VALUE_2 = "another string value 2";
		String ANOTHER_STRING_VALUE_3 = "another string value 3";
		Preferences anotherNode = this.classNode.node("another test node");
		this.expectedValues.clear();
		anotherNode.put(ANOTHER_KEY_NAME_1, ANOTHER_STRING_VALUE_1);	this.expectedValues.put(ANOTHER_KEY_NAME_1, ANOTHER_STRING_VALUE_1);
		anotherNode.put(ANOTHER_KEY_NAME_2, ANOTHER_STRING_VALUE_2);	this.expectedValues.put(ANOTHER_KEY_NAME_2, ANOTHER_STRING_VALUE_2);
		anotherNode.put(ANOTHER_KEY_NAME_3, ANOTHER_STRING_VALUE_3);	this.expectedValues.put(ANOTHER_KEY_NAME_3, ANOTHER_STRING_VALUE_3);

		this.nodeHolder.setValue(anotherNode);
		// collectionChanged does not pass any items in the this.event
		this.verifyEvent(Collections.EMPTY_MAP);
		this.verifyAdapter(this.preferencesAdapter);
		
		this.event = null;
		this.expectedValues.clear();
		this.nodeHolder.setValue(null);
		this.verifyEvent(this.expectedValues);
		assertFalse(((Iterator) this.preferencesAdapter.getValue()).hasNext());
		
		this.event = null;
		this.nodeHolder.setValue(this.testNode);
		this.verifyEvent(Collections.EMPTY_MAP);
		this.expectedValues.clear();
		this.expectedValues.put(KEY_NAME_1, STRING_VALUE_1);
		this.expectedValues.put(KEY_NAME_2, STRING_VALUE_2);
		this.expectedValues.put(KEY_NAME_3, STRING_VALUE_3);
		this.verifyAdapter(this.preferencesAdapter);
	}

	public void testAddPreference() throws Exception {
		this.verifyAdapter(this.preferencesAdapter);
		assertNull(this.event);

		String ANOTHER_KEY_NAME = "another key";
		String ANOTHER_STRING_VALUE = "another string value";
		this.testNode.put(ANOTHER_KEY_NAME, ANOTHER_STRING_VALUE);
		this.waitForEventQueueToClear();
		Map expectedItems = new HashMap();
		expectedItems.put(ANOTHER_KEY_NAME, ANOTHER_STRING_VALUE);
		this.verifyEvent(expectedItems);
		this.expectedValues.put(ANOTHER_KEY_NAME, ANOTHER_STRING_VALUE);
		this.verifyAdapter(this.preferencesAdapter);
	}

	public void testRemovePreference() throws Exception {
		this.verifyAdapter(this.preferencesAdapter);
		assertNull(this.event);

		this.testNode.remove(KEY_NAME_2);
		this.waitForEventQueueToClear();

		assertNotNull(this.event);
		assertEquals(this.preferencesAdapter, this.event.getSource());
		assertEquals(ValueModel.VALUE, this.event.getCollectionName());
		assertEquals(1, this.event.size());
		assertEquals(KEY_NAME_2, ((PreferencePropertyValueModel) this.event.items().next()).getKey());

		this.expectedValues.remove(KEY_NAME_2);
		this.verifyAdapter(this.preferencesAdapter);
	}

	public void testChangePreference() throws Exception {
		this.verifyAdapter(this.preferencesAdapter);
		assertNull(this.event);

		String DIFFERENT = "something completely different";
		this.testNode.put(KEY_NAME_2, DIFFERENT);
		this.waitForEventQueueToClear();

		assertNull(this.event);

		this.expectedValues.put(KEY_NAME_2, DIFFERENT);
		this.verifyAdapter(this.preferencesAdapter);
	}

	public void testGetValue() throws Exception {
		this.verifyNode(this.testNode);
		this.verifyAdapter(this.preferencesAdapter);
	}

	/**
	 * test a situation where
	 * - we are listening to the node when it gets removed from the preferences "repository"
	 * - we get notification that it has been removed
	 * - we try to remove our this.listener
	 * - the node will throw an IllegalStateException - the adapter should handle it OK...
	 */
	public void testRemoveNode() throws Exception {
		assertTrue(this.preferencesAdapter.hasAnyCollectionChangeListeners(ValueModel.VALUE));

		Preferences parent = this.testNode.parent();
		parent.addNodeChangeListener(this.buildParentNodeChangeListener());
		this.testNode.removeNode();
		this.testNode.flush();		// this seems to be required for the this.event to trigger...
		this.waitForEventQueueToClear();

		assertTrue(this.listenerRemoved);
		assertFalse(this.preferencesAdapter.hasAnyCollectionChangeListeners(ValueModel.VALUE));
	}

	private NodeChangeListener buildParentNodeChangeListener() {
		return new NodeChangeListener() {
			public void childAdded(NodeChangeEvent e) {
				throw new IllegalStateException("unexpected this.event: " + e);
			}
			public void childRemoved(NodeChangeEvent e) {
				if (e.getChild() == PreferencesCollectionValueModelTests.this.testNode) {
					PreferencesCollectionValueModelTests.this.preferencesAdapter.removeCollectionChangeListener(ValueModel.VALUE, PreferencesCollectionValueModelTests.this.listener);
					// this line of code will not execute if the line above triggers an exception
					PreferencesCollectionValueModelTests.this.listenerRemoved = true;
				}
			}
		};
	}

	public void testHasListeners() throws Exception {
		assertTrue(this.preferencesAdapter.hasAnyCollectionChangeListeners(ValueModel.VALUE));
		assertTrue(this.nodeHasAnyPrefListeners(this.testNode));
		this.preferencesAdapter.removeCollectionChangeListener(ValueModel.VALUE, this.listener);
		assertFalse(this.nodeHasAnyPrefListeners(this.testNode));
		assertFalse(this.preferencesAdapter.hasAnyCollectionChangeListeners(ValueModel.VALUE));

		CollectionChangeListener listener2 = this.buildCollectionChangeListener();
		this.preferencesAdapter.addCollectionChangeListener(listener2);
		assertTrue(this.preferencesAdapter.hasAnyCollectionChangeListeners(ValueModel.VALUE));
		assertTrue(this.nodeHasAnyPrefListeners(this.testNode));
		this.preferencesAdapter.removeCollectionChangeListener(listener2);
		assertFalse(this.nodeHasAnyPrefListeners(this.testNode));
		assertFalse(this.preferencesAdapter.hasAnyCollectionChangeListeners(ValueModel.VALUE));
	}

	private void verifyEvent(Map items) {
		assertNotNull(this.event);
		assertEquals(this.preferencesAdapter, this.event.getSource());
		assertEquals(ValueModel.VALUE, this.event.getCollectionName());
		assertEquals(items.size(), this.event.size());
		this.verifyItems(items, this.event.items());
	}

	private void verifyNode(Preferences node) throws Exception {
		String[] keys = node.keys();
		assertEquals(this.expectedValues.size(), keys.length);
		for (int i = 0; i < keys.length; i++) {
			assertEquals(this.expectedValues.get(keys[i]), node.get(keys[i], "<missing preference>"));
		}
	}

	private void verifyAdapter(PreferencesCollectionValueModel cvm) {
		assertEquals(this.expectedValues.size(), cvm.size());
		this.verifyItems(this.expectedValues, (Iterator) cvm.getValue());
	}

	private void verifyItems(Map expected, Iterator stream) {
		while (stream.hasNext()) {
			PreferencePropertyValueModel model = (PreferencePropertyValueModel) stream.next();
			model.addPropertyChangeListener(ValueModel.VALUE, this.itemListener);
			assertEquals(expected.get(model.getKey()), model.getValue());
			model.removePropertyChangeListener(ValueModel.VALUE, this.itemListener);
		}
	}

	private boolean nodeHasAnyPrefListeners(Preferences node) throws Exception {
		PreferenceChangeListener[] prefListeners = (PreferenceChangeListener[]) ClassTools.getFieldValue(node, "prefListeners");
		return prefListeners.length > 0;
	}

}
