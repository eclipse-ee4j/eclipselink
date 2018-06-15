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
package org.eclipse.persistence.testing.oxm.readonly;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;

import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.*;

import java.util.*;
import java.io.*;

import org.w3c.dom.Document;

public class DirectMappingTestCases extends OXTestCase {
    private DocumentBuilder parser;

    public DirectMappingTestCases() {
        super("Tests Read-Only Direct Mappings");
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testOneAttributeNoDocumentPres() throws Exception {
        OneDirectMappingProject project = new OneDirectMappingProject();
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.firstName == null) {
            fail("read only attribute was not set on a read");
        }
        emp.firstName = "Bill";

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/empty-employee.xml");
        Document resultDocument = marshaller.objectToXML(emp);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    public void testOneAttributeWithDocumentPres() throws Exception {
        OneDirectMappingProject project = new OneDirectMappingProject();
        XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(Employee.class);
        desc.setShouldPreserveDocument(true);
        DOMPlatform platform = new DOMPlatform();
        project.setLogin(new XMLLogin(platform));
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.firstName == null) {
            fail("read only attribute was not set on a read");
        }

        emp.firstName = "Bill";

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/directemployee1.xml");
        Document resultDocument = marshaller.objectToXML(emp);
        removeEmptyTextNodes(resultDocument);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    public void testTwoAttributesOneReadOnly() throws Exception {
        TwoDirectMappingProject project = new TwoDirectMappingProject();
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.firstName == null) {
            fail("read only attribute was not set on a read");
        }

        emp.firstName = "Bill";
        emp.firstName2 = "Hank";

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/directemployee2.xml");
        Document resultDocument = marshaller.objectToXML(emp);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    /*public void testTwoAttributesNoReadOnly() throws Exception {
        try {
            TwoDirectMappingProject project = new TwoDirectMappingProject();
            XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(Employee.class);

            org.eclipse.persistence.oxm.mappings.XMLDirectMapping mapping = (org.eclipse.persistence.oxm.mappings.XMLDirectMapping)desc.getMappingForAttributeName("firstName");
            mapping.readWrite();

            XMLContext context = getXMLContext(project);
            fail("No Exception was thrown with 2 writeable mappings to the same field");
        } catch (IntegrityException ex) {
        }
    }*/

    private Document parse(String resource) throws Exception {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        Document document = parser.parse(stream);
        removeEmptyTextNodes(document);
        return document;
    }
}
