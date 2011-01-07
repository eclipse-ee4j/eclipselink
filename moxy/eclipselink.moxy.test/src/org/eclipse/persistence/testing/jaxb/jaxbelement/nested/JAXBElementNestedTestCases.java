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
 *     Denise Smith - July 15, 2009
 ******************************************************************************/  

package org.eclipse.persistence.testing.jaxb.jaxbelement.nested;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.textui.TestRunner;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JAXBElementNestedTestCases extends JAXBTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/nested/root.xml";

	public JAXBElementNestedTestCases(String name) throws Exception {
		super(name);
		Class[] classes = new Class[2];
		classes[0] = ObjectFactory.class;
		classes[1] = Root.class;
		setClasses(classes);
		setControlDocument(XML_RESOURCE);
	}

	public Object getWriteControlObject() {
		Root root = new Root();

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		builderFactory.setIgnoringElementContentWhitespace(true);
		Document doc;
		try {
			doc = builderFactory.newDocumentBuilder().newDocument();		
			Element elem = doc.createElementNS("someuri", "ns0:elem2");
			QName qname = new QName("someuri", "elem2");

			JAXBElement jaxbElement = new JAXBElement(qname, Object.class, elem);
			root.setElem1(jaxbElement);

		} catch (Exception e) {
			fail("An exception occurred in getControlObject");
			e.printStackTrace();
		}
		return root;
	}

	public Object getControlObject() {
		Root root = new Root();

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		builderFactory.setIgnoringElementContentWhitespace(true);
		Document doc;
		try {
			doc = builderFactory.newDocumentBuilder().newDocument();
			
			Element elem = doc.createElementNS("someuri", "ns0:elem2");
			elem.setAttributeNS(XMLConstants.XMLNS_URL,XMLConstants.XMLNS + ":ns0", "someuri");
			
			QName qname = new QName("someuri", "elem2");

			JAXBElement jaxbElement = new JAXBElement(qname, Object.class, elem);
			root.setElem1(jaxbElement);

		} catch (Exception e) {
			fail("An exception occurred in getControlObject");
			e.printStackTrace();
		}
		return root;
	}

}
