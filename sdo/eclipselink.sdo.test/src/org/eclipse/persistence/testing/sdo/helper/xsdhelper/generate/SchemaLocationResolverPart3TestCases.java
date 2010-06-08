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
 * We are testing schemas with a sequence of 2 addresses (billto, shipto)
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
import org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.slr.Example3SLR;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class SchemaLocationResolverPart3TestCases extends XSDHelperGenerateTestCases {
    public SchemaLocationResolverPart3TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.SchemaLocationResolverPart3TestCases" };
        TestRunner.main(arguments);
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/customerBillToShipTo.xsd";
    }

    public String getControlFileNameSchema() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerBillToShipTo.xsd";
    }

    public String getControlFileNameSchemaBillToShipTo() {
        return "org/eclipse/persistence/testing/sdo/schemas/customerBillToShipTo.xsd";
    }

    public java.util.Map getMap() {
        HashMap schemaLocationMap = new HashMap();
        schemaLocationMap.put(new QName("my.uri", "CustomerType"), "customer.xsd");
        return schemaLocationMap;
    }

    public void testTypeIsNotReAddedUsing2Addresses() throws Exception{
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
        //String controlSchema = getSchema(getControlFileNameSchemaExample3());
        String controlSchema = getSchema(getControlFileNameSchemaBillToShipTo());
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);
      StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertSchemaIdentical(getDocument(getControlFileNameSchemaBillToShipTo()), generatedSchemaDoc);

    }

    public List defineTypesFromSchema() {
        InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/schemas/customerBillToShipTo.xsd");

        //InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/customer.xsd");        
        return xsdHelper.define(is, null);
    }

    public List getTypesToGenerateFrom() {
        List types = new ArrayList();
        String uri = "my.uri";
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        //ADDRESS TYPE
        SDOType addressType = new SDOType(uri, "AddressType");
        addressType.setDataType(false);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setType(stringType);
        addressType.addDeclaredProperty(streetProp);

        //****Customer TYPE*****
        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setContainment(true);
        shipToProp.setType(addressType);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setContainment(true);
        billToProp.setType(addressType);

        SDOType customerType = new SDOType(uri, "CustomerType");
        customerType.setDataType(false);
        customerType.addDeclaredProperty(shipToProp);
        customerType.addDeclaredProperty(billToProp);

        //types.add(addressType);
        types.add(customerType);

        return types;
    }
}
