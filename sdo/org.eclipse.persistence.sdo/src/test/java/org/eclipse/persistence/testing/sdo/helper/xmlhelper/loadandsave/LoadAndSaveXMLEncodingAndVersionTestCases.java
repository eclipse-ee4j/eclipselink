/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import java.util.ArrayList;
import java.util.List;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

public class LoadAndSaveXMLEncodingAndVersionTestCases extends LoadAndSaveTestCases {
    static String VERSION = "1.0";
    private static String ENCODING = "windows-1252";
    public LoadAndSaveXMLEncodingAndVersionTestCases(String name) {
        super(name);
    }

    @Override
    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/Customer.xsd";
    }

    @Override
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElementEncoding.xml");
    }

    @Override
    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElementEncodingNoSchema.xml");
    }

    @Override
    protected String getControlRootURI() {
        return NON_DEFAULT_URI;
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    @Override
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
        return packages;
    }

    @Override
    protected String getControlDataObjectFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElement.xml");
    }

    @Override
    protected String getControlRootName() {
        return "customer";
    }

    @Override
    protected String getRootInterfaceName() {
        return "CustomerType";
    }

    @Override
    public void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        // create a new Type for Customers
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        customerType.set("uri", getControlRootURI());
        customerType.set("name", "customer");

        // create a first name property
        addProperty(customerType, "firstName", stringType, false, false, true);

        // create a last name property
        addProperty(customerType, "lastName", stringType, false, false, true);

        // now define the Customer type so that customers can be made
        Type customerSDOType = typeHelper.define(customerType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", customerSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveXMLEncodingAndVersionTestCases" };
        TestRunner.main(arguments);
    }

    @Override
    protected void compareXML(String controlFileName, String testString, boolean compareNodes) throws Exception {
        String controlString = getControlString(controlFileName);
        log("Expected:" + controlString);
        log("Actual  :" + testString);

        assertEquals(removeCopyrightFromString(removeWhiteSpaceFromString(controlString)), removeWhiteSpaceFromString(testString));
    }

    @Override
    protected void verifyAfterLoad(XMLDocument document) {
        //Only check version, not encoding since some parsers don't support encoding
        assertTrue(document.getXMLVersion().equals(LoadAndSaveXMLEncodingAndVersionTestCases.VERSION));
    }

}
