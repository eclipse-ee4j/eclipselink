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
*     dmccann - Feb 09/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.xsdhelper;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class SDOXSDHelperExceptionTestCases extends XSDHelperTestCases {
    public SDOXSDHelperExceptionTestCases(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.SDOXSDHelperExceptionTestCases" };
        TestRunner.main(arguments);
    }

    public void testIsAttributeException() throws Exception {
        try {
            assertFalse("Expected [false] but was [true]", xsdHelper.isAttribute(null));
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurrred: " + e.getMessage());
        }
    }

    public void testIsElementException() throws Exception {
        try {
            assertFalse("Expected [false] but was [true]", xsdHelper.isElement(null));
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurrred: " + e.getMessage());
        }
    }

    public void testIsMixedException() throws Exception {
        try {
            assertFalse("Expected [false] but was [true]", xsdHelper.isMixed(null));
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurrred: " + e.getMessage());
        }
    }

    public void testIsXSDException() throws Exception {
        try {
            assertFalse("Expected [false] but was [true]", xsdHelper.isXSD(null));
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurrred: " + e.getMessage());
        }
    }

    public void testGetLocalNameViaPropertyException() throws Exception {
        try {
            Property property = null;
            String result = xsdHelper.getLocalName(property);
            assertNull("Expected [null] but was ["+result+"]", result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurrred: " + e.getMessage());
        }
    }

    public void testGetLocalNameViaTypeException() throws Exception {
        try {
            Type type = null;
            String result = xsdHelper.getLocalName(type);
            assertNull("Expected [null] but was ["+result+"]", result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurrred: " + e.getMessage());
        }
    }

    public void testGetNamespaceURIViaPropertyException() throws Exception {
        try {
            Property property = null;
            String result = xsdHelper.getNamespaceURI(property);
            assertNull("Expected [null] but was ["+result+"]", result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurrred: " + e.getMessage());
        }
    }

}
