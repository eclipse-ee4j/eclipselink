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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery;

import commonj.sdo.DataObject;
import java.util.ArrayList;
import java.util.List;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class SDODataObjectGetByXPathQueryTest extends SDODataObjectGetByXPathQueryTestCases {
    public SDODataObjectGetByXPathQueryTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery.SDODataObjectGetByXPathQueryTest" };
        TestRunner.main(arguments);
    }
    
    //case:a/b.0/c[number="123"] where number is a many type property, and no objects meets requirement
    public void testMultipleCaseOueryD1Colon() {
        dataObject_d1.set(property_d_number, "123");
        this.assertNull(dataObject_a.get("schema:PName-a/PName-b.0/PName-c[number=\"1\"]"));
    }

    //case:a/b.0/c[number="123"] where number is a many type property, and no objects meets requirement
    public void testMultipleCaseOueryD1AliasName() {
        List aliasNames = new ArrayList();
        aliasNames.add("alias1");
        aliasNames.add("alias2");
        property_d_number.setAliasNames(aliasNames);
        this.assertEquals(dataObject_d1, dataObject_a.get("schema:PName-a/PName-b.0/PName-c[alias1=\"1\"]"));
    }

    //case:a/b.0/c[number="123"] where number is a many type property, and no objects meets requirement
    public void testMultipleCaseOueryD1AliasName_() {
        List aliasNames = new ArrayList();
        aliasNames.add("alias1");
        aliasNames.add("alias2");
        property_b.setAliasNames(aliasNames);
        this.assertEquals(dataObject_d1, dataObject_a.get("schema:PName-a/alias2.0/PName-c[number=\"1\"]"));
    }

    public void testMiddleOpenContentPropertyAliasName() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        SDOProperty property_open = new SDOProperty(aHelperContext);
        property_open.setName("openProperty");
        property_open.setType(dataObjectType);
        property_open.setMany(false);
        property_open.setContainment(true);
        List aliasNames_ = new ArrayList();
        aliasNames_.add("alias1");
        aliasNames_.add("alias2");
        property_open.setAliasNames(aliasNames_);
        SDOType t = new SDOType("turi", "t");

        //d.setType(t);
        SDOProperty p = new SDOProperty(aHelperContext);
        p.setName("P");
        p.setType(SDOConstants.SDO_STRING);
        p.setMany(false);
        t.addDeclaredProperty(p);
        SDODataObject d = (SDODataObject)dataFactory.create(t);
        d.set(p, "test");

        dataObject_b.set(property_open, d);

        this.assertEquals("test", dataObject_a.get("schema:PName-a/openProperty/P"));

    }

    //case:a/b.0/c[number="123"] where number is a many type property, and no objects meets requirement
    public void testMultipleCaseOueryD1AliasName1() {
        //List aliasNames = new ArrayList();
        //aliasNames.add("alias1");
        //aliasNames.add("alias2");
        //property_d_number.setAliasNames(aliasNames);
        this.assertEquals(dataObject_d1, dataObject_a.get("schema:PName-a/PName-b.0/PName-c[alias1=\"1\"]"));
    }

    public void testMultipleCaseOueryD1AliasNameBaseTypes() {
        //List aliasNames = new ArrayList();
        //aliasNames.add("alias1");
        //aliasNames.add("alias2");
        type_d.addBaseType(baseType);
        type_d.addBaseType(baseType1);

        dataObject_d0._setType(type_d);

        dataObject_d0.set("alias3", "1");
        this.assertEquals(dataObject_d0, dataObject_a.get("schema:PName-a/PName-b.0/PName-c[alias3=\"1\"]"));
    }

    public void testMultipleCaseOueryD1AliasNameBaseTypes1() {
        //List aliasNames = new ArrayList();
        //aliasNames.add("alias1");
        //aliasNames.add("alias2");
        //property_d_number.setAliasNames(aliasNames);
        type_c.addBaseType(baseType);
        type_c.addBaseType(baseType1);
        dataObject_c._setType(type_c);
        dataObject_c.set("alias3", "1");
        this.assertNull(dataObject_a.get("schema:PName-a/PName-b.0/alias10"));
    }

    public void testMultipleCaseOueryD1AliasNameBaseTypes2() {
        //List aliasNames = new ArrayList();
        //aliasNames.add("alias1");
        //aliasNames.add("alias2");
        //property_d_number.setAliasNames(aliasNames);
        SDOType t = new SDOType("turi", "t");

        SDOProperty p = new SDOProperty(aHelperContext);
        p.setName("P");
        p.setType(SDOConstants.SDO_STRING);
        p.setMany(false);
        t.addDeclaredProperty(p);
        SDODataObject d = (SDODataObject)dataFactory.create(t);
        d.set(p, "test");

        type_b.addBaseType(baseType);
        type_b.addBaseType(baseType1);
        dataObject_b._setType(type_b);
        dataObject_b.set("alias5", d);
        this.assertEquals("test", dataObject_a.get("schema:PName-a/alias5/P"));
    }

    public void testMultipleCaseOueryD1AliasNameBaseTypes3() {
        //List aliasNames = new ArrayList();
        //aliasNames.add("alias1");
        //aliasNames.add("alias2");
        //property_d_number.setAliasNames(aliasNames);
        SDOType t = new SDOType("turi", "t");

        SDOProperty p = new SDOProperty(aHelperContext);
        p.setName("P");
        p.setType(SDOConstants.SDO_STRING);
        p.setMany(false);
        t.addDeclaredProperty(p);
        SDODataObject d = (SDODataObject)dataFactory.create(t);
        d.set(p, "test");

        type_b.addBaseType(baseType);
        type_b.addBaseType(baseType1);
        dataObject_b._setType(type_b);
        dataObject_b.set("alias5", d);
        this.assertEquals("test", dataObject_a.get("schema:PName-a/baseProperty2/P"));
    }

    //case:a/b.0/c[number="123"] where number is a many type property, and no objects meets requirement
    public void testMultipleCaseOueryD1AliasName1_() {
        this.assertEquals(dataObject_d1, dataObject_a.get("schema:PName-a/alias2.0/PName-c[number=\"1\"]"));
    }

    // case: a/b[number=123]/c, more than one dataobject b in value list of dataobject a's property
    // b meets the query requirement number=123
    public void testMultipleCaseOuery() {
        dataObject_d1.set(property_d_number, "123");
        this.assertEquals(dataObject_d0, dataObject_a.get(property));
    }

    //case:a/b.0/c[number="123"] where number is a many type property, and no objects meets requirement
    public void testMultipleCaseOueryD1() {
        dataObject_d1.set(property_d_number, "123");
        this.assertNull(dataObject_a.get("PName-a/PName-b.0/PName-c[number1=\"1\"]"));
    }

    //case:a/b.0/c[number='123'] where number is a many type property, and there are objects that meets requirement
    // and value is doule 5.55
    public void testMultipleCaseOueryD() {
        dataObject_d1.set(property_d_number, "123");
        this.assertEquals(dataObject_d1, dataObject_a.get(propertyD));
    }

    // case: a/b[number=123]/c, no match for the query condition: number=123
    public void testQueryNotMatch() {
        this.assertNull(dataObject_a.get("PName-a0/PName-b0[number='23']/PName-c0.0"));
    }

    // case: a/b[number=123]/c, dataobject b in value list of dataobject a's property b does not have
    // property number
    public void testQueryMiddlePositionalAtLast() {        
        DataObject resultDO = dataObject_a.getDataObject(property1);
        String result = resultDO.getString(PROPERTY_NAME_D_NUMBER);      
        this.assertEquals("one", result);
    }

    // case: a/b[number=123]/c[2], c is many type property, and access by bracket and second position
    public void testQueryMiddlePositionalAtLast1() {        
        DataObject resultDO = dataObject_a.getDataObject("PName-a0/PName-b0[number=\"123\"]/PName-c0[2]");
        String result = resultDO.getString(PROPERTY_NAME_D_NUMBER);      
        this.assertEquals("two", result);
    }

    // case: a/b[number=123]/c[2], c is many type property, and access by bracket and second position
    public void testQueryMiddlePositionalAtLast1ContainingAt() {        
        DataObject resultDO = dataObject_a.getDataObject("@PName-a0/@PName-b0[number=\"123\"]/@PName-c0[2]");
        String result = resultDO.getString(PROPERTY_NAME_D_NUMBER);      
        this.assertEquals("two", result);
    }

    // case: a/b[number=123]/c[name='Jane']
    // case: a/b.0/c[number='123'], no match for condition: name='jane'
    public void testPositionalInMiddleQueryAtLastDataObject() {
        this.assertEquals(dataObject_d0, dataObject_a.get(property));

    }

    // case: null input
    public void testQueryBasePathWithNullValue() {
        String v = null;
        this.assertNull(dataObject_a.get(v));
    }

    // purpose: test path as "/"
    public void testGetByXPathQueryWithShortBracketPath() {
        this.assertEquals(dataObject_d1, dataObject_c.get("PName-c[number1='5.55']"));

    }
}
