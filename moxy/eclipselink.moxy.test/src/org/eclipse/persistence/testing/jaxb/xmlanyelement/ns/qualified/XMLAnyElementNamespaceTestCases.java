/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - 2.3.1
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

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
        namespaces.put("namespace","ns1");
       namespaces.put("someuri","ns2");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }
    
    protected JAXBMarshaller getJSONMarshaller() throws Exception{
    	   Map<String, String> namespaces = new HashMap<String, String>();
           namespaces.put("namespace","ns1");
           namespaces.put("someuri","ns2");
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
            cust.anyElem = doc.createElementNS("someuri", "localName");

        } catch(Exception ex) {
            fail("unexpected exception creating control object");
        }
        return cust;
    }
}
