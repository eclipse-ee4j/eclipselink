/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.objectgraph;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.ObjectGraph;
import org.eclipse.persistence.jaxb.Subgraph;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;

public class ObjectGraphBindingsTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_dynamic.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_dynamic_write.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_dynamic.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_dynamic_write.json";

    public ObjectGraphBindingsTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[]{Customer.class, Address.class, PhoneNumber.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);

        jaxbUnmarshaller.setProperty(UnmarshallerProperties.OBJECT_GRAPH, "bindings_graph");
        jaxbMarshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, "bindings_graph");
    }

    @Override
    protected Object getControlObject() {
        Customer cust = new Customer();
        cust.lastName = "Doe";
        cust.age = "35";
        cust.address = new Address();
        cust.address.country = "Canada";

        return cust;
    }

    @Override
    public Object getWriteControlObject() {
        Customer cust = new Customer();
        cust.age = "35";
        cust.firstName = "John";
        cust.lastName = "Doe";
        cust.gender = "Make";
        cust.address = new Address();
        cust.address.city = "Ottawa";
        cust.address.country = "Canada";
        cust.address.street = "123 Fake Street";
        cust.phoneNumbers = new ArrayList<PhoneNumber>();
        PhoneNumber pn = new PhoneNumber();
        pn.areaCode = "613";
        pn.number = "123-4567";
        cust.phoneNumbers.add(pn);

        pn = new PhoneNumber();
        pn.areaCode = "613";
        pn.number = "345-6789";
        cust.phoneNumbers.add(pn);
        return cust;
    }

    protected Map getProperties() {

        Map overrides = new HashMap();

        String overridesString =
        "<?xml version='1.0' encoding='UTF-8'?>" +
        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
        "<java-types>" +
        "<java-type name='org.eclipse.persistence.testing.jaxb.objectgraph.Customer'>" +
        "<xml-named-object-graphs>" +
        "<xml-named-object-graph name=\"bindings_graph\">" +
            "<xml-named-attribute-node name=\"lastName\"/>" +
            "<xml-named-attribute-node name=\"age\"/>" +
            "<xml-named-attribute-node name=\"address\" subgraph=\"country\"/>" +
        "</xml-named-object-graph>" +
        "</xml-named-object-graphs>" +
        "</java-type>" +
        " <java-type name='org.eclipse.persistence.testing.jaxb.objectgraph.Address'/>" +
        "</java-types>" +
        "</xml-bindings>";


        DOMSource src = null;
        try {
            Document doc = parser.parse(new ByteArrayInputStream(overridesString.getBytes()));
            src = new DOMSource(doc.getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during setup");
        }

        overrides.put("org.eclipse.persistence.testing.jaxb.objectgraph", src);

        Map props = new HashMap();
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return props;
    }
}

