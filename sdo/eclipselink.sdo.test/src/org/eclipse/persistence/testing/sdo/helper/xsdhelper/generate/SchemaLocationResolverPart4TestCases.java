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
/*
 * See document XSDHelperGenerateV2.doc for use case descriptions
 * These tests generate a schema with a sequence of addresses shipTo, billTo that are in
 * a different namespace than the customer
 */
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import commonj.sdo.Type;

import java.io.InputStream;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaLocationResolver;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.slr.Example3SLRMultiple;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.slr.Example4SLRMultiple;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class SchemaLocationResolverPart4TestCases extends XSDHelperGenerateTestCases {
    public SchemaLocationResolverPart4TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.SchemaLocationResolverPart4TestCases" };
        TestRunner.main(arguments);
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/customerBillToShipToDiffURI.xsd";
    }

    public String getControlFileNameSchemaBillToShipToDiffURI() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerBillToShipToDiffURI.xsd";
    }
    public String getControlFileNameSchemaBillToShipToDiffURIExample4() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerBillToShipToDiffURIExample4.xsd";
    }

    public java.util.Map getMap() {
        HashMap schemaLocationMap = new HashMap();
        schemaLocationMap.put(new QName("my.uri1", "CustomerType"), "customer.xsd");
        schemaLocationMap.put(new QName("my.uri2", "AddressType"), "address.xsd");
        schemaLocationMap.put(new QName("my.uri2", "PhoneType"), "phone.xsd");

        return schemaLocationMap;
    }

    // With the proposed API the behavior from Example 2 Current Specification can be maintained.
    public void testExample3NewMultipleImport() throws Exception {
        // get schema types
        defineTypesFromSchema();
        // get types from defined types
        Type customerType = typeHelper.getType("my.uri1", "CustomerType");

        // setup a list of types to pass to generate
        ArrayList types = new ArrayList();
        types.add(customerType);

        SchemaLocationResolver slr = new Example3SLRMultiple();

        int sizeBefore = types.size();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, slr);

        int sizeAfter = types.size();
        assertEquals(1, sizeBefore);
        assertEquals(1, sizeAfter);
        String controlSchema = getSchema(getControlFileNameSchemaBillToShipToDiffURI());

        //String controlSchema = getSchema(getControlFileNameSchema());        
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
        // TODO: below type address is still appearing
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchemaBillToShipToDiffURI()), generatedSchemaDoc);
    }

    public void testExample4NewMultipleImport() throws Exception{
        // get schema types
        defineTypesFromSchema();
        // get types from defined types
        Type customerType = typeHelper.getType("my.uri1", "CustomerType");

        // setup a list of types to pass to generate
        ArrayList types = new ArrayList();
        types.add(customerType);

        SchemaLocationResolver slr = new Example4SLRMultiple();

        int sizeBefore = types.size();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, slr);

        int sizeAfter = types.size();
        assertEquals(1, sizeBefore);
        assertEquals(1, sizeAfter);
        String controlSchema = getSchema(getControlFileNameSchemaBillToShipToDiffURIExample4());

        //String controlSchema = getSchema(getControlFileNameSchema());        
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
        // TODO: below type address is still appearing
        
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchemaBillToShipToDiffURIExample4()), generatedSchemaDoc);
    }

    public List defineTypesFromSchema() {
        InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/schemas/customerPart4.xsd");

        //InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/customer.xsd");        
        return xsdHelper.define(is, FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/schemas/");
    }

    public List getTypesToGenerateFrom() {
        List types = new ArrayList();
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        //ADDRESS TYPE
        SDOType addressType = new SDOType("my.uri2", "AddressType");
        addressType.setDataType(false);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setType(stringType);
        addressType.addDeclaredProperty(streetProp);

        //PHONE TYPE
        SDOType phoneType = new SDOType("my.uri2", "PhoneType");
        phoneType.setDataType(false);

        SDOProperty numberProp = new SDOProperty(aHelperContext);
        numberProp.setName("number");
        numberProp.setType(stringType);
        phoneType.addDeclaredProperty(numberProp);

        //****Customer TYPE*****
        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billToType");
        billToProp.setContainment(true);
        billToProp.setType(addressType);

        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipToType");
        shipToProp.setContainment(true);
        shipToProp.setType(addressType);

        SDOProperty phoneProp = new SDOProperty(aHelperContext);
        phoneProp.setName("phoneType");
        phoneProp.setContainment(true);
        phoneProp.setType(phoneType);

        SDOType customerType = new SDOType("my.url1", "CustomerType");
        customerType.setDataType(false);
        customerType.addDeclaredProperty(billToProp);
        customerType.addDeclaredProperty(shipToProp);
        customerType.addDeclaredProperty(phoneProp);

        //types.add(addressType);
        types.add(customerType);

        return types;
    }
}
