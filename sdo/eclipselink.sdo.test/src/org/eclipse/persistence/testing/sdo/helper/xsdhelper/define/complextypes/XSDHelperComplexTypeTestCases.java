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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.complextypes;

import java.util.List;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;

import commonj.sdo.Property;
import commonj.sdo.Type;

// Note: the getProperties() has not been tested yet
public class XSDHelperComplexTypeTestCases extends XSDHelperTestCases {
    public XSDHelperComplexTypeTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(XSDHelperComplexTypeTestCases.class);
    }
    
    public void testComplexTypeWithEmptyContent() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithEmptyContent.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);

        // check Type Name
        assertEquals(aType.getName(), "myTestType");

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);

        // check if it has properties
        assertEquals(aType.getDeclaredProperties().size(), 0);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

    }

    public void testComplexTypeWithContent() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithContent.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);

        // check Type Name
        assertEquals(aType.getName(), "myTestType");

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);

        // check if it has properties
        assertEquals(aType.getDeclaredProperties().size(), 2);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

    }

    public void testComplexTypeWithAnonymous() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithAnonymous.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);

        // check Type Name
        assertEquals(aType.getName(), "myTestType");

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);

        // check if it has properties
        assertEquals(aType.getDeclaredProperties().size(), 2);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

    }

    public void testComplexTypeWithSDO_NAME() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithSDO_NAME.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);

        // check Type Name
        assertEquals(aType.getName(), "SDO_NAME");

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);

        // check if it has properties
        assertEquals(aType.getDeclaredProperties().size(), 2);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

    }

    public void testComplexTypeWithAbstract() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithAbstract.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);

        // check Type Name
        assertEquals(aType.getName(), "myTestType");

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);

        // check if it has properties
        assertEquals(aType.getDeclaredProperties().size(), 2);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertTrue(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

    }

    public void testComplexTypeWithAliasName() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithAliasName.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);

        // check Type Name
        assertEquals(aType.getName(), "myTestType");

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);

        // check if it has properties
        assertEquals(aType.getDeclaredProperties().size(), 2);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 1);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

    }

    public void testComplexTypeExtendingComplexType() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeEntendingComplexType.xsd");

        List types = xsdHelper.define(xsdSchema);
        assertEquals(types.size(), 2);

        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }

        assertNotNull(aType);

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 1);
        Type baseType = (Type)aType.getBaseTypes().get(0);
        assertEquals(baseType.getName(), "TestType");
        assertEquals(baseType.getURI(), "http://www.example.org");

        // check if it has properties
        assertEquals(aType.getDeclaredProperties().size(), 2);
        assertEquals(aType.getProperties().size(), 3);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

    }

    public void testComplexTypeComplexContentRestrictingComplex() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeComplexContentRestrictingComplex.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 2);

        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }

        assertNotNull(aType);

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 1);
        Type baseType = (Type)aType.getBaseTypes().get(0);
        assertEquals(baseType.getName(), "TestType");
        assertEquals(baseType.getURI(), "http://www.example.org");

        // check if it has properties        
        assertEquals(aType.getDeclaredProperties().size(), 2);
        assertEquals(aType.getProperties().size(), 3);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

    }

    public void testComplexTypeSimpleContentRestrictingComplex() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeSimpleContentRestrictingComplex.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 2);

        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }

        assertNotNull(aType);

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 1);
        Type baseType = (Type)aType.getBaseTypes().get(0);
        assertEquals(baseType.getName(), "TestType");
        assertEquals(baseType.getURI(), "http://www.example.org");

        // check if it has properties
        assertEquals(aType.getDeclaredProperties().size(), 1);
        assertEquals(aType.getProperties().size(), 2);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

    }

    public void testComplexTypeWithMixedContent() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithMixedContent.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);

        // check Type Name
        assertEquals(aType.getName(), "myTestType");

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);

        // check if it has properties
        assertEquals(aType.getDeclaredProperties().size(), 2);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen()); //changed for SDO JIRA-106

        // check if it is not sequenced
        assertTrue(aType.isSequenced());

    }

    public void testComplexTypeWithSDOSequence() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithSDOSequence.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);

        // check Type Name
        assertEquals(aType.getName(), "myTestType");

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);

        // check if it has properties
        assertEquals(aType.getDeclaredProperties().size(), 2);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertTrue(aType.isSequenced());

    }

    public void testComplexTypeExtendingSimpleType() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeEntendingSimpleType.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 2);

        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }

        assertNotNull(aType);

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);

        /*
        Type baseType = (Type)aType.getBaseTypes().get(0);
        assertEquals(baseType.getName(), "mySimpleType");
        assertEquals(baseType.getURI(), "http://www.example.org");
        */
        // check if it has properties !! Owned property is checked with name and type here, but definition is slightly different from specification !!
        assertEquals(aType.getDeclaredProperties().size(), 2);
        Property aPro = aType.getProperty("myAttribute");

        //Property aPro = (SDOProperty)aType.getDeclaredProperties().get(0);
        assertEquals(aPro.getName(), "myAttribute");

        Type proType = (Type)aPro.getType();
        assertEquals(proType, SDOConstants.SDO_STRING);

        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertFalse(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

        Property prop2 = aType.getProperty("value");
        assertNotNull(prop2);
        assertEquals(prop2.getType().getName(), "mySimpleType");
    }

    public void testComplexTypeWithOpenContent() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithMultipleOpenContent.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 5);

        Type aType = null;
        for(int i = 0; i < types.size(); i++) {
            if(((Type)types.get(i)).getName().equals("myTestType2")) {
                aType = (Type)types.get(i);
                break;
            }
        }
        assertNotNull(aType);
        // check Type Name
        assertEquals(aType.getName(), "myTestType2");
        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");
        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);
        // check if it has properties
        // !! not yet for testing yet !! how to define the size of <any />?
        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);
        // check if it is not datatype
        assertFalse(aType.isDataType());
        // check if it is not abstract
        assertFalse(aType.isAbstract());
        // check if it is not open
        assertTrue(aType.isOpen());
        // check if it is sequenced
        assertTrue(aType.isSequenced());

        aType = null;
        for(int i = 0; i < types.size(); i++) {
            if(((Type)types.get(i)).getName().equals("myTestType4")) {
                aType = (Type)types.get(i);
                break;
            }
        }
        assertNotNull(aType);
        // check Type Name
        assertEquals(aType.getName(), "myTestType4");
        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");
        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);
        // check if it has properties
        // !! not yet for testing yet !! how to define the size of <any />?
        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);
        // check if it is not datatype
        assertFalse(aType.isDataType());
        // check if it is not abstract
        assertFalse(aType.isAbstract());
        
        assertTrue(aType.getProperty("test").isMany());
        assertTrue(aType.getProperty("test2").isMany());
        // check if it is not open
        assertTrue(aType.isOpen());
        // check if it is not sequenced
        assertTrue(aType.isSequenced());        
        

        aType = null;
        for(int i = 0; i < types.size(); i++) {
            if(((Type)types.get(i)).getName().equals("myTestType")) {
                aType = (Type)types.get(i);
                break;
            }
        }
        // check Type Name
        assertEquals(aType.getName(), "myTestType");
        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");
        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);
        // check if it has properties
        // !! not yet for testing yet !! how to define the size of <any />?
        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);
        // check if it is not datatype
        assertFalse(aType.isDataType());
        // check if it is not abstract
        assertFalse(aType.isAbstract());
        // check if it is open
        assertTrue(aType.isOpen());
        // check if it is sequenced
        assertTrue("Type was not 'sequenced' as expected.", aType.isSequenced());

        aType = null;
        for(int i = 0; i < types.size(); i++) {
            if(((Type)types.get(i)).getName().equals("myTestType5")) {
                aType = (Type)types.get(i);
                break;
            }
        }
        // check Type Name
        assertEquals(aType.getName(), "myTestType5");
        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");
        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);
        // check if it has properties
        // !! not yet for testing yet !! how to define the size of <any />?
        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);
        // check if it is not datatype
        assertFalse(aType.isDataType());
        // check if it is not abstract
        assertFalse(aType.isAbstract());
        assertTrue(aType.getProperty("test").isMany());
        assertTrue(aType.getProperty("test2").isMany());
        // check if it is open
        assertTrue(aType.isOpen());
        // check if it is sequenced
        assertTrue(aType.isSequenced());
        
        aType = null;
        for(int i = 0; i < types.size(); i++) {
            if(((Type)types.get(i)).getName().equals("myTestType3")) {
                aType = (Type)types.get(i);
                break;
            }
        }
        // check Type Name
        assertEquals(aType.getName(), "myTestType3");
        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");
        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);
        // check if it has properties
        // !! not yet for testing yet !! how to define the size of <any />?
        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);
        // check if it is not datatype
        assertFalse(aType.isDataType());
        // check if it is not abstract
        assertFalse(aType.isAbstract());
        // check if it is not open
        assertTrue(aType.isOpen());
        // check if it is sequenced
        assertTrue("Type was not 'sequenced' as expected.", aType.isSequenced());              
        
    }

    public void testComplexTypeWithOpenAttributes() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/complextypes/ComplexTypeWithOpenAttributes.xsd");

        List types = xsdHelper.define(xsdSchema);

        assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);

        // check Type Name
        assertEquals(aType.getName(), "myTestType");

        // check Type URI
        assertEquals(aType.getURI(), "http://www.example.org");

        // check base types
        assertEquals(aType.getBaseTypes().size(), 0);

        // check if it has properties
        // !! not yet for testing yet !! how to define the size of <any />?
        // check if it has no alias names
        assertEquals(aType.getAliasNames().size(), 0);

        // check if it is not datatype
        assertFalse(aType.isDataType());

        // check if it is not abstract
        assertFalse(aType.isAbstract());

        // check if it is not open
        assertTrue(aType.isOpen());

        // check if it is not sequenced
        assertFalse(aType.isSequenced());

    }
}
