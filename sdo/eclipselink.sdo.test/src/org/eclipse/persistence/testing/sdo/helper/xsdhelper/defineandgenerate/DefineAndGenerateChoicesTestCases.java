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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import commonj.sdo.Property;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class DefineAndGenerateChoicesTestCases extends XSDHelperDefineAndGenerateTestCases {
    public DefineAndGenerateChoicesTestCases(String name) {
        super(name);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/OrderSequenceAndChoice.xsd";
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate.DefineAndGenerateChoicesTestCases" };
        TestRunner.main(arguments);
    }

    public String getControlGeneratedFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/OrderSequenceAndChoiceGenerated.xsd";
    }

    public List getTypesToGenerateFrom() {
        return getControlTypes();
    }

    public List getControlTypes() {
        List types = new ArrayList();

        //((SDOTypeHelper)typeHelper).reset();
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        Type addressType = registerAddressType();

        /*
        DataObject purchaseOrderTypeType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("uri");
        purchaseOrderTypeType.set(prop, NON_DEFAULT_URI);
        prop = (SDOProperty)purchaseOrderTypeType.getType().getProperty("name");
        purchaseOrderTypeType.set(prop, "PurchaseOrderType");
        DataObject shipToProp = addProperty(purchaseOrderTypeType, "shipTo", addressType, true, false, true);
        DataObject billToProp = addProperty(purchaseOrderTypeType, "billTo", addressType, true, false, true);
        addProperty(purchaseOrderTypeType, "comment", stringType, false, false, true);

        addProperty(purchaseOrderTypeType, "poId", stringType);
        Type POType =  typeHelper.define(purchaseOrderTypeType);
        */
        SDOType POType = new SDOType(NON_DEFAULT_URI, "PurchaseOrderType");
        POType.setInstanceClassName("org.example.PurchaseOrderType");
        SDOProperty shipToProp = new SDOProperty(getHelperContext());
        shipToProp.setName("shipTo");
        shipToProp.setType(addressType);
        shipToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        shipToProp.setContainment(true);
        SDOProperty billToProp = new SDOProperty(getHelperContext());
        billToProp.setName("billTo");
        billToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        billToProp.setType(addressType);
        billToProp.setContainment(true);
        SDOProperty commentProp = new SDOProperty(getHelperContext());
        commentProp.setName("comment");
        commentProp.setType(SDOConstants.SDO_STRING);
        commentProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty poIDProp = new SDOProperty(getHelperContext());
        poIDProp.setName("poId");
        poIDProp.setType(SDOConstants.SDO_STRING);
        poIDProp.setContainment(false);
        //poIDProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, false);
        POType.addDeclaredProperty(shipToProp);
        POType.addDeclaredProperty(billToProp);
        POType.addDeclaredProperty(commentProp);
        POType.addDeclaredProperty(poIDProp);

        ((SDOProperty)POType.getProperty("poId")).setXsd(true);
        ((SDOProperty)POType.getProperty("poId")).setXsdLocalName("poId");
        ((SDOProperty)POType.getProperty("shipTo")).setXsd(true);
        ((SDOProperty)POType.getProperty("shipTo")).setXsdLocalName("shipTo");
        ((SDOProperty)POType.getProperty("billTo")).setXsd(true);
        ((SDOProperty)POType.getProperty("billTo")).setXsdLocalName("billTo");
        ((SDOProperty)POType.getProperty("comment")).setXsd(true);
        ((SDOProperty)POType.getProperty("comment")).setXsdLocalName("comment");

        types.add(POType);
        types.add(addressType);

        return types;
    }

    protected Type registerAddressType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        /*DataObject addressType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, NON_DEFAULT_URI);
        prop = (SDOProperty)addressType.getType().getProperty("name");
        addressType.set(prop, "AddressType");
        addProperty(addressType, "name", stringType, false, false, true);
        addProperty(addressType, "street", stringType, false, false, true);
        addProperty(addressType, "USPrice", stringType, false, false, true);
        addProperty(addressType, "CDNPrice", stringType, false, false, true);
        addProperty(addressType, "exchangeRate", stringType, false, false, true);
        addProperty(addressType, "city", stringType, false, false, true);
        DataObject newProperty = addProperty(addressType, "country", stringType, false, false, true);
        prop = (SDOProperty)newProperty.getType().getProperty("default");
        newProperty.set(prop, "US");
        addProperty(addressType, "state", stringType, false, false, true);

        addProperty(addressType, "one", stringType, false, false, true);
        addProperty(addressType, "two", stringType, false, false, true);
        addProperty(addressType, "three", stringType, false, false, true);
        addProperty(addressType, "four", stringType, false, false, true);
        addProperty(addressType, "five", stringType, false, false, true);

        addProperty(addressType, "province", stringType, false, false, true);
        addProperty(addressType, "zip", stringType, false, false, true);
        addProperty(addressType, "postalCode", stringType, false, false, true);

        Type addressSDOType = typeHelper.define(addressType);
        */
        SDOType addressSDOType = new SDOType(NON_DEFAULT_URI, "AddressType");
        addressSDOType.setInstanceClassName("org.example.AddressType");
        SDOProperty nameProp = new SDOProperty(getHelperContext());
        nameProp.setName("name");
        nameProp.setType(SDOConstants.SDO_STRING);
        nameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty streetProp = new SDOProperty(getHelperContext());
        streetProp.setName("street");
        streetProp.setType(SDOConstants.SDO_STRING);
        streetProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty USPriceProp = new SDOProperty(getHelperContext());
        USPriceProp.setName("USPrice");
        USPriceProp.setType(SDOConstants.SDO_STRING);
        USPriceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty CDNPriceProp = new SDOProperty(getHelperContext());
        CDNPriceProp.setName("CDNPrice");
        CDNPriceProp.setType(SDOConstants.SDO_STRING);
        CDNPriceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty exchangeProp = new SDOProperty(getHelperContext());
        exchangeProp.setName("exchangeRate");
        exchangeProp.setType(SDOConstants.SDO_STRING);
        exchangeProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty cityProp = new SDOProperty(getHelperContext());
        cityProp.setName("city");
        cityProp.setType(SDOConstants.SDO_STRING);
        cityProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty countryProp = new SDOProperty(getHelperContext());
        countryProp.setName("country");
        countryProp.setType(SDOConstants.SDO_STRING);
        countryProp.setDefault("US");
        countryProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty stateProp = new SDOProperty(getHelperContext());
        stateProp.setName("state");
        stateProp.setType(SDOConstants.SDO_STRING);
        stateProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty oneProp = new SDOProperty(getHelperContext());
        oneProp.setName("one");
        oneProp.setType(SDOConstants.SDO_STRING);
        oneProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty twoProp = new SDOProperty(getHelperContext());
        twoProp.setName("two");
        twoProp.setType(SDOConstants.SDO_STRING);
        twoProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty threeProp = new SDOProperty(getHelperContext());
        threeProp.setName("three");
        threeProp.setType(SDOConstants.SDO_STRING);
        threeProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty fourProp = new SDOProperty(getHelperContext());
        fourProp.setName("four");
        fourProp.setType(SDOConstants.SDO_STRING);
        fourProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty fiveProp = new SDOProperty(getHelperContext());
        fiveProp.setName("five");
        fiveProp.setType(SDOConstants.SDO_STRING);
        fiveProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty provinceProp = new SDOProperty(getHelperContext());
        provinceProp.setName("province");
        provinceProp.setType(SDOConstants.SDO_STRING);
        provinceProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty zipProp = new SDOProperty(getHelperContext());
        zipProp.setName("zip");
        zipProp.setType(SDOConstants.SDO_STRING);
        zipProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);
        SDOProperty postalCodeProp = new SDOProperty(getHelperContext());
        postalCodeProp.setName("postalCode");
        postalCodeProp.setType(SDOConstants.SDO_STRING);
        postalCodeProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, true);

        addressSDOType.addDeclaredProperty(nameProp);
        addressSDOType.addDeclaredProperty(streetProp);
        addressSDOType.addDeclaredProperty(USPriceProp);
        addressSDOType.addDeclaredProperty(CDNPriceProp);
        addressSDOType.addDeclaredProperty(exchangeProp);
        addressSDOType.addDeclaredProperty(cityProp);
        addressSDOType.addDeclaredProperty(stateProp);
        addressSDOType.addDeclaredProperty(oneProp);
        addressSDOType.addDeclaredProperty(twoProp);
        addressSDOType.addDeclaredProperty(threeProp);
        addressSDOType.addDeclaredProperty(fourProp);
        addressSDOType.addDeclaredProperty(fiveProp);
        addressSDOType.addDeclaredProperty(provinceProp);
        addressSDOType.addDeclaredProperty(zipProp);
        addressSDOType.addDeclaredProperty(postalCodeProp);
        addressSDOType.addDeclaredProperty(countryProp);

        ((SDOProperty)addressSDOType.getProperty("name")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("name")).setXsdLocalName("name");
        ((SDOProperty)addressSDOType.getProperty("street")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("street")).setXsdLocalName("street");
        ((SDOProperty)addressSDOType.getProperty("USPrice")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("USPrice")).setXsdLocalName("USPrice");
        ((SDOProperty)addressSDOType.getProperty("CDNPrice")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("CDNPrice")).setXsdLocalName("CDNPrice");
        ((SDOProperty)addressSDOType.getProperty("exchangeRate")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("exchangeRate")).setXsdLocalName("exchangeRate");
        ((SDOProperty)addressSDOType.getProperty("city")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("city")).setXsdLocalName("city");
        ((SDOProperty)addressSDOType.getProperty("country")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("country")).setXsdLocalName("country");
        ((SDOProperty)addressSDOType.getProperty("state")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("state")).setXsdLocalName("state");
        ((SDOProperty)addressSDOType.getProperty("one")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("one")).setXsdLocalName("one");
        ((SDOProperty)addressSDOType.getProperty("two")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("two")).setXsdLocalName("two");
        ((SDOProperty)addressSDOType.getProperty("three")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("three")).setXsdLocalName("three");
        ((SDOProperty)addressSDOType.getProperty("four")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("four")).setXsdLocalName("four");
        ((SDOProperty)addressSDOType.getProperty("five")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("five")).setXsdLocalName("five");
        ((SDOProperty)addressSDOType.getProperty("province")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("province")).setXsdLocalName("province");

        ((SDOProperty)addressSDOType.getProperty("zip")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("zip")).setXsdLocalName("zip");
        ((SDOProperty)addressSDOType.getProperty("postalCode")).setXsd(true);
        ((SDOProperty)addressSDOType.getProperty("postalCode")).setXsdLocalName("postalCode");

        return addressSDOType;
    }

    protected void compareGeneratedTypes(List controlTypes, List generatedTypes) {
        super.compareGeneratedTypes(controlTypes, generatedTypes);
        //also check order
        Type addressType = typeHelper.getType(NON_DEFAULT_URI, "AddressType");
        assertEquals(16, addressType.getDeclaredProperties().size());
        assertEquals("name", ((Property)addressType.getDeclaredProperties().get(0)).getName());
        assertEquals("street", ((Property)addressType.getDeclaredProperties().get(1)).getName());
        assertEquals("USPrice", ((Property)addressType.getDeclaredProperties().get(2)).getName());
        assertEquals("CDNPrice", ((Property)addressType.getDeclaredProperties().get(3)).getName());
        assertEquals("exchangeRate", ((Property)addressType.getDeclaredProperties().get(4)).getName());
        assertEquals("city", ((Property)addressType.getDeclaredProperties().get(5)).getName());
        assertEquals("state", ((Property)addressType.getDeclaredProperties().get(6)).getName());
        assertEquals("one", ((Property)addressType.getDeclaredProperties().get(7)).getName());
        assertEquals("two", ((Property)addressType.getDeclaredProperties().get(8)).getName());
        assertEquals("three", ((Property)addressType.getDeclaredProperties().get(9)).getName());
        assertEquals("four", ((Property)addressType.getDeclaredProperties().get(10)).getName());
        assertEquals("five", ((Property)addressType.getDeclaredProperties().get(11)).getName());
        assertEquals("province", ((Property)addressType.getDeclaredProperties().get(12)).getName());
        assertEquals("zip", ((Property)addressType.getDeclaredProperties().get(13)).getName());
        assertEquals("postalCode", ((Property)addressType.getDeclaredProperties().get(14)).getName());
        assertEquals("country", ((Property)addressType.getDeclaredProperties().get(15)).getName());

    }
}
