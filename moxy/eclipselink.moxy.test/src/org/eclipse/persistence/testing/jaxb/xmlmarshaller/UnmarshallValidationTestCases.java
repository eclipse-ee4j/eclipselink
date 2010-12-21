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
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.*;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshalException;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

import org.xml.sax.InputSource;

import org.eclipse.persistence.platform.xml.XMLPlatformException;

import junit.framework.TestCase;

public class UnmarshallValidationTestCases extends TestCase {

    static String INVALID_CONTEXT_PATH = "org.eclipse.persistence.testing.oxm.jaxb.invalidtype";
    static String SINGLE_ERROR_XML = "org/eclipse/persistence/testing/oxm/jaxb/Employee_OneError.xml";
    static String DOUBLE_ERROR_XML = "org/eclipse/persistence/testing/oxm/jaxb/Employee_TwoError.xml";
    static String SINGLE_WARNING_XML = "org/eclipse/persistence/testing/oxm/jaxb/Employee_OneWarning.xml";
    static String SINGLE_FATAL_ERROR_XML = "org/eclipse/persistence/testing/oxm/jaxb/Employee_OneFatalError.xml";

    static boolean test1passed;
    private Unmarshaller unmarshaller;
    private String contextPath;
    private ValidationEventHandler eventHandler;

    public UnmarshallValidationTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        contextPath = System.getProperty("jaxb.test.contextpath", JAXBSAXTestSuite.CONTEXT_PATH);

        JAXBContext context = JAXBContext.newInstance(contextPath);
        unmarshaller = context.createUnmarshaller();
        if (!unmarshaller.isValidating()) {
            unmarshaller.setValidating(true);
        }
        eventHandler = unmarshaller.getEventHandler();
    }

    public void tearDown() throws Exception {
        unmarshaller.setEventHandler(eventHandler);
    }

    public void testIgnoreOneError() throws Exception {
        // First check which parser we are using.  Xerxes will throw 1 error in this case,
        // but Oracle xmlparserv2 will throw 2.

        int numberOfErrorsToIgnore = 1;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        if (builderFactory.getClass().getPackage().getName().contains("oracle")) {
            numberOfErrorsToIgnore = 2;
        }

        unmarshaller.setEventHandler(new CustomErrorValidationEventHandler(numberOfErrorsToIgnore));
        InputStream stream = ClassLoader.getSystemResourceAsStream(SINGLE_ERROR_XML);
        try {
            Object o = unmarshaller.unmarshal(stream);
        } catch (UnmarshalException ex) {
            ex.printStackTrace();
            fail("Unable to ignore the xml validation errors");
            return;
        }
        test1passed = true;
        assertTrue("", true);
    }

    public void testFailOnSecondError() throws Exception {
        unmarshaller.setEventHandler(new CustomErrorValidationEventHandler());
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        try {
            Object o = unmarshaller.unmarshal(stream);
        } catch (UnmarshalException ex) {
            assertTrue("", true);
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFatalError() throws Exception {
        unmarshaller.setEventHandler(new CustomFatalErrorValidationEventHandler());
        InputStream stream = ClassLoader.getSystemResourceAsStream(SINGLE_FATAL_ERROR_XML);

        try {
            Object o = unmarshaller.unmarshal(stream);
        } catch (UnmarshalException ex) {
            assertTrue("", true);
            return;
        }
        fail("No Exceptions thrown but all fatal errors should throw an exception.");
    }

    public void testFatalErrorDefaultHandler() throws Exception {
        unmarshaller.setEventHandler(null);
        InputStream stream = ClassLoader.getSystemResourceAsStream(SINGLE_FATAL_ERROR_XML);

        try {
            Object o = unmarshaller.unmarshal(stream);
        } catch (UnmarshalException ex) {
            assertTrue(true);
            return;
        }
        fail("No Exceptions thrown but all fatal errors should throw an exception.");
    }

    public void testInvalidTypeSet() throws Exception {
        try {
            org.eclipse.persistence.sessions.Project p = new EmployeeInvalidTypeProject();
        } catch (XMLPlatformException platformException) {
            assertTrue("An incorrect XMLPlatformException was thrown.",platformException.getErrorCode() == XMLPlatformException.XML_PLATFORM_INVALID_TYPE);
            return;
        } catch (Exception e) {
            fail("The wrong exception was thrown.  Should have been platformException xmlPlatformInvalidTypeException ");
            return;
        }
        fail("No Exceptions thrown but all fatal errors should throw an exception.");
    }

}