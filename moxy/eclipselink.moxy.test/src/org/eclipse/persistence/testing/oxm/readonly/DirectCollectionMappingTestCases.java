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
package org.eclipse.persistence.testing.oxm.readonly;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.*;
import java.util.*;
import java.io.*;
import org.eclipse.persistence.oxm.mappings.*;
import org.w3c.dom.Document;

public class DirectCollectionMappingTestCases extends OXTestCase {
    private DocumentBuilder parser;

    public DirectCollectionMappingTestCases() {
        super("Tests Read-Only Direct Mappings");
    }

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
