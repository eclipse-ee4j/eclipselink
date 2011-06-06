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
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.*;
import java.util.*;
import java.io.*;
import org.w3c.dom.Document;

public class TransformationMappingTestCases extends OXTestCase {
    private DocumentBuilder parser;

    public TransformationMappingTestCases() {
        super("Tests Read-Only Direct Mappings");
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testOneAttributeNoDocumentPres() throws Exception {
        OneTransformationMappingProject project = new OneTransformationMappingProject();
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_tm.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.normalHours == null) {
            fail("read only attribute was not set on a read");
        }
        Vector newHours = new Vector();
        newHours.addElement("2:00AM");
        newHours.addElement("7:00PM");
        emp.normalHours = newHours;

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/empty-employee.xml");
        Document resultDocument = marshaller.objectToXML(emp);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    public void testOneAttributeWithDocumentPres() throws Exception {
        OneTransformationMappingProject project = new OneTransformationMappingProject();
        XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(Employee.class);
        desc.setShouldPreserveDocument(true);
        DOMPlatform platform = new DOMPlatform();
        project.setLogin(new XMLLogin(platform));

        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_tm.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.normalHours == null) {
            fail("read only attribute was not set on a read");
        }

        Vector newHours = new Vector();
        newHours.addElement("2:00AM");
        newHours.addElement("7:00PM");
        emp.normalHours = newHours;

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/transformationemployee1.xml");
        Document resultDocument = marshaller.objectToXML(emp);
        removeEmptyTextNodes(resultDocument);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    public void testTwoAttributesOneReadOnly() throws Exception {
        TwoTransformationMappingProject project = new TwoTransformationMappingProject();
        XMLContext context = getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/readonly/employee_tm.xml");
        Employee emp = (Employee)unmarshaller.unmarshal(in);
        if (emp.normalHours == null) {
            fail("read only attribute was not set on a read");
        }

        Vector newHours = new Vector();
        newHours.addElement("2:00AM");
        newHours.addElement("7:00PM");
        emp.normalHours = newHours;

        Vector newHours2 = new Vector();
        newHours2.addElement("12:00PM");
        newHours2.addElement("7:00PM");
        emp.normalHours2 = newHours2;

        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/readonly/transformationemployee2.xml");
        Document resultDocument = marshaller.objectToXML(emp);

        this.assertXMLIdentical(controlDocument, resultDocument);
    }

    /*public void testTwoAttributesNoReadOnly() throws Exception {
        try {
            TwoTransformationMappingProject project = new TwoTransformationMappingProject();
            XMLDescriptor desc = (XMLDescriptor)project.getDescriptor(Employee.class);

            XMLTransformationMapping mapping = (XMLTransformationMapping)desc.getMappingForAttributeName("normalHours");
            mapping.readWrite();
            NormalHoursTransformer transformer = new NormalHoursTransformer();
            mapping.addFieldTransformer("normal-hours/start-time/text()", transformer);
            mapping.addFieldTransformer("normal-hours/end-time/text()", transformer);

            XMLContext context = getXMLContext(project);
            fail("No Exception was thrown with 2 writeable mappings to the same field");
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
