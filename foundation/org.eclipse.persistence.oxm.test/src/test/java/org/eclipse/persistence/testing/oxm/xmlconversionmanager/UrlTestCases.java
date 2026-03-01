/*
 * Copyright (c) 2012, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import junit.framework.TestCase;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;

import java.net.URL;

public class UrlTestCases extends TestCase {

    private static final String URL = "http://www.example.com/TEST";

    private XMLConversionManager xmlConversionManager;

    @Override
    public void setUp() {
        xmlConversionManager = XMLConversionManager.getDefaultXMLManager();
    }

    public void testValidStringToUrl() throws Exception {
        URL control = new URL(URL);
        URL test = xmlConversionManager.convertObject(URL, URL.class);
        assertEquals(control, test);
    }

    public void testNullStringToUrl() throws Exception {
        URL test = xmlConversionManager.convertObject(null, URL.class);
        assertNull(test);
    }

    public void testInvalidStringToUrl() {
        try {
            URL test = xmlConversionManager.convertObject("abc", URL.class);
        } catch(ConversionException e) {
            return;
        } catch(Exception e) {
            fail("The wrong exception was caught.");
        }
        fail("A ConversionException should have been thrown, but not exceptions were.");
    }

    public void testUrlToString() throws Exception {
        URL control = new URL(URL);
        String test = xmlConversionManager.convertObject(control, String.class);
        assertEquals(URL, test);
    }

    public void testNullUrlToString() {
        String test = xmlConversionManager.convertObject(null, String.class);
        assertNull(test);
    }

    public void testUrlToUrl() throws Exception {
        URL control = new URL(URL);
        URL test = xmlConversionManager.convertObject(control, URL.class);
        assertEquals(control, test);
    }

    public void testUrlToInteger() {
        try {
            URL control = new URL(URL);
            xmlConversionManager.convertObject(control, Integer.class);
        } catch(ConversionException e) {
            return;
        } catch(Exception e) {
            fail("The wrong exception was caught.");
        }
        fail("A ConversionException should have been thrown, but not exceptions were.");
   }

}
