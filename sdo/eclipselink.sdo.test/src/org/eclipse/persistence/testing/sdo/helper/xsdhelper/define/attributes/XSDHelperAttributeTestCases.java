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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.attributes;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;
import org.eclipse.persistence.exceptions.SDOException;

public class XSDHelperAttributeTestCases extends XSDHelperTestCases {
    public XSDHelperAttributeTestCases(String name) {
        super(name);
    }

    public void testAttributeWithName() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithName.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has one type   
        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        assertEquals(p.getName(), "myTestAttribute");

        // check if Property's Type is as schema        
        assertEquals(p.getType(), SDOConstants.SDO_STRING);

        // check alias name
        //  assertNull(p.getAliasNames());
        // check containing type        
        Type myTestType = typeHelper.getType("http://www.example.org", "myTestType");
        assertEquals(p.getContainingType(), myTestType);

        //TODO: assert containing type 
        // check default value
        assertNull(p.getDefault());

        // check opposite Property
        assertNull(p.getOpposite());

        assertFalse(p.isContainment());

        // check if it many
        assertFalse(p.isMany());

    }

    public void testAttributeWithSDO_Name() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithSDO_Name.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has one type   
        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        assertEquals("SDO_NAME", p.getName());

        // check if Property's Type is as schema
        assertEquals(p.getType(), SDOConstants.SDO_STRING);

        // check alias name
        //assertNull(p.getAliasNames());
        // check containing type
        //assertNull(p.getContainingType());				
        Type myTestType = typeHelper.getType("http://www.example.org", "myTestType");
        assertEquals(p.getContainingType(), myTestType);

        // check default value
        assertNull(p.getDefault());

        // check opposite Property
        assertNull(p.getOpposite());

        assertFalse(p.isContainment());

        // check if it many
        assertFalse(p.isMany());

    }

    public void testAttributeWithDefaultValue() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithDefaultValue.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has one type   
        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        assertEquals(p.getName(), "myTestAttribute");

        // check if Property's Type is as schema
        assertEquals(p.getType(), SDOConstants.SDO_STRING);

        // check alias name
        //assertNull(p.getAliasNames());
        // check containing type        
        Type myTestType = typeHelper.getType("http://www.example.org", "myTestType");
        assertEquals(p.getContainingType(), myTestType);
        //assertNull(p.getContainingType());
        // check default value
        assertEquals((String)p.getDefault(), "DEFAULT");

        // check opposite Property
        assertNull(p.getOpposite());

        assertFalse(p.isContainment());

        // check if it many
        assertFalse(p.isMany());

    }

    public void testAttributeWithFixedValue() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithFixedValue.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 1 types 
        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        assertEquals(p.getName(), "myTestAttribute");

        // check if Property's Type is as schema
        assertEquals(p.getType(), SDOConstants.SDO_STRING);

        // check alias name
        //assertNull(p.getAliasNames());
        // check containing type        
        Type myTestType = typeHelper.getType("http://www.example.org", "myTestType");
        assertEquals(p.getContainingType(), myTestType);
        //assertNull(p.getContainingType());
        // check default value
        assertEquals((String)p.getDefault(), "FIXED");

        // check opposite Property
        assertNull(p.getOpposite());

        assertFalse(p.isContainment());

        // check if it many
        assertFalse(p.isMany());

    }

    public void testAttributeWithReference() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithReference.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has one type   
        assertEquals(2, types.size());

        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        assertEquals(properties.size(), 1);
        assertEquals(aType.getProperties().size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        assertEquals(p.getName(), "attributeInt");

        // check if Property's Type is as schema
        assertEquals(p.getType().getName(), "attributeInt");

        // check alias name
        //assertNull(p.getAliasNames());
        // check containing type
        // check containing type        
        Type myTestType = typeHelper.getType("http://www.example.org", "myTestType");
        assertEquals(myTestType, p.getContainingType());
        // check default value !! it is propbably good to also test string !!
        // !! reason: getDefault() returns Obj, but actual xml default value can be int or float which is not Obj !!
        assertEquals(p.getDefault(), new Integer(3));

        // check opposite Property
        assertNull(p.getOpposite());

        assertFalse(p.isContainment());

        // check if it many
        assertFalse(p.isMany());

    }

    public void testAttributeWithSDO_String() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithSDO_String.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has one type   
        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        assertEquals(p.getName(), "myTestAttribute");

        // check if Property's Type is as schema
        assertEquals(p.getType(), SDOConstants.SDO_STRING);

        // check alias name
        //assertNull(p.getAliasNames());
        // check containing type
        // check containing type        
        Type myTestType = typeHelper.getType("http://www.example.org", "myTestType");
        assertEquals(p.getContainingType(), myTestType);
        //assertNull(p.getContainingType());
        // check default value 
        assertNull(p.getDefault());

        // check opposite Property
        assertNull(p.getOpposite());

        assertFalse(p.isContainment());

        // check if it many
        assertFalse(p.isMany());

    }

    public void testAttributeWithSDO_PropertyTypeWithDefineFailureOnRef() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithSDO_PropertyTypeWithInvalidRef.xsd");
        try {
            xsdHelper.define(xsdSchema);
            fail("An SDOException " + SDOException.CANNOT_SET_PROPERTY_TYPE_ANNOTATION_IF_TARGET_DATATYPE_TRUE //
            		+ " should have occurred but did not.");
        } catch (SDOException e) {            
            assertEquals(SDOException.CANNOT_SET_PROPERTY_TYPE_ANNOTATION_IF_TARGET_DATATYPE_TRUE ,e.getErrorCode());            
        }
    }

    public void testAttributeWithSDO_PropertyType() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithSDO_PropertyType.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types   
        assertEquals(2, types.size());

        //Type aType = (Type)types.get(0);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        assertNotNull(aType);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        assertEquals(p.getName(), "myTestAttribute");

        // check if Property's Type is as schema
        assertEquals("P_TYPE", p.getType().getName());

        // check alias name
        //assertNull(p.getAliasNames());
        // check containing type
        // check containing type        
        Type myTestType = typeHelper.getType("http://www.example.org", "myTestType");
        assertEquals(p.getContainingType(), myTestType);
        //assertNull(p.getContainingType());
        // check default value 
        //TODO:should defaultValue be null - 20070703: since propertyType is now complex - we expect a null Object reference for default
        assertNull(p.getDefault());
        //assertEquals(new Integer(0), p.getDefault());
        // check opposite Property
        assertNull(p.getOpposite());

        // assertTrue(p.isContainment());
        assertFalse(p.isContainment());

        // check if it many
        assertFalse(p.isMany());

    }
    
    public void testAttributeWithSDO_OppositePropertyWithDefineFailureOnRef() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithSDO_OppositePropertyWithInvalidRef.xsd");
        try {
            xsdHelper.define(xsdSchema);
            fail("An SDOException " + SDOException.CANNOT_SET_PROPERTY_TYPE_ANNOTATION_IF_TARGET_DATATYPE_TRUE //
            		+ " should have occurred but did not.");
        } catch (SDOException e) {            
            assertEquals(SDOException.CANNOT_SET_PROPERTY_TYPE_ANNOTATION_IF_TARGET_DATATYPE_TRUE ,e.getErrorCode());            
        }
    }

    public void testAttributeWithSDO_OppositeProperty() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithSDO_OpppsiteProperty.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has one type   
        assertEquals(2, types.size());

        //Type aType = (Type)types.get(0);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        assertNotNull(aType);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        assertEquals(p.getName(), "myTestAttribute");

        // check if Property's Type is as schema
        assertEquals(p.getType().getName(), "P_TYPE");

        // check alias name
        //assertNull(p.getAliasNames());
        // check containing type				
        Type myTestType = typeHelper.getType("http://www.example.org", "myTestType");
        assertEquals(p.getContainingType(), myTestType);
        //assertNull(p.getContainingType());
        // check default value         
        //TODO:should defaultValue be null - 20070703: since propertyType is now complex - we expect a null Object reference for default
        assertNull(p.getDefault());
        //assertEquals(new Integer(0), p.getDefault());

        // check opposite Property
        Property op_pro = p.getOpposite();
        //assertEquals("theProp", op_pro.getName());
        assertEquals("id", op_pro.getName());

        //assertTrue(p.isContainment());
        assertFalse(p.isContainment());

        // check if it many
        assertFalse(p.isMany());

    }
    
    public void testAttributeWithSDO_DataType() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/attributes/AttributeWithSDO_DataType.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has one type   
        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        assertEquals(p.getName(), "myTestAttribute");

        // check if Property's Type is as schema
        assertEquals(p.getType().getName(), "String");

        // check alias name
        //assertNull(p.getAliasNames());
        // check containing type				
        Type myTestType = typeHelper.getType("http://www.example.org", "myTestType");
        assertEquals(p.getContainingType(), myTestType);
        //assertNull(p.getContainingType());
        // check default value 
        assertNull(p.getDefault());

        // check opposite Property
        assertNull(p.getOpposite());

        assertFalse(p.isContainment());

        // check if it many
        assertFalse(p.isMany());

    }

    // !! Global Attribute, not specificied by SDO !!
}
