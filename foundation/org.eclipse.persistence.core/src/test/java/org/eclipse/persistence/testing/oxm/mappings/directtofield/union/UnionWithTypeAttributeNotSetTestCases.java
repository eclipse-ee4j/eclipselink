/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.union;

import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class UnionWithTypeAttributeNotSetTestCases extends UnionWithTypeAttributeTestCases {
    public UnionWithTypeAttributeNotSetTestCases(String name) throws Exception {
        super(name);
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/directtofield/union/UnionWithTypeAttributeNotSet.xml");
    }

    @Override
    protected Object getControlObject() {
        Person person = new Person();
        person.setAge("10");
        person.setFirstName(CONTROL_FIRST_NAME);
        person.setLastName(CONTROL_LAST_NAME);
        return person;
    }

    @Override
    protected Document getWriteControlDocument() throws Exception {
        String xmlResource = "org/eclipse/persistence/testing/oxm/mappings/directtofield/union/UnionWithTypeAttributeNotSetWriting.xml";
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        Document writeControlDocument = parser.parse(inputStream);
        removeEmptyTextNodes(writeControlDocument);
        return writeControlDocument;
    }

    @Override
    public void testObjectToContentHandler() throws Exception {
        SAXDocumentBuilder builder = new SAXDocumentBuilder();
        xmlMarshaller.marshal(getWriteControlObject(), builder);

        log("**testObjectToXMLDocument**");
        log("Expected:");
        log(getWriteControlDocument());
        log("\nActual:");
        log(builder.getDocument());

        this.assertXMLIdentical(this.getWriteControlDocument(), builder.getDocument());
    }
}
