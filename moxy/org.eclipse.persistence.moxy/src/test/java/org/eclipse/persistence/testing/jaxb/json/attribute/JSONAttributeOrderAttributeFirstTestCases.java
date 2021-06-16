/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Radek Felcman - March 2019 - 2.7.5 - Initial implementation
package org.eclipse.persistence.testing.jaxb.json.attribute;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

import java.util.HashMap;
import java.util.Map;

public class JSONAttributeOrderAttributeFirstTestCases extends JSONMarshalUnmarshalTestCases {
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/address_attribute_first.json";

    public JSONAttributeOrderAttributeFirstTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
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
