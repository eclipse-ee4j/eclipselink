/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The XML APIs are a bit awkward, and they like to declare
 * "checked" exceptions (boo). This utility class simplifies using those APIs.
 * In particular, it facilitates the getting and setting of the values
 * of the children of a particular node (e.g. when reading and writing
 * the attributes of an object from and to an XML document).
 */
public final class XMLTools {

	/**The DOM parser factory. */
	private static DocumentBuilderFactory documentBuilderFactory;

	/**The DOM parser. Just keep one around and synchronize access to it. */
	private static DocumentBuilder documentBuilder;


	/**The transformer factory. */
	private static TransformerFactory transformerFactory;

	/**The transformer. Just keep one around and synchronize access to it. */
	private static Transformer transformer;


	// ********** parsing **********

	/**
	 * @see javax.xml.parsers.DocumentBuilderFactory#newInstance() for
	 * documentation on how the implementation class is determined
	 */
	private static synchronized DocumentBuilderFactory documentBuilderFactory() {
		if (documentBuilderFactory == null) {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
		}
		return documentBuilderFactory;
	}

	private static synchronized DocumentBuilder documentBuilder() {
		if (documentBuilder == null) {
			try {
				documentBuilder = documentBuilderFactory().newDocumentBuilder();
			} catch (ParserConfigurationException ex) {
				throw new RuntimeException(ex);
			}
		}
		return documentBuilder;
	}

