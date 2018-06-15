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
package org.eclipse.persistence.testing.oxm.unmapped;

import java.io.InputStream;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

public class UnmappedChildTestCases extends OXTestCase {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/unmapped/UnmappedChild.xml";
    private XMLUnmarshaller xmlUnmarshaller;

    public UnmappedChildTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.unmapped.UnmappedChildTestCases" });
    }

    public void setUp() {
        MyUnmappedContentHandler.INSTANCE_COUNTER = 0;
        EmployeeProject employeeProject = new EmployeeProject();
        XMLContext xmlContext = new XMLContext(employeeProject);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
    }

    public void testUnmappedChildContent() {
        xmlUnmarshaller.setUnmappedContentHandlerClass(MyUnmappedContentHandler.class);
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Employee testEmployee = (Employee)xmlUnmarshaller.unmarshal(inputStream);
        if (!getControlObject().equals(testEmployee)) {
            fail("The unmarshalled object does not equal the control object.");
        }
        assertEquals(2, MyUnmappedContentHandler.INSTANCE_COUNTER);
    }

    public void testInvalidContentHandlerClass() {
        xmlUnmarshaller.setUnmappedContentHandlerClass(InvalidContentHandler2.class);
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        try {
            xmlUnmarshaller.unmarshal(inputStream);
        } catch (Exception e) {
            assertTrue(e instanceof XMLMarshalException);
            assertEquals(XMLMarshalException.ERROR_INSTANTIATING_UNMAPPED_CONTENTHANDLER, ((XMLMarshalException)e).getErrorCode());
            return;
        }
        fail("An exception should have occurred but didn't.");
    }

    public void testInvalidContentHandlerClassNoInterface() {
        xmlUnmarshaller.setUnmappedContentHandlerClass(InvalidContentHandler.class);
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        try {
            xmlUnmarshaller.unmarshal(inputStream);
        } catch (Exception e) {
            assertTrue(e instanceof XMLMarshalException);
            assertEquals(XMLMarshalException.UNMAPPED_CONTENTHANDLER_DOESNT_IMPLEMENT, ((XMLMarshalException)e).getErrorCode());
            return;
        }
        fail("An exception should have occurred but didn't.");
    }

    public void tearDown() {
        MyUnmappedContentHandler.INSTANCE_COUNTER = 0;
    }

    public Object getControlObject() {
        Employee employee = new Employee();
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
        return employee;
    }
}
