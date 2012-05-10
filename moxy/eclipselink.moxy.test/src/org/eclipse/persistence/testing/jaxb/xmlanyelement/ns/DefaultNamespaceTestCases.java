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
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultNamespaceTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/ns/root.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/ns/root.json";

    public DefaultNamespaceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Root.class;
        setClasses(classes);
        
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("urn:test","ns1");
        namespaces.put("","ns2");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }
    
    protected JAXBMarshaller getJSONMarshaller() throws Exception{
    	   Map<String, String> namespaces = new HashMap<String, String>();
           namespaces.put("urn:test","ns1");
           namespaces.put("","ns2");
           JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
           jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
           jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
           return jsonMarshaller;
    }

    @Override
    protected Root getControlObject() {
        Root root = new Root();

        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        Document document = xmlPlatform.createDocument();
        Element element = document.createElement("child");
        root.setChild(element);

        return root;
    }

}