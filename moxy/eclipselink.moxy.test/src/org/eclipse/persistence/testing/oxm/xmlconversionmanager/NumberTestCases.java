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
 *   Denise Smith - April 23/2009 - 2.0 
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import java.math.BigDecimal;

import junit.textui.TestRunner;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class NumberTestCases extends OXTestCase {
	
	XMLConversionManager xmlConversionManager;
	
	public void setUp() {
		xmlConversionManager = XMLConversionManager.getDefaultXMLManager();
	}
	
	public NumberTestCases(String name) {
		super(name);
	}
	
	public void testConvertWithPlusToByte() {
		Byte controlByte = new Byte("1");
		Byte testByte = (Byte) xmlConversionManager.convertObject("+1", Byte.class);
		String testString = String.valueOf(testByte);
		String controlString = String.valueOf(controlByte);
		assertEquals(controlString, testString);
	}
	
	public void testConvertWithPlusToInteger() {
		Integer controlInteger = new Integer("1");
		Integer testInteger = (Integer) xmlConversionManager.convertObject("+1", Integer.class);
		String testString = String.valueOf(testInteger);
		String controlString = String.valueOf(controlInteger);
		assertEquals(controlString, testString);
	}
	
	public void testConvertWithMinuToByte() {
		Byte controlByte = new Byte("-1");
		Byte testByte = (Byte) xmlConversionManager.convertObject("-1", Byte.class);
		String testString = String.valueOf(testByte);
		String controlString = String.valueOf(controlByte);
		assertEquals(controlString, testString);
	}
	
	public void testConvertWithMinusToInteger() {
		Integer controlInteger = new Integer("-1");
		Integer testInteger = (Integer) xmlConversionManager.convertObject("-1", Integer.class);
		String testString = String.valueOf(testInteger);
		String controlString = String.valueOf(controlInteger);
		assertEquals(controlString, testString);
	}

	public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlconversionmanager.NumberTestCases" };
        TestRunner.main(arguments);
    }

}
