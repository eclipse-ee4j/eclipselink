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
package org.eclipse.persistence.testing.oxm.unmapped;

import java.io.InputStream;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

public class UnmappedRootTestCases extends OXTestCase {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/unmapped/UnmappedRoot.xml";
    private XMLUnmarshaller xmlUnmarshaller;

    public UnmappedRootTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.unmapped.UnmappedRootTestCases" });
    }

    public void setUp() {
        MyUnmappedContentHandler.INSTANCE_COUNTER = 0;
        EmployeeProject employeeProject = new EmployeeProject();
        XMLContext xmlContext = new XMLContext(employeeProject);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
    }

    public void testUnmappedRootContent() {
        xmlUnmarshaller.setUnmappedContentHandlerClass(MyUnmappedContentHandler.class);
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        xmlUnmarshaller.unmarshal(inputStream);
        assertEquals(1, MyUnmappedContentHandler.INSTANCE_COUNTER);
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
}
