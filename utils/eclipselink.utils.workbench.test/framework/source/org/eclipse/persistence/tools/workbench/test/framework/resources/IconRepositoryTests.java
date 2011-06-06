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
package org.eclipse.persistence.tools.workbench.test.framework.resources;

import javax.swing.Icon;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.framework.resources.AbstractIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultIconRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.IconRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.IconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.resources.MissingIconException;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;


public class IconRepositoryTests extends TestCase {
	private IconRepository iconRepository;
	
	public static Test suite() {
		return new TestSuite(IconRepositoryTests.class);
	}
	
	public IconRepositoryTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		iconRepository = new DefaultIconRepository(this.buildResourceFileNames());
	}

	private IconResourceFileNameMap buildResourceFileNames() {
		return new TestResourceFileNameMap();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testValidIcon() {
		Icon icon1 = iconRepository.getIcon("MW");
		assertNotNull(icon1);
		// test cache
		Icon icon2 = iconRepository.getIcon("MW");
		assertTrue(icon1 == icon2);
	}

    public void testHasIcon() {
        assertFalse(iconRepository.hasIcon("BOGUS_MW"));
        assertTrue(iconRepository.hasIcon("MW"));
    }
    
	public void testMissingFile() {
		Icon icon = null;
		boolean exCaught = false;
		try {
			icon = iconRepository.getIcon("BOGUS_MW");
		} catch (MissingIconException ex) {
			if (ex.getKey().equals("BOGUS_MW")) {
				exCaught = true;
			}
		}
		assertTrue("bogus icon: " + icon, exCaught);
	}

	public void testKey() {
		Icon icon = null;
		boolean exCaught = false;
		try {
			icon = iconRepository.getIcon("DOUBLE_BOGUS_MW");
		} catch (MissingIconException ex) {
			if (ex.getKey().equals("DOUBLE_BOGUS_MW")) {
				exCaught = true;
			}
		}
		assertTrue("bogus icon: " + icon, exCaught);
	}

	public void testDuplicateEntriesInResourceFileNameMap() {
		boolean exCaught = false;
		try {
			iconRepository = new DefaultIconRepository(new InvalidResourceFileNameMap());
		} catch (IllegalStateException ex) {
			if (ex.getMessage().indexOf("MW") != -1) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}


private static class TestResourceFileNameMap extends AbstractIconResourceFileNameMap {
	protected String[][] getEntries() {
		return entries;
	}
	private static final String[][] entries = {
		{"MW",				"icons/mw.gif"},
		{"BOGUS_MW",	"icons/xxx.gif"},
	};
}
	
private static class InvalidResourceFileNameMap extends AbstractIconResourceFileNameMap {
	protected String[][] getEntries() {
		return entries;
	}
	private static final String[][] entries = {
		{"MW",				"icons/mw.gif"},
		{"BOGUS_MW",	"icons/xxx.gif"},
		{"MW",				"icons/mw2.gif"},		// duplicate entry
	};
}
	
}
