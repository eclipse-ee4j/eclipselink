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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class DefineAndGenerateSequencesPurchaseOrderTestCases extends XSDHelperDefineAndGenerateTestCases {
    public DefineAndGenerateSequencesPurchaseOrderTestCases(String name) {
        super(name);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplexSequenced.xsd";
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate.DefineAndGenerateSequencesPurchaseOrderTestCases" };
        TestRunner.main(arguments);
    }

    public String getControlGeneratedFileName() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplexSequenced.xsd";
    }

    public List getTypesToGenerateFrom() {
        return getControlTypes();
    }

    public List getControlTypes() {
        SDOType changeSummaryType = (SDOType) aHelperContext.getTypeHelper().getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);

        List<Type> types = new ArrayList<Type>();
        ((SDOTypeHelper)typeHelper).reset();
        //String xsdSchema = getSchema("org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderComplexSequenced.xsd");
        //List types = xsdHelper.define(xsdSchema);
        DataObject addressTypeDO = dataFactory.create("commonj.sdo", "Type");
        addressTypeDO.set("uri", NON_DEFAULT_URI);
        addressTypeDO.set("name", "AddressType");
        addressTypeDO.set("sequenced", true);

        addProperty(addressTypeDO, "name", SDOConstants.SDO_STRING, false, false, true);
        addProperty(addressTypeDO, "street", SDOConstants.SDO_STRING, false, true, true);
        addProperty(addressTypeDO, "city", SDOConstants.SDO_STRING, false, false, true);

        Type addressType = typeHelper.define(addressTypeDO);
        ((SDOProperty)addressType.getProperty("name")).setXsd(true);
        ((SDOProperty)addressType.getProperty("name")).setXsdLocalName("name");
        ((SDOProperty)addressType.getProperty("street")).setXsd(true);
        ((SDOProperty)addressType.getProperty("street")).setXsdLocalName("street");
        ((SDOProperty)addressType.getProperty("city")).setXsd(true);
        ((SDOProperty)addressType.getProperty("city")).setXsdLocalName("city");
        //-----------------
        DataObject usAddressTypeDO = dataFactory.create("commonj.sdo", "Type");
        usAddressTypeDO.set("uri", NON_DEFAULT_URI);
        usAddressTypeDO.set("name", "usAddressType");
        usAddressTypeDO.set("sequenced", true);
        List<Type> baseTypes = new ArrayList<Type>();
        baseTypes.add(addressType);
        usAddressTypeDO.set("baseType", baseTypes);

        DataObject stateProp = addProperty(usAddressTypeDO, "state", SDOConstants.SDO_STRING);
        stateProp.set(SDOConstants.XMLELEMENT_PROPERTY, true);

        Type usAddressType = typeHelper.define(usAddressTypeDO);
        ((SDOProperty)usAddressType.getProperty("state")).setXsd(true);
        ((SDOProperty)usAddressType.getProperty("state")).setXsdLocalName("state");
        //-----------------
        DataObject cdnAddressTypeDO = dataFactory.create("commonj.sdo", "Type");
        cdnAddressTypeDO.set("uri", NON_DEFAULT_URI);
        cdnAddressTypeDO.set("name", "cdnAddressType");
        cdnAddressTypeDO.set("sequenced", true);
        baseTypes = new ArrayList<Type>(); // reset
        baseTypes.add(addressType);
        cdnAddressTypeDO.set("baseType", baseTypes);

        addProperty(cdnAddressTypeDO, "province", SDOConstants.SDO_STRING, false, false, true);
        addProperty(cdnAddressTypeDO, "postalcode", SDOConstants.SDO_STRING, false, false, true);

        Type cdnAddressType = typeHelper.define(cdnAddressTypeDO);
        ((SDOProperty)cdnAddressType.getProperty("province")).setXsd(true);
        ((SDOProperty)cdnAddressType.getProperty("province")).setXsdLocalName("province");
        ((SDOProperty)cdnAddressType.getProperty("postalcode")).setXsd(true);
        ((SDOProperty)cdnAddressType.getProperty("postalcode")).setXsdLocalName("postalcode");

        //-----------------
        DataObject cdnAddressMailingTypeDO = dataFactory.create("commonj.sdo", "Type");
        cdnAddressMailingTypeDO.set("uri", NON_DEFAULT_URI);
        cdnAddressMailingTypeDO.set("name", "cdnAddressMailingType");
        cdnAddressMailingTypeDO.set("sequenced", true);
        baseTypes = new ArrayList<Type>(); // reset
        baseTypes.add(cdnAddressType);
        cdnAddressMailingTypeDO.set("baseType", baseTypes);

        DataObject deliveryInfoProp = addProperty(cdnAddressMailingTypeDO, "deliveryInfo", SDOConstants.SDO_STRING);
        deliveryInfoProp.set(SDOConstants.XMLELEMENT_PROPERTY, true);

        Type cdnAddressMailingType = typeHelper.define(cdnAddressMailingTypeDO);
        ((SDOProperty)cdnAddressMailingType.getProperty("deliveryInfo")).setXsd(true);
        ((SDOProperty)cdnAddressMailingType.getProperty("deliveryInfo")).setXsdLocalName("deliveryInfo");

        //-----------------
        DataObject itemTypeDO = dataFactory.create("commonj.sdo", "Type");
        itemTypeDO.set("uri", NON_DEFAULT_URI);
        itemTypeDO.set("name", "LineItemType");

        DataObject prodcutNameProp = addProperty(itemTypeDO, "productName", SDOConstants.SDO_STRING);
        prodcutNameProp.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        DataObject priceProp = addProperty(itemTypeDO, "price", SDOConstants.SDO_FLOAT);
        priceProp.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        DataObject shipDateProp = addProperty(itemTypeDO, "shipDate", SDOConstants.SDO_STRING);
        shipDateProp.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        DataObject commentProp = addProperty(itemTypeDO, "comment", SDOConstants.SDO_STRING);
        commentProp.set(SDOConstants.XMLELEMENT_PROPERTY, true);

        Type itemType = typeHelper.define(itemTypeDO);
        ((SDOProperty)itemType.getProperty("productName")).setXsd(true);
        ((SDOProperty)itemType.getProperty("productName")).setXsdLocalName("productName");

        ((SDOProperty)itemType.getProperty("price")).setXsd(true);
        ((SDOProperty)itemType.getProperty("price")).setXsdLocalName("price");

        ((SDOProperty)itemType.getProperty("shipDate")).setXsd(true);
        ((SDOProperty)itemType.getProperty("shipDate")).setXsdLocalName("shipDate");

        ((SDOProperty)itemType.getProperty("comment")).setXsd(true);
        ((SDOProperty)itemType.getProperty("comment")).setXsdLocalName("comment");
        //-----------------        
        DataObject itemsTypeDO = dataFactory.create("commonj.sdo", "Type");
        itemsTypeDO.set("uri", NON_DEFAULT_URI);
        itemsTypeDO.set("name", "Items");        
        itemsTypeDO.set("sequenced", true);

        addProperty(itemsTypeDO, "item", itemType, true, true, true);

        Type itemsType = typeHelper.define(itemsTypeDO);
        ((SDOProperty)itemsType.getProperty("item")).setXsd(true);
        ((SDOProperty)itemsType.getProperty("item")).setXsdLocalName("item");
        //-----------------
        DataObject poTypeDO = dataFactory.create("commonj.sdo", "Type");
        poTypeDO.set("uri", NON_DEFAULT_URI);
        poTypeDO.set("name", "PurchaseOrderType");

        addProperty(poTypeDO, "address", addressType, true, true, true);
        addProperty(poTypeDO, "comment", SDOConstants.SDO_STRING, false, false, true);
        addProperty(poTypeDO, "items", itemsType, true, false, true);

        Type poType = typeHelper.define(poTypeDO);
        ((SDOProperty)poType.getProperty("address")).setXsd(true);
        ((SDOProperty)poType.getProperty("address")).setXsdLocalName("address");
        ((SDOProperty)poType.getProperty("comment")).setXsd(true);
        ((SDOProperty)poType.getProperty("comment")).setXsdLocalName("comment");
        ((SDOProperty)poType.getProperty("items")).setXsd(true);
        ((SDOProperty)poType.getProperty("items")).setXsdLocalName("items");
      //  ((SDOProperty)poType.getProperty("myCS")).setXsd(true);
//        ((SDOProperty)poType.getProperty("myCS")).setXsdLocalName("myCS");
      
       //-----------------
        DataObject companyTypeDO = dataFactory.create("commonj.sdo", "Type");
        companyTypeDO.set("uri", NON_DEFAULT_URI);
        companyTypeDO.set("name", "CompanyType");

        DataObject csProp = addProperty(companyTypeDO, "myCS", changeSummaryType, true, false, true);
        csProp.set("readOnly", true);
        addProperty(companyTypeDO, "order", poType, true, false, true);        

        Type companyType = typeHelper.define(companyTypeDO);
        ((SDOProperty)companyType.getProperty("myCS")).setXsd(true);
        ((SDOProperty)companyType.getProperty("myCS")).setXsdLocalName("myCS");
        
        ((SDOProperty)companyType.getProperty("order")).setXsd(true);
        ((SDOProperty)companyType.getProperty("order")).setXsdLocalName("order");

        //------------------
        
        types.add(companyType);        
        types.add(cdnAddressType);
        types.add(usAddressType);
        types.add(itemsType);
        types.add(poType);        
        types.add(cdnAddressMailingType);
        types.add(itemType);
        types.add(addressType);


        return types;
    }
}
