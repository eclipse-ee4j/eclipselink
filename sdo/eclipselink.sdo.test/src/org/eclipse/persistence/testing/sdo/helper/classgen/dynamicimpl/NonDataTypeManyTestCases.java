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
//     bdoughan - Mar 18/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl;

import java.io.InputStream;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class NonDataTypeManyTestCases extends SDOTestCase {

    private static String XSD = "org/eclipse/persistence/testing/sdo/helper/classgen/dynamicimpl/NonDataTypeMany.xsd";
    private static String CONTROL_STRING = "control";

    public NonDataTypeManyTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        InputStream xsdInputStream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(XSD);
        this.aHelperContext.getXSDHelper().define(xsdInputStream, null);
    }

    public void testCreateObject() {
        DataObject nonDataTypeManyADO = aHelperContext.getDataFactory().create("http://www.example.com", "NonDataTypeManyA");
        NonDataTypeManyA nonDataTypeManyA = (NonDataTypeManyA) nonDataTypeManyADO;

        DataObject nonDataTypeManyBDO = aHelperContext.getDataFactory().create("http://www.example.com", "NonDataTypeManyB");
        NonDataTypeManyB nonDataTypeManyB = (NonDataTypeManyB) nonDataTypeManyBDO;
        nonDataTypeManyA.getB().add(nonDataTypeManyB);
        assertEquals(nonDataTypeManyB, nonDataTypeManyA.getB().get(0));

        DataObject nonDataTypeManyCDO = aHelperContext.getDataFactory().create("http://www.example.com", "NonDataTypeManyC");
        NonDataTypeManyC nonDataTypeManyC = (NonDataTypeManyC) nonDataTypeManyCDO;
        nonDataTypeManyA.getC().add(nonDataTypeManyC);
        assertEquals(nonDataTypeManyC, nonDataTypeManyA.getC().get(0));
    }

}
