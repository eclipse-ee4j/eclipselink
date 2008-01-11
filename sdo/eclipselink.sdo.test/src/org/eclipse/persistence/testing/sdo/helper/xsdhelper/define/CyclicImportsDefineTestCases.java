/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;

public class CyclicImportsDefineTestCases extends XSDHelperDefineTestCases {
    String uri = "my.uri";
    String uri2 = "my.uri2";

    public CyclicImportsDefineTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(CyclicImportsDefineTestCases.class);
    }

    public String getSchemaToDefine() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/Cyclic1.xsd";
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
    }
    /**
     * Failure case - should resolve to the schema location (i.e. base location will be 
     * discarded) which isn't a valid URL
     */
    public void testFailWithInvalidBaseSchemaLocation() {
        Source xsdSource = new StreamSource(getSchemaToDefine());
        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation("file:./org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/");
        
        Source result = schemaResolver.resolveSchema(xsdSource, uri2, "Cyclic2.xsd");
        assertTrue("The schema should not have been resolved as the base location is invalid", result == null);
    }

    /**
     * Success case - invalid base location but absolute schema location 
     */
    public void testPassWithInvalidBaseSchemaLocation() {
        Source xsdSource = new StreamSource(getSchemaToDefine());
        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation("file:./org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/");
        
        Source result = schemaResolver.resolveSchema(xsdSource, uri2, getSchemaLocation() + "Cyclic2.xsd");
        assertTrue("The schema should have been resolved as the base location is invalid but the schema location is absolute", result != null);
    }

    /**
     * Failure case - the resolved URI should be a valid URL
     */
    public void testBaseSchemaLocationWithoutProtocol() {
        Source xsdSource = new StreamSource(getSchemaToDefine());
        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation(USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/");
        
        Source result = schemaResolver.resolveSchema(xsdSource, uri2, "Cyclic2.xsd");
        assertTrue("The schema should not have been resolved as the base location does not contain a protocol", result == null);
    }

    /**
     * Success case - relative portions of the location should be normalized
     */
    public void testBaseSchemaLocationWithRelativePath() {
        Source xsdSource = new StreamSource(getSchemaToDefine());
        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation(FILE_PROTOCOL + USER_DIR + "/./oracle/sdo/../sdo/testing/helper/xsdhelper/generate/");
        
        Source result = schemaResolver.resolveSchema(xsdSource, uri2, "Cyclic2.xsd");
        assertTrue("The schema should have been resolved but wasn't", result != null);
    }

    /**
     * Success case - DefaultSchemaResolver subclass CyclicSchemaResolver should handle
     * the empty schema location and return the correct schema source based on the namespace
     * in the include/import statement
     */
    public void testEmptySchemaLocation() {
        testDefine(new StreamSource(FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/Cyclic3.xsd"), new CyclicSchemaResolver());
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

        try {
            FileInputStream inStream = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/cyclic.xml");
            XMLDocument theDoc = xmlHelper.load(inStream);
            assertNotNull(theDoc);
            assertNotNull(theDoc.getRootObject());
            DataObject shipToDO = theDoc.getRootObject().getDataObject("shipTo");
            DataObject billToDo = theDoc.getRootObject().getDataObject("billTo");
            assertNotNull(shipToDO);
            assertNotNull(billToDo);
            DataObject shipToPhoneData = shipToDO.getDataObject("thePhone");
            assertNotNull(shipToPhoneData);
            assertEquals("1234567", shipToPhoneData.getString("number"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during xmlhelper.load");
        }
    }

    public List<SDOType> getControlTypes() {
        List<SDOType> types = new ArrayList<SDOType>();

        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri, "quantityType");
        quantityType.setXsd(true);
        quantityType.setXsdLocalName("quantityType");
        quantityType.setDataType(true);
        quantityType.setInstanceClassName("java.lang.String");
        quantityType.addBaseType((SDOType)intType);

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri, "SKU");
        SKUType.setXsd(true);
        SKUType.setXsdLocalName("SKU");
        SKUType.setInstanceClassName("java.lang.String");
        SKUType.setDataType(true);
        SKUType.addBaseType((SDOType)stringType);

        /****PHONE TYPE*****/
        SDOType phoneType = new SDOType(uri, "PhoneType");
        phoneType.setXsd(true);
        phoneType.setXsdLocalName("PhoneType");
        phoneType.setDataType(false);
        phoneType.setInstanceClassName("uri.my.PhoneType");

        SDOProperty numberProp = new SDOProperty(aHelperContext);
        numberProp.setName("number");
        numberProp.setXsdLocalName("number");
        numberProp.setXsd(true);
        numberProp.setType(stringType);
        phoneType.addDeclaredProperty(numberProp);

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType USaddrType = new SDOType(uri2, "USAddress");
        USaddrType.setXsd(true);
        USaddrType.setXsdLocalName("USAddress");
        USaddrType.setDataType(false);
        USaddrType.setInstanceClassName("uri2.my.USAddress");

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setXsd(true);
        streetProp.setXsdLocalName("street");
        streetProp.setType(stringType);
        USaddrType.addDeclaredProperty(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setXsdLocalName("city");
        cityProp.setType(stringType);
        cityProp.setXsd(true);
        USaddrType.addDeclaredProperty(cityProp);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        quantityProp.setXsdLocalName("quantity");
        //quantityProp.setType(quantityType);
        quantityProp.setType(stringType);
        quantityProp.setXsd(true);
        USaddrType.addDeclaredProperty(quantityProp);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        partNumProp.setXsdLocalName("partNum");
        partNumProp.setType(SKUType);
        partNumProp.setXsd(true);
        USaddrType.addDeclaredProperty(partNumProp);

        SDOProperty phoneProp = new SDOProperty(aHelperContext);
        phoneProp.setName("thePhone");
        phoneProp.setXsdLocalName("thePhone");
        phoneProp.setType(phoneType);
        phoneProp.setXsd(true);
        phoneProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        USaddrType.addDeclaredProperty(phoneProp);

        /****PURCHASEORDER TYPE*****/
        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setXsdLocalName("shipTo");
        shipToProp.setContainment(true);
        shipToProp.setType(USaddrType);
        shipToProp.setXsd(true);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setXsdLocalName("billTo");
        billToProp.setContainment(true);
        billToProp.setType(USaddrType);
        billToProp.setXsd(true);

        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setXsd(true);
        POtype.setXsdLocalName("PurchaseOrder");
        POtype.setInstanceClassName("uri.my.PurchaseOrder");
        POtype.setDataType(false);
        POtype.addDeclaredProperty(shipToProp);
        POtype.addDeclaredProperty(billToProp);
        //POtype.addDeclaredProperty(quantityProp);
        //POtype.addDeclaredProperty(partNumProp);
        types.add(POtype);
        types.add(USaddrType);
        types.add(phoneType);
        //types.add(quantityType);
        types.add(SKUType);
        return types;
    }
}