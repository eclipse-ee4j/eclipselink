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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class CyclicImportsTestCases extends XSDHelperGenerateTestCases {
    public CyclicImportsTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate.CyclicImportsTestCases" };
        TestRunner.main(arguments);
    }

    public Map getMap() {
        HashMap schemaLocationMap = new HashMap();
        schemaLocationMap.put(new QName("my.uri2", "USAddress"), "Cyclic2.xsd");
        return schemaLocationMap;
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/Cyclic1.xsd";
    }

    protected List getGenerateAllControlFileNames() {
        ArrayList controlFiles = new ArrayList();
        controlFiles.add("org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/Cyclic1.xsd");
        controlFiles.add("org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/Cyclic2.xsd");
        return controlFiles;
    }

    public List getTypesToGenerateFrom() {
        //for this test all the types we want generated are in the list of types but some are in different targetnamespaces
        List types = new ArrayList();
        String uri = "my.uri";
        String uri2 = "my.uri2";

        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri, "quantityType");
        quantityType.setDataType(true);
        quantityType.addBaseType((SDOType)intType);

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri, "SKU");
        SKUType.setDataType(true);
        SKUType.addBaseType((SDOType)stringType);

        /****PHONE TYPE*****/
        SDOType phoneType = new SDOType(uri, "PhoneType");
        phoneType.setDataType(false);

        SDOProperty numberProp = new SDOProperty(aHelperContext);
        numberProp.setName("number");
        numberProp.setXsdLocalName("number");
        numberProp.setXsd(true);
        numberProp.setType(stringType);
        phoneType.addDeclaredProperty(numberProp);

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType USaddrType = new SDOType(uri2, "USAddress");
        USaddrType.setDataType(false);

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setXsdLocalName("street");
        streetProp.setXsd(true);
        streetProp.setType(stringType);
        USaddrType.addDeclaredProperty(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setXsdLocalName("city");
        cityProp.setXsd(true);
        cityProp.setType(stringType);
        USaddrType.addDeclaredProperty(cityProp);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        quantityProp.setXsdLocalName("quantity");
        quantityProp.setXsd(true);
        quantityProp.setType(quantityType);
        USaddrType.addDeclaredProperty(quantityProp);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
          partNumProp.setXsdLocalName("partNum");
        partNumProp.setXsd(true);
        
        partNumProp.setType(SKUType);
        USaddrType.addDeclaredProperty(partNumProp);

        /****PURCHASEORDER TYPE*****/
        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
          shipToProp.setXsdLocalName("shipTo");
        shipToProp.setXsd(true);
        
        shipToProp.setContainment(true);
        shipToProp.setType(USaddrType);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
          billToProp.setXsdLocalName("billTo");
        billToProp.setXsd(true);
        
        billToProp.setContainment(true);
        billToProp.setType(USaddrType);

        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setDataType(false);
        POtype.addDeclaredProperty(shipToProp);
        POtype.addDeclaredProperty(billToProp);
        //POtype.addDeclaredProperty(quantityProp);
        //POtype.addDeclaredProperty(partNumProp);
        //types.add(USaddrType);
        types.add(POtype);
        types.add(phoneType);
        //types.add(quantityType);
        types.add(SKUType);
        return types;
    }
}
