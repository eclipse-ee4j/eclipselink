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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import commonj.sdo.Type;
import commonj.sdo.impl.HelperProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;

public class ImportsTestCases extends XSDHelperGenerateTestCases {
    public ImportsTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.ImportsTestCases" };
        TestRunner.main(arguments);
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/ImportsWithNamespacesGenerated.xsd";
    }

    public Map getMap() {
        HashMap schemaLocationMap = new HashMap();
        schemaLocationMap.put(new QName("my.uri2", "USAddress"), "Address.xsd");
        schemaLocationMap.put(new QName("my.uri3", "quantityType"), "Quantity.xsd");
        schemaLocationMap.put(new QName("my.uri4", "SKU"), "SKU.xsd");
        return schemaLocationMap;
    }

    protected List getGenerateAllControlFileNames() {
        ArrayList controlFiles = new ArrayList();
        controlFiles.add("org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/ImportsWithNamespaces.xsd");
        controlFiles.add("org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/Quantity.xsd");
        controlFiles.add("org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/SKU.xsd");
        controlFiles.add("org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/Address.xsd");
        return controlFiles;
    }

    protected HashMap getSchemaNamespacesMap() {
        HashMap namespaces = new HashMap();
        namespaces.put("my.uri", "ImportsWithNamespaces.xsd");
        namespaces.put("my.uri2", "Address.xsd");
        namespaces.put("my.uri3", "Quantity.xsd");
        namespaces.put("my.uri4", "SKU.xsd");
        return namespaces;
    }

    public List getTypesToGenerateFrom() {
        //for this test all the types we want generated are in the list of types but some are in different targetnamespaces
        List types = new ArrayList();
        String uri = "my.uri";
        String uri2 = "my.uri2";
        String uri3 = "my.uri3";
        String uri4 = "my.uri4";

        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType USaddrType = new SDOType(uri2, "USAddress");
        USaddrType.setDataType(false);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setType(stringType);        
        USaddrType.addDeclaredProperty(streetProp);
        
        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setType(stringType);
        USaddrType.addDeclaredProperty(cityProp);

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri3, "quantityType");
        quantityType.setDataType(true);
        quantityType.addBaseType((SDOType)intType);

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri4, "SKU");
        SKUType.setDataType(true);
        SKUType.addBaseType((SDOType)stringType);

        /****PURCHASEORDER TYPE*****/
        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setContainment(true);        
        shipToProp.setType(USaddrType);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setContainment(true);
        billToProp.setType(USaddrType);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        quantityProp.setType(quantityType);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        partNumProp.setType(SKUType);

        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setDataType(false);
        POtype.addDeclaredProperty(shipToProp);
        POtype.addDeclaredProperty(billToProp);
        POtype.addDeclaredProperty(quantityProp);
        POtype.addDeclaredProperty(partNumProp);
       
        //types.add(USaddrType);
        types.add(POtype);

        //types.add(quantityType);
        //types.add(SKUType);
        return types;
    }
}
