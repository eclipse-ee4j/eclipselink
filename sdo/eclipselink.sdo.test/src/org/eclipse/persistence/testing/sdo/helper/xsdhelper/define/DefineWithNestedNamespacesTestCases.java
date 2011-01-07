/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

import commonj.sdo.DataObject;

public class DefineWithNestedNamespacesTestCases extends XSDHelperDefineTestCases {
    public DefineWithNestedNamespacesTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(DefineWithNestedNamespacesTestCases.class);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/namespaces/nestedNamespaces.xsd";
    }

    public List getControlTypes() {
        // create a new Type for Address
        DataObject AddressDO = dataFactory.create("commonj.sdo", "Type");
        AddressDO.set("uri", "uri1");
        AddressDO.set("name", "Address");
        DataObject streetProperty = AddressDO.createDataObject("property");
        streetProperty.set("name", "street");
        streetProperty.set("type", SDOConstants.SDO_STRING);
        streetProperty.set(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        SDOType addressType = (SDOType) typeHelper.define(AddressDO);
        addressType.setInstanceClassName("uri1.Address");
        
        // create a new Type for Employee
        DataObject EmployeeDO = dataFactory.create("commonj.sdo", "Type");
        EmployeeDO.set("uri", "uri1");
        EmployeeDO.set("name", "Employee");
        DataObject nameProperty = EmployeeDO.createDataObject("property");
        nameProperty.set("name", "name");
        nameProperty.set("type", SDOConstants.SDO_STRING);
        nameProperty.set(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        DataObject addressProperty = EmployeeDO.createDataObject("property");
        nameProperty.set("name", "address");
        nameProperty.set("type", addressType);
        SDOType employeeType = (SDOType) typeHelper.define(EmployeeDO);
        employeeType.setInstanceClassName("uri1.Employee");

        List types = new ArrayList();
        types.add(addressType);
        types.add(employeeType);
        return types;
    }
}
