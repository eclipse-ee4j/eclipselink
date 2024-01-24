/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     dmccann - Feb 09/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.xsdhelper;

import junit.textui.TestRunner;

import commonj.sdo.Property;
import commonj.sdo.Type;

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
