/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Mar 18/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl;

import java.io.InputStream;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class NonDataTypeTestCases extends SDOTestCase {

    private static String XSD = "org/eclipse/persistence/testing/sdo/helper/classgen/dynamicimpl/NonDataType.xsd";
    private static String CONTROL_STRING = "control";

    public NonDataTypeTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        InputStream xsdInputStream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(XSD);
        this.aHelperContext.getXSDHelper().define(xsdInputStream, null);
    }

    public void testCreateObject() {
        DataObject nonDataTypeADO = aHelperContext.getDataFactory().create("http://www.example.com", "NonDataTypeA");
        NonDataTypeA nonDataTypeA = (NonDataTypeA) nonDataTypeADO;

        DataObject nonDataTypeBDO = aHelperContext.getDataFactory().create("http://www.example.com", "NonDataTypeB");
        NonDataTypeB nonDataTypeB = (NonDataTypeB) nonDataTypeBDO;
        nonDataTypeA.setB(nonDataTypeB);
        assertEquals(nonDataTypeB, nonDataTypeA.getB());

        DataObject nonDataTypeCDO = aHelperContext.getDataFactory().create("http://www.example.com", "NonDataTypeC");
        NonDataTypeC nonDataTypeC = (NonDataTypeC) nonDataTypeCDO;
        nonDataTypeA.setC(nonDataTypeC);
        assertEquals(nonDataTypeC, nonDataTypeA.getC());
    }

}
