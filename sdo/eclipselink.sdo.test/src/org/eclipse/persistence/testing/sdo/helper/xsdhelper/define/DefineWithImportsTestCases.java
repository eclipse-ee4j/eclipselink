/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import commonj.sdo.Type;
import commonj.sdo.impl.HelperProvider;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.helper.ClassConstants;

/**
 * Test case: variant case to force a define with an undefined my.uri5 schemaLocation url
 *   <xsd:import namespace="my.uri5"/> - missing schemaLocation="uri5.xsd"
 */
public class DefineWithImportsTestCases extends XSDHelperDefineTestCases {
    public DefineWithImportsTestCases(String name) {
        super(name);
    }
    public static void main(String[] args) {
        TestRunner.run(DefineWithImportsTestCases.class);
    }
    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/ImportsWithNamespaces.xsd";
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
    }

    public List<Type> getControlTypes() {
        List<Type> types = new ArrayList<Type>();
        String uri = "my.uri";
        String uri2 = "my.uri2";
        String uri3 = "my.uri3";
        String uri4 = "my.uri4";

        String javaPackage = "uri.my";

        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType USaddrType = new SDOType(uri2, "USAddress");        
        USaddrType.setXsd(true);
        USaddrType.setXsdLocalName("USAddress");
        USaddrType.setDataType(false);
        USaddrType.setInstanceClassName("uri2.my." + "USAddress");

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setType(stringType);
        //streetProp.setAttribute(true);
        streetProp.setXsd(true);
        streetProp.setXsdLocalName("street");
        streetProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setType(stringType);
        //cityProp.setAttribute(true);
        cityProp.setXsd(true);
        cityProp.setXsdLocalName("city");
        cityProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(cityProp);

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri3, "quantityType");
        quantityType.setXsd(true);
        quantityType.setXsdLocalName("quantityType");
        quantityType.setDataType(true);
        quantityType.getBaseTypes().add(intType);
        //quantityType.setInstanceClassName("java.lang.Integer");
        quantityType.setInstanceClassName(ClassConstants.PINT.getName());

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri4, "SKU");
        SKUType.setXsd(true);
        SKUType.setXsdLocalName("SKU");
        SKUType.setDataType(true);
        SKUType.getBaseTypes().add(stringType);
        SKUType.setInstanceClassName("java.lang.String");

        /****PURCHASEORDER TYPE*****/
        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setXsd(true);
        POtype.setXsdLocalName("PurchaseOrder");
        POtype.setInstanceClassName(javaPackage + "." + "PurchaseOrder");
        POtype.setDataType(false);

        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setContainment(true);
        //shipToProp.setElement(true);
        shipToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        shipToProp.setType(USaddrType);
        shipToProp.setXsd(true);
        shipToProp.setXsdLocalName("shipTo");
        shipToProp.setContainingType(POtype);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setContainment(true);
        //billToProp.setElement(true);
        billToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        billToProp.setType(USaddrType);
        billToProp.setXsd(true);
        billToProp.setXsdLocalName("billTo");
        billToProp.setContainingType(POtype);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        //quantityProp.setAttribute(true);
        quantityProp.setType(quantityType);
        quantityProp.setXsd(true);
        quantityProp.setXsdLocalName("quantity");
        quantityProp.setContainingType(POtype);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        //partNumProp.setAttribute(true);
        partNumProp.setType(SKUType);
        partNumProp.setXsd(true);
        partNumProp.setXsdLocalName("partNum");
        partNumProp.setContainingType(POtype);

        POtype.getDeclaredProperties().add(shipToProp);
        POtype.getDeclaredProperties().add(billToProp);
        POtype.getDeclaredProperties().add(quantityProp);
        POtype.getDeclaredProperties().add(partNumProp);

        types.add(POtype);
        types.add(SKUType);
        types.add(USaddrType);
        types.add(quantityType);

        return types;

    }
    
    public void testExceptionCase() {
        try {
            String invalidURLFile = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/ImportsWithNamespacesError.xsd";
            InputStream is = getSchemaInputStream(invalidURLFile);
            List types = xsdHelper.define(is, getSchemaLocation());
        } catch(org.eclipse.persistence.exceptions.SDOException ex) {
            //dont do anything ... just for code coverage to hit this warning
        }
    }
}
