/* Copyright (c) 2007, Oracle. All rights reserved. */
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy;

import java.util.List;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveTestCases;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public abstract class IsSetNillableOptionalWithDefaultTestCases extends LoadAndSaveTestCases {
	
	protected static final int ID_DEFAULT = 10;
	protected static final String FIRSTNAME_DEFAULT = "default-first";
	protected static final String LASTNAME_DEFAULT = "default-last";
	protected static final String task_DEFAULT = SDOConstants.EMPTY_STRING;
	protected static final String ID_NAME = "id";
	protected static final String FIRSTNAME_NAME = "firstname";
	protected static final String LASTNAME_NAME = "lastname";
	protected static final String TASK_NAME = "task";
	
	/*
	 * UC 4
	<xsd:element name='employee'>
	<xsd:complexType><xsd:sequence>
		<xsd:element name=''id" type='xsd:int' default="10" nillable='true'/>
		<xsd:element name='firsname' type='xsd:string' default='default-first' nillable='true'/>
	</xsd:sequence></xsd:complexType>
	</xsd:element>
	 */
    public IsSetNillableOptionalWithDefaultTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/DirectIsSetNillableOptionalNodeNullPolicyWithDefaultsElement.xsd";
                                                                               
    }

    protected String getControlRootURI() {
        return "http://www.example.org";
    }

    protected String getControlRootName() {
        return "employee";
    }

    protected String getRootInterfaceName() {
        return "EmployeeType";
    }
    
    protected void verifyAfterLoad(XMLDocument doc) {
        super.verifyAfterLoad(doc);
        Object value;
        boolean isSet;
        value = doc.getRootObject().get(TASK_NAME);
        isSet = doc.getRootObject().isSet(TASK_NAME);
        assertTrue(value instanceof List);
        assertTrue(((List)value).size() == 0);
        assertFalse(isSet);

        value = doc.getRootObject().get(LASTNAME_NAME);
        isSet = doc.getRootObject().isSet(LASTNAME_NAME);
        assertNotNull(value);
        assertTrue(isSet);
    }

    public void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");

        // create employee type
        DataObject employeeTypeDO = dataFactory.create("commonj.sdo", "Type");        
        employeeTypeDO.set("uri", getControlRootURI());
        employeeTypeDO.set("name", getRootInterfaceName());
        
        // add id property
        DataObject employeeIDProperty = addProperty(employeeTypeDO, ID_NAME, intType, false, false, true);
        employeeIDProperty.set("nullable", true);
        employeeIDProperty.set("default", 10);
        
        // add firstname property
        DataObject employeeFirstnameProperty = addProperty(employeeTypeDO, FIRSTNAME_NAME, stringType, false, false, true);
        employeeFirstnameProperty.set("nullable", true);
        employeeFirstnameProperty.set("default", "default-first");
        
        // add task property
        DataObject employeeTaskProperty = addProperty(employeeTypeDO, TASK_NAME, stringType, false, true, true);
        //employeeTaskProperty.set("nullable", false);
        //employeeTaskProperty.set("default", new ArrayList());
        employeeTaskProperty.setBoolean("many", true);
        
        // add lastname property
        DataObject employeeLastnameProperty = addProperty(employeeTypeDO, LASTNAME_NAME, stringType, false, false, true);
        //employeeLastnameProperty.set("nullable", false);
        //employeeLastnameProperty.set("default", "default-last");
        
        // define type
        Type employeeSDOType = typeHelper.define(employeeTypeDO);
        // create a property of type employee - and associate the discriptor by QName
        DataObject propDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        propDO.set("name", getControlRootName());
        propDO.set("type", employeeSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }
}