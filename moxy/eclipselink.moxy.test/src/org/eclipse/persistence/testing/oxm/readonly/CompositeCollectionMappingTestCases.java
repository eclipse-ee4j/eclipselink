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
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.testing.oxm.*;
import java.util.*;
import java.io.*;
import org.w3c.dom.Document;

public class CompositeCollectionMappingTestCases extends OXTestCase {
    private DocumentBuilder parser;

    public CompositeCollectionMappingTestCases() {
        super("Tests Read-Only Direct Mappings");
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testOneAttributeNoDocumentPres() throws Exception {
        OneCompositeCollectionMappingProject project = new OneCompositeCollectionMappingProject();
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_cc.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.otherAddresses == null) {
            fail("read only attribute was not set on a read");
        }
        Address newAddr = new Address();
        newAddr.street = "123 SomeStreet";
        emp.otherAddresses.addElement(newAddr);

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/empty-employee.xml");
        Document resultDocument = marshaller.objectToXML(emp);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    public void testOneAttributeWithDocumentPres() throws Exception {
        OneCompositeCollectionMappingProject project = new OneCompositeCollectionMappingProject();
        XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(Employee.class);
        desc.setShouldPreserveDocument(true);
        DOMPlatform platform = new DOMPlatform();
        project.setLogin(new XMLLogin(platform));

        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_cc.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.otherAddresses == null) {
            fail("read only attribute was not set on a read");
        }
        Address newAddr = new Address();
        newAddr.street = "123 Something Street";
        emp.otherAddresses.addElement(newAddr);

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/compositecollectionemployee1.xml");
        Document resultDocument = marshaller.objectToXML(emp);
        removeEmptyTextNodes(resultDocument);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    public void testTwoAttributesOneReadOnly() throws Exception {
        TwoCompositeCollectionMappingProject project = new TwoCompositeCollectionMappingProject();
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_cc.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.otherAddresses == null) {
            fail("read only attribute was not set on a read");
        }
        Address newAddr = new Address();
        newAddr.street = "123 Cool Lane";
        emp.otherAddresses2.addElement(newAddr);
        emp.otherAddresses = null;

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/compositecollectionemployee2.xml");
        Document resultDocument = marshaller.objectToXML(emp);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    /*public void testTwoAttributesNoReadOnly() throws Exception {
        try {
            TwoCompositeCollectionMappingProject project = new TwoCompositeCollectionMappingProject();
            XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(Employee.class);

            XMLCompositeCollectionMapping mapping = (XMLCompositeCollectionMapping)desc.getMappingForAttributeName("otherAddresses");
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
