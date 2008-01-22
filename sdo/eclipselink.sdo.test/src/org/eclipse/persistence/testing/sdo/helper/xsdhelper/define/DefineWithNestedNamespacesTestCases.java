/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
        List types = new ArrayList();

        SDOType addressType = new SDOType("uri1", "Address");
        addressType.setXsd(true);
        addressType.setXsdLocalName("Address");
        addressType.setDataType(false);
        addressType.setInstanceClassName("uri1" + "." + "Address");

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setXsdLocalName("street");
        streetProp.setXsd(true);
        streetProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        streetProp.setContainingType(addressType);
        streetProp.setType(SDOConstants.SDO_STRING);
        addressType.addDeclaredProperty(streetProp);

        SDOType empType = new SDOType("uri1", "Employee");
        empType.setXsd(true);
        empType.setXsdLocalName("Employee");
        empType.setDataType(false);
        empType.setInstanceClassName("uri1" + "." + "Employee");

        SDOProperty nameProp = new SDOProperty(aHelperContext);
        nameProp.setName("name");
        nameProp.setXsdLocalName("name");
        nameProp.setXsd(true);
        nameProp.setContainingType(empType);
        nameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        nameProp.setType(SDOConstants.SDO_STRING);
        empType.addDeclaredProperty(nameProp);

        SDOProperty addressProp = new SDOProperty(aHelperContext);
        addressProp.setName("address");
        addressProp.setXsdLocalName("address");
        addressProp.setXsd(true);
        addressProp.setContainingType(empType);
        addressProp.setContainment(true);
        addressProp.setType(addressType);
        empType.addDeclaredProperty(addressProp);

        types.add(addressType);
        types.add(empType);

        return types;
    }
}