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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.simpletypes;

import commonj.sdo.Type;
import java.math.BigInteger;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;
import org.eclipse.persistence.internal.helper.ClassConstants;

public class XSDHelperSimpleTypeTestCases extends XSDHelperTestCases {
    public XSDHelperSimpleTypeTestCases(String name) {
        super(name);
    }

    public void testSimpleTypeWithName() {
        String f = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpletypes/SimpleTypeWithName.xsd";
        String xmlSchema = getSchema(f);

        List types = xsdHelper.define(xmlSchema);

        this.assertEquals(types.size(), 1);

        Type type = (Type)types.get(0);

        // Name
        this.assertEquals(type.getName(), "my-int");

        //abstract
        this.assertFalse(type.isAbstract());

        // Base
        this.assertEquals(type.getBaseTypes().size(), 1);
        Type baseType = (Type)type.getBaseTypes().get(0);
        this.assertEquals(baseType, SDOConstants.SDO_INT);
        //this.assertEquals(baseType.getName(), "int");
        //this.assertEquals(baseType.getURI(), "commonj.sdo");
        // dataType
        this.assertTrue(type.isDataType());

        // Uri
        this.assertEquals(type.getURI(), "http://www.example.org");
    }

    public void testSimpleTypeAnonymous() {
        String f = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpletypes/SimpleTypeWithAnonymous.xsd";
        String xmlSchema = getSchema(f);

        List types = xsdHelper.define(xmlSchema);

        this.assertEquals(types.size(), 1);

        Type type = (Type)types.get(0);

        // Name
        this.assertEquals(type.getName(), "myElement");

        //abstract
        this.assertFalse(type.isAbstract());

        // Base
        this.assertEquals(type.getBaseTypes().size(), 1);
        Type baseType = (Type)type.getBaseTypes().get(0);
        this.assertEquals(baseType, SDOConstants.SDO_INT);
        assertEquals(((SDOType)baseType).getSubTypes().size(), 0);
        //this.assertEquals(baseType.getName(), "int");
        //this.assertEquals(baseType.getURI(), "commonj.sdo");
        // dataType
        this.assertTrue(type.isDataType());

        // Uri
        this.assertEquals(type.getURI(), "http://www.example.org");
    }

    public void testSimpleTypeWithSDO_Name() {
        String f = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpletypes/SimpleTypeWithSDO_Name.xsd";
        String xmlSchema = getSchema(f);

        List types = xsdHelper.define(xmlSchema);

        this.assertEquals(types.size(), 1);

        Type type = (Type)types.get(0);

        // Name
        this.assertEquals(type.getName(), "SDO_NAME");

        //abstract
        this.assertFalse(type.isAbstract());

        // Base
        this.assertEquals(type.getBaseTypes().size(), 1);
        Type baseType = (Type)type.getBaseTypes().get(0);
        this.assertEquals(baseType, SDOConstants.SDO_INT);
        assertEquals(((SDOType)baseType).getSubTypes().size(), 0);
        // this.assertEquals(baseType.getName(), "int");
        //this.assertEquals(baseType.getURI(), "commonj.sdo");
        // dataType
        this.assertTrue(type.isDataType());

        // Uri
        this.assertEquals(type.getURI(), "http://www.example.org");
    }

    public void testSimpleTypeWithAbstract() {
        String f = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpletypes/SimpleTypeWithAbstract.xsd";
        String xmlSchema = getSchema(f);

        List types = xsdHelper.define(xmlSchema);

        this.assertEquals(types.size(), 1);

        Type type = (Type)types.get(0);

        // Name
        this.assertEquals(type.getName(), "my-int");

        //abstract
        this.assertFalse(type.isAbstract());

        // Base
        this.assertEquals(type.getBaseTypes().size(), 1);
        Type baseType = (Type)type.getBaseTypes().get(0);
        this.assertEquals(baseType, SDOConstants.SDO_INT);
        assertEquals(((SDOType)baseType).getSubTypes().size(), 0);
        //this.assertEquals(baseType.getName(), "int");
        //this.assertEquals(baseType.getURI(), "commonj.sdo");
        // dataType
        this.assertTrue(type.isDataType());

        // Uri
        this.assertEquals(type.getURI(), "http://www.example.org");
    }

    //commonj.sdo/java
    public void testSimpleTypeWithSDO_Java_InstanceClass() {
        String f = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpletypes/SimpleTypeWithSDOJavaInstanceClass.xsd";
        String xmlSchema = getSchema(f);

        List types = xsdHelper.define(xmlSchema);

        this.assertEquals(types.size(), 1);

        Type type = (Type)types.get(0);

        // Name
        this.assertEquals("my-int", type.getName());

        //abstract
        this.assertFalse(type.isAbstract());

        // Base
        this.assertEquals(type.getBaseTypes().size(), 0);

        // dataType
        this.assertTrue(type.isDataType());

        // Uri
        this.assertEquals(type.getURI(), "http://www.example.org");

        this.assertEquals("INSTANCE_CLASS", ((SDOType)type).getInstanceClassName());
    }

