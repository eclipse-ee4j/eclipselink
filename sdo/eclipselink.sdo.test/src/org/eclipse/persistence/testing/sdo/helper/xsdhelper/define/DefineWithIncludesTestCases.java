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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.helper.ClassConstants;

public class DefineWithIncludesTestCases extends XSDHelperDefineTestCases {
    public DefineWithIncludesTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(DefineWithIncludesTestCases.class);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/IncludesWithNamespaces.xsd";
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
    }

    public List<Type> getControlTypes() {
        SDOType intType = (SDOType) typeHelper.getType("commonj.sdo", "Int");
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");

        // create a new Type for USAddress
        DataObject USaddrDO = dataFactory.create("commonj.sdo", "Type");
        USaddrDO.set("uri", "my.uri");
        USaddrDO.set("name", "USAddress");
        DataObject streetProperty = USaddrDO.createDataObject("property");
        streetProperty.set("name", "street");
        DataObject cityProperty = USaddrDO.createDataObject("property");
        cityProperty.set("name", "city");
        SDOType usAddrType = (SDOType) typeHelper.define(USaddrDO);
        usAddrType.setInstanceClassName("uri.my.USAddress");

        // create a new Type for Quantity
        DataObject QuantityTypeDO = dataFactory.create("commonj.sdo", "Type");
        QuantityTypeDO.set("uri", "my.uri");
        QuantityTypeDO.set("name", "quantityType");
        SDOType quantityType = (SDOType) typeHelper.define(QuantityTypeDO);
        quantityType.addBaseType(intType);
        quantityType.setInstanceClassName(ClassConstants.PINT.getName());

        // create a new Type for SKU
        DataObject SkuDO = dataFactory.create("commonj.sdo", "Type");
        SkuDO.set("uri", "my.uri");
        SkuDO.set("name", "SKU");
        SDOType skuType = (SDOType) typeHelper.define(SkuDO);
        skuType.addBaseType(stringType);
        skuType.setInstanceClassName("java.lang.String");

        // create a new Type for PurchaseOrder
        DataObject PurchaseOrderDO = dataFactory.create("commonj.sdo", "Type");
        PurchaseOrderDO.set("uri", "my.uri");
        PurchaseOrderDO.set("name", "PurchaseOrder");
        DataObject shipToProperty = PurchaseOrderDO.createDataObject("property");
        shipToProperty.set("name", "shipTo");
        shipToProperty.set("type", usAddrType);
        DataObject billToProperty = PurchaseOrderDO.createDataObject("property");
        billToProperty.set("name", "billTo");
        billToProperty.set("type", usAddrType);
        DataObject quantityProperty = PurchaseOrderDO.createDataObject("property");
        quantityProperty.set("name", "quantity");
        quantityProperty.set("type", quantityType);
        DataObject partNumberProperty = PurchaseOrderDO.createDataObject("property");
        partNumberProperty.set("name", "partNum");
        partNumberProperty.set("type", skuType);
        SDOType purchaseOrderType = (SDOType) typeHelper.define(PurchaseOrderDO);
        purchaseOrderType.setInstanceClassName("uri.my.PurchaseOrder");

        List<Type> types = new ArrayList<Type>();
        types.add(usAddrType);
        types.add(quantityType);
        types.add(skuType);
        types.add(purchaseOrderType);
        return types;
    }
}
