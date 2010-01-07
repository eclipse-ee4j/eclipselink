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
package org.eclipse.persistence.testing.oxm.readonly;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.testing.oxm.*;
import java.util.*;
import java.io.*;
import org.w3c.dom.Document;

public class CompositeObjectMappingTestCases extends OXTestCase {
    private DocumentBuilder parser;

    public CompositeObjectMappingTestCases() {
        super("Tests Read-Only Direct Mappings");
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testOneAttributeNoDocumentPres() throws Exception {
        OneCompositeObjectMappingProject project = new OneCompositeObjectMappingProject();
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_co.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.primaryAddress == null) {
            fail("read only attribute was not set on a read");
        }
        Address newAddr = new Address();
        newAddr.street = "123 SomeStreet";
        emp.primaryAddress = newAddr;

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/empty-employee.xml");
        Document resultDocument = marshaller.objectToXML(emp);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    public void testOneAttributeWithDocumentPres() throws Exception {
        OneCompositeObjectMappingProject project = new OneCompositeObjectMappingProject();
        XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(Employee.class);
        desc.setShouldPreserveDocument(true);
        DOMPlatform platform = new DOMPlatform();
        project.setLogin(new XMLLogin(platform));

        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_co.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.primaryAddress == null) {
            fail("read only attribute was not set on a read");
        }
        Address newAddr = new Address();
        newAddr.street = "123 Something Street";
        emp.primaryAddress = newAddr;

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/compositeobjectemployee1.xml");
        Document resultDocument = marshaller.objectToXML(emp);
        removeEmptyTextNodes(resultDocument);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    public void testTwoAttributesOneReadOnly() throws Exception {
        TwoCompositeObjectMappingProject project = new TwoCompositeObjectMappingProject();
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_co.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.primaryAddress == null) {
            fail("read only attribute was not set on a read");
        }
        emp.primaryAddress2.street = "123 Cool Lane";
        emp.primaryAddress = null;

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/compositeobjectemployee2.xml");
        Document resultDocument = marshaller.objectToXML(emp);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    /*public void testTwoAttributesNoReadOnly() throws Exception {
        try {
            TwoCompositeObjectMappingProject project = new TwoCompositeObjectMappingProject();
            XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(Employee.class);

            XMLCompositeObjectMapping mapping = (XMLCompositeObjectMapping)desc.getMappingForAttributeName("primaryAddress");
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
