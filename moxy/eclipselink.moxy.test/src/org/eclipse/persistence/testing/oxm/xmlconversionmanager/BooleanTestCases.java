/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
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