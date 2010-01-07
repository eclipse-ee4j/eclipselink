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
package org.eclipse.persistence.tools.workbench.test.utility;

import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;

public class TriStateBooleanTests extends TestCase {

	public static Test suite() {
		return new TestSuite(TriStateBooleanTests.class);
	}
	
	public TriStateBooleanTests(String name) {
		super(name);
	}

	public void testHashCode() {
		final Object VALUE = new Object();

		TriStateBoolean boolean01 = TriStateBoolean.TRUE;
		TriStateBoolean boolean02 = TriStateBoolean.FALSE;
		TriStateBoolean boolean03 = TriStateBoolean.UNDEFINED;

		TriStateBoolean boolean04 = new TriStateBoolean(Boolean.TRUE);
		TriStateBoolean boolean05 = new TriStateBoolean(Boolean.FALSE);
		TriStateBoolean boolean06 = new TriStateBoolean((Boolean) null);

		TriStateBoolean boolean07 = new TriStateBoolean(true);
		TriStateBoolean boolean08 = new TriStateBoolean(false);

		TriStateBoolean boolean09 = new TriStateBoolean("true");
		TriStateBoolean boolean10 = new TriStateBoolean("false");
		TriStateBoolean boolean11 = new TriStateBoolean("anything");
		TriStateBoolean boolean12 = new TriStateBoolean("undefined");
		TriStateBoolean boolean13 = new TriStateBoolean((String) null);

		String aString01 = new String("true");
		String aString02 = new String("false");

		Boolean boolean14 = Boolean.TRUE;
		Boolean boolean15 = Boolean.FALSE;

		Hashtable table = new Hashtable(16);
		table.put(boolean01, VALUE);
		table.put(boolean02, VALUE);
		table.put(boolean03, VALUE);
		table.put(boolean04, VALUE);
		table.put(boolean05, VALUE);
		table.put(boolean06, VALUE);
		table.put(boolean07, VALUE);
		table.put(boolean08, VALUE);
		table.put(boolean09, VALUE);
		table.put(boolean10, VALUE);
		table.put(boolean11, VALUE);
		table.put(boolean12, VALUE);
		table.put(boolean13, VALUE);
		table.put(boolean14, VALUE);
		table.put(boolean15, VALUE);
		table.put(aString01, VALUE);
		table.put(aString02, VALUE);

		assertTrue("The size of the hashtable is not 7", table.size() == 7);
	}

