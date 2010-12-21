/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import java.util.ArrayList;
import java.util.List;


import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class LoadAndSaveWithReadOnlyTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveWithReadOnlyTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSavePurchaseOrderComplexTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/readonly.xml";
    }

    protected String getSchemaName() {
        return "org/eclipse/persistence/testing/sdo/schemas/ReadOnly.xsd";
    }

    protected String getControlRootURI() {
        return "myns";
    }

    protected String getControlRootName() {
        return "employee";
    }

    protected String getRootInterfaceName() {
        return "EmployeeType";
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add("myns");
        return packages;
    }    

    protected void registerTypes() {
        Type stringType = SDOConstants.SDO_STRING;
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject employeeTypeType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)employeeTypeType.getType().getProperty("uri");
        employeeTypeType.set(prop, getControlRootURI());
        prop = (SDOProperty)employeeTypeType.getType().getProperty("name");
        employeeTypeType.set(prop, "employee");
        addProperty(employeeTypeType, "name", stringType, false, false, true);

        Type EMPType = typeHelper.define(employeeTypeType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", EMPType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }
}
