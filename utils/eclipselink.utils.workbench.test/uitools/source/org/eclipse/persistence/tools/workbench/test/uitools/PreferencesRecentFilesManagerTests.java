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

import java.io.File;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.uitools.PreferencesRecentFilesManager;
import org.eclipse.persistence.tools.workbench.uitools.RecentFilesManager;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;

public class PreferencesRecentFilesManagerTests extends PreferencesTestCase {
	boolean stateChangeEventFired;
	private StateChangeListener listener;
	private static final String MAX_SIZE_KEY = "max size";

	public static Test suite() {
		return new TestSuite(PreferencesRecentFilesManagerTests.class);
	}
	
	public PreferencesRecentFilesManagerTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.stateChangeEventFired = false;
		this.listener = this.buildListener();
	}

	private StateChangeListener buildListener() {
		return new StateChangeListener() {
			public void stateChanged(StateChangeEvent e) {
				PreferencesRecentFilesManagerTests.this.stateChangeEventFired = true;
			}
		};
	}

	private RecentFilesManager buildManager(Preferences node) {
		RecentFilesManager mgr = new PreferencesRecentFilesManager(node, node, MAX_SIZE_KEY);
		mgr.addStateChangeListener(this.listener);
		return mgr;
	}

	public void testInitializeNormal() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		RecentFilesManager mgr = this.buildManager(this.testNode);

		File[] files = mgr.getRecentFiles();
		assertEquals(3, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
	}

	public void testInitializeNonIntegerKey() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("A", "bogus");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		RecentFilesManager mgr = this.buildManager(this.testNode);

		File[] files = mgr.getRecentFiles();
		assertEquals(3, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
	}

	public void testInitializeNonNormalizedKey() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("003", "CCC");
		RecentFilesManager mgr = this.buildManager(this.testNode);

		File[] files = mgr.getRecentFiles();
		assertEquals(2, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());
	}

	public void testInitializeExceedMax() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.put("5", "EEE");
		this.testNode.put("6", "FFF");
		int max = 4;
		this.testNode.putInt(MAX_SIZE_KEY, max);
		RecentFilesManager mgr = this.buildManager(this.testNode);

		File[] files = mgr.getRecentFiles();
		assertEquals(max, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
		assertEquals("DDD", files[3].getPath());
	}

	public void testIncreaseMaxSize() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.put("5", "EEE");
		this.testNode.put("6", "FFF");
		int originalMax = 4;
		this.testNode.putInt(MAX_SIZE_KEY, originalMax);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		int newMax = 9;
		mgr.setMaxSize(newMax);

		// this will "uncover" some preferences
		File[] files = mgr.getRecentFiles();
		assertEquals(6, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
		assertEquals("DDD", files[3].getPath());
		assertEquals("EEE", files[4].getPath());
		assertEquals("FFF", files[5].getPath());

		assertTrue(this.stateChangeEventFired);
	}

	public void testDecreaseMaxSize() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.put("5", "EEE");
		this.testNode.put("6", "FFF");
		int originalMax = 4;
		this.testNode.putInt(MAX_SIZE_KEY, originalMax);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		int newMax = 2;
		mgr.setMaxSize(newMax);

		File[] files = mgr.getRecentFiles();
		assertEquals(newMax, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());

		assertEquals("AAA", this.testNode.get("1", "bogus"));
		assertEquals("BBB", this.testNode.get("2", "bogus"));
		assertEquals("CCC", this.testNode.get("3", "bogus"));
		assertEquals("DDD", this.testNode.get("4", "bogus"));
		assertEquals("EEE", this.testNode.get("5", "bogus"));
		assertEquals("FFF", this.testNode.get("6", "bogus"));

		assertTrue(this.stateChangeEventFired);
	}

	public void testInvalidMaxSize1() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.put("5", "EEE");
		this.testNode.put("6", "FFF");
		this.testNode.putInt(MAX_SIZE_KEY, -1);

		boolean exCaught = false;
		try {
			RecentFilesManager mgr = this.buildManager(this.testNode);
			assertNull(mgr);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testInvalidMaxSize2() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.put("5", "EEE");
		this.testNode.put("6", "FFF");
		this.testNode.putInt(MAX_SIZE_KEY, 4);
		RecentFilesManager mgr = this.buildManager(this.testNode);

		boolean exCaught = false;
		try {
			mgr.setMaxSize(RecentFilesManager.MAX_MAX_SIZE + 1);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testSetMostRecentFileAddNew() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 7);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		mgr.setMostRecentFile(new File("EEE"));

		File[] files = mgr.getRecentFiles();
		assertEquals(5, files.length);
		assertEquals("EEE", files[0].getPath());
		assertEquals("AAA", files[1].getPath());
		assertEquals("BBB", files[2].getPath());
		assertEquals("CCC", files[3].getPath());
		assertEquals("DDD", files[4].getPath());

		assertEquals("EEE", this.testNode.get("1", "bogus"));
		assertEquals("AAA", this.testNode.get("2", "bogus"));
		assertEquals("BBB", this.testNode.get("3", "bogus"));
		assertEquals("CCC", this.testNode.get("4", "bogus"));
		assertEquals("DDD", this.testNode.get("5", "bogus"));

		assertTrue(this.stateChangeEventFired);
	}

	public void testSetMostRecentFileAddDuplicate() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 7);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		mgr.setMostRecentFile(new File("BBB"));

		File[] files = mgr.getRecentFiles();
		assertEquals(4, files.length);
		assertEquals("BBB", files[0].getPath());
		assertEquals("AAA", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
		assertEquals("DDD", files[3].getPath());

		assertEquals("BBB", this.testNode.get("1", "bogus"));
		assertEquals("AAA", this.testNode.get("2", "bogus"));
		assertEquals("CCC", this.testNode.get("3", "bogus"));
		assertEquals("DDD", this.testNode.get("4", "bogus"));

		assertTrue(this.stateChangeEventFired);
	}

	public void testSetMostRecentFileReplaceNew() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 4);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		mgr.setMostRecentFile(new File("EEE"));

		File[] files = mgr.getRecentFiles();
		assertEquals(4, files.length);
		assertEquals("EEE", files[0].getPath());
		assertEquals("AAA", files[1].getPath());
		assertEquals("BBB", files[2].getPath());
		assertEquals("CCC", files[3].getPath());

		assertEquals("EEE", this.testNode.get("1", "bogus"));
		assertEquals("AAA", this.testNode.get("2", "bogus"));
		assertEquals("BBB", this.testNode.get("3", "bogus"));
		assertEquals("CCC", this.testNode.get("4", "bogus"));

		assertTrue(this.stateChangeEventFired);
	}

	public void testSetMostRecentFileReplaceDuplicate() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 4);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		mgr.setMostRecentFile(new File("BBB"));

		File[] files = mgr.getRecentFiles();
		assertEquals(4, files.length);
		assertEquals("BBB", files[0].getPath());
		assertEquals("AAA", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
		assertEquals("DDD", files[3].getPath());

		assertEquals("BBB", this.testNode.get("1", "bogus"));
		assertEquals("AAA", this.testNode.get("2", "bogus"));
		assertEquals("CCC", this.testNode.get("3", "bogus"));
		assertEquals("DDD", this.testNode.get("4", "bogus"));

		assertTrue(this.stateChangeEventFired);
	}

	public void testChangePreferenceNormal() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 4);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		this.testNode.put("2", "NEW FILE");
		this.waitForEventQueueToClear();

		File[] files = mgr.getRecentFiles();
		assertEquals(4, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("NEW FILE", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
		assertEquals("DDD", files[3].getPath());

		assertEquals("AAA", this.testNode.get("1", "bogus"));
		assertEquals("NEW FILE", this.testNode.get("2", "bogus"));
		assertEquals("CCC", this.testNode.get("3", "bogus"));
		assertEquals("DDD", this.testNode.get("4", "bogus"));

		assertTrue(this.stateChangeEventFired);
	}

	public void testChangePreferenceAdd() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 7);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		this.testNode.put("5", "NEW FILE");
		this.waitForEventQueueToClear();

		File[] files = mgr.getRecentFiles();
		assertEquals(5, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
		assertEquals("DDD", files[3].getPath());
		assertEquals("NEW FILE", files[4].getPath());

		assertEquals("AAA", this.testNode.get("1", "bogus"));
		assertEquals("BBB", this.testNode.get("2", "bogus"));
		assertEquals("CCC", this.testNode.get("3", "bogus"));
		assertEquals("DDD", this.testNode.get("4", "bogus"));
		assertEquals("NEW FILE", this.testNode.get("5", "bogus"));

		assertTrue(this.stateChangeEventFired);
	}

	public void testChangePreferenceAddBeyondMax() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 4);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		this.testNode.put("5", "XXX FILE");
		this.waitForEventQueueToClear();

		File[] files = mgr.getRecentFiles();
		assertEquals(4, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
		assertEquals("DDD", files[3].getPath());

		assertEquals("AAA", this.testNode.get("1", "bogus"));
		assertEquals("BBB", this.testNode.get("2", "bogus"));
		assertEquals("CCC", this.testNode.get("3", "bogus"));
		assertEquals("DDD", this.testNode.get("4", "bogus"));
		assertEquals("XXX FILE", this.testNode.get("5", "bogus"));

		assertFalse(this.stateChangeEventFired);
	}

	public void testChangePreferenceNonIntegerKey() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 4);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		this.testNode.put("XXX", "NEW FILE");
		this.waitForEventQueueToClear();

		File[] files = mgr.getRecentFiles();
		assertEquals(4, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
		assertEquals("DDD", files[3].getPath());

		assertEquals("AAA", this.testNode.get("1", "bogus"));
		assertEquals("BBB", this.testNode.get("2", "bogus"));
		assertEquals("CCC", this.testNode.get("3", "bogus"));
		assertEquals("DDD", this.testNode.get("4", "bogus"));
		assertEquals("NEW FILE", this.testNode.get("XXX", "bogus"));

		assertFalse(this.stateChangeEventFired);
	}

	public void testChangePreferenceOutOfRange() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 4);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		this.testNode.put("-1", "NEW FILE -1");
		this.testNode.put("6", "NEW FILE 6");
		this.waitForEventQueueToClear();

		File[] files = mgr.getRecentFiles();
		assertEquals(4, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
		assertEquals("DDD", files[3].getPath());

		assertEquals("AAA", this.testNode.get("1", "bogus"));
		assertEquals("BBB", this.testNode.get("2", "bogus"));
		assertEquals("CCC", this.testNode.get("3", "bogus"));
		assertEquals("DDD", this.testNode.get("4", "bogus"));
		assertEquals("NEW FILE -1", this.testNode.get("-1", "bogus"));
		assertEquals("NEW FILE 6", this.testNode.get("6", "bogus"));

		assertFalse(this.stateChangeEventFired);
	}

	public void testChangePreferenceNonNormalizedKey() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 4);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		this.testNode.put("003", "NEW FILE 003");
		this.waitForEventQueueToClear();

		File[] files = mgr.getRecentFiles();
		assertEquals(4, files.length);
		assertEquals("AAA", files[0].getPath());
		assertEquals("BBB", files[1].getPath());
		assertEquals("CCC", files[2].getPath());
		assertEquals("DDD", files[3].getPath());

		assertEquals("AAA", this.testNode.get("1", "bogus"));
		assertEquals("BBB", this.testNode.get("2", "bogus"));
		assertEquals("CCC", this.testNode.get("3", "bogus"));
		assertEquals("DDD", this.testNode.get("4", "bogus"));
		assertEquals("NEW FILE 003", this.testNode.get("003", "bogus"));

		assertFalse(this.stateChangeEventFired);
	}

	public void testChangePreferenceRemove() throws Exception {
		this.testNode.put("1", "AAA");
		this.testNode.put("2", "BBB");
		this.testNode.put("3", "CCC");
		this.testNode.put("4", "DDD");
		this.testNode.putInt(MAX_SIZE_KEY, 4);
		RecentFilesManager mgr = this.buildManager(this.testNode);
		this.testNode.remove("2");
		this.waitForEventQueueToClear();

		File[] files = mgr.getRecentFiles();
		assertEquals(1, files.length);
		assertEquals("AAA", files[0].getPath());

		this.waitForEventQueueToClear();
		assertEquals("AAA", this.testNode.get("1", "bogus"));
		assertEquals("CCC", this.testNode.get("3", "bogus"));
		assertEquals("DDD", this.testNode.get("4", "bogus"));

		assertTrue(this.stateChangeEventFired);
	}

}
