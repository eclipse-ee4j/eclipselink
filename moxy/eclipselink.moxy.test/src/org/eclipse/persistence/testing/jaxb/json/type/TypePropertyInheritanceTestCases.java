/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Martin Vojtek - 2.6.0
package org.eclipse.persistence.testing.jaxb.json.type;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.type.model.CustomerWithInheritance;
import org.eclipse.persistence.testing.jaxb.json.type.model.PersonWithType;

/**
 * Tests unmarshal of type and xsi:type properties.
 *
 * @author Martin Vojtek
 *
 */
public class TypePropertyInheritanceTestCases extends JSONTestCases {
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/type/type_property_inheritance.json";

    public TypePropertyInheritanceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{PersonWithType.class});
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

        CustomerWithInheritance c = new CustomerWithInheritance();
        c.name = "theName";
        c.type = "propertyType";

        QName name = new QName("");

        JAXBElement<Object> jbe = new JAXBElement<Object>(name, Object.class, c );
        return jbe;
    }

    protected Object getControlObject() {
        CustomerWithInheritance c = new CustomerWithInheritance();
        c.name = "theName";
        c.type = "propertyType";
        return c;
    }

}
