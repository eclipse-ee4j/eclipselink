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
// Martin Vojtek - November 14/2014 - 2.6 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue;

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
 * Tests XmlValue behavior when using external xml mapping.
 * XmlValue specified in XML overrides default behavior of XmlValue annotation.
 * When xml mapping is used, class with property annotated with XmlValue can inherit from class different than java.lang.Object.
 */
public class XmlValueCdnPriceInheritanceTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlvalue/cdnprice.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlvalue/cdnprice.json";

    public XmlValueCdnPriceInheritanceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { CDNPriceInheritance.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }


    public Map getProperties(){
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlvalue/eclipselink-oxm-cdnprice-inheritance.xml");

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSourceMap);

        return properties;
    }

    public void testSchemaGen() throws Exception{
        List controlSchemas = new ArrayList();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlvalue/cdnpriceinheritance_schema.xsd");
        controlSchemas.add(is);

        super.testSchemaGen(controlSchemas);

        InputStream src = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlvalue/cdnpriceinheritance_schema.xsd");
        String result = validateAgainstSchema(src, new StreamSource(schema));
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    protected Object getControlObject() {
        CDNPriceInheritance price = new CDNPriceInheritance();
        price.price = new BigDecimal("123.45678901234567890");
        return price;
    }

}
