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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import java.util.List;
import java.util.ArrayList;
import junit.textui.TestRunner;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class CyclicElementRefTestCases extends XSDHelperDefineTestCases {
   String uri = "my.uri";
    String uri2 = "my.uri2";
    public CyclicElementRefTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(CyclicElementRefTestCases.class);
    }

    public String getSchemaToDefine() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/Cyclic1ElementRef.xsd";
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
    }

    /**
    * Success case - all types should be defined successfully with no infinite
    * looping or multiple schema processing.
    */
    public void testDefine() {
        testDefine(new StreamSource(getSchemaToDefine()), new DefaultSchemaResolver());
    }
    
    protected void testDefine(Source xsdSource, DefaultSchemaResolver schemaResolver) {
        schemaResolver.setBaseSchemaLocation(getSchemaLocation());

        List types = ((SDOXSDHelper)xsdHelper).define(xsdSource, schemaResolver);
        
        log("\nExpected:\n");
        List<SDOType> controlTypes = getControlTypes();
        log(controlTypes);

        log("\nActual:\n");
        log(types);

        compare(getControlTypes(), types);
    }

    
     public List<SDOType> getControlTypes() {
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
        SDOType intType = (SDOType) typeHelper.getType("commonj.sdo", "Int");

        // create a new Type for Phone
        DataObject PhoneTypeDO = dataFactory.create("commonj.sdo", "Type");
        PhoneTypeDO.set("uri", "my.uri");
        PhoneTypeDO.set("name", "PhoneType");
        DataObject numberProperty = PhoneTypeDO.createDataObject("property");
        numberProperty.set("name", "number");
        SDOType phoneType = (SDOType) typeHelper.define(PhoneTypeDO);
        phoneType.addBaseType(stringType);
        phoneType.setInstanceClassName("uri.my.PhoneType");
      
        // create a new Type for PurchaseOrder
        DataObject PurchaseOrderDO = dataFactory.create("commonj.sdo", "Type");
        PurchaseOrderDO.set("uri", "my.uri2");
        PurchaseOrderDO.set("name", "PurchaseOrder");
        DataObject orderNameProperty = PurchaseOrderDO.createDataObject("property");
        orderNameProperty.set("name", "ordername");
        orderNameProperty.set("type", SDOConstants.SDO_STRING);
        orderNameProperty.set(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        DataObject phoneTypeProperty = PurchaseOrderDO.createDataObject("property");
        phoneTypeProperty.set("name", "phoneType");
        phoneTypeProperty.set("type", phoneType);
        DataObject phoneTypeAttrProperty = PurchaseOrderDO.createDataObject("property");
        phoneTypeAttrProperty.set("name", "phoneTypeAttr");
        phoneTypeAttrProperty.set("type", SDOConstants.SDO_STRING);
        
        SDOType purchaseOrderType = (SDOType) typeHelper.define(PurchaseOrderDO);
        purchaseOrderType.setInstanceClassName("uri2.my.PurchaseOrder");

        List<SDOType> types = new ArrayList<SDOType>();
        types.add(phoneType);
        types.add(purchaseOrderType);        
        return types;
    }
}
