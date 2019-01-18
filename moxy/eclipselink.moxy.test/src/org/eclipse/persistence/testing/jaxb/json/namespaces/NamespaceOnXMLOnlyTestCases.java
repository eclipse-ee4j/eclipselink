/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Radek Felcman - January 2019 - Initial implementation
package org.eclipse.persistence.testing.jaxb.json.namespaces;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.model.PurchaseOrderType;
import org.eclipse.persistence.testing.jaxb.json.namespaces.model.USAddress;

import javax.xml.XMLConstants;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamespaceOnXMLOnlyTestCases extends JAXBWithJSONTestCases {

    private static final String NAMESPACE = "http://tempuri.org/PurchaseOrderSchema.xsd";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/purchase_order.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/purchase_order.json";
    private final static String XML_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/purchase_order.xsd";

    private Schema schema;

    public NamespaceOnXMLOnlyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{PurchaseOrderType.class, USAddress.class});
    }

    @Override
    protected Object getControlObject() {
        USAddress shipTo = new USAddress();
        shipTo.setName("Oracle Czech");
        shipTo.setStreet("U Trezorky");
        shipTo.setCity("Prague");
        shipTo.setState("Czech Republic");
        shipTo.setZip(15800);
        List<USAddress> shipToList = new ArrayList<USAddress>();
        shipToList.add(shipTo);

        USAddress billTo = new USAddress();
        billTo.setName("Oracle US");
        billTo.setStreet("Oracle Parkway");
        billTo.setCity("Redwood Shores");
        billTo.setState("California");
        billTo.setZip(94065);

        PurchaseOrderType purchaseOrder = new PurchaseOrderType();
        purchaseOrder.setShipTo(shipToList);
        purchaseOrder.setBillTo(billTo);
        return purchaseOrder;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schema = sf.newSchema(Thread.currentThread().getContextClassLoader().getResource(XML_SCHEMA_RESOURCE));
    }

    @Override
    public Map getProperties() {
        Map props = new HashMap();
        Map<String, String> namespaceMap = new HashMap<>();
        namespaceMap.put(NAMESPACE, "");
        props.put(JAXBContextProperties.NAMESPACE_PREFIX_MAPPER, namespaceMap);
        props.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "@");
        props.put(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
        return props;
    }

    @Override
    public Unmarshaller getJAXBUnmarshaller() {
        Unmarshaller unmarshaller = super.getJAXBUnmarshaller();
        unmarshaller.setSchema(schema);
        return unmarshaller;
    }

}
