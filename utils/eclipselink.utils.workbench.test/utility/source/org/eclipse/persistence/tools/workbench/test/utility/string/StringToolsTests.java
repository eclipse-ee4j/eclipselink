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
package org.eclipse.persistence.tools.workbench.test.utility.string;

import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public class StringToolsTests extends TestCase {

	public static Test suite() {
		return new TestSuite(StringToolsTests.class);
	}
	
	public StringToolsTests(String name) {
		super(name);
	}
	
	public void testCapitalizeCharArray() {
		this.verifyCapitalizeCharArray("Oracle", new char[] {'O','r','a','c','l','e'});
		this.verifyCapitalizeCharArray("Oracle", new char[] {'o','r','a','c','l','e'});
		this.verifyCapitalizeCharArray("   ", new char[] {' ',' ',' '});
		this.verifyCapitalizeCharArray("ORACLE", new char[] {'O','R','A','C','L','E'});
		this.verifyCapitalizeCharArray("", new char[0]);
		this.verifyCapitalizeCharArray("A", new char[] {'a'});
		this.verifyCapitalizeCharArray("\u00C9cole", new char[] {'\u00E9','c','o','l','e'});
	}

	private void verifyCapitalizeCharArray(String expected, char[] string) {
		assertEquals(expected, new String(StringTools.capitalize(string)));
	}

	public void testCapitalizeString() {
		verifyCapitalizeString("Oracle", "Oracle");
		verifyCapitalizeString("Oracle", "oracle");
		verifyCapitalizeString("   ", "   ");
		verifyCapitalizeString("ORACLE", "ORACLE");
		verifyCapitalizeString("", "");
		verifyCapitalizeString("A", "a");
		verifyCapitalizeString("\u00C9cole", "\u00E9cole"); // �cole->�COLE
	}

	private void verifyCapitalizeString(String expected, String string) {
		assertEquals(expected, StringTools.capitalize(string));
	}

	public void testCapitalizeOnCharArrayStringBuffer() {
		this.verifyCapitalizeOnCharArrayStringBuffer("Oracle", new char[] {'O','r','a','c','l','e'});
		this.verifyCapitalizeOnCharArrayStringBuffer("Oracle", new char[] {'o','r','a','c','l','e'});
		this.verifyCapitalizeOnCharArrayStringBuffer("   ", new char[] {' ',' ',' '});
		this.verifyCapitalizeOnCharArrayStringBuffer("ORACLE", new char[] {'O','R','A','C','L','E'});
		this.verifyCapitalizeOnCharArrayStringBuffer("", new char[0]);
		this.verifyCapitalizeOnCharArrayStringBuffer("A", new char[] {'a'});
		this.verifyCapitalizeOnCharArrayStringBuffer("\u00C9cole", new char[] {'\u00E9','c','o','l','e'});
	}

	private void verifyCapitalizeOnCharArrayStringBuffer(String expected, char[] string) {
		StringBuffer sb = new StringBuffer();
		StringTools.capitalizeOn(string, sb);
		assertEquals(expected, sb.toString());
	}

	public void testCapitalizeOnStringStringBuffer() {
		verifyCapitalizeOnStringStringBuffer("Oracle", "Oracle");
		verifyCapitalizeOnStringStringBuffer("Oracle", "oracle");
		verifyCapitalizeOnStringStringBuffer("   ", "   ");
		verifyCapitalizeOnStringStringBuffer("ORACLE", "ORACLE");
		verifyCapitalizeOnStringStringBuffer("", "");
		verifyCapitalizeOnStringStringBuffer("A", "a");
		verifyCapitalizeOnStringStringBuffer("\u00C9cole", "\u00E9cole"); // �cole->�COLE
	}

	private void verifyCapitalizeOnStringStringBuffer(String expected, String string) {
		StringBuffer sb = new StringBuffer();
		StringTools.capitalizeOn(string, sb);
		assertEquals(expected, sb.toString());
	}

	public void testCapitalizeOnCharArrayWriter() {
		this.verifyCapitalizeOnCharArrayWriter("Oracle", new char[] {'O','r','a','c','l','e'});
		this.verifyCapitalizeOnCharArrayWriter("Oracle", new char[] {'o','r','a','c','l','e'});
		this.verifyCapitalizeOnCharArrayWriter("   ", new char[] {' ',' ',' '});
		this.verifyCapitalizeOnCharArrayWriter("ORACLE", new char[] {'O','R','A','C','L','E'});
		this.verifyCapitalizeOnCharArrayWriter("", new char[0]);
		this.verifyCapitalizeOnCharArrayWriter("A", new char[] {'a'});
		this.verifyCapitalizeOnCharArrayWriter("\u00C9cole", new char[] {'\u00E9','c','o','l','e'});
	}

	private void verifyCapitalizeOnCharArrayWriter(String expected, char[] string) {
		Writer writer = new StringWriter();
		StringTools.capitalizeOn(string, writer);
		assertEquals(expected, writer.toString());
	}

	public void testCapitalizeOnStringWriter() {
		verifyCapitalizeOnStringWriter("Oracle", "Oracle");
		verifyCapitalizeOnStringWriter("Oracle", "oracle");
		verifyCapitalizeOnStringWriter("   ", "   ");
		verifyCapitalizeOnStringWriter("ORACLE", "ORACLE");
		verifyCapitalizeOnStringWriter("", "");
		verifyCapitalizeOnStringWriter("A", "a");
		verifyCapitalizeOnStringWriter("\u00C9cole", "\u00E9cole"); // �cole->�COLE
	}

	private void verifyCapitalizeOnStringWriter(String expected, String string) {
		Writer writer = new StringWriter();
		StringTools.capitalizeOn(string, writer);
		assertEquals(expected, writer.toString());
	}

	public void testUnapitalizeCharArray() {
		this.verifyUncapitalizeCharArray("oracle", new char[] {'O','r','a','c','l','e'});
		this.verifyUncapitalizeCharArray("oracle", new char[] {'o','r','a','c','l','e'});
		this.verifyUncapitalizeCharArray("   ", new char[] {' ',' ',' '});
		this.verifyUncapitalizeCharArray("oRACLE", new char[] {'O','R','A','C','L','E'});
		this.verifyUncapitalizeCharArray("", new char[0]);
		this.verifyUncapitalizeCharArray("a", new char[] {'A'});
		this.verifyUncapitalizeCharArray("\u00E9cole", new char[] {'\u00C9','c','o','l','e'});
	}

	private void verifyUncapitalizeCharArray(String expected, char[] string) {
		assertEquals(expected, new String(StringTools.uncapitalize(string)));
	}

	public void testUncapitalizeString() {
		verifyUncapitalizeString("oracle", "Oracle");
		verifyUncapitalizeString("oracle", "oracle");
		verifyUncapitalizeString("   ", "   ");
		verifyUncapitalizeString("oRACLE", "ORACLE");
		verifyUncapitalizeString("", "");
		verifyUncapitalizeString("a", "A");
		verifyUncapitalizeString("\u00E9cole", "\u00C9cole"); // �cole->�COLE
	}

	public void testUncapitalizeJavaBeanString() {
		verifyUncapitalizeJavaBeanString("oracle", "Oracle");
		verifyUncapitalizeJavaBeanString("oracle", "oracle");
		verifyUncapitalizeJavaBeanString("   ", "   ");
		verifyUncapitalizeJavaBeanString("ORACLE", "ORACLE");
		verifyUncapitalizeJavaBeanString("LName", "LName");
		verifyUncapitalizeJavaBeanString("", "");
		verifyUncapitalizeJavaBeanString("a", "A");
		verifyUncapitalizeJavaBeanString("\u00E9cole", "\u00C9cole"); // �cole->�COLE
	}
	private void verifyUncapitalizeString(String expected, String string) {
		assertEquals(expected, StringTools.uncapitalize(string));
	}

	private void verifyUncapitalizeJavaBeanString(String expected, String string) {
		assertEquals(expected, StringTools.uncapitalizeJavaBean(string));
	}

	public void testUncapitalizeOnCharArrayStringBuffer() {
		this.verifyUncapitalizeOnCharArrayStringBuffer("oracle", new char[] {'O','r','a','c','l','e'});
		this.verifyUncapitalizeOnCharArrayStringBuffer("oracle", new char[] {'o','r','a','c','l','e'});
		this.verifyUncapitalizeOnCharArrayStringBuffer("   ", new char[] {' ',' ',' '});
		this.verifyUncapitalizeOnCharArrayStringBuffer("oRACLE", new char[] {'O','R','A','C','L','E'});
		this.verifyUncapitalizeOnCharArrayStringBuffer("", new char[0]);
		this.verifyUncapitalizeOnCharArrayStringBuffer("a", new char[] {'A'});
		this.verifyUncapitalizeOnCharArrayStringBuffer("\u00E9cole", new char[] {'\u00C9','c','o','l','e'});
	}

	private void verifyUncapitalizeOnCharArrayStringBuffer(String expected, char[] string) {
		StringBuffer sb = new StringBuffer();
		StringTools.uncapitalizeOn(string, sb);
		assertEquals(expected, sb.toString());
	}

	public void testUncapitalizeOnStringStringBuffer() {
		verifyUncapitalizeOnStringStringBuffer("oracle", "Oracle");
		verifyUncapitalizeOnStringStringBuffer("oracle", "oracle");
		verifyUncapitalizeOnStringStringBuffer("   ", "   ");
		verifyUncapitalizeOnStringStringBuffer("oRACLE", "ORACLE");
		verifyUncapitalizeOnStringStringBuffer("", "");
		verifyUncapitalizeOnStringStringBuffer("a", "A");
		verifyUncapitalizeOnStringStringBuffer("\u00E9cole", "\u00C9cole"); // �cole->�COLE
	}

	private void verifyUncapitalizeOnStringStringBuffer(String expected, String string) {
		StringBuffer sb = new StringBuffer();
		StringTools.uncapitalizeOn(string, sb);
		assertEquals(expected, sb.toString());
	}

	public void testUncapitalizeOnCharArrayWriter() {
		this.verifyUncapitalizeOnCharArrayWriter("oracle", new char[] {'O','r','a','c','l','e'});
		this.verifyUncapitalizeOnCharArrayWriter("oracle", new char[] {'o','r','a','c','l','e'});
		this.verifyUncapitalizeOnCharArrayWriter("   ", new char[] {' ',' ',' '});
		this.verifyUncapitalizeOnCharArrayWriter("oRACLE", new char[] {'O','R','A','C','L','E'});
		this.verifyUncapitalizeOnCharArrayWriter("", new char[0]);
		this.verifyUncapitalizeOnCharArrayWriter("a", new char[] {'A'});
		this.verifyUncapitalizeOnCharArrayWriter("\u00E9cole", new char[] {'\u00C9','c','o','l','e'});
	}

	private void verifyUncapitalizeOnCharArrayWriter(String expected, char[] string) {
		Writer writer = new StringWriter();
		StringTools.uncapitalizeOn(string, writer);
		assertEquals(expected, writer.toString());
	}

	public void testUncapitalizeOnStringWriter() {
		verifyUncapitalizeOnStringWriter("oracle", "Oracle");
		verifyUncapitalizeOnStringWriter("oracle", "oracle");
		verifyUncapitalizeOnStringWriter("   ", "   ");
		verifyUncapitalizeOnStringWriter("oRACLE", "ORACLE");
		verifyUncapitalizeOnStringWriter("", "");
		verifyUncapitalizeOnStringWriter("a", "A");
		verifyUncapitalizeOnStringWriter("\u00E9cole", "\u00C9cole"); // �cole->�COLE
	}

	private void verifyUncapitalizeOnStringWriter(String expected, String string) {
		Writer writer = new StringWriter();
		StringTools.uncapitalizeOn(string, writer);
		assertEquals(expected, writer.toString());
	}

	public void testRemoveFirstOccurrence() {
		this.verifyRemoveFirstOccurrence("Emplo&yee", '&', "Employee");
		this.verifyRemoveFirstOccurrence("Emplo&yee&", '&', "Employee&");
		this.verifyRemoveFirstOccurrence("Employee &Foo", '&', "Employee Foo");
		this.verifyRemoveFirstOccurrence("Employee&", '&', "Employee");
		this.verifyRemoveFirstOccurrence("&Employee", '&', "Employee");		
	}
	
	private void verifyRemoveFirstOccurrence(String string, char charToRemove, String expectedString) {
		assertEquals(expectedString, StringTools.removeFirstOccurrence(string, charToRemove));
	}

	public void testRemoveAllOccurrences() {
		this.verifyRemoveAllOccurrences("Employee Fred", ' ', "EmployeeFred");
		this.verifyRemoveAllOccurrences(" Employee ", ' ', "Employee");
		this.verifyRemoveAllOccurrences("Employee   Foo", ' ', "EmployeeFoo");
		this.verifyRemoveAllOccurrences(" Emp loyee   Foo", ' ', "EmployeeFoo");
	}
	
	private void verifyRemoveAllOccurrences(String string, char charToRemove, String expectedString) {
		assertEquals(expectedString, StringTools.removeAllOccurrences(string, charToRemove));
	}

	public void testConvertCamelBackToAllCaps() {
		assertEquals("TEST", StringTools.convertCamelBackToAllCaps("test"));
		assertEquals("TEST", StringTools.convertCamelBackToAllCaps("TEST"));
		assertEquals("TEST_TEST", StringTools.convertCamelBackToAllCaps("testTest"));
		assertEquals("TEST_TEST", StringTools.convertCamelBackToAllCaps("TestTest"));
		assertEquals("TEST_TEST_TEST", StringTools.convertCamelBackToAllCaps("testTESTTest"));
		assertEquals("TEST_TEST_TEST", StringTools.convertCamelBackToAllCaps("TestTESTTest"));
		assertEquals("TEST_TEST_TEST_T", StringTools.convertCamelBackToAllCaps("TestTESTTestT"));
	}

	public void testConvertCamelBackToAllCapsMaxLength() {
		assertEquals("TEST", StringTools.convertCamelBackToAllCaps("test", 44));
		assertEquals("TEST", StringTools.convertCamelBackToAllCaps("test", 4));
		assertEquals("TES", StringTools.convertCamelBackToAllCaps("test", 3));
		assertEquals("TEST", StringTools.convertCamelBackToAllCaps("TEST", 5));
		assertEquals("TE", StringTools.convertCamelBackToAllCaps("TEST", 2));
		assertEquals("TEST_TEST", StringTools.convertCamelBackToAllCaps("testTest", 9));
		assertEquals("TEST_TES", StringTools.convertCamelBackToAllCaps("testTest", 8));
		assertEquals("TEST_T", StringTools.convertCamelBackToAllCaps("testTest", 6));
		assertEquals("TEST_", StringTools.convertCamelBackToAllCaps("testTest", 5));
		assertEquals("TEST", StringTools.convertCamelBackToAllCaps("testTest", 4));
		assertEquals("TEST_TEST", StringTools.convertCamelBackToAllCaps("TestTest", 9));
		assertEquals("TEST_TEST", StringTools.convertCamelBackToAllCaps("TestTest", 1100));
		assertEquals("TEST_TEST_", StringTools.convertCamelBackToAllCaps("testTESTTest", 10));
		assertEquals("TEST_TEST_TEST", StringTools.convertCamelBackToAllCaps("TestTESTTest", 14));
		assertEquals("TEST_TEST_TEST_T", StringTools.convertCamelBackToAllCaps("TestTESTTestT", 16));
		assertEquals("TEST_TEST_TEST_", StringTools.convertCamelBackToAllCaps("TestTESTTestT", 15));
	}

	public void testConvertAllCapsToCamelBack() {
		assertEquals("test", StringTools.convertAllCapsToCamelBack("TEST", false));
		assertEquals("Test", StringTools.convertAllCapsToCamelBack("TEST", true));
		assertEquals("test", StringTools.convertAllCapsToCamelBack("TeST", false));
		assertEquals("testTest", StringTools.convertAllCapsToCamelBack("TEST_TEST", false));
		assertEquals("TestTest", StringTools.convertAllCapsToCamelBack("TEST_TEST", true));
		assertEquals("testTestTest", StringTools.convertAllCapsToCamelBack("TEST_TEST_TEST", false));
		assertEquals("TestTestTest", StringTools.convertAllCapsToCamelBack("TEST_TEST_TEST", true));
		assertEquals("testTestTestT", StringTools.convertAllCapsToCamelBack("TEST_TEST_TEST_T", false));
		assertEquals("TestTestTestT", StringTools.convertAllCapsToCamelBack("TEST_TEST_TEST_T", true));
	}

	public void testPad() {
		assertEquals("fred", StringTools.pad("fred", 4));
		assertEquals("fred  ", StringTools.pad("fred", 6));
		boolean exThrown = false;
		try {
			assertEquals("fr", StringTools.pad("fred", 2));
		} catch (IllegalArgumentException ex) {
			exThrown = true;
		}
		assertTrue(exThrown);
	}

	public void testPadOrTruncate() {
		assertEquals("fred", StringTools.padOrTruncate("fred", 4));
		assertEquals("fred  ", StringTools.padOrTruncate("fred", 6));
		assertEquals("fr", StringTools.padOrTruncate("fred", 2));
	}

	public void testZeroPad() {
		assertEquals("1234", StringTools.zeroPad("1234", 4));
		assertEquals("001234", StringTools.zeroPad("1234", 6));
		boolean exThrown = false;
		try {
			assertEquals("12", StringTools.zeroPad("1234", 2));
		} catch (IllegalArgumentException ex) {
			exThrown = true;
		}
		assertTrue(exThrown);
	}

	public void testZeroPadOrTruncate() {
		assertEquals("1234", StringTools.zeroPadOrTruncate("1234", 4));
		assertEquals("001234", StringTools.zeroPadOrTruncate("1234", 6));
		assertEquals("34", StringTools.zeroPadOrTruncate("1234", 2));
	}

	public void testCommonPrefixLength() {
		assertEquals(3, StringTools.commonPrefixLength("fooZZZ", "fooBBB"));
		assertEquals(3, StringTools.commonPrefixLength("foo", "fooBBB"));
		assertEquals(3, StringTools.commonPrefixLength("fooZZZ", "foo"));
		assertEquals(3, StringTools.commonPrefixLength("foo", "foo"));
	}

}
