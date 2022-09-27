/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - Sep 2022 - - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Test for: Specifying QName in XmlVariableNode results namespace written twice
 *
 * https://github.com/eclipse-ee4j/eclipselink/issues/1709
 */
public class XmlVariableNodeQNameNS2TestCases extends JAXBWithJSONTestCases {
     protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootqname2NS.xml";
     protected final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootqname2NSWrite.xml";
     protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootqname2NS.json";

     private Marshaller marshaller = null;
     private Unmarshaller unmarshaller = null;


    public XmlVariableNodeQNameNS2TestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setClasses(new Class<?>[]{Data.class, DataChild.class});

    }

    @Override
    protected Marshaller getJSONMarshaller() throws Exception{
        if (marshaller == null) {
            Map<String, String> namespaces = new HashMap<>();
            namespaces.put("uri1:", "xxx");
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
            marshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        }
        return marshaller;
    }

    @Override
    protected Unmarshaller getJSONUnmarshaller() throws Exception{
        if (unmarshaller == null) {
            Map<String, String> namespaces = new HashMap<>();
            namespaces.put("uri1:", "xxx");
            unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
            unmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
        }
        return unmarshaller;
    }

    @Override
    protected Map getProperties() {
        Map overrides = new HashMap();
        String overridesString =
                "<?xml version='1.0' encoding='UTF-8'?>" +
                        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
                        "<xml-schema>" +
                        "<xml-ns namespace-uri='uri1:' prefix='xxx'/>" +
                        "</xml-schema>" +
                        "<java-types/>" +
                        "</xml-bindings>";

        DOMSource src = null;
        try {
            Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
            src = new DOMSource(doc.getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during setup");
        }

        overrides.put("org.eclipse.persistence.testing.jaxb.xmlvariablenode", src);

        Map props = new HashMap();
        props.put(JAXBContextProperties.OXM_METADATA_SOURCE, overrides);
        return props;
    }

    @Override
    protected Object getControlObject() {
        Data data = new Data();
        DataChild dataChild = new DataChild(new QName("uri1:", "childdata"));
        data.setChild(dataChild);

        return data;
    }
}
