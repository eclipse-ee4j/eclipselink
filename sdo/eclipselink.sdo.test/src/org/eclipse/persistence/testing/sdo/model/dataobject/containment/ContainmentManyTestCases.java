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
package org.eclipse.persistence.testing.sdo.model.dataobject.containment;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class ContainmentManyTestCases extends ContainmentTestCases {
    public ContainmentManyTestCases(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(org.eclipse.persistence.testing.sdo.model.dataobject.containment.ContainmentManyTestCases.class);
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
        prop2.setMany(true);
        prop2.setType(typeHelper.getType("http://testing", "firstChildType"));
        prop2.setContainment(true);
        prop2.setContainingType(type);
        type.addDeclaredProperty(prop2);

        SDOProperty prop3 = new SDOProperty(aHelperContext);
        prop3.setName("child2");
        prop3.setMany(true);
        prop3.setType(typeHelper.getType("http://testing", "firstChildType"));
        prop3.setContainment(true);
        prop3.setContainingType(type);
        type.addDeclaredProperty(prop3);

        SDOProperty prop4 = new SDOProperty(aHelperContext);
        prop4.setName("nullTypeProp");
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
        prop2.setMany(true);
        prop2.setType(typeHelper.getType("http://testing", "secondChildType"));
        prop2.setContainment(true);
        prop2.setContainingType(type);
        type.addDeclaredProperty(prop2);

        ((SDOTypeHelper)typeHelper).addType(type);
        return type;
    }

    public SDOType getSecondChildType() {
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
        prop2.setMany(true);
        prop2.setType(typeHelper.getType("http://testing", "myRoot"));
        prop2.setContainment(true);
        prop2.setContainingType(type);
        type.addDeclaredProperty(prop2);

        ((SDOTypeHelper)typeHelper).addType(type);
        return type;
    }

    public void testSetCircularReferenceManyProperty() {
        rootDataObject.set("name", "myRoot");
        List firstList = new ArrayList();
        firstList.add(firstChildDataObject);
        rootDataObject.set("child", firstList);

        List secondList = new ArrayList();
        secondList.add(secondChildDataObject);
        firstChildDataObject.set("child", secondList);
        try {
            List thirdList = new ArrayList();
            thirdList.add(rootDataObject);
            secondChildDataObject.set("child", thirdList);
            //secondChildDataObject.set("child", rootDataObject);
            fail("An IllegalArgumentException due to circular reference should have been thrown");
        } catch (IllegalArgumentException e) {
        }
    }
}
