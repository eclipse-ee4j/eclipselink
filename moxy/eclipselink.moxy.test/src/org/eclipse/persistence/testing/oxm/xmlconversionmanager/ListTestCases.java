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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class ListTestCases extends OXTestCase {
    private static final String CONTROL_ITEM_1 = "apple";
    private static final String CONTROL_ITEM_2 = "ball";
    private static final String CONTROL_STRING = "apple ball";

    // XML Conversion Manager
    private XMLConversionManager xcm;

    public ListTestCases(String name) {
        super(name);
    }

    public void setUp() {
        xcm = XMLConversionManager.getDefaultXMLManager();
    }

    public void testListToString() {
        List list = new ArrayList(2);
        list.add(CONTROL_ITEM_1);
        list.add(CONTROL_ITEM_2);
        String control = CONTROL_STRING;
        String test = (String)xcm.convertObject(list, String.class);
        assertEquals(control, test);
    }

    public void testListToString_string() {
        List list = new ArrayList(2);
        list.add(CONTROL_ITEM_1);
        list.add(CONTROL_ITEM_2);
        String control = CONTROL_STRING;
        String test = (String)xcm.convertObject(list, String.class, XMLConstants.STRING_QNAME);
        assertEquals(control, test);
    }

    public void testStringToList() {
        String string = CONTROL_STRING;
        List control = new ArrayList(2);
        control.add(CONTROL_ITEM_1);
        control.add(CONTROL_ITEM_2);
        List test = (List)xcm.convertObject(string, List.class);
        assertEquals(control, test);
    }

    public void testStringToList_string() {
        String string = CONTROL_STRING;
        List control = new ArrayList(2);
        control.add(CONTROL_ITEM_1);
        control.add(CONTROL_ITEM_2);
        List test = (List)xcm.convertObject(string, List.class, XMLConstants.STRING_QNAME);
        assertEquals(control, test);
    }
}
