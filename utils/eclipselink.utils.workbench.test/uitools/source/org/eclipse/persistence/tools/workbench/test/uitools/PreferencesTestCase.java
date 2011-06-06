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

import java.util.List;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * set up and tear down a test node for any subclass that
 * needs to test preferences-related stuff
 */
public abstract class PreferencesTestCase extends TestCase {
	protected Preferences classNode;
	public Preferences testNode;
	protected static final String TEST_NODE_NAME = "test node";

	public PreferencesTestCase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Preferences packageNode = Preferences.userNodeForPackage(this.getClass());
		this.classNode = packageNode.node(ClassTools.shortClassNameForObject(this));
		// clean out any leftover junk...
		if ((this.classNode.keys().length > 0) || (this.classNode.childrenNames().length > 0)) {
			this.classNode.removeNode();
			// ...and re-create the node
			this.classNode = packageNode.node(ClassTools.shortClassNameForObject(this));
		}
		this.testNode = this.classNode.node(TEST_NODE_NAME);
	}

	protected void tearDown() throws Exception {
		// wait for all the events to be delivered before tearing down
		this.waitForEventQueueToClear();
		Preferences node = this.classNode.parent();
		this.classNode.removeNode();
		while (this.nodeIsVestigial(node)) {
			Preferences parent = node.parent();
			node.removeNode();
			node = parent;
		}
		TestTools.clear(this);
		super.tearDown();
	}

	private boolean nodeIsVestigial(Preferences node) throws Exception {
		return (node != null)
			&& (node.keys().length == 0)
			&& (node.childrenNames().length == 0)
			&& (node != Preferences.userRoot());
	}

	protected void waitForEventQueueToClear() {
		try {
			while ( ! this.preferencesEventQueue().isEmpty()) {
				Thread.sleep(100);
			}
			Thread.sleep(100);
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private List preferencesEventQueue() {
		return (List) ClassTools.getStaticFieldValue(AbstractPreferences.class, "eventQueue");
	}

}
