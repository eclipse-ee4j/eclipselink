/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.json.namespaces;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.json.JsonSchemaOutputResolver;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class NamespacesOnContextTestCases extends JSONMarshalUnmarshalTestCases{
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/person.json";
    private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/personSchema.json";

    public NamespacesOnContextTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Person.class});
    }

    protected Object getControlObject() {
        Person p = new Person();
        p.setId(10);
        p.setFirstName("Jill");
        p.setLastName("MacDonald");

        List<String> middleNames = new ArrayList<String>();
        middleNames.add("Jane");
        middleNames.add("Janice");
        p.setMiddleNames(middleNames);

        Address addr = new Address();
        addr.setStreet("The Street");
        addr.setCity("Ottawa");
        p.setAddress(addr);

        return p;
    }


    public Map getProperties(){
        Map props = new HashMap();
        props.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "@");

        Map<String, String> namespaceMap = new HashMap<String, String>();

        namespaceMap.put("namespace0", "ns0");
        namespaceMap.put("namespace1", "ns1");
        namespaceMap.put("namespace2", "ns2");
        namespaceMap.put("namespace3", "ns3");


        props.put(JAXBContextProperties.NAMESPACE_PREFIX_MAPPER, namespaceMap);
        return props;
    }

     public void testJSONSchemaGen() throws Exception{
         InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
         super.generateJSONSchema(controlSchema);
     }



}