	public void testBooleanValue()
	{
		TriStateBoolean boolean01 = TriStateBoolean.TRUE;
		TriStateBoolean boolean02 = TriStateBoolean.FALSE;
		TriStateBoolean boolean03 = TriStateBoolean.UNDEFINED;

		TriStateBoolean boolean04 = new TriStateBoolean(Boolean.TRUE);
		TriStateBoolean boolean05 = new TriStateBoolean(Boolean.FALSE);
		TriStateBoolean boolean06 = new TriStateBoolean((Boolean) null);

		TriStateBoolean boolean07 = new TriStateBoolean(true);
		TriStateBoolean boolean08 = new TriStateBoolean(false);

		TriStateBoolean boolean09 = new TriStateBoolean("true");
		TriStateBoolean boolean10 = new TriStateBoolean("false");
		TriStateBoolean boolean11 = new TriStateBoolean("anything");
		TriStateBoolean boolean12 = new TriStateBoolean("undefined");
		TriStateBoolean boolean13 = new TriStateBoolean((String) null);

		assertTrue("TriStateBoolean.TRUE.booleanValue() should return true", boolean01.booleanValue());
		assertFalse("TriStateBoolean.FALSE.booleanValue() should return false", boolean02.booleanValue());
		boolean exCaught = false;
		try {
			boolean03.booleanValue();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("TriStateBoolean.UNDEFINED.booleanValue() should throw exception", exCaught);

		assertTrue("new TriStateBoolean(Boolean.TRUE).booleanValue() should return true", boolean04.booleanValue());
		assertFalse("new TriStateBoolean(Boolean.FALSE).booleanValue() should return false", boolean05.booleanValue());
		exCaught = false;
		try {
			boolean06.booleanValue();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("new TriStateBoolean((Boolean) null).booleanValue() should throw exception", exCaught);

		assertTrue("new TriStateBoolean(true).booleanValue() should return true", boolean07.booleanValue());
		assertFalse("new TriStateBoolean(false).booleanValue() should return false", boolean08.booleanValue());

		assertTrue("new TriStateBoolean(\"true\").booleanValue() should return true", boolean09.booleanValue());
		assertFalse("new TriStateBoolean(\"false\").booleanValue() should return false", boolean10.booleanValue());
		assertFalse("new TriStateBoolean(\"anything\").booleanValue() should return false", boolean11.booleanValue());
		exCaught = false;
		try {
			boolean12.booleanValue();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("new TriStateBoolean(\"undefined\").booleanValue() should throw exception", exCaught);
		exCaught = false;
		try {
			boolean13.booleanValue();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("new TriStateBoolean((String) null).booleanValue() should throw exception", exCaught);
	}

	/*
	 * Test for Boolean getBoolean()
	 */
	public void testGetBoolean()
	{
		TriStateBoolean boolean01 = TriStateBoolean.TRUE;
		TriStateBoolean boolean02 = TriStateBoolean.FALSE;
		TriStateBoolean boolean03 = TriStateBoolean.UNDEFINED;

		TriStateBoolean boolean04 = new TriStateBoolean(Boolean.TRUE);
		TriStateBoolean boolean05 = new TriStateBoolean(Boolean.FALSE);
		TriStateBoolean boolean06 = new TriStateBoolean((Boolean) null);

		TriStateBoolean boolean07 = new TriStateBoolean(true);
		TriStateBoolean boolean08 = new TriStateBoolean(false);

		TriStateBoolean boolean09 = new TriStateBoolean("true");
		TriStateBoolean boolean10 = new TriStateBoolean("false");
		TriStateBoolean boolean11 = new TriStateBoolean("anything");
		TriStateBoolean boolean12 = new TriStateBoolean("undefined");
		TriStateBoolean boolean13 = new TriStateBoolean((String) null);

		assertSame("TriStateBoolean.TRUE.getBoolean() should return Boolean.TRUE", boolean01.getValue(), Boolean.TRUE);
		assertSame("TriStateBoolean.FALSE.getBoolean() should return Boolean.FALSE", boolean02.getValue(), Boolean.FALSE);
		assertNull("TriStateBoolean.UNDEFINED.getBoolean() should return false", boolean03.getValue());

		assertSame("new TriStateBoolean(Boolean.TRUE).getBoolean() should return Boolean.TRUE", boolean04.getValue(), Boolean.TRUE);
		assertSame("new TriStateBoolean(Boolean.FALSE).getBoolean() should return Boolean.FALSE", boolean05.getValue(), Boolean.FALSE);
		assertNull("new TriStateBoolean((Boolean) null).getBoolean() should return null", boolean06.getValue());

		assertSame("new TriStateBoolean(true).getBoolean() should return Boolean.TRUE", boolean07.getValue(), Boolean.TRUE);
		assertSame("new TriStateBoolean(false).getBoolean() should return Boolean.FALSE", boolean08.getValue(), Boolean.FALSE);

		assertSame("new TriStateBoolean(\"true\").getBoolean() should return Boolean.TRUE", boolean09.getValue(), Boolean.TRUE);
		assertSame("new TriStateBoolean(\"false\").getBoolean() should return Boolean.FALSE", boolean10.getValue(), Boolean.FALSE);
		assertSame("new TriStateBoolean(\"anything\").getBoolean() should return Boolean.FALSE", boolean11.getValue(), Boolean.FALSE);
		assertNull("new TriStateBoolean(\"undefined\").getBoolean() should return null", boolean12.getValue());
		assertNull("new TriStateBoolean((String) \"null\").getBoolean() should return null", boolean13.getValue());
	}

	/*
	 * Test for TriStateBoolean getBoolean(boolean)
	 */
	public void testGetBooleanboolean()
	{
		TriStateBoolean boolean01 = TriStateBoolean.valueOf(true);
		TriStateBoolean boolean02 = TriStateBoolean.valueOf(false);

		assertSame("TriStateBoolean.getBoolean(true) should return TriStateBoolean.TRUE", boolean01, TriStateBoolean.TRUE);
		assertSame("TriStateBoolean.getBoolean(false) should return TriStateBoolean.FALSE", boolean02, TriStateBoolean.FALSE);
	}

	/*
	 * Test for TriStateBoolean getBoolean(Boolean)
	 */
	public void testGetBooleanBoolean()
	{
		TriStateBoolean boolean01 = TriStateBoolean.valueOf(Boolean.TRUE);
		TriStateBoolean boolean02 = TriStateBoolean.valueOf(Boolean.FALSE);
		TriStateBoolean boolean03 = TriStateBoolean.valueOf((Boolean) null);

		assertSame("TriStateBoolean.getBoolean(Boolean.TRUE) should return TriStateBoolean.TRUE", boolean01, TriStateBoolean.TRUE);
		assertSame("TriStateBoolean.getBoolean(Boolean.FALSE) should return TriStateBoolean.FALSE", boolean02, TriStateBoolean.FALSE);
		assertSame("TriStateBoolean.getBoolean(null) should return TriStateBoolean.TRUE", boolean03, TriStateBoolean.UNDEFINED);
	}

	/*
	 * Test for boolean equals(Object)
	 */
	public void testEqualsObject()
	{
		TriStateBoolean boolean01 = TriStateBoolean.TRUE;
		TriStateBoolean boolean02 = TriStateBoolean.FALSE;
		TriStateBoolean boolean03 = TriStateBoolean.UNDEFINED;

		TriStateBoolean boolean04 = new TriStateBoolean(Boolean.TRUE);
		TriStateBoolean boolean05 = new TriStateBoolean(Boolean.FALSE);
		TriStateBoolean boolean06 = new TriStateBoolean((Boolean) null);

		TriStateBoolean boolean07 = new TriStateBoolean(true);
		TriStateBoolean boolean08 = new TriStateBoolean(false);

		TriStateBoolean boolean09 = new TriStateBoolean("true");
		TriStateBoolean boolean10 = new TriStateBoolean("false");
		TriStateBoolean boolean11 = new TriStateBoolean("anything");
		TriStateBoolean boolean12 = new TriStateBoolean("undefined");
		TriStateBoolean boolean13 = new TriStateBoolean((String) null);

		assertTrue ("TriStateBoolean.TRUE.equals(TriStateBoolean.TRUE) should be equal", boolean01.equals(boolean01));
		assertFalse("TriStateBoolean.TRUE.equals(TriStateBoolean.FALSE) should not be equal", boolean01.equals(boolean02));
		assertFalse("TriStateBoolean.TRUE.equals(TriStateBoolean.UNDEFINED) should not be equal", boolean01.equals(boolean03));
		assertTrue ("TriStateBoolean.TRUE.equals(new TriStateBoolean(Boolean.TRUE)) should be equal", boolean01.equals(boolean04));
		assertFalse("TriStateBoolean.TRUE.equals(new TriStateBoolean(Boolean.FALSE)) should not be equal", boolean01.equals(boolean05));
		assertFalse("TriStateBoolean.TRUE.equals(new TriStateBoolean((Boolean) null) should not be equal", boolean01.equals(boolean06));
		assertTrue ("TriStateBoolean.TRUE.equals(new TriStateBoolean(true)) should not be equal", boolean01.equals(boolean07));
		assertFalse("TriStateBoolean.TRUE.equals(new TriStateBoolean(false)) should not be equal", boolean01.equals(boolean08));
		assertTrue ("TriStateBoolean.TRUE.equals(new TriStateBoolean(\"true\")) should not be equal", boolean01.equals(boolean09));
		assertFalse("TriStateBoolean.TRUE.equals(new TriStateBoolean(\"false\")) should not be equal", boolean01.equals(boolean10));
		assertFalse("TriStateBoolean.TRUE.equals(new TriStateBoolean(\"anything\")) should not be equal", boolean01.equals(boolean11));
		assertFalse("TriStateBoolean.TRUE.equals(new TriStateBoolean(\"undefined\")) should not be equal", boolean01.equals(boolean12));
		assertFalse("TriStateBoolean.TRUE.equals(new TriStateBoolean((String) null)) should not be equal", boolean01.equals(boolean13));
		assertFalse("TriStateBoolean.TRUE.equals(null) should not be equal", boolean01.equals(null));
		assertFalse("TriStateBoolean.TRUE.equals(\"true\") should not be equal", boolean01.equals("true"));

		assertFalse("TriStateBoolean.FALSE.equals(TriStateBoolean.TRUE) should not be equal", boolean02.equals(boolean01));
		assertTrue ("TriStateBoolean.FALSE.equals(TriStateBoolean.FALSE) should be equal", boolean02.equals(boolean02));
		assertFalse("TriStateBoolean.FALSE.equals(TriStateBoolean.UNDEFINED) should not be equal", boolean02.equals(boolean03));
		assertFalse("TriStateBoolean.FALSE.equals(new TriStateBoolean(Boolean.TRUE)) should not be equal", boolean02.equals(boolean04));
		assertTrue ("TriStateBoolean.FALSE.equals(new TriStateBoolean(Boolean.FALSE)) should be equal", boolean02.equals(boolean05));
		assertFalse("TriStateBoolean.FALSE.equals(new TriStateBoolean((Boolean) null) should not be equal", boolean02.equals(boolean06));
		assertFalse("TriStateBoolean.FALSE.equals(new TriStateBoolean(true)) should not be equal", boolean02.equals(boolean07));
		assertTrue ("TriStateBoolean.FALSE.equals(new TriStateBoolean(false)) should be equal", boolean02.equals(boolean08));
		assertFalse("TriStateBoolean.TRUE.equals(new TriStateBoolean(\"true\")) should not be equal", boolean02.equals(boolean09));
		assertTrue ("TriStateBoolean.FALSE.equals(new TriStateBoolean(\"false\")) should be equal", boolean02.equals(boolean10));
		assertTrue ("TriStateBoolean.FALSE.equals(new TriStateBoolean(\"anything\")) should be equal", boolean02.equals(boolean11));
		assertFalse("TriStateBoolean.FALSE.equals(new TriStateBoolean(\"undefined\")) should not be equal", boolean02.equals(boolean12));
		assertFalse("TriStateBoolean.FALSE.equals(new TriStateBoolean((String) null)) should not be equal", boolean02.equals(boolean13));
		assertFalse("TriStateBoolean.FALSE.equals(null) should not be equal", boolean02.equals(null));
		assertFalse("TriStateBoolean.FALSE.equals(\"true\") should not be equal", boolean02.equals("true"));

		assertFalse("TriStateBoolean.UNDEFINED.equals(TriStateBoolean.TRUE) should be not equal", boolean03.equals(boolean01));
		assertFalse("TriStateBoolean.UNDEFINED.equals(TriStateBoolean.FALSE) should not be equal", boolean03.equals(boolean02));
		assertTrue ("TriStateBoolean.UNDEFINED.equals(TriStateBoolean.UNDEFINED) should be equal", boolean03.equals(boolean03));
		assertFalse("TriStateBoolean.UNDEFINED.equals(new TriStateBoolean(Boolean.TRUE)) should not be equal", boolean03.equals(boolean04));
		assertFalse("TriStateBoolean.UNDEFINED.equals(new TriStateBoolean(Boolean.FALSE)) should not be equal", boolean03.equals(boolean05));
		assertTrue ("TriStateBoolean.UNDEFINED.equals(new TriStateBoolean((Boolean) null) should be equal", boolean03.equals(boolean06));
		assertFalse("TriStateBoolean.UNDEFINED.equals(new TriStateBoolean(true)) should not be equal", boolean03.equals(boolean07));
		assertFalse("TriStateBoolean.UNDEFINED.equals(new TriStateBoolean(false)) should not be equal", boolean03.equals(boolean08));
		assertFalse("TriStateBoolean.UNDEFINED.equals(new TriStateBoolean(\"true\")) should not be equal", boolean03.equals(boolean09));
		assertFalse("TriStateBoolean.UNDEFINED.equals(new TriStateBoolean(\"false\")) should not be equal", boolean03.equals(boolean10));
		assertFalse("TriStateBoolean.UNDEFINED.equals(new TriStateBoolean(\"anything\")) should not be equal", boolean03.equals(boolean11));
		assertTrue ("TriStateBoolean.UNDEFINED.equals(new TriStateBoolean(\"undefined\")) should be equal", boolean03.equals(boolean12));
		assertTrue ("TriStateBoolean.UNDEFINED.equals(new TriStateBoolean((String) null)) should not be equal", boolean03.equals(boolean13));
		assertFalse("TriStateBoolean.UNDEFINED.equals(null) should not be equal", boolean03.equals(null));
		assertFalse("TriStateBoolean.UNDEFINED.equals(\"true\") should not be equal", boolean03.equals("true"));

		assertTrue ("new TriStateBoolean(Boolean.TRUE).equals(TriStateBoolean.TRUE) should be equal", boolean04.equals(boolean01));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(TriStateBoolean.FALSE) should not be equal", boolean04.equals(boolean02));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(TriStateBoolean.UNDEFINED) should not be equal", boolean04.equals(boolean03));
		assertTrue ("new TriStateBoolean(Boolean.TRUE).equals(new TriStateBoolean(Boolean.TRUE)) should be equal", boolean04.equals(boolean04));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(new TriStateBoolean(Boolean.FALSE)) should not be equal", boolean04.equals(boolean05));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(new TriStateBoolean((Boolean) null) should not be equal", boolean04.equals(boolean06));
		assertTrue("new TriStateBoolean(Boolean.TRUE).equals(new TriStateBoolean(true)) should be equal", boolean04.equals(boolean07));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(new TriStateBoolean(false)) should not be equal", boolean04.equals(boolean08));
		assertTrue("new TriStateBoolean(Boolean.TRUE).equals(new TriStateBoolean(\"true\")) should be equal", boolean04.equals(boolean09));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(new TriStateBoolean(\"false\")) should not be equal", boolean04.equals(boolean10));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(new TriStateBoolean(\"anything\")) should not be equal", boolean04.equals(boolean11));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(new TriStateBoolean(\"undefined\")) should not be equal", boolean04.equals(boolean12));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(new TriStateBoolean((String) null)) should not be equal", boolean04.equals(boolean13));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(null) should not be equal", boolean04.equals(null));
		assertFalse("new TriStateBoolean(Boolean.TRUE).equals(\"true\") should not be equal", boolean04.equals("true"));

		// TODO

		assertTrue ("new TriStateBoolean(true).equals(TriStateBoolean.TRUE) should be equal", boolean07.equals(boolean01));
		assertFalse("new TriStateBoolean(true).equals(TriStateBoolean.FALSE) should not be equal", boolean07.equals(boolean02));
		assertFalse("new TriStateBoolean(true).equals(TriStateBoolean.UNDEFINED) should not be equal", boolean07.equals(boolean03));
		assertTrue ("new TriStateBoolean(true).equals(new TriStateBoolean(Boolean.TRUE)) should be equal", boolean07.equals(boolean04));
		assertFalse("new TriStateBoolean(true).equals(new TriStateBoolean(Boolean.FALSE)) should not be equal", boolean07.equals(boolean05));
		assertFalse("new TriStateBoolean(true).equals(new TriStateBoolean((Boolean) null) should not be equal", boolean07.equals(boolean06));
		assertTrue ("new TriStateBoolean(true).equals(new TriStateBoolean(true)) should be equal", boolean07.equals(boolean07));
		assertFalse("new TriStateBoolean(true).equals(new TriStateBoolean(false)) should not be equal", boolean07.equals(boolean08));
		assertTrue ("new TriStateBoolean(true).equals(new TriStateBoolean(\"true\")) should be equal", boolean07.equals(boolean09));
		assertFalse("new TriStateBoolean(true).equals(new TriStateBoolean(\"false\")) should not be equal", boolean07.equals(boolean10));
		assertFalse("new TriStateBoolean(true).equals(new TriStateBoolean(\"anything\")) should not be equal", boolean07.equals(boolean11));
		assertFalse("new TriStateBoolean(true).equals(new TriStateBoolean(\"undefined\")) should not be equal", boolean07.equals(boolean12));
		assertFalse("new TriStateBoolean(true).equals(new TriStateBoolean((String) null)) should not be equal", boolean07.equals(boolean13));
		assertFalse("new TriStateBoolean(true).equals(null) should not be equal", boolean07.equals(null));
		assertFalse("new TriStateBoolean(true).equals(\"true\") should not be equal", boolean07.equals("true"));

		assertFalse("new TriStateBoolean(false).equals(TriStateBoolean.TRUE) should be equal", boolean08.equals(boolean01));
		assertTrue ("new TriStateBoolean(false).equals(TriStateBoolean.FALSE) should not be equal", boolean08.equals(boolean02));
		assertFalse("new TriStateBoolean(false).equals(TriStateBoolean.UNDEFINED) should not be equal", boolean08.equals(boolean03));
		assertFalse("new TriStateBoolean(false).equals(new TriStateBoolean(Boolean.TRUE)) should be equal", boolean08.equals(boolean04));
		assertTrue ("new TriStateBoolean(false).equals(new TriStateBoolean(Boolean.FALSE)) should not be equal", boolean08.equals(boolean05));
		assertFalse("new TriStateBoolean(false).equals(new TriStateBoolean((Boolean) null) should not be equal", boolean08.equals(boolean06));
		assertFalse("new TriStateBoolean(false).equals(new TriStateBoolean(true)) should be equal", boolean08.equals(boolean07));
		assertTrue ("new TriStateBoolean(false).equals(new TriStateBoolean(false)) should not be equal", boolean08.equals(boolean08));
		assertFalse("new TriStateBoolean(false).equals(new TriStateBoolean(\"true\")) should be equal", boolean08.equals(boolean09));
		assertTrue ("new TriStateBoolean(false).equals(new TriStateBoolean(\"false\")) should not be equal", boolean08.equals(boolean10));
		assertTrue ("new TriStateBoolean(false).equals(new TriStateBoolean(\"anything\")) should not be equal", boolean08.equals(boolean11));
		assertFalse("new TriStateBoolean(false).equals(new TriStateBoolean(\"undefined\")) should not be equal", boolean08.equals(boolean12));
		assertFalse("new TriStateBoolean(false).equals(new TriStateBoolean((String) null)) should not be equal", boolean08.equals(boolean13));
		assertFalse("new TriStateBoolean(false).equals(null) should not be equal", boolean08.equals(null));
		assertFalse("new TriStateBoolean(false).equals(\"true\") should not be equal", boolean08.equals("true"));

		// TODO
	}

	public void testIsFalse()
	{
		TriStateBoolean boolean01 = TriStateBoolean.TRUE;
		TriStateBoolean boolean02 = TriStateBoolean.FALSE;
		TriStateBoolean boolean03 = TriStateBoolean.UNDEFINED;

		TriStateBoolean boolean04 = new TriStateBoolean(Boolean.TRUE);
		TriStateBoolean boolean05 = new TriStateBoolean(Boolean.FALSE);
		TriStateBoolean boolean06 = new TriStateBoolean((Boolean) null);

		TriStateBoolean boolean07 = new TriStateBoolean(true);
		TriStateBoolean boolean08 = new TriStateBoolean(false);

		TriStateBoolean boolean09 = new TriStateBoolean("true");
		TriStateBoolean boolean10 = new TriStateBoolean("false");
		TriStateBoolean boolean11 = new TriStateBoolean("anything");
		TriStateBoolean boolean12 = new TriStateBoolean("undefined");
		TriStateBoolean boolean13 = new TriStateBoolean((String) null);

		assertFalse("TriStateBoolean.TRUE.isFalse() should return false", boolean01.isFalse());
		assertTrue("TriStateBoolean.FALSE.isFalse() should return true", boolean02.isFalse());
		assertFalse("TriStateBoolean.UNDEFINED.isFalse() should return false", boolean03.isFalse());

		assertFalse("TriStateBoolean(Boolean.TRUE).isFalse() should return false", boolean04.isFalse());
		assertTrue("TriStateBoolean(Boolean.FALSE).isFalse() should return true", boolean05.isFalse());
		assertFalse("new TriStateBoolean((Boolean) null) should return false", boolean06.isFalse());

		assertFalse("new TriStateBoolean(true).isFalse() should return false", boolean07.isFalse());
		assertTrue("new TriStateBoolean(false).isFalse() should return false", boolean08.isFalse());

		assertFalse("new TriStateBoolean(\"true\").isFalse() should return false", boolean09.isFalse());
		assertTrue("new TriStateBoolean(\"false\").isFalse() should return true", boolean10.isFalse());
		assertTrue("new TriStateBoolean(\"anything\") should return false", boolean11.isFalse());
		assertFalse("new TriStateBoolean(\"undefined\") should return false", boolean12.isFalse());
		assertFalse("new TriStateBoolean((String) null).isFalse() should return false", boolean13.isFalse());
	}

	public void testIsTrue()
	{
		TriStateBoolean boolean01 = TriStateBoolean.TRUE;
		TriStateBoolean boolean02 = TriStateBoolean.FALSE;
		TriStateBoolean boolean03 = TriStateBoolean.UNDEFINED;

		TriStateBoolean boolean04 = new TriStateBoolean(Boolean.TRUE);
		TriStateBoolean boolean05 = new TriStateBoolean(Boolean.FALSE);
		TriStateBoolean boolean06 = new TriStateBoolean((Boolean) null);

		TriStateBoolean boolean07 = new TriStateBoolean(true);
		TriStateBoolean boolean08 = new TriStateBoolean(false);

		TriStateBoolean boolean09 = new TriStateBoolean("true");
		TriStateBoolean boolean10 = new TriStateBoolean("false");
		TriStateBoolean boolean11 = new TriStateBoolean("anything");
		TriStateBoolean boolean12 = new TriStateBoolean("undefined");
		TriStateBoolean boolean13 = new TriStateBoolean((String) null);

		assertTrue("TriStateBoolean.TRUE.isTrue() should return true", boolean01.isTrue());
		assertFalse("TriStateBoolean.FALSE.isTrue() should return false", boolean02.isTrue());
		assertFalse("TriStateBoolean.UNDEFINED.isTrue() should return false", boolean03.isTrue());

		assertTrue("TriStateBoolean(Boolean.TRUE).isTrue() should return true", boolean04.isTrue());
		assertFalse("TriStateBoolean(Boolean.FALSE).isTrue() should return false", boolean05.isTrue());
		assertFalse("new TriStateBoolean((Boolean) null) should return false", boolean06.isTrue());

		assertTrue("new TriStateBoolean(true).isTrue() should return true", boolean07.isTrue());
		assertFalse("new TriStateBoolean(false).isTrue() should return false", boolean08.isTrue());

		assertTrue("new TriStateBoolean(\"true\").isTrue() should return true", boolean09.isTrue());
		assertFalse("new TriStateBoolean(\"false\").isTrue() should return false", boolean10.isTrue());
		assertFalse("new TriStateBoolean(\"anything\") should return false", boolean11.isTrue());
		assertFalse("new TriStateBoolean(\"undefined\") should return false", boolean12.isTrue());
		assertFalse("new TriStateBoolean((String) null).isTrue() should return false", boolean13.isTrue());
	}

	public void testIsUndefined()
	{
		TriStateBoolean boolean01 = TriStateBoolean.TRUE;
		TriStateBoolean boolean02 = TriStateBoolean.FALSE;
		TriStateBoolean boolean03 = TriStateBoolean.UNDEFINED;

		TriStateBoolean boolean04 = new TriStateBoolean(Boolean.TRUE);
		TriStateBoolean boolean05 = new TriStateBoolean(Boolean.FALSE);
		TriStateBoolean boolean06 = new TriStateBoolean((Boolean) null);

		TriStateBoolean boolean07 = new TriStateBoolean(true);
		TriStateBoolean boolean08 = new TriStateBoolean(false);

		TriStateBoolean boolean09 = new TriStateBoolean("true");
		TriStateBoolean boolean10 = new TriStateBoolean("false");
		TriStateBoolean boolean11 = new TriStateBoolean("anything");
		TriStateBoolean boolean12 = new TriStateBoolean("undefined");
		TriStateBoolean boolean13 = new TriStateBoolean((String) null);

		assertFalse("TriStateBoolean.TRUE.isUndefined() should return false", boolean01.isUndefined());
		assertFalse("TriStateBoolean.FALSE.isUndefined() should return false", boolean02.isUndefined());
		assertTrue("TriStateBoolean.UNDEFINED.isUndefined() should return true", boolean03.isUndefined());

		assertFalse("TriStateBoolean(Boolean.TRUE).isUndefined() should return false", boolean04.isUndefined());
		assertFalse("TriStateBoolean(Boolean.FALSE).isUndefined() should return false", boolean05.isUndefined());
		assertTrue("new TriStateBoolean((Boolean) null) should return true", boolean06.isUndefined());

		assertFalse("new TriStateBoolean(true).isUndefined() should return false", boolean07.isUndefined());
		assertFalse("new TriStateBoolean(false).isUndefined() should return false", boolean08.isUndefined());

		assertFalse("new TriStateBoolean(\"true\").isUndefined() should return false", boolean09.isUndefined());
		assertFalse("new TriStateBoolean(\"false\").isUndefined() should return false", boolean10.isUndefined());
		assertFalse("new TriStateBoolean(\"anything\") should return false", boolean11.isUndefined());
		assertTrue("new TriStateBoolean(\"undefined\") should return true", boolean12.isUndefined());
		assertTrue("new TriStateBoolean((String) null).isUndefined() should return true", boolean13.isUndefined());
	}

	public void testToBoolean()
	{
		TriStateBoolean boolean01 = TriStateBoolean.valueOf("true");
		TriStateBoolean boolean02 = TriStateBoolean.valueOf("false");
		TriStateBoolean boolean03 = TriStateBoolean.valueOf("anything");
		TriStateBoolean boolean04 = TriStateBoolean.valueOf("undefined");
		TriStateBoolean boolean05 = TriStateBoolean.valueOf((String) null);

		assertTrue("TriStateBoolean.toBoolean(\"true\").isTrue() should return true", boolean01.isTrue());
		assertFalse("TriStateBoolean.toBoolean(\"true\").isFalse() should return false", boolean01.isFalse());
		assertFalse("TriStateBoolean.toBoolean(\"true\").isUndefined() should return false", boolean01.isUndefined());

		assertFalse("TriStateBoolean.toBoolean(\"false\").isTrue() should return false", boolean02.isTrue());
		assertTrue("TriStateBoolean.toBoolean(\"false\").isFalse() should return true", boolean02.isFalse());
		assertFalse("TriStateBoolean.toBoolean(\"false\").isUndefined() should return false", boolean02.isUndefined());

		assertFalse("TriStateBoolean.toBoolean(\"anything\").isTrue() should return false", boolean03.isTrue());
		assertTrue("TriStateBoolean.toBoolean(\"anything\").isFalse() should return true", boolean03.isFalse());
		assertFalse("TriStateBoolean.toBoolean(\"anything\").isUndefined() should return false", boolean03.isUndefined());

		assertFalse("TriStateBoolean.toBoolean(\"undefined\").isTrue() should return false", boolean04.isTrue());
		assertFalse("TriStateBoolean.toBoolean(\"undefined\").isFalse() should return false", boolean04.isFalse());
		assertTrue("TriStateBoolean.toBoolean(\"undefined\").isUndefined() should return true", boolean04.isUndefined());

		assertFalse("TriStateBoolean.toBoolean((String) null).isTrue() should return false", boolean05.isTrue());
		assertFalse("TriStateBoolean.toBoolean((String) null).isFalse() should return false", boolean05.isFalse());
		assertTrue("TriStateBoolean.toBoolean((String) null).isUndefined() should return true", boolean05.isUndefined());
	}

	/*
	 * Test for String toString()
	 */
	public void testToString()
	{
		TriStateBoolean boolean01 = TriStateBoolean.TRUE;
		TriStateBoolean boolean02 = TriStateBoolean.FALSE;
		TriStateBoolean boolean03 = TriStateBoolean.UNDEFINED;

		assertEquals("TriStateBoolean.TRUE.toString() should return true", boolean01.toString(), "true");
		assertEquals("TriStateBoolean.FALSE.toString() should return false", boolean02.toString(), "false");
		assertEquals("TriStateBoolean.UNDEFINED.toString() should return undefined", boolean03.toString(), "undefined");
	}

	public void testValueOf()
	{
		TriStateBoolean boolean01 = TriStateBoolean.valueOf("true");
		TriStateBoolean boolean02 = TriStateBoolean.valueOf("false");
		TriStateBoolean boolean03 = TriStateBoolean.valueOf("anything");
		TriStateBoolean boolean04 = TriStateBoolean.valueOf("undefined");
		TriStateBoolean boolean05 = TriStateBoolean.valueOf((String) null);

		assertTrue("TriStateBoolean.valueOf(\"true\").isTrue() should return true", boolean01.isTrue());
		assertFalse("TriStateBoolean.toBtoBooleanoolean(\"true\").isFalse() should return false", boolean01.isFalse());
		assertFalse("TriStateBoolean.valueOf(\"true\").isUndefined() should return false", boolean01.isUndefined());

		assertFalse("TriStateBoolean.valueOf(\"false\").isTrue() should return false", boolean02.isTrue());
		assertTrue("TriStateBoolean.toBotoBooleanolean(\"false\").isFalse() should return true", boolean02.isFalse());
		assertFalse("TriStateBoolean.valueOf(\"false\").isUndefined() should return false", boolean02.isUndefined());

		assertFalse("TriStateBoolean.valueOf(\"anything\").isTrue() should return false", boolean03.isTrue());
		assertTrue("TriStateBoolean.toBootoBooleanlean(\"anything\").isFalse() should return true", boolean03.isFalse());
		assertFalse("TriStateBoolean.valueOf(\"anything\").isUndefined() should return false", boolean03.isUndefined());

		assertFalse("TriStateBoolean.valueOf(\"undefined\").isTrue() should return false", boolean04.isTrue());
		assertFalse("TriStateBoolean.valueOf(\"undefined\").isFalse() should return false", boolean04.isFalse());
		assertTrue("TriStateBoolean.valueOf(\"undefined\").isUndefined() should return true", boolean04.isUndefined());

		assertFalse("TriStateBoolean.valueOf((String) null).isTrue() should return false", boolean05.isTrue());
		assertFalse("TriStateBoolean.valueOf((String) null).isFalse() should return false", boolean05.isFalse());
		assertTrue("TriStateBoolean.valueOf((String) null).isUndefined() should return true", boolean05.isUndefined());
	}
}
