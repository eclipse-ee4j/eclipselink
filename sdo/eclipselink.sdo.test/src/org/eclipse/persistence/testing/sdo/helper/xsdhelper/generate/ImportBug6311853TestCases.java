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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class ImportBug6311853TestCases extends XSDHelperGenerateTestCases {
    public ImportBug6311853TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.ImportBug6311853TestCases" };
        TestRunner.main(arguments);
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/ImportBug6311853.xsd";

    }

    public Map getMap() {
        HashMap schemaLocationMap = new HashMap();
        schemaLocationMap.put(new QName("my.uri2", "Person"), "ImportBug6311853.xsd");
        schemaLocationMap.put(new QName("addressURI", "Address"), "ImportBug6311853Address.xsd");
        return schemaLocationMap;
    }

    public List getTypesToGenerateFrom() {
        List types = new ArrayList();
        String uri = "my.uri2";

        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOProperty xmlDataTypeProperty = (SDOProperty) typeHelper.getOpenContentProperty(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_DATATYPE);
        SDOProperty xmlSchemaTypeProperty = (SDOProperty) typeHelper.getOpenContentProperty(SDOConstants.ORACLE_SDO_URL, SDOConstants. XML_SCHEMA_TYPE_NAME);

        DataObject addressTypeDO = dataFactory.create(typeType);
        addressTypeDO.set("name", "Address");
        addressTypeDO.set("uri", "addressURI");
        addProperty(addressTypeDO, "street", SDOConstants.SDO_STRING, false, false, false);
        addProperty(addressTypeDO, "city", SDOConstants.SDO_STRING, false, false, false);
        Type addressType = typeHelper.define(addressTypeDO);

        DataObject personTypeDO = dataFactory.create(typeType);
        personTypeDO.set("name", "Person");
        personTypeDO.set("uri", uri);
        addProperty(personTypeDO, "name", SDOConstants.SDO_STRING, false, false, false);
        DataObject addressPropDO = addProperty(personTypeDO, "address", addressType, true, false, true);
        addressPropDO.set(xmlDataTypeProperty, dataObjectType);
        addressPropDO.set(xmlSchemaTypeProperty, addressType);
        Type personType = typeHelper.define(personTypeDO);

        types.add(personType);

        return types;
    }

    public void testGenerateSchemaRoundTrip() throws Exception {
        DefaultSchemaLocationResolver resolver = new DefaultSchemaLocationResolver(getMap());
        FileInputStream is = new FileInputStream(getControlFileName());

        List types = ((SDOXSDHelper)xsdHelper).define(is, FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/");
        ArrayList firstType = new ArrayList(1);
        firstType.add(typeHelper.getType("my.uri2", "Person"));
        String generatedSchema = ((SDOXSDHelper)xsdHelper).generate(firstType, resolver);

        String controlSchema = getSchema(getControlFileName());

        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);

        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);
        reader.close();

        assertSchemaIdentical(getDocument(getControlFileName()), generatedSchemaDoc);

    }
}
