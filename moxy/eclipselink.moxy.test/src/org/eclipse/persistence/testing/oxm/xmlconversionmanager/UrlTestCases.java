/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;

public class UrlTestCases extends TestCase {

    private static final String URL = "http://www.example.com/TEST";

    private XMLConversionManager xmlConversionManager;

    @Override
    public void setUp() {
        xmlConversionManager = XMLConversionManager.getDefaultXMLManager();
    }

    public void testValidStringToUrl() throws Exception {
        URL control = new URL(URL);
        URL test = (URL) xmlConversionManager.convertObject(URL, URL.class);
        assertEquals(control, test);
    }

    public void testNullStringToUrl() throws Exception {
        URL test = (URL) xmlConversionManager.convertObject(null, URL.class);
        assertNull(test);
    }

    public void testInvalidStringToUrl() {
        try {
            URL test = (URL) xmlConversionManager.convertObject("abc", URL.class);
        } catch(ConversionException e) {
            return;
        } catch(Exception e) {
            fail("The wrong exception was caught.");
        }
        fail("A ConversionException should have been thrown, but not exceptions were.");
    }

    public void testUrlToString() throws Exception {
        URL control = new URL(URL);
        String test = (String) xmlConversionManager.convertObject(control, String.class);
        assertEquals(URL, test);
    }

    public void testNullUrlToString() {
        String test = (String) xmlConversionManager.convertObject(null, String.class);
        assertNull(test);
    }

    public void testUrlToUrl() throws Exception {
        URL control = new URL(URL);
        URL test = (URL) xmlConversionManager.convertObject(control, URL.class);
        assertEquals(control, test);
    }

    public void testUrlToInteger() {
        try {
            URL control = new URL(URL);
            URL test = (URL) xmlConversionManager.convertObject(control, Integer.class);
        } catch(ConversionException e) {
            return;
        } catch(Exception e) {
            fail("The wrong exception was caught.");
        }
        fail("A ConversionException should have been thrown, but not exceptions were.");
   }

}
