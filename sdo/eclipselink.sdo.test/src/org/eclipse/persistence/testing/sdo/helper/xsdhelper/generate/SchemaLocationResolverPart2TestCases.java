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
/*
 * See document XSDHelperGenerateV2.doc for use case descriptions
 */
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import commonj.sdo.Type;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class SchemaLocationResolverPart2TestCases extends XSDHelperGenerateTestCases {
    public SchemaLocationResolverPart2TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.SchemaLocationResolverPart2TestCases" };
        TestRunner.main(arguments);
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/customerPart2.xsd";
    }

    public String getControlFileNameSchema() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerPart2.xsd";
    }

    public String getControlFileNameSchemaExample3() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerExample3Multiple.xsd";
    }

    public String getControlFileNameSchemaExample4() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerExample4Multiple.xsd";
    }

    public java.util.Map getMap() {
        HashMap schemaLocationMap = new HashMap();
        schemaLocationMap.put(new QName("my.uri1", "CustomerType"), "customer.xsd");
        schemaLocationMap.put(new QName("my.uri2", "AddressType"), "address.xsd");
        schemaLocationMap.put(new QName("my.uri2", "PhoneType"), "phone.xsd");
        schemaLocationMap.put(new QName("my.url2", "AddressType"), "customerurl2.xsd");
        schemaLocationMap.put(new QName("my.url2", "PhoneType"), "customerurl2.xsd");
        return schemaLocationMap;
    }

    /*
     * With the proposed API the behavior from Example 2 Current Specification can be maintained.
     */
    public void testExample3NewMultipleImport() throws Exception{
        // get schema types
        defineTypesFromSchema();
        // get types from defined types
        Type customerType = typeHelper.getType("my.uri1", "CustomerType");

        // setup a list of types to pass to generate
        ArrayList types = new ArrayList();
        types.add(customerType);

        SchemaLocationResolver slr = new Example3SLRMultiple();

        //int sizeBefore = types.size();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, slr);

        //int sizeAfter = types.size();
        //assertEquals(2, sizeBefore);
        // TODO: type was not removed, size is 2
        //assertEquals(1, sizeAfter);
        String controlSchema = getSchema(getControlFileNameSchemaExample3());

        //String controlSchema = getSchema(getControlFileNameSchema());        
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
        // TODO: below type address is still appearing
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchemaExample3()), generatedSchemaDoc);
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

        //int sizeBefore = types.size();
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(types, slr);

        String controlSchema = getSchema(getControlFileNameSchemaExample4());

        //String controlSchema = getSchema(getControlFileNameSchema());        
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
        // TODO: below type address is still appearing
        
        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchemaExample4()), generatedSchemaDoc);

    }

    public void testGenerateSchemaRoundTrip() throws Exception{
        org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver resolver = new org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver(getMap());
        resolver.setMap(getMap());
        List allTypes = defineTypesFromSchema();

        ArrayList types = new ArrayList();
        Iterator typesIter = allTypes.iterator();
        while(typesIter.hasNext()) {
            SDOType nextType = (SDOType)typesIter.next();
            if(nextType.getURI().equals("my.uri1")) {
                types.add(nextType);
            }
        }
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

    public List defineTypesFromSchema() {
        InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/schemas/customerPart2.xsd");

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
        SDOProperty addressProp = new SDOProperty(aHelperContext);
        addressProp.setName("addressType");
        addressProp.setContainment(true);
        addressProp.setType(addressType);

        SDOProperty phoneProp = new SDOProperty(aHelperContext);
        phoneProp.setName("phoneType");
        phoneProp.setContainment(true);
        phoneProp.setType(phoneType);

        SDOType customerType = new SDOType("my.url1", "CustomerType");
        customerType.setDataType(false);
        customerType.addDeclaredProperty(addressProp);
        customerType.addDeclaredProperty(phoneProp);

        //types.add(addressType);
        types.add(customerType);

        return types;
    }
}
