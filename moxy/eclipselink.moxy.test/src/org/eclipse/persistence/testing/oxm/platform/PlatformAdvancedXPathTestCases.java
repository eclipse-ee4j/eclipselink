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
package org.eclipse.persistence.testing.oxm.platform;

import java.io.InputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;

public class PlatformAdvancedXPathTestCases extends org.eclipse.persistence.testing.oxm.XMLTestCase {
	private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/platform/advanced_xpath.xml";
	private static final String XML_RESOURCE_NS = "org/eclipse/persistence/testing/oxm/platform/advanced_xpath_ns.xml";

	private XMLPlatform xmlPlatform;
	
	public PlatformAdvancedXPathTestCases(String name) {
        super(name);
    }
	
	public void setUp() {
		xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
	}
	
	public void testSelectSingleNodeAdvancedWithoutNamespace() {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
		Document document = xmlPlatform.newXMLParser().parse(inputStream);
		Node node = xmlPlatform.selectSingleNodeAdvanced(document.getDocumentElement(), "employee[@a='1']", null);
		Attr idAttr = (Attr) node.getAttributes().getNamedItem("id");
		assertEquals("1", idAttr.getValue());
	}

	public void testSelectSingleNodeAdvancedWithNamespace() {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_NS);
		Document document = xmlPlatform.newXMLParser().parse(inputStream);
		NamespaceResolver namespaceResolver = new NamespaceResolver();
		namespaceResolver.put("ns", "urn:example");
		Node node = xmlPlatform.selectSingleNodeAdvanced(document.getDocumentElement(), "ns:employee[@a='1']", namespaceResolver);
		Attr idAttr = (Attr) node.getAttributes().getNamedItem("id");
		assertEquals("1", idAttr.getValue());
	}

	public void testSelectNodesAdvancedWithoutNamespace() {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
		Document document = xmlPlatform.newXMLParser().parse(inputStream);
		NodeList nodeList = xmlPlatform.selectNodesAdvanced(document.getDocumentElement(), "employee[@a='1']", null);
		assertEquals(2, nodeList.getLength());
	}

	public void testSelectNodesAdvancedWithNamespace() {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_NS);
		Document document = xmlPlatform.newXMLParser().parse(inputStream);
		NamespaceResolver namespaceResolver = new NamespaceResolver();
		namespaceResolver.put("ns", "urn:example");
		NodeList nodeList = xmlPlatform.selectNodesAdvanced(document.getDocumentElement(), "ns:employee[@a='1']", namespaceResolver);
		assertEquals(2, nodeList.getLength());
	}

}
