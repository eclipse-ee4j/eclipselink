/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - November 2012 - 2.4
package org.eclipse.persistence.testing.jaxb.json.namespaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.PropertyException;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class SeparatorInNameTestCases extends JSONMarshalUnmarshalTestCases{
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/person_separator.json";

    public SeparatorInNameTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Person.class});
    }

    protected Object getControlObject() {
        Person p = new Person();
        p.setId(10);
        p.setFirstName("Jill");
        p.setLastName("MacDonald");
        p.theattribute = "attributeValue";
        p.a = "aValue";
        p.aa = "aaValue";
        p.aaa = "aaaValue";
        p.aaaa = new Address();
        Address addr = new Address();
        List<Address> addresses = new ArrayList<Address>();
        addresses.add(addr);
        p.aaaaa = addresses;
        List<String> middleNames = new ArrayList<String>();
        middleNames.add("Jane");
        middleNames.add("Janice");
        p.setMiddleNames(middleNames);

        return p;
    }


    public Map getProperties(){
        Map props = new HashMap();
        props.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "@");

        Map<String, String> namespaceMap = new HashMap<String, String>();

        namespaceMap.put("namespace0", "ns0");
        namespaceMap.put("namespace1", "");
        namespaceMap.put("namespace2", "ns2");
        namespaceMap.put("namespace3", "ns3");

        props.put(JAXBContextProperties.JSON_NAMESPACE_SEPARATOR, 'a');
        props.put(JAXBContextProperties.NAMESPACE_PREFIX_MAPPER, namespaceMap);
        return props;
    }
}
