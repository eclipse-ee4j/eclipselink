/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlelement;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.Employee;

public class DirectToXMLElementIdentifiedByNameNullTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/identifiedbyname/xmlelement/DirectToXMLElementNull.xml";
    private final static String XML_COMPARISON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/identifiedbyname/xmlelement/DirectToXMLElementNullWriting.xml";
    private final static String CONTROL_FIRST_NAME = null;
    private final static String CONTROL_LAST_NAME = "Doe";
    private Document comparisonControlDocument;

    public DirectToXMLElementIdentifiedByNameNullTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new DirectToXMLElementIdentifiedByNameProject());
    }

    public void setUp() {
        try {
            super.setUp();
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_COMPARISON_RESOURCE);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder parser = builderFactory.newDocumentBuilder();
            comparisonControlDocument = parser.parse(inputStream);
            removeEmptyTextNodes(comparisonControlDocument);
        } catch (Exception e) {
            e.printStackTrace();
            this.fail("An exception occurred during setup");
        }
    }

    protected Document getWriteControlDocument() throws Exception {
        return comparisonControlDocument;
    }

    protected Object getControlObject() {
        Employee employee = new Employee();

        //employee id not set
        employee.setFirstName(CONTROL_FIRST_NAME);
        employee.setLastName(CONTROL_LAST_NAME);
        return employee;
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
