/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement;

import java.io.InputStream;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// TopLink imports
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class RootElementWithCommentTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/simpletypes/rootelement/SimpleRootElementWithCommentTest.xml";
    private XMLMarshaller xmlMarshaller;

    public RootElementWithCommentTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        EmployeeProject p = new EmployeeProject();
        p.getDescriptor(Employee.class).removeMappingForAttributeName("name");

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("married");
        mapping.setXPath("text()");
        p.getDescriptor(Employee.class).addMapping(mapping);

        setProject(p);
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setMarried(true);

        return employee;
    }

    protected Document getWriteControlDocument() throws Exception {
        String xmlResource = "org/eclipse/persistence/testing/oxm/mappings/simpletypes/rootelement/SimpleRootElementWithCommentTestWriting.xml";
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
        // DO NOTHING BECAUSE CONTENT HANDLER CAN NOT READ COMMENTS
    }
}
