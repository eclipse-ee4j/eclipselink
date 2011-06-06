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
package org.eclipse.persistence.tools.workbench.test.utility;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XMLToolsReadTests extends TestCase {
	private Document testDocument;
	private Node rootNode;

	public static Test suite() {
		return new TestSuite(XMLToolsReadTests.class);
	}

	public XMLToolsReadTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.testDocument = this.buildTestDocument();
		this.rootNode = XMLTools.child(this.testDocument, "root-element");
	}

	private Document buildTestDocument() {
		return XMLTools.parse(new StringReader(this.buildTestDocumentString()));
	}

	private String buildTestDocumentString() {
		StringBuffer sb = new StringBuffer(2000);
		sb.append("<?xml version = '1.0' encoding = 'UTF-8'?>");
		sb.append("<root-element>");
		sb.append(		"<element-0>");
		sb.append(			"<element-0-text-1>some text</element-0-text-1>");
		sb.append(			"<element-0-text-2></element-0-text-2>");
		sb.append(			"<element-0-text-3/>");
		sb.append(			"<element-0-non-text>");
		sb.append(				"<element-0-non-text-child>");
		sb.append(				"</element-0-non-text-child>");
		sb.append(			"</element-0-non-text>");
		sb.append(		"</element-0>");
		sb.append(		"<element-1>");
		sb.append(			"<element-1-int>42</element-1-int>");
		sb.append(			"<element-1-boolean-true-1>true</element-1-boolean-true-1>");
		sb.append(			"<element-1-boolean-true-2>T</element-1-boolean-true-2>");
		sb.append(			"<element-1-boolean-true-3>1</element-1-boolean-true-3>");
		sb.append(			"<element-1-boolean-false-1>false</element-1-boolean-false-1>");
		sb.append(			"<element-1-boolean-false-2>F</element-1-boolean-false-2>");
		sb.append(			"<element-1-boolean-false-3>0</element-1-boolean-false-3>");
		sb.append(		"</element-1>");
		sb.append(		"<element-2>");
		sb.append(			"<element-2.0>");
		sb.append(			"</element-2.0>");
		sb.append(			"<element-2.0>");
		sb.append(			"</element-2.0>");
		sb.append(			"<element-2.0>");
		sb.append(			"</element-2.0>");
		sb.append(		"</element-2>");
		sb.append(		"<element-3>element 3 contents</element-3>");
		sb.append("</root-element>");
		return sb.toString();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testChild() {
		assertEquals("element-1", XMLTools.child(this.rootNode, "element-1").getNodeName());
		assertEquals("element-3", XMLTools.child(this.rootNode, "element-3").getNodeName());
		assertEquals(null, XMLTools.child(this.rootNode, "element-1x"));
	}

	public void testChildren() {
		Node[] children = XMLTools.children(this.rootNode);
		assertEquals(4, children.length);
		for (int i = 1; i < 4; i++) {
			assertEquals("element-" + i, children[i].getNodeName());
		}
	}

	public void testChildrenNamed() {
		Node element2Node = XMLTools.child(this.rootNode, "element-2");
		Node[] children = XMLTools.children(element2Node, "element-2.0");
		assertEquals(3, children.length);
		for (int i = 0; i < children.length; i++) {
			assertEquals("element-2.0", children[i].getNodeName());
		}
	}

	public void testTextContent() {
		Node node = XMLTools.child(this.rootNode, "element-0");
		Node childNode = XMLTools.child(node, "element-0-text-1");
		assertEquals("some text", XMLTools.textContent(childNode));

		childNode = XMLTools.child(node, "element-0-text-2");
		assertEquals("", XMLTools.textContent(childNode));

		childNode = XMLTools.child(node, "element-0-text-3");
		assertEquals("", XMLTools.textContent(childNode));

		childNode = XMLTools.child(node, "element-0-non-text");
		boolean exCaught = false;
		try {
			String text = XMLTools.textContent(childNode);
			text = text.toString();
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testChildTextContent() {
		assertEquals("element 3 contents", XMLTools.childTextContent(this.rootNode, "element-3"));
	}

	public void testChildTextContentDefaultValue() {
		assertEquals("element 3 contents", XMLTools.childTextContent(this.rootNode, "element-3", "default value 3"));
		assertEquals("default value 4", XMLTools.childTextContent(this.rootNode, "element-4", "default value 4"));
	}

	public void testChildIntContent() {
		Node node = XMLTools.child(this.rootNode, "element-1");
		assertEquals(42, XMLTools.childIntContent(node, "element-1-int"));
	}

	public void testChildIntContentDefaultValue() {
		Node node = XMLTools.child(this.rootNode, "element-1");
		assertEquals(42, XMLTools.childIntContent(node, "element-1-int", 99));
		assertEquals(99, XMLTools.childIntContent(node, "element-1-int-x", 99));
	}

	public void testChildBooleanContent() {
		Node node = XMLTools.child(this.rootNode, "element-1");
		assertTrue(XMLTools.childBooleanContent(node, "element-1-boolean-true-1"));
		assertTrue(XMLTools.childBooleanContent(node, "element-1-boolean-true-2"));
		assertTrue(XMLTools.childBooleanContent(node, "element-1-boolean-true-3"));

		assertFalse(XMLTools.childBooleanContent(node, "element-1-boolean-false-1"));
		assertFalse(XMLTools.childBooleanContent(node, "element-1-boolean-false-2"));
		assertFalse(XMLTools.childBooleanContent(node, "element-1-boolean-false-3"));
	}

	public void testChildBooleanContentDefaultValue() {
		Node node = XMLTools.child(this.rootNode, "element-1");
		assertTrue(XMLTools.childBooleanContent(node, "element-1-boolean-true-1", false));
		assertTrue(XMLTools.childBooleanContent(node, "element-1-boolean-true-2", false));
		assertTrue(XMLTools.childBooleanContent(node, "element-1-boolean-true-3", false));
		assertFalse(XMLTools.childBooleanContent(node, "element-1-boolean-true-bogus", false));

		assertFalse(XMLTools.childBooleanContent(node, "element-1-boolean-false-1", true));
		assertFalse(XMLTools.childBooleanContent(node, "element-1-boolean-false-2", true));
		assertFalse(XMLTools.childBooleanContent(node, "element-1-boolean-false-3", true));
		assertTrue(XMLTools.childBooleanContent(node, "element-1-boolean-false-bogus", true));
	}

}
