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

public class InheritanceTestCases extends SDOTestCase {

    private static String XSD = "org/eclipse/persistence/testing/sdo/helper/classgen/dynamicimpl/Inheritance.xsd";
    private static String CONTROL_STRING = "control";

    public InheritanceTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        InputStream xsdInputStream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(XSD);
        this.aHelperContext.getXSDHelper().define(xsdInputStream, null);
    }

    public void testCreateRoot() {
        DataObject inheritanceADO = aHelperContext.getDataFactory().create("http://www.example.com", "InheritanceA");
        InheritanceA inheritanceA = (InheritanceA) inheritanceADO;

        inheritanceA.setPropertyA(CONTROL_STRING);
        assertEquals(CONTROL_STRING, inheritanceA.getPropertyA());
    }

    public void testCreateTrunk() {
        DataObject inheritanceBDO = aHelperContext.getDataFactory().create("http://www.example.com", "InheritanceB");
        InheritanceB inheritanceB = (InheritanceB) inheritanceBDO;

        inheritanceB.setPropertyA(CONTROL_STRING);
        assertEquals(CONTROL_STRING, inheritanceB.getPropertyA());

        inheritanceB.setPropertyB(CONTROL_STRING);
        assertEquals(CONTROL_STRING, inheritanceB.getPropertyB());
    }

    public void testCreateTrunkEmpty() {
        DataObject inheritanceCDO = aHelperContext.getDataFactory().create("http://www.example.com", "InheritanceC");
        InheritanceC inheritanceC = (InheritanceC) inheritanceCDO;

        inheritanceC.setPropertyA(CONTROL_STRING);
        assertEquals(CONTROL_STRING, inheritanceC.getPropertyA());

        inheritanceC.setPropertyB(CONTROL_STRING);
        assertEquals(CONTROL_STRING, inheritanceC.getPropertyB());
    }

    public void testCreateLeaf() {
        DataObject inheritanceDDO = aHelperContext.getDataFactory().create("http://www.example.com", "InheritanceD");
        InheritanceD inheritanceD = (InheritanceD) inheritanceDDO;

        inheritanceD.setPropertyA(CONTROL_STRING);
        assertEquals(CONTROL_STRING, inheritanceD.getPropertyA());

        inheritanceD.setPropertyB(CONTROL_STRING);
        assertEquals(CONTROL_STRING, inheritanceD.getPropertyB());

        inheritanceD.setPropertyD(CONTROL_STRING);
        assertEquals(CONTROL_STRING, inheritanceD.getPropertyD());
    }

}
