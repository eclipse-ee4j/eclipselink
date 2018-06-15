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
//   Denise Smith  - Dec 2012
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlEnum via eclipselink-oxm.xml
 *
 */
public class XmlEnumInheritanceTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlenum/subset.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlenum/subset.json";
    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     * @throws Exception
     */
    public XmlEnumInheritanceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {SubsetHolder.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

     public Map getProperties(){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlenum/eclipselink-oxm-subset.xml");

            HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
            metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum", new StreamSource(inputStream));
            Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

            return properties;
        }

        public void testSchemaGen() throws Exception{
            List controlSchemas = new ArrayList();
            InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlenum/subset.xsd");
            controlSchemas.add(is);
            super.testSchemaGen(controlSchemas);

        }

        public void testInstanceDocValidation() {
            InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlenum/subset.xsd");
            StreamSource schemaSource = new StreamSource(schema);

            InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            String result = validateAgainstSchema(instanceDocStream, schemaSource);
            assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
        }

        @Override
        protected Object getControlObject() {
            // setup control objects
            SubsetHolder holder = new SubsetHolder();
            holder.name= "test";
            holder.coin = CoinSubset.NICKEL;
            return holder;
        }
}
