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

import java.util.ListResourceBundle;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.framework.resources.DefaultStringRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.MissingStringException;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceBundleStringRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;



public class StringRepositoryTests extends TestCase {
	private StringRepository simpleRepos;
	private StringRepository defaultRepos;

	public static Test suite() {
		return new TestSuite(StringRepositoryTests.class);
	}
	
	public StringRepositoryTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.simpleRepos = new ResourceBundleStringRepository(TestResourceBundle.class);
		this.defaultRepos = new DefaultStringRepository(TestResourceBundle.class);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

    public void testHasString() {
        assertFalse(this.simpleRepos.hasString("BOGUS_KEY"));
        assertTrue(this.simpleRepos.hasString("OK"));
    }
    
	public void testGetString() {
		assertEquals("O&K", this.simpleRepos.getString("OK"));
		assertEquals("OK", this.defaultRepos.getString("OK"));

		assertEquals("&Cancel & Die", this.simpleRepos.getString("CANCEL"));
		assertEquals("Cancel & Die", this.defaultRepos.getString("CANCEL"));
	}

	public void testGetStringArgument() {
		assertEquals("Single-argument message: {0, number}.", this.simpleRepos.getString("FORMATTED_MSG_1", new Integer(42)));
		assertEquals("Single-argument message: 42.", this.defaultRepos.getString("FORMATTED_MSG_1", new Integer(42)));
	}

	public void testGetStringArgumentArgument() {
		assertEquals("Two-argument message: {0, number} + {1, number}.", this.simpleRepos.getString("FORMATTED_MSG_2", new Integer(42), new Integer(17)));
		assertEquals("Two-argument message: 42 + 17.", this.defaultRepos.getString("FORMATTED_MSG_2", new Integer(42), new Integer(17)));
	}

	public void testGetStringArgumentArgumentArgument() {
		assertEquals("Three-argument message: {0, number} + {1, number} = {2, number}.", this.simpleRepos.getString("FORMATTED_MSG_3", new Integer(42), new Integer(17), new Integer(59)));
		assertEquals("Three-argument message: 42 + 17 = 59.", this.defaultRepos.getString("FORMATTED_MSG_3", new Integer(42), new Integer(17), new Integer(59)));
	}

	public void testGetStringArguments() {
		assertEquals("{3}-argument message: {0, number} + {1, number} = {2, number}.", this.simpleRepos.getString("FORMATTED_MSG_4", new Object[] {new Integer(42), new Integer(17), new Integer(59), "Four"}));
		assertEquals("Four-argument message: 42 + 17 = 59.", this.defaultRepos.getString("FORMATTED_MSG_4", new Object[] {new Integer(42), new Integer(17), new Integer(59), "Four"}));
	}

	public void testMissingString() {
		String string = null;
		boolean exCaught = false;
		try {
			string = this.simpleRepos.getString("BOGUS_KEY");
		} catch (MissingStringException ex) {
			if (ex.getKey().equals("BOGUS_KEY")) {
				exCaught = true;
			}
		}
		assertTrue("string found for BOGUS_KEY: " + string, exCaught);

		exCaught = false;
		try {
			string = this.defaultRepos.getString("BOGUS_KEY");
		} catch (MissingStringException ex) {
			if (ex.getKey().equals("BOGUS_KEY")) {
				exCaught = true;
			}
		}
		assertTrue("string found for BOGUS_KEY: " + string, exCaught);
	}

	public void testDuplicateKey() {
		boolean exCaught = false;
		try {
			StringRepository duplicateStringRepository = new ResourceBundleStringRepository(DuplicateKeyResourceBundle.class);
			duplicateStringRepository.getString(DuplicateKeyResourceBundle.DUPLICATE_KEY);
		} catch (IllegalStateException ex) {
			if (ex.getMessage().indexOf(DuplicateKeyResourceBundle.DUPLICATE_KEY) != -1) {
				exCaught = true;
			}
		}
		assertTrue("duplicate resource key not detected: " + DuplicateKeyResourceBundle.DUPLICATE_KEY, exCaught);
	}


public static class DuplicateKeyResourceBundle extends ListResourceBundle {
	static final String DUPLICATE_KEY = "CANCEL";

	public Object[][] getContents() {
		return contents;
	}

	private static final Object[][] contents = {
		{"OK",							"O&K"},
		{DUPLICATE_KEY,			"&Cancel & Die"},
		{"FORMATTED_MSG_1",	"Single-argument message: {0, number}."},
		{"FORMATTED_MSG_2",	"Two-argument message: {0, number} + {1, number}."},
		{"FORMATTED_MSG_3",	"Three-argument message: {0, number} + {1, number} = {2, number}."},
		{"FORMATTED_MSG_4",	"{3}-argument message: {0, number} + {1, number} = {2, number}."},
		{DUPLICATE_KEY,			"&Cancel & Die - duplicate"},
	};
}

}
