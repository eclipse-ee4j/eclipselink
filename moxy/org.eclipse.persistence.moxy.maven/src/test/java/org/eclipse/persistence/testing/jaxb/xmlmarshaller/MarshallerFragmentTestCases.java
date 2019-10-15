/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.oxm.XMLConstants;
import org.w3c.dom.Document;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class MarshallerFragmentTestCases extends OXTestCase {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/jaxb/Employee.xml";
    protected final static String CONTROL_EMPLOYEE_NAME = "Jane Doe";
    protected Marshaller marshaller;
    protected Employee controlObject;
    protected Document controlDocument;
    protected Boolean originalSetting;

    public MarshallerFragmentTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        //set up XMLMarshaller
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();

        String contextPath = System.getProperty("jaxb.test.contextpath", JAXBSAXTestSuite.CONTEXT_PATH);
        JAXBContext jaxbContext = JAXBContext.newInstance(contextPath, getClass().getClassLoader());
        marshaller = jaxbContext.createMarshaller();

        originalSetting = (Boolean)marshaller.getProperty(XMLConstants.JAXB_FRAGMENT);
        marshaller.setProperty(XMLConstants.JAXB_FRAGMENT, new Boolean(true));

        //set up controlObject
        controlObject = new Employee();
        controlObject.setName(CONTROL_EMPLOYEE_NAME);

        //set up control Document
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        controlDocument = parser.parse(inputStream);
        removeEmptyTextNodes(controlDocument);
        removeCopyrightNode(controlDocument);
    }

    public void tearDown() {
        try {
            marshaller.setProperty(XMLConstants.JAXB_FRAGMENT, originalSetting);
        } catch (Exception e) {
            fail("An error occurred during tear down" + e.getMessage());
        }
    }

    public void testMarshalFragmentObjectToWriter() throws Exception {
        StringWriter writer = new StringWriter();

        marshaller.marshal(controlObject, writer);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);

        String controlStringNoWS = removeWhiteSpaceFromString(new String(bytes));
        controlStringNoWS = removeCopyrightFromString(controlStringNoWS);
        String writerStringNoWS = removeWhiteSpaceFromString(writer.toString());

        log("\nWRITERSTRING:" + writerStringNoWS);
        log("CONTROLSTRING:" + controlStringNoWS);

        assertEquals(controlStringNoWS, writerStringNoWS);

    }
}
