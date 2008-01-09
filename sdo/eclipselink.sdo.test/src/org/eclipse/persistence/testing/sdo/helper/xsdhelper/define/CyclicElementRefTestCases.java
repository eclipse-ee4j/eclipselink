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
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import java.util.List;
import java.util.ArrayList;
import junit.textui.TestRunner;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class CyclicElementRefTestCases extends XSDHelperDefineTestCases {
   String uri = "my.uri";
    String uri2 = "my.uri2";
    public CyclicElementRefTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(CyclicElementRefTestCases.class);
    }

    public String getSchemaToDefine() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/Cyclic1ElementRef.xsd";
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
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
    }

    
     public List<SDOType> getControlTypes() {
        List<SDOType> types = new ArrayList<SDOType>();

        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");

        /****PHONE TYPE*****/
        SDOType phoneType = new SDOType(uri, "PhoneType");
        phoneType.setXsd(true);
        phoneType.setXsdLocalName("PhoneType");
        phoneType.setDataType(false);
        phoneType.setInstanceClassName("my.PhoneType");

        SDOProperty numberProp = new SDOProperty(aHelperContext);
        numberProp.setName("number");
        numberProp.setXsdLocalName("number");
        numberProp.setXsd(true);
        numberProp.setType(stringType);
        phoneType.addDeclaredProperty(numberProp);
      
        /****PURCHASEORDER TYPE*****/
        SDOProperty orderNameProp = new SDOProperty(aHelperContext);
        orderNameProp.setName("ordername");
        orderNameProp.setXsdLocalName("ordername");
        orderNameProp.setContainment(false);
        orderNameProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        orderNameProp.setType(SDOConstants.SDO_STRING);
        orderNameProp.setXsd(true);

        SDOProperty phoneProp = new SDOProperty(aHelperContext);
        phoneProp.setName("phoneType");
        phoneProp.setXsdLocalName("phoneType");
        phoneProp.setContainment(true);
        phoneProp.setType(phoneType);
        phoneProp.setXsd(true);
        
        SDOProperty phoneAttrProp = new SDOProperty(aHelperContext);
        phoneAttrProp.setName("phoneTypeAttr");
        phoneAttrProp.setXsdLocalName("phoneTypeAttr");
        phoneAttrProp.setContainment(false);
        phoneAttrProp.setType(SDOConstants.SDO_STRING);
        phoneAttrProp.setXsd(true);

        SDOType POtype = new SDOType(uri2, "PurchaseOrder");
        POtype.setXsd(true);
        POtype.setXsdLocalName("PurchaseOrder");
        POtype.setInstanceClassName("uri2.my.PurchaseOrder");
        POtype.setDataType(false);
        POtype.addDeclaredProperty(orderNameProp);
        POtype.addDeclaredProperty(phoneProp);
        POtype.addDeclaredProperty(phoneAttrProp);
        
        
        types.add(phoneType);
        types.add(POtype);        
        return types;
    }
}