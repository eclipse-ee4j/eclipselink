/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 19 July 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref.object;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Test scenario when ID / IDREF strings contain whitespace.  Also test marshal validation.
 * JAXB TCK 2.1a test: xml_schema/msData/datatypes/jaxb/test107447.xsd
 */
public class XmlIdRefObjectWhitespaceTestCases extends JAXBWithJSONTestCases {

    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/whitespace.xsd";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/whitespace.xml";
    private final static String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmlidref/whitespace_write.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/whitespace.json";
    private final static String JSON_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmlidref/whitespace_write.json";

    public XmlIdRefObjectWhitespaceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Whitespace.class });
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_RESOURCE_WRITE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_RESOURCE_WRITE);
    }

    protected Object getControlObject() {
        Whitespace w = new Whitespace();

        w.id = "abc";
        w.idref = w;
        w.idrefs.add(w);

        return w;
    }

    @Override
    public void testValidatingMarshal() {
        try {
            SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Schema schema = sf.newSchema(Thread.currentThread().getContextClassLoader().getResource(XSD_RESOURCE));

            Marshaller m = getJAXBContext().createMarshaller();
            m.setSchema(schema);

            m.marshal(getControlObject(), new ByteArrayOutputStream());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