	/**
	 * Build and return an XML document based on the contents
	 * of the specified input source.
	 * DocumentBuilder#parse(InputSource inputSource) throws RuntimeExceptions
	 */
	public static synchronized Document parse(InputSource inputSource) {
		try {
			return documentBuilder().parse(inputSource);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Build and return an XML document based on the contents
	 * of the specified reader.
	 * DocumentBuilder#parse(Reader reader)
	 */
	public static Document parse(Reader reader) {
		Document document = null;
		try {
			document = parse(new InputSource(reader));
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		return document;
	}

	/**
	 * Build and return an XML document based on the contents
	 * of the specified input stream.
	 * DocumentBuilder#parse(InputStream inputStream) throws RuntimeExceptions
	 */
	public static Document parse(InputStream inputStream) {
		try {
			return parse(new InputStreamReader(inputStream, "UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Build and return an XML document based on the contents
	 * of the specified file.
	 * DocumentBuilder#parse(File file) throws RuntimeExceptions
	 */
	public static Document parse(File file) {
		InputStream inputStream;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file), 8192);	// 8KB
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		Document document = parse(inputStream);
		try {
			inputStream.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return document;
	}


	// ********** reading **********

	/**
	 * Return the child element node of the specified parent node with
	 * the specified name. Return null if the child is not found.
	 * Node#getChildElement(String childName)
	 */
	public static Node child(Node parent, String childName) {
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			if ((child.getNodeType() == Node.ELEMENT_NODE)
					&& child.getNodeName().equals(childName)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Return all the child element nodes of the specified node.
	 * Node#getChildElements()
	 */
	public static Node[] children(Node node) {
		NodeList children = node.getChildNodes();
		int len = children.getLength();
		List result = new ArrayList(len);
		for (int i = 0; i < len; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				result.add(child);
			}
		}
		return (Node[]) result.toArray(new Node[result.size()]);
	}

	/**
	 * Return all the child element nodes of the specified node
	 * with the specified name.
	 * Node#getChildElements(String childName)
	 */
	public static Node[] children(Node node, String childName) {
		NodeList children = node.getChildNodes();
		int len = children.getLength();
		List result = new ArrayList(len);
		for (int i = 0; i < len; i++) {
			Node child = children.item(i);
			if ((child.getNodeType() == Node.ELEMENT_NODE)
					&& child.getNodeName().equals(childName)) {
				result.add(child);
			}
		}
		return (Node[]) result.toArray(new Node[result.size()]);
	}

	/**
	 * Return the text content of the specified node.
	 * Throw an exception if the node is not a "simple" node.
	 * Node#getTextContent()
	 */
	public static String textContent(Node node) {
		NodeList children = node.getChildNodes();
		// <foo></foo> or <foo/>
		if (children.getLength() == 0) {
			return "";
		}

		// <foo>bar</foo>
		if (children.getLength() == 1) {
			Node child = children.item(0);
			if (child.getNodeType() == Node.TEXT_NODE) {
				return node.getFirstChild().getNodeValue();
			}
		}

		// if this is not a "simple" node, throw an exception
		throw new IllegalArgumentException(node.getNodeName());
	}

	/**
	 * Return the text content of the specified child node.
	 * The child node must exist (or you will get a NPE).
	 * 
	 * For example, given the following XML:
	 * 
	 * 	<parent>
	 * 		<child>Charlie</child>
	 * 	</parent>
	 * 
	 * XMLTools.childTextContent(parentNode, "child")
	 * will return "Charlie".
	 * Node#getChildTextContent(String childName)
	 */
	public static String childTextContent(Node parent, String childName) {
		return textContent(child(parent, childName));
	}

	/**
	 * Return the text content of the specified child node.
	 * If the child node does not exist, return the specified default value.
	 * Node#getChildTextContent(String childName, String defaultValue)
	 */
	public static String childTextContent(Node parent, String childName, String defaultValue) {
		Node child = child(parent, childName);
		if (child == null) {
			return defaultValue;
		}
		return textContent(child);
	}

	/**
	 * Return the int content of the specified child node.
	 * The child node must exist (or you will get a NPE).
	 * Node#getChildIntContent(String childName)
	 */
	public static int childIntContent(Node parent, String childName) {
		return convertToInt(textContent(child(parent, childName)));
	}

	/**
	 * Return the int content of the specified child node.
	 * If the child node does not exist, return the specified default value.
	 * Node#getChildIntContent(String childName, int defaultValue)
	 */
	public static int childIntContent(Node parent, String childName, int defaultValue) {
		Node child = child(parent, childName);
		if (child == null) {
			return defaultValue;
		}
		return convertToInt(textContent(child));
	}

	/**
	 * Convert the specified string to an int.
	 */
	private static int convertToInt(String string) {
		return Integer.parseInt(string);
	}

	/**
	 * Return the boolean content of the specified child node.
	 * The child node must exist (or you will get a NPE).
	 * Node#getChildBooleanContent(String childName)
	 */
	public static boolean childBooleanContent(Node parent, String childName) {
		return convertToBoolean(textContent(child(parent, childName)));
	}

	/**
	 * Return the boolean content of the specified child node.
	 * If the child node does not exist, return the specified default value.
	 * Node#getChildBooleanContent(String childName, boolean defaultValue)
	 */
	public static boolean childBooleanContent(Node parent, String childName, boolean defaultValue) {
		Node child = child(parent, childName);
		if (child == null) {
			return defaultValue;
		}
		return convertToBoolean(textContent(child));
	}

	/**
	 * Convert the specified string to a boolean.
	 */
	private static boolean convertToBoolean(String string) {
		String s = string.toLowerCase();
		if (s.equals("t") || s.equals("true") || s.equals("1")) {
			return true;
		}
		if (s.equals("f") || s.equals("false") || s.equals("0")) {
			return false;
		}
		throw new IllegalArgumentException(string);
	}


	// ********** writing **********

	/**
	 * Build and return a new document. Once the document has been
	 * built, it can be printed later by calling XMLTools.print(Document, File)
	 * or XMLTools.print(Document, OutputStream).
	 */
	public static Document newDocument() {
		return documentBuilder().newDocument();
	}

	/**
	 * Add a simple text node with the specified name and text
	 * to the specified parent node.
	 * Node#addSimpleTextNode(String childName, String text)
	 */
	public static void addSimpleTextNode(Node parent, String childName, String text) {
		Node child = parent.getOwnerDocument().createElement(childName);
		Node childTextNode = parent.getOwnerDocument().createTextNode(text);
		child.appendChild(childTextNode);
		parent.appendChild(child);
	}

	/**
	 * Add a simple text node with the specified name and text
	 * to the specified parent node. If the text equals the default
	 * value, do not add the simple text node at all.
	 * Node#addSimpleTextNode(String childName, String text, String defaultValue)
	 */
	public static void addSimpleTextNode(Node parent, String childName, String text, String defaultValue) {
		if ( ! text.equals(defaultValue)) {
			addSimpleTextNode(parent, childName, text);
		}
	}

	/**
	 * Add a simple text node with the specified name and numeric text
	 * to the specified parent node.
	 * Node#addSimpleTextNode(String childName, int text)
	 */
	public static void addSimpleTextNode(Node parent, String childName, int text) {
		addSimpleTextNode(parent, childName, String.valueOf(text));
	}

	/**
	 * Add a simple text node with the specified name and numeric text
	 * to the specified parent node. If numeric text equals the default
	 * value, do not add the simple text node at all.
	 * Node#addSimpleTextNode(String childName, int text, int defaultValue)
	 */
	public static void addSimpleTextNode(Node parent, String childName, int text, int defaultValue) {
		if (text != defaultValue) {
			addSimpleTextNode(parent, childName, text);
		}
	}

	/**
	 * Add a simple text node with the specified name and boolean text
	 * to the specified parent node.
	 * Node#addSimpleTextNode(String childName, boolean text)
	 */
	public static void addSimpleTextNode(Node parent, String childName, boolean text) {
		addSimpleTextNode(parent, childName, String.valueOf(text));
	}

	/**
	 * Add a simple text node with the specified name and boolean text
	 * to the specified parent node. If the boolean text equals the default
	 * value, do not add the simple text node at all.
	 * Node#addSimpleTextNode(String childName, boolean text, boolean defaultValue)
	 */
	public static void addSimpleTextNode(Node parent, String childName, boolean text, boolean defaultValue) {
		if (text != defaultValue) {
			addSimpleTextNode(parent, childName, text);
		}
	}

	/**
	 * Add a list of simple text nodes with the specified name and text
	 * to the specified parent node's children node.
	 * 
	 * For example, the following call:
	 * 	XMLTools.addSimpleTextNodes(parentNode, "children", "child", new String[] {"foo", "bar", "baz"})
	 * will generate the following XML:
	 * 
	 * 	<parent>
	 * 		...
	 * 		<children>
	 * 			<child>foo</child>
	 * 			<child>bar</child>
	 * 			<child>baz</child>
	 * 		</children>
	 * 	</parent>
	 * 
	 * 
	 * will return a list of three "child" nodes.
	 * Node#addSimpleTextNodes(String childrenName, String childName, String[] childrenTexts)
	 */
	public static void addSimpleTextNodes(Node parent, String childrenName, String childName, String[] childrenTexts) {
		Node childrenNode = parent.getOwnerDocument().createElement(childrenName);
		parent.appendChild(childrenNode);
		int len = childrenTexts.length;
		for (int i = 0; i < len; i++) {
			addSimpleTextNode(childrenNode, childName, childrenTexts[i]);
		}
	}

	/**
	 * @see javax.xml.transform.TransformerFactory#newInstance() for
	 * documentation on how the implementation class is determined
	 */
	private static synchronized TransformerFactory transformerFactory() {
		if (transformerFactory == null) {
			transformerFactory = TransformerFactory.newInstance();
		}
		return transformerFactory;
	}

	private static synchronized Transformer transformer() {
		if (transformer == null) {
			try {
				transformer = transformerFactory().newTransformer();
			} catch (TransformerConfigurationException ex) {
				throw new RuntimeException(ex);
			}
			try {
				transformer.setOutputProperty("indent", "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "3");
			} catch (IllegalArgumentException ex) {
				// ignore exception - the output will still be valid XML, it just won't be very user-friendly
			}
		}
		return transformer;
	}

	/**
	 * Print the specified source to the specified result.
	 */
	public static synchronized void print(Source source, Result result) {
		try {
			transformer().transform(source, result);
		} catch (TransformerException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Print the specified document to the specified output stream.
	 * Document#print(OutputStream outputStream)
	 */
	public static void print(Document document, OutputStream outputStream) {
		print(new DOMSource(document), new StreamResult(outputStream));
	}

	/**
	 * Print the previously built document to the specified file.
	 * Document#print(File file)
	 */
	public static void print(Document document, File file) {
		OutputStream outputStream;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(file), 8192);	// 8KB
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		print(document, outputStream);
		try {
			outputStream.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}


	// ********** constructor **********

	/**
	 * Suppress default constructor, ensuring non-instantiability.
	 */
	private XMLTools() {
		super();
		throw new UnsupportedOperationException();
	}

}
