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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.union;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.w3c.dom.Document;

public class UnionWithTypeAttributeNotSetTestCases extends UnionWithTypeAttributeTestCases {
    public UnionWithTypeAttributeNotSetTestCases(String name) throws Exception {
        super(name);
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/directcollection/union/UnionWithTypeAttributeNotSet.xml");
    }

    protected Object getControlObject() {
        Person person = new Person();
        person.addItem("10");
        person.setFirstName(CONTROL_FIRST_NAME);
        person.setLastName(CONTROL_LAST_NAME);
        return person;
    }

    protected Document getWriteControlDocument() throws Exception {
        String xmlResource = "org/eclipse/persistence/testing/oxm/mappings/directcollection/union/UnionWithTypeAttributeNotSetWriting.xml";
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        Document writeControlDocument = parser.parse(inputStream);
        removeEmptyTextNodes(writeControlDocument);
        return writeControlDocument;
    }
    
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
