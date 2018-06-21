/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class BooleanTestCases extends OXTestCase {

    XMLConversionManager xmlConversionManager;

    public BooleanTestCases(String name) {
        super(name);
    }

    public void setUp() {
        xmlConversionManager = XMLConversionManager.getDefaultXMLManager();
    }

    public void testConvertEmptyStringTo_boolean() {
        boolean test = (Boolean) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, boolean.class);
        assertEquals(false, test);
    }

    public void testConvertEmptyStringTo_Boolean() {
        Boolean test = (Boolean) xmlConversionManager.convertObject(XMLConstants.EMPTY_STRING, Boolean.class);
        assertEquals(false, test.booleanValue());
    }

}
