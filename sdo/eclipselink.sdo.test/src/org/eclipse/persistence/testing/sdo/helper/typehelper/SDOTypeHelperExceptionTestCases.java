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
//     dmccann - Feb 09/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.typehelper;

import java.util.List;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class SDOTypeHelperExceptionTestCases extends SDOXMLHelperTestCases {
    public SDOTypeHelperExceptionTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.SDOTypeHelperExceptionTestCases" };
        TestRunner.main(arguments);
    }

    public void testDefineOCPropException() throws Exception {
        DataObject myPropDO = null;
        try {
            typeHelper.defineOpenContentProperty("my.uri", myPropDO);
            fail("An IllegalArugmentException should have occurred");
        } catch (Exception e) {}
    }

    public void testDefineListException() throws Exception {
        List<Type> myTypes = null;
        try {
            typeHelper.define(myTypes);
            fail("An IllegalArugmentException should have occurred");
        } catch (Exception e) {}
    }
}
