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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;

public class LoadAndSaveWithImportsTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveWithImportsTestCases(String name) {
        super(name);
    }

    protected List defineTypes() {
        try {
            URL url = new URL(getSchemaLocation() + getSchemaName());
            InputStream is = url.openStream();

            return xsdHelper.define(is, getSchemaLocation());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getUnrelatedSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/OrderBookingPO.xsd";
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/schemas/";
    }

    protected String getSchemaName() {
        return "OrderBookingRequest.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/orderBookingRequest.xml");
    }

    protected String getNoSchemaControlFileName() {
        //return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/orderBookingRequestNoSchema.xml";
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/orderBookingRequestNoSchema.xml");
    }
    
     protected String getControlWriteFileName() {
            
       return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/orderBookingRequestWrite.xml");      
    }

    protected String getControlRootURI() {
        return "http://www.globalcompany.com/ns/OrderBooking";
    }

    protected String getControlRootName() {
        return "SOAOrderBookingProcessRequest";
    }
    
    protected String getRootInterfaceName() {
        return "SOAOrderBookingProcessRequest";
    }
    
    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add("com/globalcompany/ns/orderbooking");
        packages.add("com/globalcompany/ns/order");
        return packages;
    }
    
    protected void generateClasses(String tmpDirName) throws Exception{
            
        URL url = new URL(getSchemaLocation() + getSchemaName());
        InputStream is = url.openStream();
        
        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);

        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation(getSchemaLocation());
        StreamSource ss = new StreamSource(is);
        classGenerator.generate(ss, tmpDirName, schemaResolver);
    }

        
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveWithImportsTestCases" };
        TestRunner.main(arguments);
    }

    public void registerTypes() {
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type decimalType = typeHelper.getType("commonj.sdo", "Decimal");
        Type dateType = typeHelper.getType("commonj.sdo", "Date");
        Type booleanType = typeHelper.getType("commonj.sdo", "Boolean");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject nameType = defineType("http://www.globalcompany.com/ns/order", "Name");
        addProperty(nameType, "First", stringType,false, false, true);
        addProperty(nameType, "Last", stringType,false, false, true);
        Type nameSDOType = typeHelper.define(nameType);

        DataObject itemType = defineType("http://www.globalcompany.com/ns/order", "ItemType");
        addProperty(itemType, "ProductName", stringType,false, false, true);
        addProperty(itemType, "itemType", stringType,false, false, true);
        addProperty(itemType, "partnum", stringType,false, false, true);
        addProperty(itemType, "price", decimalType,false, false, true);
        addProperty(itemType, "Quantity", decimalType,false, false, true);
        Type itemSDOType = typeHelper.define(itemType);

        DataObject orderItemsType = defineType("http://www.globalcompany.com/ns/order", "OrderItemsType");
        DataObject itemsProp = addProperty(orderItemsType, "Item", itemSDOType, true, true, true);        
        Type orderItemsSDOType = typeHelper.define(orderItemsType);

        DataObject addressType = defineType("http://www.globalcompany.com/ns/order", "Address");
        addProperty(addressType, "Street", stringType,false, false, true);
        addProperty(addressType, "City", stringType,false, false, true);
        addProperty(addressType, "State", stringType,false, false, true);
        addProperty(addressType, "Zip", stringType,false, false, true);
        addProperty(addressType, "Country", stringType,false, false, true);
        Type addressSDOType = typeHelper.define(addressType);

        DataObject usAddressType = defineType("http://www.globalcompany.com/ns/order", "USAddress");
        addProperty(usAddressType, "Name", nameSDOType,true, false, true);
        addProperty(usAddressType, "Address", addressSDOType,true, false, true);
        Type usAddressSDOType = typeHelper.define(usAddressType);

        DataObject contactType = defineType("http://www.globalcompany.com/ns/order", "ContactType");
        addProperty(contactType, "PhoneNumber", stringType,false, false, true);
        addProperty(contactType, "EmailAddress", stringType,false, false, true);
        Type contactSDOType = typeHelper.define(contactType);

        DataObject supplierInfoType = defineType("http://www.globalcompany.com/ns/order", "SupplierInfoType");
        addProperty(supplierInfoType, "SupplierPrice", decimalType,false, false, true);
        addProperty(supplierInfoType, "SupplierName", stringType,false, false, true);
        Type supplierInfoSDOType = typeHelper.define(supplierInfoType);

        DataObject orderInfoType = defineType("http://www.globalcompany.com/ns/order", "OrderInfoType");
        //addProperty(orderInfoType, "OrderDate", dateType, false, false, true);
        addProperty(orderInfoType, "OrderDate", SDOConstants.SDO_DATETIME, false, false, true);
        addProperty(orderInfoType, "OrderPrice", decimalType,false, false, true);
        addProperty(orderInfoType, "OrderStatus", stringType,false, false, true);
        addProperty(orderInfoType, "OrderComments", stringType,false, false, true);
        addProperty(orderInfoType, "ApprovalRequired", booleanType,false, false, true);

        //DataObject orderPoProp = addProperty(orderInfoType, "PurchaseOrder", poSDOType);
        //orderPoProp.set("containment", false);        
        Type orderInfoSDOType = typeHelper.define(orderInfoType);

        DataObject poType = defineType("http://www.globalcompany.com/ns/order", "PurchaseOrderType");
        addProperty(poType, "CustID", stringType, false, false, true);
        addProperty(poType, "ID", stringType,false, false, true);
        addProperty(poType, "ShipTo", usAddressSDOType,true, false, true);
        addProperty(poType, "BillTo", usAddressSDOType,true, false, true);
        addProperty(poType, "UserContact", contactSDOType,true, false, true);
        addProperty(poType, "OrderItems", orderItemsSDOType,true, false, true);
        addProperty(poType, "SupplierInfo", supplierInfoSDOType,true, false, true);
        addProperty(poType, "OrderInfo", orderInfoSDOType,true, false, true);
        Type poSDOType = typeHelper.define(poType);


        DataObject purchaseOrderPropDO = dataFactory.create(propertyType);
        purchaseOrderPropDO.set("name", "PurchaseOrder");
        purchaseOrderPropDO.set("type", poSDOType);
        Property prop = typeHelper.defineOpenContentProperty("http://www.globalcompany.com/ns/order", purchaseOrderPropDO);
        
        DataObject purchaseOrderPropDO2 = dataFactory.create(propertyType);
        purchaseOrderPropDO2.set("name", "PurchaseOrder2");
        purchaseOrderPropDO2.set("type", poSDOType);
        Property prop2 = typeHelper.defineOpenContentProperty("http://www.globalcompany.com/ns/order", purchaseOrderPropDO2);
        
        DataObject purchaseOrderPropDO3 = dataFactory.create(propertyType);
        purchaseOrderPropDO3.set("name", "PurchaseOrder3");
        purchaseOrderPropDO3.set("type", poSDOType);
        Property prop3 = typeHelper.defineOpenContentProperty("http://www.globalcompany.com/ns/order", purchaseOrderPropDO3);
        
        DataObject purchaseOrderPropDO4 = dataFactory.create(propertyType);
        purchaseOrderPropDO4.set("name", "PurchaseOrder4");
        purchaseOrderPropDO4.set("type", poSDOType);
        Property prop4 = typeHelper.defineOpenContentProperty("http://www.globalcompany.com/ns/order", purchaseOrderPropDO4);


        // create a new Type for Customers
        //TODO: the getControlRootURI is not in the NamespaceResolver for the type
        DataObject SOAOrderBookingProcessRequestTypeDO = defineType(getControlRootURI(), "SOAOrderBookingProcessRequest");
        SOAOrderBookingProcessRequestTypeDO.set("open", true);
        //DataObject poProp = addProperty(SOAOrderBookingProcessRequestTypeDO, "PurchaseOrder", poSDOType, true, false, true);
        
        // now define the Customer type so that customers can be made
        Type SOAOrderBookingProcessRequestType = typeHelper.define(SOAOrderBookingProcessRequestTypeDO);
        
        DataObject SOAOrderBookingProcessRequestPropDO = dataFactory.create(propertyType);
        SOAOrderBookingProcessRequestPropDO.set("name", getControlRootName());
        SOAOrderBookingProcessRequestPropDO.set("type", SOAOrderBookingProcessRequestType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), SOAOrderBookingProcessRequestPropDO);
    }
}
