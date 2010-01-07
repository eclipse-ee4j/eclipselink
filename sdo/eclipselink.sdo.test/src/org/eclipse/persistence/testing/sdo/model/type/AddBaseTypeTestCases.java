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
package org.eclipse.persistence.testing.sdo.model.type;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class AddBaseTypeTestCases extends SDOTestCase {
    private SDOType rootSDOType;
    private SDOType child1SDOType;
    private SDOType child2SDOType;
    private SDOType child3SDOType;

    public AddBaseTypeTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject rootType = defineType("my.uri", "root");
        addProperty(rootType, "rootProp1", stringType);
        addProperty(rootType, "rootProp2", stringType);
        addProperty(rootType, "rootProp3", stringType);
        rootSDOType = (SDOType)typeHelper.define(rootType);

        DataObject child1Type = defineType("my.uri", "child1");
        addProperty(child1Type, "child1Prop1", stringType);
        addProperty(child1Type, "child1Prop2", stringType);
        addProperty(child1Type, "child1Prop3", stringType);
        child1SDOType = (SDOType)typeHelper.define(child1Type);

        DataObject child2Type = defineType("my.uri", "child2");
        addProperty(child2Type, "child2Prop1", stringType);
        addProperty(child2Type, "child2Prop2", stringType);
        addProperty(child2Type, "child2Prop3", stringType);
        child2SDOType = (SDOType)typeHelper.define(child2Type);

        DataObject child3Type = defineType("my.uri", "child3");
        addProperty(child3Type, "child3Prop1", stringType);
        addProperty(child3Type, "child3Prop2", stringType);
        addProperty(child3Type, "child3Prop3", stringType);
        child3SDOType = (SDOType)typeHelper.define(child3Type);
    }

    public void testAddDeclaredPropertyToRoot() {
        child3SDOType.addBaseType(child2SDOType);
        child2SDOType.addBaseType(child1SDOType);
        child1SDOType.addBaseType(rootSDOType);

        SDOProperty testProp = new SDOProperty(aHelperContext);
        testProp.setName("tester");
        rootSDOType.addDeclaredProperty(testProp, 1);
        assertEquals(4, rootSDOType.getDeclaredProperties().size());
        assertEquals(4, rootSDOType.getProperties().size());
        assertEquals(3, child1SDOType.getDeclaredProperties().size());
        assertEquals(7, child1SDOType.getProperties().size());
        assertEquals(3, child2SDOType.getDeclaredProperties().size());
        assertEquals(10, child2SDOType.getProperties().size());
        assertEquals(3, child3SDOType.getDeclaredProperties().size());
        assertEquals(13, child3SDOType.getProperties().size());

        assertEquals(1, testProp.getIndexInType());

    }

    public void testAddBaseTypes() {
        //NOTE: this is testing  internal methods, users should never call addBaseType on SDOType
        child2SDOType.addBaseType(child1SDOType);
        child3SDOType.addBaseType(child2SDOType);
        child1SDOType.addBaseType(rootSDOType);

        assertEquals(3, rootSDOType.getDeclaredProperties().size());
        assertEquals(3, rootSDOType.getProperties().size());

        assertEquals(3, child1SDOType.getDeclaredProperties().size());
        assertEquals(6, child1SDOType.getProperties().size());

        assertEquals(3, child2SDOType.getDeclaredProperties().size());
        assertEquals(9, child2SDOType.getProperties().size());

        assertEquals(3, child3SDOType.getDeclaredProperties().size());
        assertEquals(12, child3SDOType.getProperties().size());

        int child2Prop2Index = ((SDOProperty)child2SDOType.getProperty("child2Prop2")).getIndexInType();
        assertEquals(7, child2Prop2Index);

        int child3Prop2Index = ((SDOProperty)child3SDOType.getProperty("child3Prop2")).getIndexInType();
        assertEquals(10, child3Prop2Index);

        int child1Prop2Index = ((SDOProperty)child1SDOType.getProperty("child1Prop2")).getIndexInType();
        assertEquals(4, child1Prop2Index);

        int rootProp2Index = ((SDOProperty)rootSDOType.getProperty("rootProp2")).getIndexInType();
        assertEquals(1, rootProp2Index);
    }

    /*  public void testAddBaseType() {
          //NOTE: this is testing  internal methods, users should never call addBaseType on SDOType
          child2SDOType.addBaseType(child1SDOType);
          //child3SDOType.addBaseType(child2SDOType);
          //child1SDOType.addBaseType(rootSDOType);


          int child2Prop2Index = ((SDOProperty)child2SDOType.getProperty("child2Prop2")).getIndexInType();
          assertEquals(4, child2Prop2Index);
          int child3Prop2Index = ((SDOProperty)child3SDOType.getProperty("child3Prop2")).getIndexInType();
          assertEquals(1, child3Prop2Index);
          child3SDOType.addBaseType(child2SDOType);

          child2Prop2Index = ((SDOProperty)child2SDOType.getProperty("child2Prop2")).getIndexInType();
          assertEquals(4, child2Prop2Index);

          child3Prop2Index = ((SDOProperty)child3SDOType.getProperty("child3Prop2")).getIndexInType();
          assertEquals(7, child3Prop2Index);
          child1SDOType.addBaseType(rootSDOType);
          child2Prop2Index = ((SDOProperty)child2SDOType.getProperty("child2Prop2")).getIndexInType();
          assertEquals(7, child2Prop2Index);
          child3Prop2Index = ((SDOProperty)child3SDOType.getProperty("child3Prop2")).getIndexInType();
          assertEquals(10, child3Prop2Index);

          int child1Prop2Index = ((SDOProperty)child1SDOType.getProperty("child1Prop2")).getIndexInType();
          assertEquals(4, child1Prop2Index);

          int rootProp2Index = ((SDOProperty)rootSDOType.getProperty("rootProp2")).getIndexInType();
          assertEquals(1, rootProp2Index);
      }
    */
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.type.AddBaseTypeTestCases" };
        TestRunner.main(arguments);
    }
}
