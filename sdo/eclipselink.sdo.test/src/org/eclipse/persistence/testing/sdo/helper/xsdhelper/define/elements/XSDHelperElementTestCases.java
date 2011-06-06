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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.elements;

import commonj.sdo.Property;
import commonj.sdo.Type;
import java.util.List;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.XSDHelperTestCases;
import org.eclipse.persistence.exceptions.SDOException;

public class XSDHelperElementTestCases extends XSDHelperTestCases {
    public XSDHelperElementTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.elements.XSDHelperElementTestCases" };
        TestRunner.main(arguments);
    }
    
    public void testElementWithName() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithName.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(types.size(), 2);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);
        this.assertEquals(aType.getProperties().size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "elementTest");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value
        this.assertNull(p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());

        this.assertTrue(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithSDO_Name() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDO_Name.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(2, types.size());
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "SDO_NAME");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "elementTest");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value
        this.assertNull(p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());
        
        this.assertTrue(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithSDO_AliasName() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDO_AlasName.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(types.size(), 2);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "elementTest");

        // check alias name
        this.assertEquals(1, p.getAliasNames().size());
        this.assertEquals(p.getAliasNames().get(0), "ALIAS_NAME");

        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value
        this.assertNull(p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());
        
        this.assertTrue(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }
     public void testElementWithNameCollisions() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithNameCollisions.xsd");
        List types = xsdHelper.define(xsdSchema);
        this.assertEquals(4, types.size());
      
        for (int i = 0; i < types.size(); i++) {
            SDOType nextType = (SDOType)types.get(i);
            Property prop = null;
            if (nextType.getName().equals("myTestType")) {
                prop = nextType.getProperty("myElement");
            }
            else if (nextType.getName().equals("myTestType2")) {
                prop = nextType.getProperty("myElement2");
            }
            else if (nextType.getName().equals("myTestType3")) {
                prop = nextType.getProperty("myElement3");
            }
            else if (nextType.getName().equals("myTestType4")) {
                prop = nextType.getProperty("myElement4");
            }
            assertNotNull(prop);
            assertEquals(1, nextType.getDeclaredProperties().size());
            assertTrue(prop.isMany());
            assertFalse(prop.isContainment());
            assertEquals(SDOConstants.SDO_OBJECT, prop.getType());
            assertTrue((Boolean)prop.get(SDOConstants.XMLELEMENT_PROPERTY));             
        }
     }

    public void testElementWithReference() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithReference.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has one type   
        this.assertEquals(2, types.size());
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);
        this.assertEquals(aType.getProperties().size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myElement");

        // check if Property's Type is as schema
        //TODO: myElement or my-int
        this.assertEquals(p.getType().getName(), "myElement");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value !! facing same problem as attribute's test !!
        this.assertEquals((Integer)p.getDefault(), new Integer(3));

        // check opposite Property
        this.assertNull(p.getOpposite());

        this.assertFalse(p.isContainment());

        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithReferenceMaxOccurs() {
        //Test for bug 5672183
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithReferenceMaxOccurs.xsd");
        List types = xsdHelper.define(xsdSchema);

        //Type myTestType = TypeHelper.INSTANCE.getType("http://www.example.org", "myTestType");
        Type myTestType = typeHelper.getType("http://www.example.org", "myTestType");
        Property p = (Property)myTestType.getDeclaredProperties().get(0);

        // check if it many
        this.assertTrue(p.isMany());
    }

    public void testElementWithMaxOccurance() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithMaxOccurance.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(types.size(), 2);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);
        this.assertEquals(1, aType.getProperties().size());
        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "elementTest");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value
        this.assertNull(p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());

        //TODO: what should containment be
        //this.assertFalse(p.isContainment());
        // check if it many
        this.assertTrue(p.isMany());

    }

    public void testElementWithNillable() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithNillable.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(types.size(), 2);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);
        this.assertEquals(1, aType.getProperties().size());
        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "elementTest");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value
        this.assertNull(p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());

        //TODO: what should containment be
        //this.assertFalse(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithSubstitution() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSubstitution.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has one type   
        this.assertEquals(types.size(), 3);
        //Type aType = (Type)types.get(0);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            if (((Type)types.get(i)).getName().equals("mySubstitute")) {
                aType = (Type)types.get(i);
            }
        }

        assertNotNull(aType);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "otherTest");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "myTestType");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "mySubstitute");

        // check default value
        this.assertNull(p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());

        //TODO: what should containment be
        //this.assertFalse(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithName_Simple() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithName_Simple.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(types.size(), 2);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);
        this.assertEquals(aType.getProperties().size(), 1);
        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "my-int");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value
        this.assertNull(p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());

        //TODO: should this be true or false
        //this.assertFalse(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithDefault_Simple() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithDefault_Simple.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(types.size(), 2);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);
        this.assertEquals(1, aType.getProperties().size());
        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "my-int");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value !! may have the problem as in attriutes' test !!
        this.assertEquals((Integer)p.getDefault(), new Integer(3));

        // check opposite Property
        this.assertNull(p.getOpposite());

        //TODO: what should containment be
        //this.assertFalse(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithFixed_Simple() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithFixed_Simple.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(types.size(), 2);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);
        this.assertEquals(1, aType.getProperties().size());
        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "my-int");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value !! may have the problem as in attriutes' test !!
        this.assertEquals((Integer)p.getDefault(), new Integer(3));

        // check opposite Property
        this.assertNull(p.getOpposite());

        //TODO: what should containment be
        //this.assertFalse(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithSDOString_Simple() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDOString_Simple.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(types.size(), 2);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        //this.assertEquals(p.getType().getName(), "string");
        this.assertEquals(SDOConstants.SDO_STRING, p.getType());

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value !! may have the problem as in attriutes' test !!
        this.assertNull((Integer)p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());

        //TODO: what should containment be
        //this.assertFalse(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithSDOPropertyType_Simple() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDOPropertyType_Simple.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(3, types.size());
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "P_TYPE");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value !! may have the problem as in attriutes' test !!
        this.assertNull((Integer)p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());

        //TODO: what should containment be
        this.assertFalse(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithSDOOppositePro_Simple() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDOOppositePro_Simple.xsd");
        List types = null;
        // JIRA-235: simple single type references: Spec sect 9.2 (1) oppositeType.dataType must be false
        try {            
            types = xsdHelper.define(xsdSchema);
            fail("An SDOException " + SDOException.CANNOT_SET_PROPERTY_TYPE_ANNOTATION_IF_TARGET_DATATYPE_TRUE //
            		+ " should have occurred but did not.");
        } catch (SDOException e) {            
            assertEquals(SDOException.CANNOT_SET_PROPERTY_TYPE_ANNOTATION_IF_TARGET_DATATYPE_TRUE ,e.getErrorCode());            
        }
/*        
        // check list has 2 types 
        this.assertEquals(types.size(), 2);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "my-IDREF");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value !! may have the problem as in attriutes' test !!
        this.assertNull((Integer)p.getDefault());

        // check opposite Property
        this.assertNotNull(p.getOpposite());
        this.assertEquals("PROPERTY", p.getOpposite().getName());

        //TODO: what should containment be
        this.assertFalse(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());
*/
    }

    public void testElementWithSDODataType() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDODataType.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has 2 types 
        this.assertEquals(types.size(), 2);
        Type aType = null;
        for (int i = 0; i < types.size(); i++) {
            Type nextType = (Type)types.get(i);
            if (nextType.getName().equals("myTestType")) {
                aType = nextType;
            }
        }
        this.assertNotNull(aType);

        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema        
        this.assertEquals(p.getType().getName(), "String");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value !! may have the problem as in attriutes' test !!
        this.assertNull((Integer)p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());
        
        this.assertFalse(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

    }

    public void testElementWithSDOChangeSummary() {
        String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/elements/ElementWithSDOChangeSummary.xsd");

        List types = xsdHelper.define(xsdSchema);

        // check list has one type   
        this.assertEquals(types.size(), 1);

        Type aType = (Type)types.get(0);
        List properties = aType.getDeclaredProperties();

        // check  the type has one Property
        this.assertEquals(properties.size(), 1);

        Property p = (Property)properties.get(0);

        // check if Property name is as schema
        this.assertEquals(p.getName(), "myTest1");

        // check if Property's Type is as schema
        this.assertEquals(p.getType().getName(), "ChangeSummaryType");

        // check alias name
        //this.assertNull(p.getAliasNames());
        // check containing type
        this.assertEquals(p.getContainingType().getName(), "myTestType");

        // check default value !! may have the problem as in attriutes' test !!
        this.assertNull((Integer)p.getDefault());

        // check opposite Property
        this.assertNull(p.getOpposite());
        
        this.assertFalse(p.isContainment());
        // check if it many
        this.assertFalse(p.isMany());

        // TODO: sdo spec p30 requires r/o state but 200606_cs changes set it to true for now
        //assertFalse(p.isReadOnly());
        this.assertTrue(p.isReadOnly());

    }
}
