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

/**
 *  @version $Header: SchemaLocationResolverTestCases.java 24-jan-2007.08:28:33 dmahar Exp $
 *  @author  mfobrien
 *  @since   release specific (what release of product did this appear in)
 */

/*
 * See document XSDHelperGenerateV2.doc for use case descriptions
 */
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaLocationResolver;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.slr.Example3SLR;
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.slr.Example5SLR;
import commonj.sdo.Type;
import commonj.sdo.impl.HelperProvider;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class SchemaLocationResolverTestCases extends XSDHelperGenerateTestCases {
    public SchemaLocationResolverTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.SchemaLocationResolverTestCases" };
        TestRunner.main(arguments);
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/customer.xsd";
    }

    public String getControlFileNameSchema() {
        return "org/eclipse/persistence/testing/sdo/schemas/customer.xsd";
    }

    public String getControlFileNameSchemaExample1() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerExample1.xsd";
    }

    public String getControlFileNameSchemaExample2() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerExample2.xsd";
    }

    public String getControlFileNameSchemaExample4() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerExample4.xsd";
    }

    public String getControlFileNameSchemaExample5() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerExample5.xsd";
    }

    public String getControlFileNameSchemaBillToShipTo() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerBillToShipTo.xsd";
    }

    public java.util.Map getMap() {
        HashMap schemaLocationMap = new HashMap();
        schemaLocationMap.put(new QName("my.uri", "CustomerType"), "customer.xsd");
        return schemaLocationMap;
    }

    // In the current version of the specification all types from the same namespace URI are 
    // generated into the same schema.
    public void testExample1Current() throws Exception{
        // get schema types
        defineTypesFromSchema();
        // get types from defined types
        Type customerType = typeHelper.getType("my.uri", "CustomerType");
        Type addressType = typeHelper.getType("my.uri", "AddressType");

        //DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());        
        // setup a list of types to pass to generate
        ArrayList types = new ArrayList();
        types.add(customerType);
        types.add(addressType);

        int sizeBefore = types.size();
        String generatedSchema = xsdHelper.generate(types);

        //String schema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);
        int sizeAfter = types.size();
        assertEquals(2, sizeBefore);
        assertEquals(2, sizeAfter);
        String controlSchema = getSchema(getControlFileNameSchemaExample1());

        //String controlSchema = getSchema(getControlFileNameSchema());        
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
        // TODO: failing
        
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchemaExample1()), generatedSchemaDoc);
        
    }

    // In the current version of the specification if a Type is generated into the resulting XML schema 
    // and was not included in the original List parameter then it is added to the list.  
    // The result is that the size of the List parameter may grow as a result of the generate call.
    public void testExample2Current() throws Exception{
        // get schema types
        defineTypesFromSchema();
        // get types from defined types
        Type customerType = typeHelper.getType("my.uri", "CustomerType");

        // setup a list of types to pass to generate
        ArrayList types = new ArrayList();
        types.add(customerType);

        int sizeBefore = types.size();
        String generatedSchema = xsdHelper.generate(types);
        int sizeAfter = types.size();
        assertEquals(1, sizeBefore);
        assertEquals(2, sizeAfter);
        String controlSchema = getSchema(getControlFileNameSchemaExample2());

        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
        
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchemaExample2()), generatedSchemaDoc);
    }

    // When null is returned from the SchemaLocationResolver it indicates that the referenced Types 
    // should be generated inline.  
    // The result for this example is the same as Example 1 Current Specification
    // this example will exercise if (schemaLocation == null) in SDOSchemaGenerator.addTypeToListIfNeeded() twice
    // TODO: 20060906 bidirectional/reference    
    public void testExample3New() throws Exception{
        // get schema types
        defineTypesFromSchema();
        // get types from defined types
        Type customerType = typeHelper.getType("my.uri", "CustomerType");
        Type addressType = typeHelper.getType("my.uri", "AddressType");

        // setup a list of types to pass to generate
        List types = new ArrayList();
        types.add(customerType);
        types.add(addressType);

        // return null for resolveSchemaLocation()
        SchemaLocationResolver slr = new Example3SLR();

        int sizeBefore = types.size();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, slr);
        int sizeAfter = types.size();
        assertEquals(2, sizeBefore);
        assertEquals(2, sizeAfter);
        //String controlSchema = getSchema(getControlFileNameSchemaExample1());
        String controlSchema = getSchema(getControlFileNameSchema());
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchema()), generatedSchemaDoc);
    }

    // When null is returned from the SchemaLocationResolver it indicates that the referenced Types 
    // should be generated inline.  
    // The result for this example is the same as Example 2 Current Specification
    public void testExample4New() throws Exception{
        // get schema types
        defineTypesFromSchema();
        // get types from defined types
        Type customerType = typeHelper.getType("my.uri", "CustomerType");

        // setup a list of types to pass to generate
        ArrayList types = new ArrayList();
        types.add(customerType);

        SchemaLocationResolver slr = new Example3SLR();

        int sizeBefore = types.size();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, slr);
        int sizeAfter = types.size();
        assertEquals(1, sizeBefore);
        assertEquals(2, sizeAfter);
        String controlSchema = getSchema(getControlFileNameSchemaExample4());

        //String controlSchema = getSchema(getControlFileNameSchema());        
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchemaExample4()), generatedSchemaDoc);
    }

    // When a non-null value is returned from the SchemaLocationResolver it indicates that the 
    // referenced Type is defined in another XML Schema.  
    // It is the responsibility of the XSDHelper to determine whether an import 
    // (source and referenced Types have the same URI) or include 
    // (source and referenced Types have a different URI) should be added to the XML Schema.
    public void testExample5New() throws Exception{
        // get schema types
        defineTypesFromSchema();
        // get types from defined types
        Type customerType = typeHelper.getType("my.uri", "CustomerType");

        // setup a list of types to pass to generate
        ArrayList types = new ArrayList();
        types.add(customerType);

        SchemaLocationResolver slr = new Example5SLR();

        int sizeBefore = types.size();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, slr);
        int sizeAfter = types.size();
        assertEquals(1, sizeBefore);
        assertEquals(1, sizeAfter);
        String controlSchema = getSchema(getControlFileNameSchemaExample5());

        //String controlSchema = getSchema(getControlFileNameSchema());        
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchemaExample5()), generatedSchemaDoc);
    }

    // In the current spec when generate(List types) is called and additional types are generated inline 
    // then the types list is modified to include those types.  
    // This behavior will remain the same and additionally when an include is generated instead of 
    // generating a type inline that type will be removed from the types List.
    public void testExample6New() throws Exception{
        // get schema types
        defineTypesFromSchema();
        // get types from defined types
        Type customerType = typeHelper.getType("my.uri", "CustomerType");
        Type addressType = typeHelper.getType("my.uri", "AddressType");

        // setup a list of types to pass to generate
        ArrayList types = new ArrayList();
        types.add(customerType);
        types.add(addressType);

        SchemaLocationResolver slr = new Example5SLR();

        int sizeBefore = types.size();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, slr);
        int sizeAfter = types.size();
        assertEquals(2, sizeBefore);
        assertEquals(1, sizeAfter);
        String controlSchema = getSchema(getControlFileNameSchemaExample5());

        //String controlSchema = getSchema(getControlFileNameSchema());        
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
        // TODO: below type address is still appearing
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchemaExample5()), generatedSchemaDoc);
    }

    public void testGenerateSchemaRoundTrip() throws Exception{
        org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver resolver = new org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver(getMap());
        resolver.setMap(getMap());
        List types = defineTypesFromSchema();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, resolver);

        String controlSchema = getSchema(getControlFileNameSchema());
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);

        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchema()), generatedSchemaDoc);
    }

    // Exception test cases
    // see line100 of SDOSchemaGenerator 
    public void testNullTypesPassedToGenerate() {
        ArrayList types = new ArrayList();
        types.add(null);
        String schema = null;
        try {
            schema = xsdHelper.generate(types);
        } catch (IllegalArgumentException e) {
            assertNull(schema);
        }
    }

    // see line90 of SDOSchemaGenerator    
    public void testEmptyListPassedToGenerate() {
        ArrayList types = new ArrayList();
        String schema = null;
        try {
            schema = xsdHelper.generate(types);
        } catch (IllegalArgumentException e) {
            assertNull(schema);
        }
    }

    public List defineTypesFromSchema() {
        InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/schemas/customer.xsd");

        //InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/customer.xsd");        
        return xsdHelper.define(is, null);
    }

    public List getTypesToGenerateFrom() {
        List types = new ArrayList();
        String uri = "my.uri";

        //String uri = null;        
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        //ADDRESS TYPE
        SDOType addressType = new SDOType(uri, "AddressType");
        addressType.setDataType(false);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setType(stringType);
        addressType.addDeclaredProperty(streetProp);

        //****Customer TYPE*****
        SDOProperty addressProp = new SDOProperty(aHelperContext);
        addressProp.setName("addressType");
        addressProp.setContainment(true);
        addressProp.setType(addressType);

        SDOType customerType = new SDOType(uri, "CustomerType");
        customerType.setDataType(false);
        customerType.addDeclaredProperty(addressProp);

        //types.add(addressType);
        types.add(customerType);

        return types;
    }
}