    public void testSimpleTypeWithSDO_JAVA_ExtendedInstanceClass() {
        String f = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpletypes/SimpleTypeWithSDOJavaExtendedInstanceClass.xsd";
        String xmlSchema = getSchema(f);

        List types = xsdHelper.define(xmlSchema);

        this.assertEquals(types.size(), 1);

        Type type = (Type)types.get(0);

        // Name
        this.assertEquals("my-int", type.getName());

        //abstract
        this.assertFalse(type.isAbstract());

        // Base
        this.assertEquals(type.getBaseTypes().size(), 1);
        Type baseType = (Type)type.getBaseTypes().get(0);
        this.assertEquals(baseType, SDOConstants.SDO_INT);
        assertEquals(((SDOType)baseType).getSubTypes().size(), 0);
        //this.assertEquals(baseType.getName(), "int");
        //this.assertEquals(baseType.getURI(), "commonj.sdo");
        // dataType
        this.assertTrue(type.isDataType());

        // Uri
        this.assertEquals(type.getURI(), "http://www.example.org");

        this.assertEquals("EXTENDED_INSTANCE_CLASS", ((SDOType)type).getInstanceClassName());
    }

    public void testSimpleTypeWithListItemTypes() {
        String f = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpletypes/SimpleTypeWithListItem.xsd";
        String xmlSchema = getSchema(f);

        List types = xsdHelper.define(xmlSchema);

        this.assertEquals(2, types.size());

        for (int i = 0; i < types.size(); i++) {
            Type type = (Type)types.get(i);
            if (type.getName().equals("my-int")) {
                assertFalse(type.isAbstract());
                assertEquals(type.getBaseTypes().size(), 0);
                assertTrue(type.isDataType());
                assertEquals(type.getURI(), "http://www.example.org");
            }
        }

        for (int i = 0; i < types.size(); i++) {
            Type type = (Type)types.get(i);
            if (type.getName().equals("testComplexType")) {
                assertFalse(type.isAbstract());
                assertEquals(type.getDeclaredProperties().size(), 1);
                assertFalse(type.isDataType());
                assertEquals(type.getURI(), "http://www.example.org");

                SDOProperty prop = (SDOProperty)type.getDeclaredProperties().get(0);
                assertEquals("testElement", prop.getName());
                assertEquals("my-int", prop.getType().getName());
                //TODO: should prop.hasMany ==true
            }
        }
    }

    public void testSimpleTypeWithUnionTypes() {
        String f = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/simpletypes/SimpleTypeWithUnionType.xsd";
        String xmlSchema = getSchema(f);

        List types = xsdHelper.define(xmlSchema);

        this.assertEquals(3, types.size());

        for (int i = 0; i < types.size(); i++) {
            Type type = (Type)types.get(i);
            if (type.getName().equals("my-int")) {
                assertEquals(type.getName(), "my-int");
                assertFalse(type.isAbstract());
                assertEquals(ClassConstants.BIGINTEGER, type.getInstanceClass());
                assertEquals(0, type.getBaseTypes().size());
                assertTrue(type.isDataType());
                assertEquals(type.getURI(), "http://www.example.org");
            }
        }

        for (int i = 0; i < types.size(); i++) {
            Type type = (Type)types.get(i);
            if (type.getName().equals("my-other-int")) {
                assertEquals(type.getName(), "my-other-int");
                assertFalse(type.isAbstract());
                assertEquals(ClassConstants.BIGINTEGER, type.getInstanceClass());
                assertEquals(1, type.getBaseTypes().size());
                assertTrue(type.isDataType());
                assertEquals(type.getURI(), "http://www.example.org");
            }
        }

        for (int i = 0; i < types.size(); i++) {
            Type type = (Type)types.get(i);
            if (type.getName().equals("testComplexType")) {
                assertEquals(type.getName(), "testComplexType");
                assertFalse(type.isAbstract());
                assertEquals(1, type.getDeclaredProperties().size());
                //TODO: should instanceclass be null?
                //assertNull(type.getInstanceClass());
                assertEquals(type.getURI(), "http://www.example.org");
                //TODO:should type be SDOObject or my int type
                SDOProperty prop = (SDOProperty)type.getDeclaredProperties().get(0);
                assertEquals("testElement", prop.getName());
                assertEquals("my-int", prop.getType().getName());
                assertEquals(ClassConstants.BIGINTEGER, prop.getType().getInstanceClass());
            }
        }
    }
}
