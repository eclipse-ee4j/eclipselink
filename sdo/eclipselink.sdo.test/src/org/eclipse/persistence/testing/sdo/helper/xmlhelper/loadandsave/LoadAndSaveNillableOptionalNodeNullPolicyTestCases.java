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

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

public class LoadAndSaveNillableOptionalNodeNullPolicyTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveNillableOptionalNodeNullPolicyTestCases(String name) {
        super(name);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/DirectOptionalNodeNullPolicyElement.xsd";
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nillable/DirectOptionalNodeNullPolicyElement.xml";
    }

    protected String getControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nillable/DirectOptionalNodeNullPolicyElementWrite.xml";
    }

    protected String getNoSchemaControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nillable/DirectOptionalNodeNullPolicyElementWriteNoSchema.xml";
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nillable/DirectOptionalNodeNullPolicyElementNoSchema.xml";
    }

    protected String getControlRootURI() {
        return NON_DEFAULT_URI;
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
        packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
        return packages;
    }
    
    protected void verifyAfterLoad(XMLDocument doc) {
        super.verifyAfterLoad(doc);
        Object value = doc.getRootObject().get("id");
        boolean isSet = doc.getRootObject().isSet("id");

        // 20070615 changed because Direct now uses IsSetOptionalNNP not OptionalNNP
        assertNotNull(value);//???	
        // default values are not set - see UC3-2
        assertFalse(isSet);//???
        //assertNull(value);	
        // default values are not set - see UC3-2
        //assertTrue(isSet);

        value = doc.getRootObject().get("first-name");
        isSet = doc.getRootObject().isSet("first-name");
        assertNull(value);
        assertTrue(isSet);

        value = doc.getRootObject().get("task");
        isSet = doc.getRootObject().isSet("task");
        assertTrue(value instanceof List);
        assertTrue(((List)value).size() == 0);
        assertFalse(isSet);

        value = doc.getRootObject().get("last-name");
        isSet = doc.getRootObject().isSet("last-name");
        assertNotNull(value);
        assertTrue(isSet);
    }

    public void registerTypes() {
        SDOType intType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.INT);
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
        SDOType stringType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.STRING);
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        // create a new Type for Customers        
        DataObject customerType = dataFactory.create(typeType);
        customerType.set("uri", getControlRootURI());
        customerType.set("name", "EmployeeType");
        //customerType.set("name", "Employee");        
        // create an idproperty (simple type = DirectMapping)
        addProperty(customerType, "id", intType, false, false, true);
        addProperty(customerType, "first-name", stringType, false, false, true);
        addProperty(customerType, "task", stringType, false, true, true);
        addProperty(customerType, "last-name", stringType, false, false, true);

        // now define the Customer type so that customers can be made
        Type customerSDOType = typeHelper.define(customerType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", customerSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveNillableOptionalNodeNullPolicyTestCases" };
        TestRunner.main(arguments);
    }
}
