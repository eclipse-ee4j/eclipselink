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
package org.eclipse.persistence.testing.oxm.classloader;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.textui.TestRunner;
import org.w3c.dom.Document;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class ClassLoaderTestCases extends OXTestCase {
    private static final String EMPLOYEE_CLASS = "org.eclipse.persistence.testing.oxm.classloader.Employee";
    private static final String PHONE_NUMBER_CLASS = "org.eclipse.persistence.testing.oxm.classloader.PhoneNumber";
    private static final String EMPLOYEE_XML_RESOURCE = "org/eclipse/persistence/testing/oxm/classloader/Employee.xml";
    private static final String PHONE_NUMBER_XML_RESOURCE = "org/eclipse/persistence/testing/oxm/classloader/PhoneNumber.xml";
    private ClassLoader employeeClassLoader;
    private ClassLoader phoneNumberClassLoader;

    public ClassLoaderTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.classloader.ClassLoaderTestCases" };
        TestRunner.main(arguments);
    }
    public void setUp() {
        employeeClassLoader = new JARClassLoader("org/eclipse/persistence/testing/oxm/classloader/Employee.jar");

        phoneNumberClassLoader = new JARClassLoader("org/eclipse/persistence/testing/oxm/classloader/PhoneNumber.jar");
    }

    public void testEmployeeClassLoaderWithSessionPath() throws Exception {
        XMLContext employeeXMLContext = new XMLContext("classloader", employeeClassLoader, "org/eclipse/persistence/testing/oxm/classloader/mySessions.xml");
        Class controlClass = employeeClassLoader.loadClass(EMPLOYEE_CLASS);
        Document employeeDocument = parse(EMPLOYEE_XML_RESOURCE);
        XMLUnmarshaller unmarshaller = employeeXMLContext.createUnmarshaller();
        Object employeeObject = unmarshaller.unmarshal(employeeDocument);
        Class testClass = employeeObject.getClass();
    }

    public void testEmployeeClassLoader() throws Exception {
        XMLContext employeeXMLContext = getXMLContext(new EmployeeProject(), employeeClassLoader);
        Class controlClass = employeeClassLoader.loadClass(EMPLOYEE_CLASS);

        Document employeeDocument = parse(EMPLOYEE_XML_RESOURCE);
        XMLUnmarshaller unmarshaller = employeeXMLContext.createUnmarshaller();
        Object employeeObject = unmarshaller.unmarshal(employeeDocument);
        Class testClass = employeeObject.getClass();
    }

    public void testPhoneNumberClassLoader() throws Exception {
        XMLContext phoneNumberXMLContext = getXMLContext(new PhoneNumberProject(), phoneNumberClassLoader);
        Class controlClass = phoneNumberClassLoader.loadClass(PHONE_NUMBER_CLASS);
        XMLUnmarshaller unmarshaller = phoneNumberXMLContext.createUnmarshaller();
        Document phoneNumberDocument = parse(PHONE_NUMBER_XML_RESOURCE);
        Object phoneNumberObject = unmarshaller.unmarshal(phoneNumberDocument);
        Class testClass = phoneNumberObject.getClass();
    }

    private Document parse(String xmlResource) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        return parser.parse(inputStream);
    }
}
