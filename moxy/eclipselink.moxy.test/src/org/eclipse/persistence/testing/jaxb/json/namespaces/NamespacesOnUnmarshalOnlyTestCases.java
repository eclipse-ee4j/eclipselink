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
//     Denise Smith - November 2012 - 2.4
package org.eclipse.persistence.testing.jaxb.json.namespaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.PropertyException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class NamespacesOnUnmarshalOnlyTestCases extends JSONMarshalUnmarshalTestCases{
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/person.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/person_no_namespaces.json";

    public NamespacesOnUnmarshalOnlyTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        setClasses(new Class[]{Person.class});
    }

    public void setUp() throws Exception{
        super.setUp();
        try {
            Map<String, String> namespaceMap = new HashMap<String, String>();

            namespaceMap.put("namespace0", "ns0");
            namespaceMap.put("namespace1", "ns1");
            namespaceMap.put("namespace2", "ns2");
            namespaceMap.put("namespace3", "ns3");
            jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaceMap);
        } catch (PropertyException e) {
            e.printStackTrace();
            fail("An error occurred during setup.");
        }
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
        /*
        Map<String, String> namespaceMap = new HashMap<String, String>();

        namespaceMap.put("ns0", "namespace0");
        namespaceMap.put("ns1", "namespace1");
        namespaceMap.put("ns2", "namespace2");
        namespaceMap.put("ns3", "namespace3");


        props.put(JAXBContext.NAMESPACES, namespaceMap);*/
        return props;
    }

}
