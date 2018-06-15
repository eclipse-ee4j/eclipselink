/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Matt MacIvor - 2.3.1
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLAnyElementNamespaceTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/ns/qualified/customer.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/ns/qualified/customer.json";

    public XMLAnyElementNamespaceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Customer.class;
        setClasses(classes);
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("namespace", "ns1");
        namespaces.put("someuri", "ns2");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    protected JAXBMarshaller getJSONMarshaller() throws Exception {
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("namespace", "ns1");
        namespaces.put("someuri", "ns2");
        JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
        jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        return jsonMarshaller;
    }

    protected Object getControlObject() {
        Customer cust = new Customer();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element elem = doc.createElementNS("someuri", "localName");
            elem.setAttributeNS(XMLConstants.XMLNS_URL, "xmlns", "someuri");

            cust.anyElem = elem;
        } catch (Exception ex) {
            fail("unexpected exception creating control object");
        }
        return cust;
    }

    public void testAttributeNoNamespace() throws Exception {
        // Test for bug 410482
        final String XML = "<root xmlns='http://www.oracle.com'><elem attr='value'/></root>";

        JAXBContext context = JAXBContextFactory.createContext(new Class[] { Root.class }, null);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Root root = (Root) unmarshaller.unmarshal(new StringReader(XML));
        Attr attr = root.element.getAttributeNodeNS(null, "attr");
        assertNotNull("Attribute node name was null.", attr.getNodeName());
        assertNotNull("Attribute local name was null.", attr.getLocalName());
    }

    @XmlRootElement(namespace = "http://www.oracle.com", name = "root")
    public static class Root {
        @XmlAnyElement
        public Element element;
    }

}
