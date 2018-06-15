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
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class Base64TestCases extends OXTestCase {
    // XML Conversion Manager
    private XMLConversionManager xcm;

    public Base64TestCases(String name) {
        super(name);
    }

    public void setUp() {
        xcm = XMLConversionManager.getDefaultXMLManager();
    }

    public void testIntegerToString_base64() {
        try {
            Integer integer = new Integer(1);
            xcm.convertObject(integer, ClassConstants.ABYTE, XMLConstants.BASE_64_BINARY_QNAME);
        } catch (ConversionException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == ConversionException.COULD_NOT_BE_CONVERTED);
        }
    }

    public void testBase64WithNewLines() {
        try {
            String base64 = "PD94bWwgdmVyc2lvbj0iMS4wIj8+PGZhbGw+PG5hbWU+TmlhZ2FyYSBGYWxsczwvbmFtZT48L2Zh\r\n" +
            "bGw+";
            xcm.convertObject(base64, ClassConstants.ABYTE, XMLConstants.BASE_64_BINARY_QNAME);
        } catch(Exception ex) {
            fail("an unexpected exception was thrown " + ex.getMessage());
        }
    }
}
