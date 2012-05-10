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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class SDODataObjectGetWithPathTest extends SDODataObjectConversionWithPathTestCases {
    public SDODataObjectGetWithPathTest(String name) {
        super(name);
    }

    //1. purpose: get with path ("a/b/c")
    public void testGetConversionWithFullPath() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        boolean c = true;
        Boolean C = new Boolean(c);

        //dataObject_a.setBoolean("PName-a/PName-b/PName-c", c);// c dataobject's a property has value boolean 'true'
        dataObject_a.set("PName-a/PName-b/PName-c", C);
        assertEquals(C, dataObject_a.get("PName-a/PName-b/PName-c"));
    }

    //2. purpose: getBoolean with path ("a/b/c")
    public void testGetBooleanConversionWithFullPath() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        boolean c = true;
        Boolean C = new Boolean(c);

        dataObject_a.setBoolean("PName-a/PName-b/PName-c", c);// c dataobject's a property has value boolean 'true'
        //dataObject_a.set("PName-a/PName-b/PName-c", C);
        assertEquals(c, dataObject_a.getBoolean("PName-a/PName-b/PName-c"));
    }

    //3. purpose: set(Path) with path ("a/b/c"), and b is not a dataobject
    public void testSetConversionFromWrongPath() {
        // dataObject's type add boolean property
        property_c_bNotSDODataOject = new SDOProperty(aHelperContext);
        property_c_bNotSDODataOject.setName(PROPERTY_NAME_C);
        property_c_bNotSDODataOject.setType(SDOConstants.SDO_BOOLEAN);
        type_c_bNotSDODataOject.addDeclaredProperty(property_c_bNotSDODataOject);
        dataObject_c_bNotSDODataOject._setType(type_c_bNotSDODataOject);

        boolean c = true;
        Boolean C = new Boolean(c);

        //dataObject_c_bNotSDODataOject.setBoolean(property_c_bNotSDODataOject, c);// c dataobject's a property has value boolean 'true'
        try {
            dataObject_a_bNotSDODataOject.set("PName-a/PName-b/PName-c", C);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }

        //assertNull(dataObject_a_bNotSDODataOject.get("PName-a/PName-b/PName-c"));
    }

    //4. purpose: setBoolean(Path) with path ("a/b/c"), and b is not a dataobject
    public void testSetBooleanConversionFromWrongPath() {
        // dataObject's type add boolean property
        property_c_bNotSDODataOject = new SDOProperty(aHelperContext);
        property_c_bNotSDODataOject.setName(PROPERTY_NAME_C);
        property_c_bNotSDODataOject.setType(SDOConstants.SDO_BOOLEAN);
        type_c_bNotSDODataOject.addDeclaredProperty(property_c_bNotSDODataOject);
        dataObject_c_bNotSDODataOject._setType(type_c_bNotSDODataOject);

        boolean c = true;
        //Boolean C = new Boolean(c);

        //dataObject_c_bNotSDODataOject.setBoolean(property_c_bNotSDODataOject, c);// c dataobject's a property has value boolean 'true'
        try {
            dataObject_a_bNotSDODataOject.setBoolean("PName-a/PName-b/PName-c", c);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }

        //assertNull(dataObject_a_bNotSDODataOject.get("PName-a/PName-b/PName-c"));
    }

    //5. purpose: get(Path) with path ("a/b/c"), and b is not a dataobject
    public void testGetBooleanConversionFromWrongPath() {
        // dataObject's type add boolean property
        property_c_bNotSDODataOject = new SDOProperty(aHelperContext);
        property_c_bNotSDODataOject.setName(PROPERTY_NAME_C);
        property_c_bNotSDODataOject.setType(SDOConstants.SDO_BOOLEAN);
        type_c_bNotSDODataOject.addDeclaredProperty(property_c_bNotSDODataOject);
        dataObject_c_bNotSDODataOject._setType(type_c_bNotSDODataOject);

        boolean c = true;
        //Boolean C = new Boolean(c);

        dataObject_c_bNotSDODataOject.setBoolean(property_c_bNotSDODataOject, c);// c dataobject's a property has value boolean 'true'
        assertNull(dataObject_a_bNotSDODataOject.get("PName-a/PName-b/PName-c"));

    }

    //6. purpose: get(Path) with path length 1: "a"
    public void testGetBooleanConversionFromPathWithLength_1() {
        property_a_pathLength_1.setType(SDOConstants.SDO_BOOLEAN);
        boolean b = true;
        Boolean B = new Boolean(b);

        dataObject_a_pathLength_1.setBoolean("PName-a-length-1", b);

        assertEquals(B, dataObject_a_pathLength_1.get("PName-a-length-1"));
    }

    //7. purpose: get(Path) with path=null
    public void testGetBooleanConversionFromNullPath() {
        String path = null;
        assertNull(dataObject_a_pathLength_1.get(path));
    }

    //8. purpose: getBoolean with path ("a/b/c"), and c's value is not
    //   set, and c's default value is null
    public void testGetBooleanConversionWithLastPropertyValueNotSet_NullDefaultValue() {
        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        boolean c = true;

        //dataObject_c.setBoolean(property_c, c);  // c dataobject's a property has value boolean 'true'
        //assertEquals(c, dataObject_a.getBoolean("PName-a/PName-b/PName-c"));
    }

    //9. purpose: set with path ("a/b/c") where 'b' has null value. !! now, null pointer exception show throw
    public void testGetConversionWithNullValueInPath() {
        SDODataObject aDataObject_a = new SDODataObject();
        SDOType aType_a = new SDOType(URINAME, TYPENAME_A);
        SDOProperty aProperty_a = new SDOProperty(aHelperContext);
        aProperty_a.setName(PROPERTY_NAME_A);
        aType_a.addDeclaredProperty(aProperty_a);
        aDataObject_a._setType(aType_a);

        SDODataObject aDataObject_b = new SDODataObject();
        SDOType aType_b = new SDOType(URINAME, TYPENAME_B);
        SDOProperty aProperty_b = new SDOProperty(aHelperContext);
        aProperty_b.setName(PROPERTY_NAME_B);
        aType_b.addDeclaredProperty(aProperty_b);
        aDataObject_b._setType(aType_b);

        // 6159746: no null Type allowed on Property Object
        aProperty_a.setType(aType_a);
        aProperty_b.setType(aType_b);
        
        aDataObject_a.set(aProperty_a, aDataObject_b);// a dataobject's a property has value b dataobject
        aDataObject_b.set(aProperty_b, null);// b dataobject's b property has value c dataobject

        //Boolean C = new Boolean(true);

        //dataObject_a.setBoolean("PName-a/PName-b/PName-c", true);// c dataobject's a property has value boolean 'true'
        // null pointer is not handled yet
        //dataObject_a.set("PName-a/PName-b/PName-c", C);
        assertNull(aDataObject_a.get("PName-a/PName-b/PName-c"));
        //assertEquals(C, aDataObject_a.get("PName-a/PName-b/PName-c"));
    }
}
