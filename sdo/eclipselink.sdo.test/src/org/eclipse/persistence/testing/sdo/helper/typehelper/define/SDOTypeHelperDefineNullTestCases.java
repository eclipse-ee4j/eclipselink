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
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.exceptions.SDOException;

public class SDOTypeHelperDefineNullTestCases extends SDOTestCase {
    public SDOTypeHelperDefineNullTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.define.SDOTypeHelperDefineNullTestCases" };
        TestRunner.main(arguments);
    }

    public void testDefineNullName() {
        DataObject dataObject = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)dataObject.getType().getProperty("uri");
        dataObject.set(prop, "http://example.com/customer");
        try {
            typeHelper.define(dataObject);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("An IllegalArgumentException should have occurred but didn't ");
    }

    public void testDefineNullURI() {        
        DataObject dataObject = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)dataObject.getType().getProperty("name");
        dataObject.set(prop, "Customer");

        typeHelper.define(dataObject);

    }

    public void testDefineNullDataObject() {
        try {
            DataObject dataObject = null;
            typeHelper.define(dataObject);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("An IllegalArgumentException should have occurred but didn't ");
    }

    public void testDefineNonTypeDataObject() {
        try {
            DataObject dataObject = dataFactory.create("my.uri", "Customer");
            typeHelper.define(dataObject);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("An IllegalArgumentException should have occurred but didn't ");
    }

    public void testDefineInvalidURI() {
        DataObject dataObject = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)dataObject.getType().getProperty("name");
        dataObject.set(prop, "Customer");

        SDOProperty prop2 = (SDOProperty)dataObject.getType().getProperty("uri");
        dataObject.set(prop2, "my.notintypehelper.uri");

        Type type = typeHelper.define(dataObject);
        assertNotNull(type);

        assertEquals("Customer", type.getName());
        assertEquals("my.notintypehelper.uri", type.getURI());
    }

    public void testPropertyWithNullType() {
        DataObject purchaseOrderTypeType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("uri");
        purchaseOrderTypeType.set(prop, "http://example.com/");

        prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("name");
        purchaseOrderTypeType.set(prop, "PurchaseOrderType");

        // create a orderDateProperty 
        addProperty(purchaseOrderTypeType, "orderDate", null);
        try {
            typeHelper.define(purchaseOrderTypeType);
        } catch (SDOException e) {                        
            assertEquals(e.getErrorCode(), SDOException.NO_TYPE_SPECIFIED_FOR_PROPERTY);
            return;
        }catch (Exception e) {                                  
        }
        fail("An exception should have been thrown but wasn't");
    }
}
