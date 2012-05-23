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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathpositional;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetListByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetListByPositionalPathTest(String name) {
        super(name);
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        Boolean bb = new Boolean(true);
        List b = new ArrayList();

        //b.add(bb);
        dataObject_a.setList("PName-a/PName-b.0/PName-c", b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBoolean(property3, true);

        this.assertEquals(bb, dataObject_a.getList("PName-a/PName-b.0/PName-c").get(0));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        String bb = "test";
        List b = new ArrayList();

        dataObject_a.setList("PName-a/PName-b[1]/PName-c", b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setString(property + ".0", bb);

        this.assertEquals(bb, dataObject_a.getList("PName-a/PName-b.0/PName-c").get(0));

    }

    /* public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);

        Boolean bb = new Boolean(true);
        List b = new ArrayList();

        dataObject_a.setList("PName-a/PName-b[number=1]/PName-c", b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setBoolean(property2+"[number=1]", true);

        this.assertEquals(bb, dataObject_a.getList("PName-a/PName-b[number=1]/PName-c").get(0));

    }*/
}
