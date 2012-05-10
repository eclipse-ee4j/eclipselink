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
package org.eclipse.persistence.testing.sdo.model.dataobject.containment;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class ContainmentTestCases extends SDOTestCase {
    protected DataObject rootDataObject;
    protected DataObject firstChildDataObject;
    protected DataObject secondChildDataObject;
    protected SDOType rootType;
    protected SDOType firstChildType;
    protected SDOType secondChildType;

    protected String getControlRootURI() {
        return "http://testing";
    }

    protected String getControlRootName() {
        return "myRoot";
    }
    
    public ContainmentTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        secondChildType = getSecondChildType();
        secondChildDataObject = dataFactory.create(secondChildType);
        firstChildType = getFirstChildType();
        firstChildDataObject = dataFactory.create(firstChildType);
        rootType = getRootSDOType();
        rootDataObject = dataFactory.create(rootType);
    }

    public SDOType getRootSDOType() {
        SDOType type = new SDOType("http://testing", "myRoot");
        type.setOpen(true);
        type.setAbstract(false);
        SDOProperty prop = new SDOProperty(aHelperContext);
        prop.setName("name");
        prop.setType(SDOConstants.SDO_STRING);
        prop.setContainingType(type);
        type.addDeclaredProperty(prop);

        SDOProperty prop2 = new SDOProperty(aHelperContext);
        prop2.setName("child");
        prop2.setType(typeHelper.getType("http://testing", "firstChildType"));
        prop2.setContainment(true);
        prop2.setContainingType(type);
        type.addDeclaredProperty(prop2);

        SDOProperty prop3 = new SDOProperty(aHelperContext);
        prop3.setName("child2");
        prop3.setType(typeHelper.getType("http://testing", "firstChildType"));
        prop3.setContainment(true);
        prop3.setContainingType(type);
        type.addDeclaredProperty(prop3);

        SDOProperty prop4 = new SDOProperty(aHelperContext);
        prop4.setName("nullTypeProp");
        // we were omitting setting the Type on purpose here - we don't need it - just add anything
        prop4.setType(SDOConstants.SDO_BOOLEAN);
        
        prop4.setContainingType(type);
        type.addDeclaredProperty(prop4);

        ((SDOTypeHelper)typeHelper).addType(type);
        return type;
    }

    public SDOType getFirstChildType() {
        SDOType type = new SDOType("http://testing", "firstChildType");
        type.setOpen(true);
        type.setAbstract(false);

        SDOProperty prop = new SDOProperty(aHelperContext);
        prop.setName("name");
        prop.setType(SDOConstants.SDO_STRING);
        prop.setContainingType(type);
        type.addDeclaredProperty(prop);

        SDOProperty prop2 = new SDOProperty(aHelperContext);
        prop2.setName("child");
        prop2.setType(typeHelper.getType("http://testing", "secondChildType"));
        prop2.setContainment(true);
        prop2.setContainingType(type);
        type.addDeclaredProperty(prop2);

        ((SDOTypeHelper)typeHelper).addType(type);
        return type;
    }

    // old version
    public SDOType getSecondChildType(boolean old) {
        SDOType type = new SDOType("http://testing", "secondChildType");
        type.setOpen(true);
        type.setAbstract(false);

        SDOProperty prop = new SDOProperty(aHelperContext);
        prop.setName("name");
        prop.setType(SDOConstants.SDO_STRING);
        prop.setContainingType(type);
        type.addDeclaredProperty(prop);

        SDOProperty prop2 = new SDOProperty(aHelperContext);
        prop2.setName("child");
        prop2.setType(typeHelper.getType("http://testing", "myRoot"));
        prop2.setContainment(true);
        prop2.setContainingType(type);
        type.addDeclaredProperty(prop2);

        ((SDOTypeHelper)typeHelper).addType(type);
        return type;
    }

    // new version
    // 6159746: no null Type allowed on Property Object
    public SDOType getSecondChildType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type dataObjectType = typeHelper.getType("commonj.sdo", "DataObject");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject aTypeDO = dataFactory.create("commonj.sdo", "Type");        
        aTypeDO.set("uri", "http://testing");
        aTypeDO.set("name", "secondChildType");
        addProperty(aTypeDO, "name", stringType, false, false, true);
        addProperty(aTypeDO, "child", dataObjectType, true, false, true);

        // define type
        SDOType anSDOType = (SDOType)typeHelper.define(aTypeDO);
        // create a property of type employee - and associate the discriptor by QName
        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", anSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);

        anSDOType.setOpen(true);
        anSDOType.setAbstract(false);

        return anSDOType;
    }
}
