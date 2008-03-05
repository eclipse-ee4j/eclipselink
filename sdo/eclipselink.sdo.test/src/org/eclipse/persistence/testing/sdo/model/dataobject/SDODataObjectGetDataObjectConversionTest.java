/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import commonj.sdo.Property;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetDataObjectConversionTest extends SDODataObjectConversionTestCases {
    public SDODataObjectGetDataObjectConversionTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetDataObjectConversionTest" };
        TestRunner.main(arguments);
    }

    public void testGetDataObjectConversionFromDefinedProperty() {
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATAOBJECT);

        SDODataObject b = new SDODataObject();

        dataObject.setDataObject(property, b);// add it to instance list

        this.assertEquals(b, dataObject.getDataObject(property));
    }

    public void testGetDataObjectConversionFromDefinedPropertyWithPath() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATAOBJECT);

        SDODataObject b = new SDODataObject();

        dataObject.setDataObject(PROPERTY_NAME, b);// add it to instance list

        this.assertEquals(b, dataObject.getDataObject(property));
    }

    //2. purpose: getDataObject with Undefined Boolean Property
    public void testGetDataObjectConversionFromUndefinedProperty() {
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_DATAOBJECT);

        try {
            dataObject.getDataObject(property);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //3. purpose: getDataObject with property set to boolean value
    public void testGetDataObjectConversionFromProperty() {
        // dataObject's type add boolean property        
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATAOBJECT);                
        type.setOpen(true);

        boolean b = true;
        dataObject.set(property, b);// add it to instance list
        try {
            dataObject.getDataObject(property);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //purpose: getDataObject with nul value
    public void testGetDataObjectConversionWithNullArgument() {
        try {
            Property p = null;
            dataObject.getDataObject(p);
            fail("IllegalArgumentException should be thrown.");
        } catch (IllegalArgumentException e) {
        }
    }

    //purpose: getBoolean with Defined Boolean Property
    public void testGetDataObjectConversionFromPropertyIndex() {
        // dataObject's type add boolean property
        SDOProperty property = ((SDOProperty)dataObject.getInstanceProperty(PROPERTY_NAME));
        property.setType(SDOConstants.SDO_DATAOBJECT);
        type.addDeclaredProperty(property);
        type.setOpen(true);

        SDODataObject b = new SDODataObject();

        dataObject.setDataObject(PROPERTY_INDEX, b);// add it to instance list

        this.assertEquals(b, dataObject.getDataObject(PROPERTY_INDEX));
    }

    //purpose: getDouble with nul value
    public void testGetDataObjectWithInvalidIndex() {
        try {
            int p = -1;
            dataObject.getDataObject(p);
       } catch (SDOException e) {
            assertEquals(SDOException.PROPERTY_NOT_FOUND_AT_INDEX ,e.getErrorCode());
            return;
        }
        fail("an SDOException should have occurred.");    
    }
}