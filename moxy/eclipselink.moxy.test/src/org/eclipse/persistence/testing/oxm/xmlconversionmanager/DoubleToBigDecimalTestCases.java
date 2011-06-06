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
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import java.math.BigDecimal;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class DoubleToBigDecimalTestCases extends OXTestCase {
	
	XMLConversionManager xmlConversionManager;
	
	public void setUp() {
		xmlConversionManager = XMLConversionManager.getDefaultXMLManager();
	}
	
	public DoubleToBigDecimalTestCases(String name) {
		super(name);
	}
	
	public void testConvertDoubleToBigDecimal1() {
		String controlString = "1.1";
		Double controlDouble = new Double(controlString);
		BigDecimal testBigDecimal = (BigDecimal) xmlConversionManager.convertObject(controlDouble, BigDecimal.class);
		String testString = String.valueOf(testBigDecimal);
		assertEquals(controlString, testString);
	}

	public void testConvertDoubleToBigDecimal2a() {
		String controlString = "1";
		Double controlDouble = new Double(1);
		BigDecimal testBigDecimal = (BigDecimal) xmlConversionManager.convertObject(controlDouble, BigDecimal.class);
		String testString = String.valueOf(testBigDecimal);
		assertEquals(controlString, testString);
	}

	public void testConvertDoubleToBigDecimal2b() {
		String controlString = "1";
		Double controlDouble = new Double(1.0);
		BigDecimal testBigDecimal = (BigDecimal) xmlConversionManager.convertObject(controlDouble, BigDecimal.class);
		String testString = String.valueOf(testBigDecimal);
		assertEquals(controlString, testString);
	}

	public void testConvertDoubleToBigDecimal3a() {
		String controlString = "1.0E-10";
		Double controlDouble = new Double(controlString);
		BigDecimal testBigDecimal = (BigDecimal) xmlConversionManager.convertObject(controlDouble, BigDecimal.class);
		String testString = String.valueOf(testBigDecimal);
		assertEquals(controlString, testString);
	}

	public void testConvertDoubleToBigDecimal3b() {
		String controlString = "0.00000000010";
		Double controlDouble = new Double(controlString);
		BigDecimal testBigDecimal = (BigDecimal) xmlConversionManager.convertObject(controlDouble, BigDecimal.class);
		String testString = String.valueOf(testBigDecimal);
		assertEquals("1.0E-10", testString);
	}
	
	public void testConvertDoubleToBigDecimal4a() {
		String controlString = "1000000000";
		Double controlDouble = new Double(1000000000);
		BigDecimal testBigDecimal = (BigDecimal) xmlConversionManager.convertObject(controlDouble, BigDecimal.class);
		String testString = String.valueOf(testBigDecimal);
		assertEquals("1.0E+9", testString);
	}

	public void testConvertDoubleToBigDecimal4b() {
		String controlString = "1000000000";
		Double controlDouble = new Double(1000000000.0);
		BigDecimal testBigDecimal = (BigDecimal) xmlConversionManager.convertObject(controlDouble, BigDecimal.class);
		String testString = String.valueOf(testBigDecimal);
		assertEquals("1.0E+9", testString);
	}
	
}
