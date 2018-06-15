/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0
package org.eclipse.persistence.testing.jaxb.json.type;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.type.model.Customer;
import org.eclipse.persistence.testing.jaxb.json.type.model.Person;

/**
 * Tests unmarshal of xsi:type property.
 *
 * @author Martin Vojtek
 *
 */
public class TypePrefixTestCases extends JSONTestCases {
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/type/type_prefix.json";

    public TypePrefixTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Person.class});
        setControlJSON(JSON_RESOURCE);
    }

    public void setUp() throws Exception{
        super.setUp();
        jsonMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        jsonMarshaller.setProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, ':');

        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi");
        jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);

        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        jsonUnmarshaller.setProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, ':');
        jsonUnmarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    protected Object getJSONReadControlObject() {

        Customer c = new Customer();
        c.name = "theName";

        QName name = new QName("");

        JAXBElement<Object> jbe = new JAXBElement<Object>(name, Object.class, c );
        return jbe;
    }

    protected Object getControlObject() {
        Customer c = new Customer();
        c.name = "theName";
        return c;
    }

}
