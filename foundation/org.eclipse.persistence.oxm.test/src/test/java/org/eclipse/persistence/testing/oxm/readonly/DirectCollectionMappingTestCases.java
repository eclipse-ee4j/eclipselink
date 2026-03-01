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
package org.eclipse.persistence.testing.oxm.readonly;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Vector;

public class DirectCollectionMappingTestCases extends OXTestCase {
    private DocumentBuilder parser;

    public DirectCollectionMappingTestCases() {
        super("Tests Read-Only Direct Mappings");
    }

    @Override
    public void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testOneAttributeNoDocumentPres() throws Exception {
        OneDirectCollectionMappingProject project = new OneDirectCollectionMappingProject();
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_dc.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.primaryResponsibilities == null) {
            fail("read only attribute was not set on a read");
        }
        Vector newResp = new Vector();
        newResp.addElement("Responsibility 1");
        newResp.addElement("Responsibility 2");
        emp.primaryResponsibilities = newResp;

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/empty-employee.xml");
        Document resultDocument = marshaller.objectToXML(emp);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    public void testOneAttributeWithDocumentPres() throws Exception {
        OneDirectCollectionMappingProject project = new OneDirectCollectionMappingProject();
        XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(Employee.class);
        desc.setShouldPreserveDocument(true);
        DOMPlatform platform = new DOMPlatform();
        project.setLogin(new XMLLogin(platform));

        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_dc.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.primaryResponsibilities == null) {
            fail("read only attribute was not set on a read");
        }
        Vector newResp = new Vector();
        newResp.addElement("Responsibility 1");
        newResp.addElement("Responsibility 2");
        emp.primaryResponsibilities = newResp;

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/directcollectionemployee1.xml");
        Document resultDocument = marshaller.objectToXML(emp);
        removeEmptyTextNodes(resultDocument);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    public void testTwoAttributesOneReadOnly() throws Exception {
        TwoDirectCollectionMappingProject project = new TwoDirectCollectionMappingProject();
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_dc.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.primaryResponsibilities == null) {
            fail("read only attribute was not set on a read");
        }
        emp.primaryResponsibilities2.addElement("Make The Coffee");
        emp.primaryResponsibilities = null;

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/directcollectionemployee2.xml");
        Document resultDocument = marshaller.objectToXML(emp);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    /*public void testTwoAttributesNoReadOnly() throws Exception {
        try {
            TwoDirectCollectionMappingProject project = new TwoDirectCollectionMappingProject();
            XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(Employee.class);

            XMLCompositeDirectCollectionMapping mapping = (XMLCompositeDirectCollectionMapping)desc.getMappingForAttributeName("primaryResponsibilities");
            mapping.readWrite();

            XMLContext context = getXMLContext(project);

            fail("No Exception was thrown");
        } catch (IntegrityException ex) {
        }
    }*/

    private Document parse(String resource) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        Document document = parser.parse(stream);
        removeEmptyTextNodes(document);
        return document;
    }
}
