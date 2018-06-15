/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//    Denise Smith - August 2013

package org.eclipse.persistence.testing.jaxb.inheritance.typeElem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.inheritance.simple.Simple;


public class InheritanceTypeElementTestCases extends JAXBWithJSONTestCases{
    private Marshaller jsonMarshaller;
    private Unmarshaller jsonUnmarshaller;

    public InheritanceTypeElementTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Root.class, Child.class, Parent.class});
        setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/typeElem/typeElement.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/typeElem/typeElement.json");

        Map<String, String> namespaces = new HashMap<String, String>() ;
        namespaces.put(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi");
        namespaces.put("theNamespace", "ns0");

        jsonMarshaller = jaxbContext.createMarshaller();
        jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        jsonUnmarshaller = jaxbContext.createUnmarshaller();
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    public Marshaller getJSONMarshaller() throws Exception{
       return jsonMarshaller;
    }

    public Unmarshaller getJSONUnmarshaller() throws Exception{
       return jsonUnmarshaller;
    }

    public Object getControlObject() {
        Root r = new Root();
        Child child = new Child();
        child.foo = "aaa";
        child.type = "bbb";
        r.thing = child;
        return r;
    }

}
