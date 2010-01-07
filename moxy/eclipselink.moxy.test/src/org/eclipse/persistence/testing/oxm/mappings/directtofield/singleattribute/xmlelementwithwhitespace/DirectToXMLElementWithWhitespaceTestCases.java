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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.xmlelementwithwhitespace;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.Employee;

public class DirectToXMLElementWithWhitespaceTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/singleattribute/xmlelementwithwhitespace/DirectToXMLElementWithWhitespace.xml";
    private final static String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/singleattribute/xmlelementwithwhitespace/DirectToXMLElementWithWhitespace_write.xml";
    private final static int CONTROL_ID = 123;
    private Document comparisonControlDocument;

    public DirectToXMLElementWithWhitespaceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new DirectToXMLElementWithWhitespaceProject());
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setID(CONTROL_ID);
        return employee;
    }

    public void setUp() {
        try {
            super.setUp();
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_WRITE);
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
