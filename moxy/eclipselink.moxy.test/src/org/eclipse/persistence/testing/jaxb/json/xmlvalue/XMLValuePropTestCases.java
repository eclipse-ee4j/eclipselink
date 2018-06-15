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
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.json.xmlvalue;

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
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XMLValuePropTestCases extends JAXBWithJSONTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/xmlvalue/person.json";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/xmlvalue/person.xml";

    private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/xmlvalue/personSchema.json";
    public XMLValuePropTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Person.class});
        setControlJSON(JSON_RESOURCE);
        setControlDocument(XML_RESOURCE);
    }

    public Map getProperties(){
        Map props = new HashMap();
        props.put(JAXBContextProperties.JSON_VALUE_WRAPPER, "valuewrapper");
        return props;
    }

    @Override
    protected Object getControlObject() {
        Person p = new Person();
        p.setFirstName("Sally");
        p.setLastName("Smith");
        p.setMiddleNames(new ArrayList());
        PhoneNumber phone = new PhoneNumber();
        phone.number = "1234567";
        phone.areaCode = "613";
        p.setPhoneNumber(phone);

        Address addr = new Address();
        List<String> addressInfos = new ArrayList<String>();
        addressInfos.add("someStreet");
        addressInfos.add("someCity");
        addressInfos.add("somePostalCode");
        addr.setAddressInfo(addressInfos);
        p.setAddress(addr);

        return p;
    }

     public void testJSONSchemaGen() throws Exception{
         InputStream controlSchema = ClassLoader.getSystemResourceAsStream(JSON_SCHEMA_RESOURCE);
         super.generateJSONSchema(controlSchema);

     }


}
