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
//     Radek Felcman - March 2019 - 2.7.5 - Initial implementation
package org.eclipse.persistence.testing.jaxb.json.attribute;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

import java.util.HashMap;
import java.util.Map;

public class JSONAttributeOrderElementFirstTestCases extends JSONMarshalUnmarshalTestCases {
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/address_element_first.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/address_attribute_first.json";

    public JSONAttributeOrderElementFirstTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        setClasses(new Class[]{AddressElementAttributeOrder.class});
    }

    protected Object getControlObject() {
        AddressElementAttributeOrder add = new AddressElementAttributeOrder();
        add.id = "10";
        add.street = "Main street";

        return add;
    }

    public Map getProperties(){
        Map props = new HashMap();
        props.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "@");
        return props;
    }

}
