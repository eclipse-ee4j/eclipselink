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
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.model.PurchaseOrderType;
import org.eclipse.persistence.testing.jaxb.json.namespaces.model.USAddress;

import javax.xml.bind.PropertyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamespaceOnXMLOnlyTestCases extends JAXBWithJSONTestCases {

    private static final String NAMESPACE = "http://tempuri.org/PurchaseOrderSchema.xsd";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/purchase_order.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/purchase_order.json";

    public NamespaceOnXMLOnlyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{PurchaseOrderType.class, USAddress.class});
    }

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

    public void setUp() throws Exception{
        super.setUp();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put(NAMESPACE, "");
        try{
            this.getJSONMarshaller().setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
            this.getJSONUnmarshaller().setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
            this.getJSONUnmarshaller().setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
            this.getJSONUnmarshaller().setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
            this.getJSONUnmarshaller().setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        }catch(PropertyException e){
            e.printStackTrace();
            fail("An error occurred setting properties during setup.");
        }
    }

    public Map getProperties(){
        Map props = new HashMap();
        props.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "@");
        Map<String, String> namespaceMap = new HashMap<String, String>();

        namespaceMap.put(NAMESPACE, "");
        props.put(JAXBContextProperties.NAMESPACE_PREFIX_MAPPER, namespaceMap);
        props.put(JAXBContextProperties.JSON_INCLUDE_ROOT, true);
        props.put(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
        return props;
    }


}
