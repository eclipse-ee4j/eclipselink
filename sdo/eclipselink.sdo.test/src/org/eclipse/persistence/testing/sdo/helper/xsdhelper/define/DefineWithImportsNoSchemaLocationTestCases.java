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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaResolver;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.xml.sax.InputSource;

public class DefineWithImportsNoSchemaLocationTestCases extends XSDHelperDefineTestCases {

    public DefineWithImportsNoSchemaLocationTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(DefineWithImportsNoSchemaLocationTestCases.class);
    }

    public void testDefine() {

        InputStream is = getSchemaInputStream(getSchemaToDefine());

        List types = ((SDOXSDHelper) xsdHelper).define(new StreamSource(is), new TestResolver(getSchemaLocation()));
        log("\nExpected:\n");
        List controlTypes = getControlTypes();
        log(controlTypes);

        log("\nActual:\n");
        log(types);

        compare(getControlTypes(), types);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/ImportsWithNamespacesNoSchemaLocations.xsd";
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
    }

    public List<Type> getControlTypes() {
        String uri = "my.uri";
        String uri2 = "my.uri2";
        String uri3 = "my.uri3";
        String uri4 = "my.uri4";

        String javaPackage = "uri.my";

        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
        SDOType intType = (SDOType) typeHelper.getType("commonj.sdo", "Int");

        // create a new Type for USAddress
        DataObject USaddrDO = dataFactory.create("commonj.sdo", "Type");
        USaddrDO.set("uri", "my.uri2");
        USaddrDO.set("name", "USAddress");
        DataObject streetProperty = USaddrDO.createDataObject("property");
        streetProperty.set("name", "street");
        DataObject cityProperty = USaddrDO.createDataObject("property");
        cityProperty.set("name", "city");
        SDOType usAddrType = (SDOType) typeHelper.define(USaddrDO);
        usAddrType.setInstanceClassName("uri2.my.USAddress");

        // create a new Type for Quantity
        DataObject QuantityTypeDO = dataFactory.create("commonj.sdo", "Type");
        QuantityTypeDO.set("uri", "my.uri3");
        QuantityTypeDO.set("name", "quantityType");
        SDOType quantityType = (SDOType) typeHelper.define(QuantityTypeDO);
        quantityType.addBaseType(intType);
        quantityType.setInstanceClassName(ClassConstants.PINT.getName());
        
        // create a new Type for SKU
        DataObject SkuDO = dataFactory.create("commonj.sdo", "Type");
        SkuDO.set("uri", "my.uri4");
        SkuDO.set("name", "SKU");
        SDOType skuType = (SDOType) typeHelper.define(SkuDO);
        skuType.addBaseType(stringType);
        skuType.setInstanceClassName("java.lang.String");

        // create a new Type for PurchaseOrder
        DataObject PurchaseOrderDO = dataFactory.create("commonj.sdo", "Type");
        PurchaseOrderDO.set("uri", "my.uri");
        PurchaseOrderDO.set("name", "PurchaseOrder");
        DataObject shipToProperty = PurchaseOrderDO.createDataObject("property");
        shipToProperty.set("name", "shipTo");
        shipToProperty.set("type", usAddrType);
        DataObject billToProperty = PurchaseOrderDO.createDataObject("property");
        billToProperty.set("name", "billTo");
        billToProperty.set("type", usAddrType);
        DataObject quantityProperty = PurchaseOrderDO.createDataObject("property");
        quantityProperty.set("name", "quantity");
        quantityProperty.set("type", quantityType);
        DataObject partNumberProperty = PurchaseOrderDO.createDataObject("property");
        partNumberProperty.set("name", "partNum");
        partNumberProperty.set("type", skuType);
        SDOType purchaseOrderType = (SDOType) typeHelper.define(PurchaseOrderDO);
        purchaseOrderType.setInstanceClassName("uri.my.PurchaseOrder");
        
        List<Type> types = new ArrayList<Type>();
        types.add(purchaseOrderType);
        types.add(skuType);
        types.add(usAddrType);
        types.add(quantityType);
        return types;
    }

    static class TestResolver implements SchemaResolver {
        String schemaLocationBase;

        TestResolver(String schemaLocation) {
            this.schemaLocationBase = schemaLocation;
        }

        public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
            try {
                if (namespace.equals("my.uri3")) {
                    URL schemaUrl = new URI(schemaLocationBase).resolve("Quantity.xsd").toURL();
                    return new StreamSource(schemaUrl.toExternalForm());
                } else if (namespace.equals("my.uri2")) {
                    URL schemaUrl = new URI(schemaLocationBase).resolve("Address.xsd").toURL();
                    return new StreamSource(schemaUrl.toExternalForm());
                } else if (namespace.equals("my.uri4")) {
                    URL schemaUrl = new URI(schemaLocationBase).resolve("SKU.xsd").toURL();
                    return new StreamSource(schemaUrl.toExternalForm());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        /**
         * Satisfy EntityResolver interface implementation.
         * Allow resolution of external entities.
         * 
         * @param publicId
         * @param systemId
         * @return null
         */
        public InputSource resolveEntity(String publicId, String systemId) {
            return null;
        }
    }
}
