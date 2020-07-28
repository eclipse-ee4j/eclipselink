/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.Property;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class SDODataObjectGetDataObjectConversionWithPathTest extends SDODataObjectConversionWithPathTestCases {
    public SDODataObjectGetDataObjectConversionWithPathTest(String name) {
        super(name);
    }

 public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDataObjectConversionWithPathTest" };
        TestRunner.main(arguments);
    }

    // purpose: pass a/b, to get dataObjcet c
    public void testGetDataObjectConversionFromDefinedProperty() {
        this.assertEquals(dataObject_c, dataObject_a.getDataObject("PName-a/PName-b"));
    }

    // purpose: opencontent properties
    public void testGetDataObjectConversionFromDefinedPropertyWithPath() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        // dataObject's type add boolean property
        type_b.setOpen(true);
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName("openTest");
        property.setType(dataObjectType);

        SDODataObject b = new SDODataObject();

        dataObject_b.setDataObject(property, b);// add it to instance list

        this.assertEquals(b, dataObject_a.getDataObject("PName-a/openTest"));
    }

    //2. purpose: getDataObject with property value is not dataobject
    public void testGetDataObjectConversionFromUndefinedProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        boolean c = true;
        Boolean C = new Boolean(c);

        dataObject_c.set(property_c, C);

        try {
            dataObject_a.getDataObject(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getDataObject with property set to boolean value
    public void testGetDataObjectConversionFromProperty() {
        //try {
        this.assertNull(dataObject_a.getDataObject("PName-a/notExistedTest"));

        //fail("IllegalArgumentException should be thrown.");
        //} catch (IllegalArgumentException e) {
        //}
    }

    //purpose: getDataObject with nul value
    public void testGetDataObjectConversionWithNullArgument() {
        String p = null;
        this.assertNull(dataObject_a.getDataObject(p));
    }
}
