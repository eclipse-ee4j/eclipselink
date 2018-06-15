/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//   Denise Smith - April 23/2009 - 2.0
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.textui.TestRunner;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
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

    public void testConvertEmptyStringTo_byte() {
        byte test = (Byte) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, byte.class);
        assertEquals(0, test);
    }

    public void testConvertEmptyStringTo_Byte() {
        Byte test = (Byte) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, Byte.class);
        assertEquals(0, test.byteValue());
    }

    public void testConvertEmptyStringTo_double() {
        double test = (Double) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, double.class);
        assertEquals(0.0, test);
    }

    public void testConvertEmptyStringTo_Double() {
        Double test = (Double) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, Double.class);
        assertEquals(0.0, test.doubleValue());
    }

    public void testConvertEmptyStringTo_float() {
        float test = (Float) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, float.class);
        assertEquals(0.0, test, 0);
    }

    public void testConvertEmptyStringTo_Float() {
        Float test = (Float) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, Float.class);
        assertEquals(0.0, test.floatValue(), 0);
    }

    public void testConvertEmptyStringTo_int() {
        int test = (Integer) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, int.class);
        assertEquals(0, test);
    }

    public void testConvertEmptyStringTo_Integer() {
        Integer test = (Integer) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, Integer.class);
        assertEquals(0, test.intValue());
    }

    public void testConvertEmptyStringTo_long() {
        long test = (Long) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, long.class);
        assertEquals(0, test);
    }

    public void testConvertEmptyStringTo_Long() {
        Long test = (Long) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, Long.class);
        assertEquals(0, test.longValue());
    }

    public void testConvertEmptyStringTo_short() {
        short test = (Short) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, short.class);
        assertEquals(0, test);
    }

    public void testConvertEmptyStringTo_Short() {
        Short test = (Short) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, Short.class);
        assertEquals(0, test.shortValue());
    }

    public void testConvertEmptyStringTo_BigDecimal() {
        BigDecimal test = (BigDecimal) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, BigDecimal.class);
        assertNull(test);
    }

    public void testConvertEmptyStringTo_BigInteger() {
        BigInteger test = (BigInteger) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, BigInteger.class);
        assertNull(test);
    }

    // Bug 21561562 - xmlconversionmanager bigdecimal to xsd:decimal is wrong
    public void testConvertBigDecimalTo_String() {
        String controlString = "0.000000001";
        BigDecimal controlBigDecimal = new BigDecimal(controlString);
        String testString = (String)xmlConversionManager.convertObject(controlBigDecimal, String.class);
        assertEquals(controlString, testString);
    }

    // Bug 21561562 - xmlconversionmanager bigdecimal to xsd:decimal is wrong
    public void testConvertBigDecimalWithQNameTo_String() {
        String controlString = "0.000000001";
        BigDecimal controlBigDecimal = new BigDecimal(controlString);
        String testString = (String)xmlConversionManager.convertObject(controlBigDecimal, String.class, XMLConstants.STRING_QNAME);
        assertEquals(controlString, testString);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlconversionmanager.NumberTestCases" };
        TestRunner.main(arguments);
    }
}
