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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery;

import commonj.sdo.DataObject;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDODataObjectGetByXPathQueryTestCases extends SDOTestCase {
    public SDODataObjectGetByXPathQueryTestCases(String name) {
        super(name);
    }

    protected static final String URINAME = "uri";
    protected static final String TYPENAME_A = "TypeName-a";
    protected static final String TYPENAME_B = "TypeName-b";
    protected static final String TYPENAME_C = "TypeName-c";
    protected static final String TYPENAME_D = "TypeName-d";
    protected static final String PROPERTY_NAME_A = "PName-a";
    protected static final String PROPERTY_NAME_A0 = "PName-a0";
    protected static final String PROPERTY_NAME_B = "PName-b";
    protected static final String PROPERTY_NAME_B0 = "PName-b0";
    protected static final String PROPERTY_NAME_B_NUMBER = "number";
    protected static final String PROPERTY_NAME_C = "PName-c";
    protected static final String PROPERTY_NAME_C0 = "PName-c0";
    protected static final String PROPERTY_NAME_C_NUMBER = "number";
    protected static final String PROPERTY_NAME_D_NUMBER = "number";
    protected static final String property = "PName-a/PName-b.0/PName-c[number='123']";
    protected static final String propertyD = "PName-a/PName-b.0/PName-c[number1='5.55']";
    protected static final String property1 = "PName-a0/PName-b0[number='123']/PName-c0.0";
    protected static final String propertyTest = "PName-a0/PName-b0[number='123']/";
    protected static final String propertyTest1 = "PName-a0/PName-b0[number='1']/";
    protected static final int PROPERTY_INDEX = 0;
    protected static final String PROPERTY_NAME_A_LENGTH_1 = "PName-a-length-1";
    protected SDODataObject dataObject_a;
    protected SDODataObject dataObject_b;
    protected SDODataObject dataObject_b0;
    protected SDODataObject dataObject_b1;
    protected SDODataObject dataObject_c;
    protected SDODataObject dataObject_c0;
    protected SDODataObject dataObject_c1;
    protected SDODataObject dataObject_d0;
    protected SDODataObject dataObject_d1;
    protected SDOType type_a;
    protected SDOType type_b;
    protected SDOType type_b0;
    protected SDOType type_b1;
    protected SDOType type_c;
    protected SDOType type_c0;
    protected SDOType type_d;
    protected SDOProperty property_a;
    protected SDOProperty property_a0;
    protected SDOProperty property_b;
    protected SDOProperty property_b0;
    protected SDOProperty property_b_number;
    protected SDOProperty property_c;
    protected SDOProperty property_c0;
    protected SDOProperty property_c_number;
    protected SDOProperty property_d_number;
    protected SDOProperty property_d_number1;
    protected SDOType baseType;
    protected SDOProperty baseProperty1;
    protected SDOProperty baseProperty2;
    protected SDOType baseType1;
    protected SDOProperty baseProperty3;

    public void setUp() {// set up as a/b/c
        super.setUp();
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        type_a = new SDOType(URINAME, TYPENAME_A);

        type_b = new SDOType(URINAME, TYPENAME_B);
        type_b.setOpen(true);

        type_c = new SDOType(URINAME, TYPENAME_C);
        type_c.setOpen(true);
        type_b0 = new SDOType(URINAME, TYPENAME_B + "0");
        type_c0 = new SDOType(URINAME, TYPENAME_C + "0");

        property_a = new SDOProperty(aHelperContext);
        property_a.setContainment(true);
        property_a.setType(type_b);
        property_a.setName(PROPERTY_NAME_A);
        type_a.addDeclaredProperty(property_a);

        property_a0 = new SDOProperty(aHelperContext);
        property_a0.setContainment(true);
        //property_a0.setMany(true);
        property_a0.setType(type_b0);
        property_a0.setName(PROPERTY_NAME_A0);
        type_a.addDeclaredProperty(property_a0);

        dataObject_a = (SDODataObject)dataFactory.create(type_a);
        //b
        property_b = new SDOProperty(aHelperContext);
        property_b.setContainment(true);
        property_b.setMany(true);
        property_b.setName(PROPERTY_NAME_B);
        property_b.setType(type_c);
        List aliasNames = new ArrayList();
        aliasNames.add("alias1");
        aliasNames.add("alias2");
        property_b.setAliasNames(aliasNames);
        type_b.addDeclaredProperty(property_b);

        dataObject_b = (SDODataObject)dataFactory.create(type_b);
        //b0
        property_b0 = new SDOProperty(aHelperContext);
        property_b0.setContainment(true);
        property_b0.setMany(true);
        property_b0.setType(type_c0);
        property_b0.setName(PROPERTY_NAME_B0);
        List alias = new ArrayList();
        alias.add("alias1");
        alias.add("alias2");
        property_b0.setAliasNames(alias);

        type_b0.addDeclaredProperty(property_b0);

        dataObject_b0 = (SDODataObject)dataFactory.create(type_b0);
        //property_b_number = new SDOProperty(aHelperContext);
        //property_b_number.setContainment(false);
        //property_b_number.setMany(false);
        //property_b_number.setName(PROPERTY_NAME_B_NUMBER);
        //type_b0.addDeclaredProperty(property_b_number);
        //b1
        //dataObject_b1 = new SDODataObject();
        //dataObject_b1.setType(type_b0);
        //c
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);

        dataObject_c = (SDODataObject)dataFactory.create(type_c);
        //c0        
        property_c0 = new SDOProperty(aHelperContext);
        property_c0.setName(PROPERTY_NAME_C0);
        property_c0.setContainment(true);
        property_c0.setMany(true);
        property_c0.setType(dataObjectType);
        type_c0.addDeclaredProperty(property_c0);

        property_c_number = new SDOProperty(aHelperContext);
        property_c_number.setContainment(false);
        property_c_number.setMany(false);
        property_c_number.setName(PROPERTY_NAME_C_NUMBER);
        property_c_number.setType(SDOConstants.SDO_STRING);
        type_c0.addDeclaredProperty(property_c_number);

        SDOProperty testProp = new SDOProperty(aHelperContext);
        testProp.setName("test");
        testProp.setType(SDOConstants.SDO_BOOLEAN);
        type_c0.addDeclaredProperty(testProp);

        SDOProperty property_c1_object = new SDOProperty(aHelperContext);
        property_c1_object.setName("PName-c1");
        property_c1_object.setContainment(true);
        property_c1_object.setMany(true);
        property_c1_object.setType(SDOConstants.SDO_DOUBLE);

        type_c0.addDeclaredProperty(property_c1_object);

        dataObject_c0 = (SDODataObject)dataFactory.create(type_c0);
        //c1                
        dataObject_c1 = (SDODataObject)dataFactory.create(type_c0);
        // d
        type_d = new SDOType(URINAME, TYPENAME_D);
        //type_d.setOpen(true);
        property_d_number = new SDOProperty(aHelperContext);
        property_d_number.setName(PROPERTY_NAME_D_NUMBER);
        property_d_number.setType(SDOConstants.SDO_STRING);
        property_d_number.setMany(false);
        List aliasNames_ = new ArrayList();
        aliasNames_.add("alias1");
        aliasNames_.add("alias2");
        property_d_number.setAliasNames(aliasNames_);
        type_d.addDeclaredProperty(property_d_number);

        property_d_number1 = new SDOProperty(aHelperContext);
        property_d_number1.setName(PROPERTY_NAME_D_NUMBER + "1");
        property_d_number1.setType(SDOConstants.SDO_DOUBLE);
        property_d_number1.setMany(true);
        type_d.addDeclaredProperty(property_d_number1);

        dataObject_d0 = (SDODataObject)dataFactory.create(type_d);

        dataObject_d1 = (SDODataObject)dataFactory.create(type_d);

        // start building tree:
        dataObject_a.set(property_a, dataObject_b);// set a/

        List objects_b = new ArrayList();
        objects_b.add(dataObject_c);//

        dataObject_b.set(property_b, objects_b);// set a/b.0

        List objects_c = new ArrayList();
        objects_c.add(dataObject_d0);
        objects_c.add(dataObject_d1);

        dataObject_c.set(property_c, objects_c);// set a/b.0/c

        dataObject_d0.set(property_d_number, "123");// set a/b.0/c[numbet=123]
        dataObject_d1.set(property_d_number, "1");

        List d_list0 = new ArrayList();
        List d_list1 = new ArrayList();
        d_list0.add(new Double("1.11"));
        d_list0.add(new Double("2.22"));
        d_list1.add(new Double("3.33"));
        d_list1.add(new Double("5.55"));

        dataObject_d0.set(property_d_number1, d_list0);// set a/b.0/c[numbet=123]
        dataObject_d1.set(property_d_number1, d_list1);

        dataObject_a.set(property_a0, dataObject_b0);// set a/

        List objects_b0 = new ArrayList();
        objects_b0.add(dataObject_c0);
        objects_b0.add(dataObject_c1);

        dataObject_b0.set(property_b0, objects_b0);// set a/b0

        dataObject_c0.set(property_c_number, "123");// set b0[number=123]
        dataObject_c1.set(property_c_number, "1");

        List str = new ArrayList();
        DataObject dataObject1 = dataFactory.create(type_d);
        dataObject1.set(PROPERTY_NAME_D_NUMBER,"one");
        DataObject dataObject2 = dataFactory.create(type_d);
        dataObject2.set(PROPERTY_NAME_D_NUMBER,"two");
        str.add(dataObject1);
        str.add(dataObject2);              

        dataObject_c0.set(property_c0, str);// set c0.0

        baseType = new SDOType("baseUri", "base");
        baseProperty1 = new SDOProperty(aHelperContext);
        baseProperty1.setName("baseProperty1");
        baseProperty1.setType(SDOConstants.SDO_STRING);
        baseProperty1.setMany(false);
        List aliasNames_baseP1 = new ArrayList();
        aliasNames_baseP1.add("alias3");
        aliasNames_baseP1.add("alias4");
        baseProperty1.setAliasNames(aliasNames_baseP1);

        baseType.addDeclaredProperty(baseProperty1);

        baseProperty2 = new SDOProperty(aHelperContext);
        baseProperty2.setName("baseProperty2");
        baseProperty2.setType(dataObjectType);
        baseProperty2.setMany(false);
        baseProperty2.setContainment(true);
        List aliasNames_baseP2 = new ArrayList();
        aliasNames_baseP2.add("alias5");
        aliasNames_baseP2.add("alias6");
        baseProperty2.setAliasNames(aliasNames_baseP2);

        baseType.addDeclaredProperty(baseProperty2);

        baseType1 = new SDOType("base1Uri", "base1");
        baseProperty3 = new SDOProperty(aHelperContext);
        baseProperty3.setName("baseProperty3");
        baseProperty3.setType(SDOConstants.SDO_STRING);
        baseProperty3.setMany(false);
        List aliasNames_baseP3 = new ArrayList();
        aliasNames_baseP3.add("alias7");
        aliasNames_baseP3.add("alias8");
        baseProperty3.setAliasNames(aliasNames_baseP3);

        baseType1.addDeclaredProperty(baseProperty3);

    }
}
