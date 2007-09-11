/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.FileReader;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

/* #  6067502 22-MAY-07 TOPLINK 4 15 N 1339 SDO 11.1.1.0.0 NO RELEASE 
 * SDO: JAVA CODE GENERATION REQUIRES SDO RESERVED WORD NAME COLLISION HANDLING 
 * Mangled class name collision will occur here resulting in the wrong Address property being used 
 * causing an isMany=true failure check
 * see  testLoadFromAndSaveAfterDefineMultipleSchemas()
 */

public class LoadAndSaveValuePropTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveValuePropTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveValuePropTestCases" };
        TestRunner.main(arguments);
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/valuePropNoSchema.xml";
    }

    protected String getControlDataObjectFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/valueProp.xml";
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/valueProp.xml";
    }

    protected String getSchemaName() {
        return "org/eclipse/persistence/testing/sdo/helper/xmlhelper/ValueProp.xsd";
    }

    protected String getControlRootURI() {
        return "urn:customer-example";
    }
    
    protected String getUnrelatedSchemaName(){
      return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithInstanceClass.xsd";
    }

    protected String getControlRootName() {
        return "customer";
    }
    
    protected String getRootInterfaceName() {
        return "CustomerType";
    }

    protected void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type dateType = typeHelper.getType("commonj.sdo", "String");
        Type pInfoType = registerPersonalInfoType();        

        //phoneNumberDO TYPE
        DataObject phoneNumberDO = dataFactory.create("commonj.sdo", "Type");
        phoneNumberDO.set("uri", getControlRootURI());
        phoneNumberDO.set("name", "phone-number");
                
        //TODO:How to do this  - is this possbile??? need an open content prop to represent value property?
        addProperty(phoneNumberDO, "value", stringType, false, false, true);
        addProperty(phoneNumberDO, "ptype", stringType, false, false, false);
        Type phoneNumberType = typeHelper.define(phoneNumberDO);
        ((SDOProperty)phoneNumberType.getProperty("value")).setValueProperty(true);
        ((SDOProperty)phoneNumberType.getProperty("value")).buildMapping(null, -1);

        //ADDRESS TYPE
        DataObject addressTypeDO = dataFactory.create("commonj.sdo", "Type");
        addressTypeDO.set("uri", getControlRootURI());
        addressTypeDO.set("name", "address-type");

        addProperty(addressTypeDO, "street", stringType, true, true, true);
        addProperty(addressTypeDO, "city", stringType, true, false, true);
        addProperty(addressTypeDO, "state", stringType, true, false, true);
        addProperty(addressTypeDO, "zip-code", stringType, true, false, true);

        Type addressType = typeHelper.define(addressTypeDO);

        //CONTACT INFO
        DataObject contactTypeDO = dataFactory.create("commonj.sdo", "Type");
        contactTypeDO.set("uri", getControlRootURI());
        contactTypeDO.set("name", "contact-info");
        addProperty(contactTypeDO, "billing-address", addressType, true, false, true);
        addProperty(contactTypeDO, "shipping-address", addressType, true, false, true);
        DataObject phoneProp = addProperty(contactTypeDO, "phone-number", phoneNumberType, true, true, true);
        Type contactType = typeHelper.define(contactTypeDO);

        //GENDER GLOBAL PROP              
        DataObject genderPropertyDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        genderPropertyDO.set("name", "gender");
        genderPropertyDO.set("type", stringType);
        Property genderProp = typeHelper.defineOpenContentProperty(getControlRootURI(), genderPropertyDO);

        //CUSTOMER
        DataObject customerTypeDO = dataFactory.create("commonj.sdo", "Type");
        customerTypeDO.set("uri", getControlRootURI());
        customerTypeDO.set("name", "customer-type");
        addProperty(customerTypeDO, "personal-info", pInfoType, true, false, true);
        //addProperty(customerTypeDO, "first-name", stringType, true, false, true);
        //addProperty(customerTypeDO, "last-name", stringType, true, false, true);
        //addProperty(customerTypeDO, "gender", genderProp, true, false);        
        //addProperty(customerTypeDO, "date-of-birth", dateType, true, false, true);
        addProperty(customerTypeDO, "contact-info", contactType, true, false, true);
        //addProperty(phoneNumberDO, "phone-number", phoneNumberType, true, false, true);
        Type customerType = typeHelper.define(customerTypeDO);

        DataObject propDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        propDO.set("name", getControlRootName());
        propDO.set("type", customerType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    private Type registerPersonalInfoType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type dateType = typeHelper.getType("commonj.sdo", "String");
        DataObject customerTypeDO = dataFactory.create("commonj.sdo", "Type");
        customerTypeDO.set("uri", getControlRootURI());
        customerTypeDO.set("name", "personal-info");
        addProperty(customerTypeDO, "first-name", stringType, true, false, true);
        addProperty(customerTypeDO, "last-name", stringType, true, false, true);
        addProperty(customerTypeDO, "gender", stringType, true, false, true);
        addProperty(customerTypeDO, "date-of-birth", dateType, true, false, true);
        Type pInfoType = typeHelper.define(customerTypeDO);
        return pInfoType;
    }
            
}