/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Martin Vojtek - October 11/2014 - 2.6 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlid;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlID behavior when using external xml mapping. XmlID specified in XML
 * overrides default behavior of XmlID annotation. When xml mapping is used, the
 * property annotated with XmlID can have type different than java.lang.String.
 */
public class XmlIdExtensionOverrideTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlid/customer.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlid/customer.json";

    public XmlIdExtensionOverrideTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Customer.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Map getProperties() {
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();

        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlid/eclipselink-oxm-customer.xml");

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlid", new StreamSource(inputStream));

        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        return properties;
    }

    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<>();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlid/customer_schema.xsd");
        controlSchemas.add(is);

        super.testSchemaGen(controlSchemas);

        InputStream src = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlid/customer_schema.xsd");
        String result = validateAgainstSchema(src, new StreamSource(schema));
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    protected Object getControlObject() {
        Customer customer = new Customer();
        MyID myID = new MyID();
        myID.representation = "myID";
        customer.id = myID;
        return customer;
    }

}
