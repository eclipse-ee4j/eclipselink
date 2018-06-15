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
//     bdoughan - Mar 17/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class DataTypeManyTestCases extends SDOTestCase {

    private static String XSD = "org/eclipse/persistence/testing/sdo/helper/classgen/dynamicimpl/DataTypeMany.xsd";
    private static String CONTROL_STRING = "control";

    public DataTypeManyTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        InputStream xsdInputStream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(XSD);
        this.aHelperContext.getXSDHelper().define(xsdInputStream, null);
    }

    public void testCreateObject() {
        DataObject dataTypeManyDO = aHelperContext.getDataFactory().create("http://www.example.com", "DataTypeMany");
        DataTypeMany dataTypeMany = (DataTypeMany) dataTypeManyDO;

        List strings = new ArrayList();
        strings.add("FOO");
        strings.add("BAR");
        dataTypeMany.setProperty1(strings);
        assertEquals(2, dataTypeMany.getProperty1().size());
    }

}
