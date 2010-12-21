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
        List<Type> controlTypes = getControlTypes();
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
    
    public List<Type> getControlTypes() {
        SDOType intType = (SDOType) typeHelper.getType("commonj.sdo", "Int");
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");

        // create a new Type for Phone
        DataObject PhoneTypeDO = dataFactory.create("commonj.sdo", "Type");
        PhoneTypeDO.set("uri", "my.uri");
        PhoneTypeDO.set("name", "PhoneType");
        DataObject numberProperty = PhoneTypeDO.createDataObject("property");
        numberProperty.set("name", "number");
        SDOType phoneType = (SDOType) typeHelper.define(PhoneTypeDO);
        phoneType.addBaseType(stringType);
        phoneType.setInstanceClassName("uri.my.PhoneType");
        
        // create a new Type for USAddress
        DataObject USaddrDO = dataFactory.create("commonj.sdo", "Type");
        USaddrDO.set("uri", "my.uri2");
        USaddrDO.set("name", "USAddress");
        DataObject streetProperty = USaddrDO.createDataObject("property");
        streetProperty.set("name", "street");
        DataObject cityProperty = USaddrDO.createDataObject("property");
        cityProperty.set("name", "city");
        DataObject quantityProperty = USaddrDO.createDataObject("property");
        quantityProperty.set("name", "quantity");
        DataObject partNumProperty = USaddrDO.createDataObject("property");
        partNumProperty.set("name", "partNum");
        DataObject phoneProperty = USaddrDO.createDataObject("property");
        phoneProperty.set("name", "thePhone");
        phoneProperty.set("type", phoneType);
        SDOType usAddrType = (SDOType) typeHelper.define(USaddrDO);
        usAddrType.setInstanceClassName("uri2.my.USAddress");

        // create a new Type for Quantity
        DataObject QuantityTypeDO = dataFactory.create("commonj.sdo", "Type");
        QuantityTypeDO.set("uri", "my.uri");
        QuantityTypeDO.set("name", "quantityType");
        SDOType quantityType = (SDOType) typeHelper.define(QuantityTypeDO);
        quantityType.addBaseType(intType);
        
        // create a new Type for SKU
        DataObject SkuDO = dataFactory.create("commonj.sdo", "Type");
        SkuDO.set("uri", "my.uri");
        SkuDO.set("name", "SKU");
        SDOType skuType = (SDOType) typeHelper.define(SkuDO);
        skuType.addBaseType(stringType);
        
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
        Type purchaseOrderType = typeHelper.define(PurchaseOrderDO);
        
        List<Type> types = new ArrayList<Type>();
        types.add(usAddrType);
        types.add(phoneType);
        types.add(skuType);
        types.add(purchaseOrderType);
        return types;
    }
}
